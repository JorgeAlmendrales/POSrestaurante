from pydantic import BaseModel
from typing import List, Optional
from datetime import date

class PrediccionPunto(BaseModel):
    ds: str
    yhat: float
    yhat_lower: float
    yhat_upper: float

class RespuestaPrediccionVentas(BaseModel):
    producto_id: int
    dias_predichos: int
    predicciones: List[PrediccionPunto]

class RespuestaPrediccionInventario(BaseModel):
    insumo_id: int
    stock_actual: float
    consumo_diario_estimado: float
    dias_hasta_agotamiento: float
    prediccion_detallada: List[PrediccionPunto]

class RespuestaError(BaseModel):
    error: str
    detalle: Optional[str] = None