package com.example.client.client;

import com.example.client.model.Voiture;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client pour appeler le Service Voiture
 * L'approche déclarative réduit fortement le code
 */
@FeignClient(name = "SERVICE-VOITURE")
public interface VoitureFeignClient {

    @GetMapping("/api/cars/byClient/{clientId}")
    Voiture getVoitureByClientId(@PathVariable Long clientId);
}
