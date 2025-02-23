import google.generativeai as genai
import os
from dotenv import load_dotenv

load_dotenv()

API_KEY = os.getenv('API_KEY')
genai.configure(api_key=API_KEY)
llm = genai.GenerativeModel("gemini-1.5-flash")

msg = llm.generate_content("A cow has got the disease Lumpy Skin Disease. What is this disease and what are some cures to it?")

print(msg.text)