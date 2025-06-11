from fastapi import FastAPI
import json

app = FastAPI()

with open("numeros_spam.json", "r") as f:
    datos = json.load(f)
    numeros_spam = set(datos.get("numeros", []))

@app.get("/es_spam/{numero}")
def es_spam(numero: str):
    return {"es_spam": numero in numeros_spam}