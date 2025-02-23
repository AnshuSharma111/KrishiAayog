import torch
import torch.nn as nn
import torchvision.transforms as transforms
from PIL import Image

# Define class labels
CLASS_LABELS = [
    "Grape - Esca (Black Measles)", "Tomato - Leaf Mold", "Peach - Healthy", 
    "Raspberry - Healthy", "Tomato - Late Blight", "Apple - Apple Scab",
    "Potato - Late Blight", "Tomato - Early Blight", "Strawberry - Leaf Scorch", 
    "Pepper (Bell) - Healthy", "Grape - Leaf Blight (Isariopsis Leaf Spot)", 
    "Grape - Healthy", "Tomato - Spider Mites (Two-Spotted Spider Mite)", 
    "Strawberry - Healthy", "Corn (Maize) - Northern Leaf Blight", 
    "Corn (Maize) - Cercospora Leaf Spot (Gray Leaf Spot)", 
    "Cherry - Healthy", "Tomato - Tomato Mosaic Virus", "Corn (Maize) - Common Rust", 
    "Peach - Bacterial Spot", "Apple - Black Rot", "Tomato - Septoria Leaf Spot", 
    "Pepper (Bell) - Bacterial Spot", "Grape - Black Rot", "Tomato - Target Spot", 
    "Tomato - Yellow Leaf Curl Virus", "Blueberry - Healthy", "Apple - Healthy", 
    "Potato - Early Blight", "Tomato - Healthy", "Squash - Powdery Mildew", 
    "Soybean - Healthy", "Tomato - Bacterial Spot", "Potato - Healthy", 
    "Apple - Cedar Apple Rust", "Orange - Huanglongbing (Citrus Greening)", 
    "Cherry - Powdery Mildew", "Corn (Maize) - Healthy"
]

# Function-based ConvBlock (Same as training)
def ConvBlock(in_channels, out_channels, pool=False):
    layers = [
        nn.Conv2d(in_channels, out_channels, kernel_size=3, padding=1),
        nn.BatchNorm2d(out_channels),
        nn.ReLU(inplace=True)
    ]
    if pool:
        layers.append(nn.MaxPool2d(4))
    return nn.Sequential(*layers)

class ResNet9(nn.Module):
    def __init__(self, in_channels, num_diseases):
        super().__init__()

        self.conv1 = ConvBlock(in_channels, 64)
        self.conv2 = ConvBlock(64, 128, pool=True)
        self.res1 = nn.Sequential(ConvBlock(128, 128), ConvBlock(128, 128))

        self.conv3 = ConvBlock(128, 256, pool=True)
        self.conv4 = ConvBlock(256, 512, pool=True)
        self.res2 = nn.Sequential(ConvBlock(512, 512), ConvBlock(512, 512))

        self.classifier = nn.Sequential(
            nn.MaxPool2d(4),
            nn.Flatten(),
            nn.Linear(512, num_diseases)
        )

    def forward(self, xb):
        out = self.conv1(xb)
        out = self.conv2(out)
        out = self.res1(out) + out
        out = self.conv3(out)
        out = self.conv4(out)
        out = self.res2(out) + out
        out = self.classifier(out)
        return out

# Function to load the trained model
def load_model(model_path, num_classes=38):
    model = ResNet9(3, num_classes)
    model.load_state_dict(torch.load(model_path, map_location=torch.device("cpu")))
    model.eval()
    return model

transform = transforms.Compose([
    transforms.Resize((256, 256)),
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225]),
])

def preprocess_image(image_path):
    img = Image.open(image_path).convert("RGB")
    img = transform(img).unsqueeze(0)  # Convert to tensor & add batch dimension
    return img