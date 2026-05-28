package com.restaurant.pos_restaurante.service.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProphetAdapter implements IAPrediccionPort {

    private final RestTemplate restTemplate;

    @Value("${ia.prophet.url:http://localhost:8000}")
    private String prophetUrl;

    @Override
    public ProphetResponse predecir(List<Map<String, Object>> datosVentas) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("datos", datosVentas);

            ProphetResponse response = restTemplate.postForObject(
                prophetUrl + "/predecir",
                request,
                ProphetResponse.class
            );
            return response;
        } catch (Exception e) {
            ProphetResponse fallback = new ProphetResponse();
            fallback.setRecomendacion("Microservicio IA no disponible temporalmente");
            fallback.setVariacionPorcentaje(0);
            return fallback;
        }
    }
}
