package com.example.client.service;

import com.example.client.model.Voiture;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Service utilisant WebClient (approche réactive utilisée en mode synchrone)
 * Client HTTP moderne avec support réactif
 */
@Service
public class VoitureClientWeb {

    private final WebClient webClient;

    public VoitureClientWeb(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Voiture getVoitureByClientId(Long clientId) {
        return webClient.get()
                .uri("http://SERVICE-VOITURE/api/cars/byClient/" + clientId)
                .retrieve()
                .bodyToMono(Voiture.class)
                .block(); // block() pour mode synchrone dans ce TP
    }
}
