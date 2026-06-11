from fastapi import APIRouter
from app.services.db_service import obtener_historial_ventas
from app.services.prophet_service import predecir_ventas

router = APIRouter()

@router.get("/recomendacion/{producto_id}")
def recomendacion(producto_id: str):

    df = obtener_historial_ventas(producto_id)

    if len(df) < 2:
        return {
            "demanda": 0,
            "mensaje": "Sin datos suficientes"
        }

    predicciones = predecir_ventas(df, dias_futuro=7)

    demanda_total = sum(p["yhat"] for p in predicciones)

    return {
        "demanda": round(demanda_total),
        "mensaje": f"Demanda estimada de {round(demanda_total)} unidades para los próximos 7 días"
    }