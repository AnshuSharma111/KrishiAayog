import requests

# API Endpoints
CROP_API_URL = "http://localhost:8000/api/crop/predict"
LIVESTOCK_API_URL = "http://localhost:8000/api/livestock/predict"

def predict(api_url, image_path):
    """Send an image to the API and get the prediction."""
    with open(image_path, "rb") as img:
        files = {"file": img}
        data = {"user_id": "test_user"}
        response = requests.post(api_url, files=files, data=data)

    if response.status_code == 200:
        print(f"✅ Prediction: {response.json()['prediction']}")
    else:
        print(f"❌ Error: {response.json()}")

if __name__ == "__main__":
    print("Select API:")
    print("1 - Crop Disease Detection")
    print("2 - Livestock Disease Detection")
    
    choice = input("Enter choice (1 or 2): ").strip()
    image_path = input("Enter image file path: ").strip()

    if choice == "1":
        predict(CROP_API_URL, image_path)
    elif choice == "2":
        predict(LIVESTOCK_API_URL, image_path)
    else:
        print("❌ Invalid choice!")
