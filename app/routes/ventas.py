from fastapi import APIRouter, HTTPException
from app.services.prophet_service import predecir_ventas
from app.services.db_service import obtener_historial_ventas, obtener_todos_productos

router = APIRouter()

@router.get("/prediccion/ventas/{producto_id}")
def prediccion_ventas(producto_id: str, dias: int = 30):
    """
    Predice ventas de un producto para los próximos N días.
    - producto_id: UUID del producto
    - dias: días a predecir (default 30)
    """
    df = obtener_historial_ventas(producto_id)

    if len(df) < 15:

        promedio = df["y"].mean()

        resultado = []

        from datetime import datetime, timedelta

        fecha = datetime.now()

        for i in range(dias):
            resultado.append({
            "ds": (fecha + timedelta(days=i)).strftime("%Y-%m-%d"),
            "yhat": round(promedio, 2),
            "yhat_lower": round(promedio * 0.8, 2),
            "yhat_upper": round(promedio * 1.2, 2)
        })

    else:
        resultado = predecir_ventas(df, dias_futuro=dias)

    return {
        "producto_id": producto_id,
        "dias_predichos": dias,
        "predicciones": resultado
    }


@router.get("/productos/{restaurante_id}")
def listar_productos(restaurante_id: str):
    """
    Lista todos los productos de un restaurante.
    Útil para saber qué producto_id usar.
    """
    productos = obtener_todos_productos(restaurante_id)
    return {"restaurante_id": restaurante_id, "productos": productos}