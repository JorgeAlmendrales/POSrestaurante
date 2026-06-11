from fastapi import FastAPI
from app.routes import ventas, inventario
from app.routes.recomendacion import router as recomendacion_router

app = FastAPI(
    title="IA Service — POS Restaurante",
    description="Microservicio de predicción de ventas e inventario con Prophet",
    version="1.0.0"
)

# Registrar las rutas
app.include_router(ventas.router,    prefix="/api/ia", tags=["Ventas"])
app.include_router(inventario.router, prefix="/api/ia", tags=["Inventario"])
app.include_router(recomendacion_router, prefix= "/api")
@app.get("/")
def health_check():
    return {"status": "ok", "mensaje": "IA Service corriendo correctamente"}