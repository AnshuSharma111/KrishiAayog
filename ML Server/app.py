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
from dotenv import load_dotenv
from auth import verify_token

# Initialize Flask app
app = Flask(__name__)

# Load environment variables
load_dotenv()

# Set up MongoDB connection
client = MongoClient(os.getenv("MONGO_URI"))
db = client[os.getenv("DB")]
cdata = db["CROP_COL"]
ldata = db["LSTOCK_COL"]

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
crop_model = load_model("Models/plant-disease-model.pth")
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
        "timestamp": datetime.datetime()
    }
    db_collection.insert_one(request_data)

#---------------------------------------------------PREDICTION_ROUTES----------------------------------------------------------
@app.route("/api/crop/predict", methods=["POST"])
def predict_crop_disease():
    # Get token from request headers
    auth_header = request.headers.get("Authorization")
    if not auth_header:
        return jsonify({"error": "Missing authentication token"}), 401

    # Extract token (Authorization: Bearer <token>)
    token = auth_header.split(" ")[1] if " " in auth_header else auth_header
    user_data = verify_token(token)

    if "error" in user_data:
        return jsonify(user_data), 401  # Return error if token is invalid
    if "file" not in request.files or "user_id" not in request.form:
        return jsonify({"error": "File and user_id are required"}), 400

    file = request.files["file"]
    user_id = request.form["user_id"]

    file_path = os.path.join(UPLOAD_DIR, file.filename)
    file.save(file_path)
    img = preprocess_image(file_path)

    with torch.no_grad():
        output = crop_model(img)
        _, predicted_class = torch.max(output, dim=1)

    prediction = CLASS_LABELS[int(predicted_class.item())]

    # Get advice (description, cause, cure) using Gemini
    description, cause, cure = gemini_query("plant", prediction)
    advice = {"description": description, "cause": cause, "cure": cure}

    # Convert image to base64
    image_b64 = encode_image_to_base64(file_path)

    # Save to MongoDB
    save_request_to_db(cdata, user_id, image_b64, prediction, "crop", advice)

    return jsonify({"prediction": prediction, "advice": advice})

@app.route("/api/livestock/predict", methods=["POST"])
def predict_skin_disease():
    if "file" not in request.files or "user_id" not in request.form:
        return jsonify({"error": "File and user_id are required"}), 400

    file = request.files["file"]
    user_id = request.form["user_id"]

    file_path = os.path.join(UPLOAD_DIR, file.filename)
    file.save(file_path)

    # Get prediction
    prediction = livestock_predict(file_path)

    # Get advice (description, cause, cure) using Gemini
    disease = "lumpy skin disease" if prediction == "Infected" else "normal"
    description, cause, cure = gemini_query("livestock", disease)
    advice = {"description": description, "cause": cause, "cure": cure}

    # Convert image to base64
    image_b64 = encode_image_to_base64(file_path)

    # Save to MongoDB
    save_request_to_db(ldata, user_id, image_b64, prediction, "livestock", advice)

    return jsonify({"prediction": prediction, "advice": advice})

#---------------------------------------------------FETCH_HISTORY_ROUTES----------------------------------------------------------
@app.route("/api/livestock/history", methods=["GET"])
def get_livestock_history():
    # Get token from request headers
    auth_header = request.headers.get("Authorization")
    if not auth_header:
        return jsonify({"error": "Missing authentication token"}), 401
    
    # Extract token (Authorization: Bearer <token>)
    token = auth_header.split(" ")[1] if " " in auth_header else auth_header
    user_data = verify_token(token)

    if "error" in user_data:
        return jsonify(user_data), 401  # Return error if token is invalid
    user_id = request.args.get("user_id")
    if not user_id:
        return jsonify({"error": "user_id is required"}), 400

    history = list(ldata.find({"user_id": user_id}, {"_id": 0}))
    return jsonify({"history": history})

@app.route("/api/crop/history", methods=["GET"])
def get_crop_history():
    user_id = request.args.get("user_id")
    if not user_id:
        return jsonify({"error": "user_id is required"}), 400

    history = list(cdata.find({"user_id": user_id}, {"_id": 0}))
    return jsonify({"history": history})

#---------------------------------------------------START SERVER----------------------------------------------------------
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000, debug=True)