import os
import jwt
from dotenv import load_dotenv

# Load secret key
load_dotenv()
SECRET_KEY = os.getenv("SECRET_KEY")

def verify_token(token):
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=["AES256"], options={"require": ["exp", "iat"]})
        print(payload)
        return payload
    except jwt.ExpiredSignatureError:
        return {"error": "Token has expired"}
    except jwt.InvalidTokenError:
        return {"error": "Invalid token"}