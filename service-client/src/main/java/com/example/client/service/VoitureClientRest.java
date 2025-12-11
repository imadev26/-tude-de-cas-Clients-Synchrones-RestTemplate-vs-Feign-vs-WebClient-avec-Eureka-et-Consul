package com.example.client.service;

import com.example.client.model.Voiture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service utilisant RestTemplate (approche classique)
 * Client HTTP synchrone traditionnel
 */
@Service
public class VoitureClientRest {

    @Autowired
    private RestTemplate restTemplate;

    public Voiture getVoitureByClientId(Long clientId) {
        String url = "http://SERVICE-VOITURE/api/cars/byClient/" + clientId;
        return restTemplate.getForObject(url, Voiture.class);
    }
}
