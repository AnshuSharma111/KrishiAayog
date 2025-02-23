import torch
import torchvision.models as models
import albumentations as A
from albumentations.pytorch import ToTensorV2
from torchvision.models import EfficientNet_B0_Weights
import cv2
import os
import numpy as np

# Load EfficientNetB0 model
def load_model(model_path="ML Server\Models\lumpy-skin-model.pth"):
    model = models.efficientnet_b0(weights=EfficientNet_B0_Weights.DEFAULT)
    num_features = model.classifier[1].in_features
    model.classifier[1] = torch.nn.Linear(num_features, 1)
    
    model.load_state_dict(torch.load(model_path, map_location=torch.device("cpu")))
    model.eval()  # Set to evaluation mode
    return model

MODEL_PATH = os.path.join("ML Server", "Models", "lumpy-skin-model.pth")
model = load_model(MODEL_PATH)

# Define image preprocessing
transform = A.Compose([
    A.Resize(224, 224),
    A.Normalize(mean=(0.485, 0.456, 0.406), std=(0.229, 0.224, 0.225)),
    ToTensorV2()
])

# Prediction function
def predict(image_path):
    image = cv2.imread(image_path)
    if image is None:
        raise ValueError("Error: Unable to load image")

    image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
    transformed = transform(image=image)["image"]
    transformed = transformed.unsqueeze(0)  # Add batch dimension

    with torch.no_grad():
        output = model(transformed)
        prediction = torch.sigmoid(output).item()  # Convert to probability

    return "Infected" if prediction > 0.5 else "Normal"