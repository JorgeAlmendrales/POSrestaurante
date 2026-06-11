package com.restaurant.pos_restaurante.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;
import java.util.HashMap;

@Service
public class IAService {

    private final RestTemplate restTemplate;

    @Value("${ia.service.url}")
    private String iaServiceUrl;

    public IAService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Predice las ventas de un producto para los próximos N días.
     * @param productoId UUID del producto
     * @param dias cantidad de días a predecir (default 30)
     */
    public Map<String, Object> predecirVentas(String productoId, int dias) {
        try {
            String url = iaServiceUrl + "/api/ia/prediccion/ventas/" + productoId + "?dias=" + dias;
            return restTemplate.getForObject(url, Map.class);
        } catch (ResourceAccessException e) {
            return errorRespuesta("El servicio de IA no está disponible. Verifica que esté corriendo en el puerto 8000.");
        } catch (HttpStatusCodeException e) {
            return errorRespuesta("Error al predecir ventas: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            return errorRespuesta("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Predice cuántos días durará el stock de un insumo.
     * @param insumoId UUID del insumo
     */
    public Map<String, Object> predecirInventario(String insumoId) {
        try {
            String url = iaServiceUrl + "/api/ia/prediccion/inventario/" + insumoId;
            return restTemplate.getForObject(url, Map.class);
        } catch (ResourceAccessException e) {
            return errorRespuesta("El servicio de IA no está disponible. Verifica que esté corriendo en el puerto 8000.");
        } catch (HttpStatusCodeException e) {
            return errorRespuesta("Error al predecir inventario: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            return errorRespuesta("Error inesperado: " + e.getMessage());
        }
    }

    public Map<String, Object> obtenerRecomendacionProducto(String productoId) {
    try {

        String url =
            iaServiceUrl + "/api/recomendacion/" + productoId;

        return restTemplate.getForObject(url, Map.class);

    } catch (ResourceAccessException e) {

        return errorRespuesta(
            "El servicio de IA no está disponible."
        );

    } catch (HttpStatusCodeException e) {

        return errorRespuesta(
            "Error al obtener recomendación IA: "
                + e.getResponseBodyAsString()
        );

    } catch (Exception e) {

        return errorRespuesta(
            "Error inesperado: " + e.getMessage()
        );
    }
}

    /**
     * Lista todos los productos de un restaurante.
     */
    public Map<String, Object> listarProductosIA(String restauranteId) {
        try {
            String url = iaServiceUrl + "/api/ia/productos/" + restauranteId;
            return restTemplate.getForObject(url, Map.class);
        } catch (ResourceAccessException e) {
            return errorRespuesta("El servicio de IA no está disponible.");
        } catch (HttpStatusCodeException e) {
            return errorRespuesta("Error al listar productos: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            return errorRespuesta("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Lista todos los insumos de un restaurante con su stock.
     */
    public Map<String, Object> listarInsumosIA(String restauranteId) {
        try {
            String url = iaServiceUrl + "/api/ia/insumos/" + restauranteId;
            return restTemplate.getForObject(url, Map.class);
        } catch (ResourceAccessException e) {
            return errorRespuesta("El servicio de IA no está disponible.");
        } catch (HttpStatusCodeException e) {
            return errorRespuesta("Error al listar insumos: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            return errorRespuesta("Error inesperado: " + e.getMessage());
        }
    }

    private Map<String, Object> errorRespuesta(String mensaje) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", mensaje);
        return error;
    }
}