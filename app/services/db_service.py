from dotenv import load_dotenv
load_dotenv()

import pandas as pd
from sqlalchemy import create_engine, text
import os


# =========================
# CONFIGURACIÓN DB
# =========================

DB_USER     = os.getenv("DB_USER", "postgres")
DB_PASSWORD = os.getenv("DB_PASSWORD", "jorge12345")
DB_HOST     = os.getenv("DB_HOST", "localhost")
DB_PORT     = os.getenv("DB_PORT", "5432")
DB_NAME     = os.getenv("DB_NAME", "pos_restaurante")

DATABASE_URL = (
    f"postgresql://{DB_USER}:{DB_PASSWORD}"
    f"@{DB_HOST}:{DB_PORT}/{DB_NAME}"
)

engine = create_engine(DATABASE_URL)


# =========================
# HISTORIAL DE VENTAS
# =========================

def obtener_historial_ventas(producto_id: str) -> pd.DataFrame:
    """
    Une pedido_items con pedidos para obtener:
    - ds: fecha del pedido (agrupada por día)
    - y: cantidad total vendida de ese producto ese día

    Nota: los IDs son UUID (texto) no enteros.
    """

    query = text("""
        SELECT 
            DATE(p.created_at) AS ds,
            SUM(pi.cantidad) AS y
        FROM pedido_items pi
        JOIN pedidos p 
            ON pi.pedido_id = p.id
        WHERE pi.producto_id = :producto_id
          AND p.estado = 'ENTREGADO'
        GROUP BY DATE(p.created_at)
        ORDER BY ds ASC
    """)

    with engine.connect() as conn:
        df = pd.read_sql(
            query,
            conn,
            params={"producto_id": producto_id}
        )

    if not df.empty:
        df["ds"] = pd.to_datetime(df["ds"])
        df["y"] = df["y"].astype(float)

    return df


# =========================
# STOCK ACTUAL
# =========================

def obtener_stock_actual(insumo_id: str) -> float:
    """
    Obtiene el stock_actual de la tabla insumos.
    """

    query = text("""
        SELECT stock_actual
        FROM insumos
        WHERE id = :insumo_id
    """)

    with engine.connect() as conn:
        resultado = conn.execute(
            query,
            {"insumo_id": insumo_id}
        ).fetchone()

    return float(resultado[0]) if resultado else 0.0


# =========================
# PRODUCTOS DEL RESTAURANTE
# =========================

def obtener_todos_productos(restaurante_id: str) -> list:
    """
    Lista todos los productos de un restaurante.
    """

    query = text("""
        SELECT id, nombre, precio
        FROM productos
        WHERE restaurante_id = :restaurante_id
          AND disponible = true
    """)

    with engine.connect() as conn:
        resultado = conn.execute(
            query,
            {"restaurante_id": restaurante_id}
        ).fetchall()

    return [
        {
            "id": str(r[0]),
            "nombre": r[1],
            "precio": float(r[2])
        }
        for r in resultado
    ]


# =========================
# INSUMOS DEL RESTAURANTE
# =========================

def obtener_todos_insumos(restaurante_id: str) -> list:
    """
    Lista todos los insumos de un restaurante
    con su stock actual.
    """

    query = text("""
        SELECT 
            id,
            nombre,
            stock_actual,
            stock_minimo,
            stock_critico,
            unidad
        FROM insumos
        WHERE restaurante_id = :restaurante_id
    """)

    with engine.connect() as conn:
        resultado = conn.execute(
            query,
            {"restaurante_id": restaurante_id}
        ).fetchall()

    return [
        {
            "id": str(r[0]),
            "nombre": r[1],
            "stock_actual": float(r[2]),
            "stock_minimo": float(r[3]),
            "stock_critico": float(r[4]),
            "unidad": r[5]
        }
        for r in resultado
    ]


# =========================
# RECETA / INGREDIENTES
# =========================

def obtener_receta_insumo(insumo_id: str) -> list:
    """
    Obtiene todos los productos
    que usan este insumo y cuánto usan.
    """

    query = text("""
        SELECT 
            ri.producto_id,
            ri.cantidad AS cantidad_por_unidad,
            ri.unidad,
            p.nombre AS producto_nombre
        FROM receta_ingredientes ri
        JOIN productos p
            ON ri.producto_id = p.id
        WHERE ri.insumo_id = :insumo_id
    """)

    with engine.connect() as conn:
        resultado = conn.execute(
            query,
            {"insumo_id": insumo_id}
        ).fetchall()

    return [
        {
            "producto_id": str(r[0]),
            "cantidad_por_unidad": float(r[1]),
            "unidad": r[2],
            "producto_nombre": r[3]
        }
        for r in resultado
    ]