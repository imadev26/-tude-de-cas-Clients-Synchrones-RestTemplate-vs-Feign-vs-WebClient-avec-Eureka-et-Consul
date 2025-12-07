package com.example.client.controller;

import com.example.client.client.VoitureFeignClient;
import com.example.client.model.Voiture;
import com.example.client.service.VoitureClientRest;
import com.example.client.service.VoitureClientWeb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller exposant les 3 méthodes de communication HTTP
 * Permet de comparer RestTemplate, Feign et WebClient
 */
@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private VoitureClientRest voitureClientRest;

    @Autowired
    private VoitureFeignClient voitureFeignClient;

    @Autowired
    private VoitureClientWeb voitureClientWeb;

    /**
     * Endpoint 1: Utilise RestTemplate
     */
    @GetMapping("/{id}/car/rest")
    public Voiture getCarWithRestTemplate(@PathVariable Long id) {
        return voitureClientRest.getVoitureByClientId(id);
    }

    /**
     * Endpoint 2: Utilise Feign Client
     */
    @GetMapping("/{id}/car/feign")
    public Voiture getCarWithFeign(@PathVariable Long id) {
        return voitureFeignClient.getVoitureByClientId(id);
    }

    /**
     * Endpoint 3: Utilise WebClient
     */
    @GetMapping("/{id}/car/webclient")
    public Voiture getCarWithWebClient(@PathVariable Long id) {
        return voitureClientWeb.getVoitureByClientId(id);
    }
}
