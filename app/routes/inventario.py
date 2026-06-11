from fastapi import APIRouter, HTTPException
from app.services.prophet_service import predecir_ventas
from app.services.db_service import (
    obtener_historial_ventas,
    obtener_stock_actual,
    obtener_todos_insumos,
    obtener_receta_insumo
)

router = APIRouter()

@router.get("/prediccion/inventario/{insumo_id}")
def prediccion_inventario(insumo_id: str, dias: int = 30):
    """
    Predice cuándo se agotará un insumo basándose en:
    1. Los productos que lo usan (receta_ingredientes)
    2. Las ventas futuras de esos productos (Prophet)
    3. El stock actual del insumo
    """

    # 1. Verificar que el insumo existe y obtener su stock
    stock = obtener_stock_actual(insumo_id)
    if stock == 0.0:
        raise HTTPException(
            status_code=404,
            detail=f"No se encontró el insumo con id {insumo_id}"
        )

    # 2. Obtener qué productos usan este insumo y cuánto
    receta = obtener_receta_insumo(insumo_id)
    if not receta:
        raise HTTPException(
            status_code=400,
            detail=f"El insumo {insumo_id} no tiene recetas registradas."
        )

    # 3. Para cada producto, predecir sus ventas y calcular consumo del insumo
    consumo_diario_total = 0.0
    detalle_productos = []

    for item in receta:
        producto_id = item["producto_id"]
        cantidad_por_unidad = item["cantidad_por_unidad"]

        df = obtener_historial_ventas(producto_id)

        if len(df) < 2:
            # Sin historial suficiente, omitir este producto
            continue

        # Si hay poco historial, usar promedio simple
        if len(df) < 15:

            promedio = float(df["y"].mean())

            predicciones = []

            from datetime import datetime, timedelta

            fecha = datetime.now()

            for i in range(dias):
                predicciones.append({
                    "ds": (fecha + timedelta(days=i)).strftime("%Y-%m-%d"),
                    "yhat": round(promedio, 2),
                    "yhat_lower": round(promedio * 0.8, 2),
                    "yhat_upper": round(promedio * 1.2, 2)
                })

        else:
            predicciones = predecir_ventas(df, dias_futuro=dias)

        # Promedio de ventas diarias predichas
        ventas_diarias_promedio = sum(p["yhat"] for p in predicciones) / dias

        # Consumo diario de este insumo por este producto
        consumo_por_producto = ventas_diarias_promedio * cantidad_por_unidad
        consumo_diario_total += consumo_por_producto

        print("Producto:", producto_id)
        print("Ventas promedio:", ventas_diarias_promedio)
        print("Cantidad receta:", cantidad_por_unidad)
        print("Consumo:", consumo_por_producto)

        detalle_productos.append({
            "producto_id": producto_id,
            "producto_nombre": item["producto_nombre"],
            "ventas_diarias_estimadas": round(ventas_diarias_promedio, 2),
            "cantidad_insumo_por_unidad": cantidad_por_unidad,
            "unidad": item["unidad"],
            "consumo_diario_estimado": round(consumo_por_producto, 3)
        })

    if consumo_diario_total == 0:
        raise HTTPException(
            status_code=400,
            detail="No hay suficiente historial de ventas para calcular el consumo."
        )

    # 4. Calcular días hasta agotamiento
    dias_hasta_agotamiento = round(stock / consumo_diario_total, 1)

    return {
    "insumo_id": insumo_id,
    "stock_actual": stock,
    "unidad": receta[0]["unidad"],

    "stock_actual_formateado":
        f"{stock} {receta[0]['unidad']}",

    "consumo_formateado":
        f"{round(consumo_diario_total, 2)} {receta[0]['unidad']}/día",

    "consumo_diario_estimado_total":
        round(consumo_diario_total, 3),

    "dias_hasta_agotamiento":
        dias_hasta_agotamiento,

    "alerta":
        dias_hasta_agotamiento < 7,

    "detalle_por_producto":
        detalle_productos
}


@router.get("/insumos/{restaurante_id}")
def listar_insumos(restaurante_id: str):
    """
    Lista todos los insumos de un restaurante con su stock.
    """
    insumos = obtener_todos_insumos(restaurante_id)
    return {"restaurante_id": restaurante_id, "insumos": insumos}