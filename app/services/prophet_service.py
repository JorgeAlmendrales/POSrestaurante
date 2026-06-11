import pandas as pd
from prophet import Prophet
import logging

# Silenciar logs internos de Prophet 
logging.getLogger("prophet").setLevel(logging.WARNING)
logging.getLogger("cmdstanpy").setLevel(logging.WARNING)

def predecir_ventas(df_historico: pd.DataFrame, dias_futuro: int = 30) -> list:
    """
    Recibe un DataFrame con columnas 'ds' y 'y'.
    Retorna una lista de predicciones para los próximos dias_futuro días.
    """
    modelo = Prophet(
        yearly_seasonality=True,   # Patrones anuales (ej: más ventas en diciembre)
        weekly_seasonality=True,   # Patrones semanales (ej: más ventas el fin de semana)
        daily_seasonality=False,   # No necesario si los datos son por día
        interval_width=0.95        # Intervalo de confianza del 95%
    )

    modelo.fit(df_historico)

    # Crear fechas futuras
    futuro = modelo.make_future_dataframe(periods=dias_futuro, freq='D')
    prediccion = modelo.predict(futuro)

    # Tomar solo los días futuros (no el historial)
    solo_futuro = prediccion.tail(dias_futuro)[['ds', 'yhat', 'yhat_lower', 'yhat_upper']]

    # Convertir fechas a string para que JSON las entienda
    solo_futuro = solo_futuro.copy()
    solo_futuro['ds'] = solo_futuro['ds'].dt.strftime('%Y-%m-%d')

    # Los valores negativos no tienen sentido en ventas
    solo_futuro['yhat']       = solo_futuro['yhat'].clip(lower=0).round(2)
    solo_futuro['yhat_lower'] = solo_futuro['yhat_lower'].clip(lower=0).round(2)
    solo_futuro['yhat_upper'] = solo_futuro['yhat_upper'].clip(lower=0).round(2)

    return solo_futuro.to_dict(orient='records')


def predecir_inventario(df_ventas: pd.DataFrame, stock_actual: float, dias_futuro: int = 30) -> dict:
    """
    Basado en las ventas futuras, estima cuántos días durará el stock actual.
    """
    prediccion_ventas = predecir_ventas(df_ventas, dias_futuro)

    # Promedio de consumo diario según la predicción
    consumo_diario = sum(p['yhat'] for p in prediccion_ventas) / dias_futuro

    # Cuántos días dura el stock con ese consumo
    if consumo_diario > 0:
        dias_restantes = round(stock_actual / consumo_diario, 1)
    else:
        dias_restantes = 999.0  # Sin consumo, dura indefinidamente

    return {
        "stock_actual": stock_actual,
        "consumo_diario_estimado": round(consumo_diario, 2),
        "dias_hasta_agotamiento": dias_restantes,
        "prediccion_detallada": prediccion_ventas
    }