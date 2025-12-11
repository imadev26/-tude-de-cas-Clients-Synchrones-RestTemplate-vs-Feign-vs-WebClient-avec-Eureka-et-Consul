package com.example.voiture.controller;

import com.example.voiture.model.Voiture;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cars")
public class VoitureController {

    // Base de données en mémoire pour le TP
    private static final Map<Long, Voiture> voitures = new HashMap<>();

    static {
        // Données de test
        voitures.put(1L, new Voiture(1L, "Toyota", "Yaris", 1L));
        voitures.put(2L, new Voiture(2L, "Honda", "Civic", 2L));
        voitures.put(3L, new Voiture(3L, "Ford", "Focus", 3L));
        voitures.put(4L, new Voiture(4L, "BMW", "Serie 3", 1L));
        voitures.put(5L, new Voiture(5L, "Mercedes", "Classe A", 2L));
    }

    /**
     * Endpoint principal pour récupérer une voiture par client ID
     * Simule un délai de traitement pour rendre les tests plus visibles
     */
    @GetMapping("/byClient/{clientId}")
    public Voiture getVoitureByClientId(@PathVariable Long clientId) {
        // Simuler un temps de traitement (20ms)
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Retourner la voiture correspondant au client
        return voitures.values().stream()
                .filter(v -> v.getClientId().equals(clientId))
                .findFirst()
                .orElse(new Voiture(0L, "Unknown", "N/A", clientId));
    }
}
