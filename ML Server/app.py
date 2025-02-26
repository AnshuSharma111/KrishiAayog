# Flask related imports
from flask import Flask, request, jsonify
from flask_cors import CORS
# Gemini related imports
import google.generativeai as genai
# MongoDB related imports
from pymongo import MongoClient
import io
import datetime
import base64
# ML related imports
import torch
from PIL import Image
from livestock_model import predict as livestock_predict
from cropdisease_model import load_model, preprocess_image, CLASS_LABELS
# Other imports
import os
import uuid
from dotenv import load_dotenv
from auth import jwt_required

# Initialize Flask app
app = Flask(__name__)

# Load environment variables
load_dotenv()

# Set up MongoDB connection
client = MongoClient(os.getenv("MONGO_URI"))
db = client[os.getenv("DB")]
cdata = db[os.getenv("CROP_COL")]
ldata = db[os.getenv("LSTOCK_COL")]

# ---------------------------------------------------GEMINI---------------------------------------------------
# Set up Gemini model
API_KEY = os.getenv('API_KEY')
genai.configure(api_key=API_KEY)
llm = genai.GenerativeModel("gemini-1.5-flash")

# Utility function to parse LLM output
def process_llm_output(message):
    edited_msg = ''''''
    for i in range(len(message)):
        if i + 3 < len(message) and message[i:i+4] == '    ':
            continue
        if message[i] not in ['\n', '*', '\'']:
            edited_msg += message[i]
    desc, ca, cu = edited_msg.find("Description"), edited_msg.find("Causes"), edited_msg.find("Cure")
    description = edited_msg[desc + 13 : ca - 3]
    cause = edited_msg[ca + 8:cu - 3]
    cure = edited_msg[cu + 8:]
    return (description, cause, cure)

# Gemini function to get description of disease, causes, and cures
def gemini_query(typeOfObject ,predicted_class):
    if predicted_class == "normal":
        return ("The cow is normal!", "It has no disease", "It is healthy!")
    if "Healthy" in predicted_class:
        return ("The plant is healthy!", "It has no disease", "It is healthy!")
    message = llm.generate_content(f'''A {typeOfObject} has got the disease {predicted_class}. What is this disease and what are some cures to it?
                                Reply in the format:
                                1) Description [STRICT 50 WORD LIMIT]
                                2) Causes [STRICT 100 WORD LIMIT] 
                                3) Cures [STRICT 100-150 WORD LIMIT]
    ''').text
    return process_llm_output(message)

# Directory to save uploaded images
UPLOAD_DIR = "uploads"
os.makedirs(UPLOAD_DIR, exist_ok=True)

# Load crop disease model
crop_model = load_model("ML Server/Models/plant-disease-model.pth")
crop_model.eval()

# Enable CORS
CORS(app)

#---------------------------------------------------MONGODB_HELPER_FUNCTIONS---------------------------------------------------
# Convert image to base64
def encode_image_to_base64(image_file):
    image = Image.open(image_file).convert("RGB")
    buffered = io.BytesIO()
    image.save(buffered, format="JPEG")
    return base64.b64encode(buffered.getvalue()).decode("utf-8")

# Save request to MongoDB
def save_request_to_db(db_collection, user_id, image_b64, prediction, model_type, advice):
    request_data = {
        "user_id": user_id,
        "image": image_b64,
        "prediction": prediction,
        "model_type": model_type,
        "advice": advice,
        "timestamp": datetime.datetime.now()
    }
    db_collection.insert_one(request_data)

#---------------------------------------------------HELPER_FUNCTIONS---------------------------------------------------
ALLOWED_EXTENSIONS = {"png", "jpg", "jpeg"}
def allowed_file(filename):
    return "." in filename and filename.rsplit(".", 1)[1].lower() in ALLOWED_EXTENSIONS

#---------------------------------------------------TEST_ROUTE-----------------------------------------------------------------
@app.route("/", methods=["GET"])
def home():
    return jsonify({"message": "Welcome to the AI Farming API"})

#---------------------------------------------------PREDICTION_ROUTES----------------------------------------------------------
@app.route("/api/crop/predict", methods=["POST"])
@jwt_required
def predict_crop_disease():
    print("Received request to predict crop disease...")
    if "file" not in request.files:
        print("No file uploaded...Exiting")
        return jsonify({"error": "No file uploaded"}), 400

    file = request.files["file"]
    if not allowed_file(file.filename):
        print("Invalid file type...Exiting")
        return jsonify({"error": "Invalid file type. Only PNG, JPG, and JPEG allowed"}), 400

    user_id = request.user["id"]

    print("File uploaded...Processing")
    unique_filename = f"{uuid.uuid4().hex}_{file.filename}"
    file_path = os.path.join(UPLOAD_DIR, unique_filename)
    file.save(file_path)
    
    img = preprocess_image(file_path)

    print("Predicting crop disease...")
    with torch.no_grad():
        output = crop_model(img)
        _, predicted_class = torch.max(output, dim=1)

    print("Prediction complete")
    prediction = CLASS_LABELS[int(predicted_class.item())]

    # Get AI-generated advice
    print("Querying Gemini for advice...")
    description, cause, cure = gemini_query("plant", prediction)
    advice = {"description": description, "cause": cause, "cure": cure}

    # Convert image to base64
    print("Encoding image to base64...")
    image_b64 = encode_image_to_base64(file_path)

    # Save to MongoDB with error handling
    print("Trying to save data to MongoDB...")
    try:
        save_request_to_db(cdata, user_id, image_b64, prediction, "crop", advice)
    except Exception as e:
        print("Failed to save data...Exiting")
        return jsonify({"error": "Failed to save data", "details": str(e)}), 500
    print("Data saved to MongoDB...Exiting")
    return jsonify({"prediction": prediction, "description": description, "cause": cause, "cure": cure})

# LIVESTOCK_PREDICTION_ROUTE
@app.route("/api/livestock/predict", methods=["POST"])
@jwt_required
def predict_skin_disease():
    print("Received request to predict livestock skin disease...")
    if "file" not in request.files:
        return jsonify({"error": "No file uploaded"}), 400

    file = request.files["file"]
    if not allowed_file(file.filename):
        print("Invalid file type...Exiting")
        return jsonify({"error": "Invalid file type. Only PNG, JPG, and JPEG allowed"}), 400

    user_id = request.user["id"]

    print("File uploaded...Processing")
    unique_filename = f"{uuid.uuid4().hex}_{file.filename}"
    file_path = os.path.join(UPLOAD_DIR, unique_filename)
    file.save(file_path)

    # Get prediction
    print("Predicting livestock skin disease...")
    prediction = livestock_predict(file_path)

    # Get AI-generated advice
    print("Querying Gemini for advice...")
    disease = "lumpy skin disease" if prediction == "Infected" else "normal"
    description, cause, cure = gemini_query("livestock", disease)
    advice = {"description": description, "cause": cause, "cure": cure}

    # Convert image to base64
    print("Encoding image to base64...")
    image_b64 = encode_image_to_base64(file_path)

    # Save to MongoDB with error handlingv 
    print("Trying to save data to MongoDB...")
    try:
        save_request_to_db(ldata, user_id, image_b64, prediction, "livestock", advice)
    except Exception as e:
        return jsonify({"error": "Failed to save data", "details": str(e)}), 500

    print("Data saved to MongoDB...Exiting")
    return jsonify({"prediction": prediction, "description": description, "cause": cause, "cure": cure})

#---------------------------------------------------FETCH_HISTORY_ROUTES----------------------------------------------------------
@app.route("/api/livestock/history", methods=["GET"])
@jwt_required
def get_livestock_history():
    print("Received request to fetch livestock history...")
    print("Fetching data from MongoDB...")
    user_id = request.user["id"]
    history = list(ldata.find({"user_id": user_id}, {"_id": 0}))
    return jsonify({"history": history})

@app.route("/api/crop/history", methods=["GET"])
@jwt_required
def get_crop_history():
    print("Received request to fetch crop history...")
    print("Fetching data from MongoDB...")
    user_id = request.user["id"]
    history = list(cdata.find({"user_id": user_id}, {"_id": 0}))
    return jsonify({"history": history})
#---------------------------------------------------START SERVER----------------------------------------------------------
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000)