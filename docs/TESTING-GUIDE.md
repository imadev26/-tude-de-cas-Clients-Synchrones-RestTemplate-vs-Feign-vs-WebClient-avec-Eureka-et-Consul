# Guide de Tests de Performance - TP24

## 📊 Tests JMeter

### Configuration JMeter

1. **Créer un Thread Group**
   - Nombre de threads : 10, 50, 100, 200, 500
   - Ramp-Up Period : 10 secondes
   - Loop Count : 10

2. **Ajouter HTTP Request Sampler**

**RestTemplate:**
```
Server: localhost
Port: 8081
Path: /api/clients/1/car/rest
Method: GET
```

**Feign:**
```
Server: localhost
Port: 8081
Path: /api/clients/1/car/feign
Method: GET
```

**WebClient:**
```
Server: localhost
Port: 8081
Path: /api/clients/1/car/webclient
Method GET
```

3. **Ajouter Listeners**
   - Summary Report
   - Aggregate Report
   - View Results Tree

---

## 📋 Tableaux de Résultats

### Tableau 1: Performance avec Eureka

| Méthode | Charge (threads) | Temps Moyen (ms) | P95 (ms) | Débit (req/s) | Erreurs (%) |
|---------|------------------|------------------|----------|---------------|-------------|
| **RestTemplate** | 10 | | | | |
| | 50 | | | | |
| | 100 | | | | |
| | 200 | | | | |
| | 500 | | | | |
| **Feign** | 10 | | | | |
| | 50 | | | | |
| | 100 | | | | |
| | 200 | | | | |
| | 500 | | | | |
| **WebClient** | 10 | | | | |
| | 50 | | | | |
| | 100 | | | | |
| | 200 | | | | |
| | 500 | | | | |

### Tableau 2: Performance avec Consul

| Méthode | Charge (threads) | Temps Moyen (ms) | P95 (ms) | Débit (req/s) | Erreurs (%) |
|---------|------------------|------------------|----------|---------------|-------------|
| **RestTemplate** | 100 | | | | |
| **Feign** | 100 | | | | |
| **WebClient** | 100 | | | | |

### Tableau 3: Consommation Ressources

| Méthode | CPU (%) | RAM (MB) | Threads actifs |
|---------|---------|----------|----------------|
| **RestTemplate** | | | |
| **Feign** | | | |
| **WebClient** | | | |

### Tableau 4: Tests de Résilience

| Scénario | RestTemplate | Feign | WebClient |
|----------|--------------|-------|-----------|
| **Panne Service Voiture** | | | |
| - Taux d'échec (%) | | | |
| - Temps de reprise (s) | | | |
| **Panne Discovery** | | | |
| - Comportement | | | |
| - Cache actif? | | | |
| **Redémarrage Service** | | | |
| - Temps re-registration (s) | | | |

---

## 🎯 Métriques à Collecter

### Avec JMeter
- ✅ Temps de réponse moyen
- ✅ Temps de réponse médian
- ✅ P90, P95, P99
- ✅ Débit (Throughput)
- ✅ Taux d'erreur
- ✅ Min/Max response time

### Avec Task Manager / htop
- ✅ CPU % du processus Java
- ✅ Mémoire utilisée (MB)
- ✅ Nombre de threads

### Avec Actuator
- ✅ `/actuator/metrics/jvm.memory.used`
- ✅ `/actuator/metrics/jvm.threads.live`
- ✅ `/actuator/health`

---

## 🔥 Scénarios de Tests

### Test 1: Charge Progressive
```
10 threads → 50 → 100 → 200 → 500
```

### Test 2: Panne Service Voiture
```
1. Démarrer test 100 threads
2. À 30s: arrêter Service Voiture
3. À 45s: redémarrer Service Voiture
4. Observer récupération
```

### Test 3: Panne Discovery
```
1. Services enregistrés
2. Arrêter Consul/Eureka
3. Tester appels (cache local?)
4. Redémarrer Discovery
```

---

## 📝 Template Analyse

### Section 1: Méthodologie
- Environnement de test (machine, OS, Java version)
- Configuration des services
- Charge appliquée
- Outils utilisés

### Section 2: Résultats Performance
- Présenter les tableaux
- Graphiques (optionnel)
- Observations

### Section 3: Consommation Ressources
- CPU et RAM par méthode
- Impact sur les performances

### Section 4: Résilience
- Comportement lors des pannes
- Temps de récupération
- Recommandations

### Section 5: Conclusion
- Meilleure méthode selon critère (latence/simplicité/résilience)
- Recommandations pour production
- Limites de l'étude

---

**Bon courage pour vos tests!** 🚀
