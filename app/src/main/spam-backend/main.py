from fastapi import FastAPI
from database import collection

app = FastAPI()

@app.get("/usuarios")
def obtener_usuarios():
    return list(collection.find({}, {"_id": 0}))
