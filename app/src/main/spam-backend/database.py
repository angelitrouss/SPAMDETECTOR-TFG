from pymongo import MongoClient

# String de conexión con credenciales reales
client = MongoClient("mongodb+srv://root:root@cluster0.sf8nbbt.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0")

# Selección de base de datos y colección
db = client["detector_llamadas"]
collection = db["usuarios"]
