import os
import jwt
from dotenv import load_dotenv
from functools import wraps
from flask import request, jsonify
import redis

# Load secret key
load_dotenv()

# Set up Redis
redis_client = redis.StrictRedis(host='localhost', port=6379, db=0, decode_responses=True)

# Get Secret Key
JWT_SECRET = os.getenv("JWT_SECRET")  # Fixed variable name for consistency

# JWT Token Verification
def verify_token(token):
    try:
        payload = jwt.decode(token, JWT_SECRET, algorithms=["HS256"])

        # Fix: Ensure Redis blacklisted check works correctly
        if redis_client.get(token) is not None:
            return {"error": "Token is blacklisted", "status_code": 401}

        return payload

    except jwt.ExpiredSignatureError:
        return {"error": "Token has expired", "status_code": 401}

    except jwt.InvalidTokenError:
        return {"error": "Invalid token", "status_code": 401}

    except Exception as e:
        return {"error": f"Token verification failed: {str(e)}", "status_code": 500}

# Middleware to secure endpoints
def jwt_required(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        auth_header = request.headers.get("Authorization")
        if not auth_header:
            return jsonify({"error": "Missing authentication token", "status_code": 401}), 401
        
        token = auth_header.split(" ")[1] if " " in auth_header else auth_header
        payload = verify_token(token)

        if "error" in payload:
            return jsonify(payload), payload["status_code"]

        request.user = payload  # Attach user data to request
        return f(*args, **kwargs)
    
    return decorated_function