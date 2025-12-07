# TP 24 : Clients Synchrones (RestTemplate vs Feign vs WebClient)

## 🎯 Objectifs pédagogiques

À la fin du lab, vous serez capable de :

- ✅ Implémenter deux microservices communiquant synchroniquement
- ✅ Configurer la découverte de services avec **Eureka** et **Consul**
- ✅ Implémenter **3 clients HTTP** : RestTemplate, Feign, WebClient
- ✅ Réaliser des tests de performance (latence / débit) et collecter des métriques
- ✅ Tester la résilience (panne service, panne discovery) et analyser les résultats

## 🔧 Prérequis

- Java 17+ (ou 11+)
- Maven
- Un IDE (IntelliJ/Eclipse)
- Postman ou curl
- **JMeter** (recommandé) pour tests de charge
- (Optionnel) Docker + Docker Compose
- (Optionnel) Prometheus + Grafana pour métriques
- Eureka OU Consul

## 🏗️ Architecture cible

### Services à créer

1. **Service Voiture** - Expose l'API des voitures
2. **Service Client** - Consomme l'API Voiture avec 3 techniques
3. **Discovery** - Eureka OU Consul

### Flux principal

```
Service Client → (RestTemplate / Feign / WebClient) → Service Voiture
                ↓
            Eureka/Consul
```

---

## 📦 Partie A — Mise en place des microservices

### Étape A1 — Créer le Service Voiture

#### A1.1 Création du projet

**Dépendances Maven :**
- Spring Web
- Spring Boot Actuator
- Eureka Discovery Client (ou Consul Discovery)

#### A1.2 API minimale à exposer

**Endpoint :** `GET /api/cars/byClient/{clientId}`

**Réponse JSON :**
```json
{
  "id": 10,
  "marque": "Toyota",
  "modele": "Yaris",
  "clientId": 1
}
```

> [!NOTE]
> Base de données non obligatoire ! Une API "en mémoire" évite l'impact MySQL sur la latence.

#### A1.3 Simuler un temps de traitement (optionnel)

```java
// Ajouter 20ms de délai pour rendre les différences visibles
Thread.sleep(20);
```

> [!TIP]
> Ce délai rend les différences entre clients HTTP plus observables.

### ✅ Test A1

```bash
# Lancer Service Voiture
mvn spring-boot:run

# Tester
curl http://localhost:8082/api/cars/byClient/1
```

**Résultat attendu :** Un JSON correct

---

### Étape A2 — Créer le Service Client

#### A2.1 Création du projet

**Dépendances Maven :**
- Spring Web
- Spring Boot Actuator
- Eureka Discovery Client (ou Consul Discovery)
- **OpenFeign** (pour Feign)
- **Spring WebFlux** (pour WebClient)

#### A2.2 Endpoints de test

Créer 3 endpoints (un par méthode) :

```
GET /api/clients/{id}/car/rest       → RestTemplate
GET /api/clients/{id}/car/feign      → Feign
GET /api/clients/{id}/car/webclient  → WebClient
```

### ✅ Test A2

Tester chaque endpoint et vérifier que le JSON revient bien.

---

## 🔌 Partie B — Implémentation des 3 clients synchrones

### Étape B1 — RestTemplate (synchrone classique)

#### B1.1 Créer le bean RestTemplate

```java
@Configuration
public class RestTemplateConfig {
    
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

#### B1.2 Créer la méthode d'appel

```java
@Service
public class VoitureClientRest {
    
    @Autowired
    private RestTemplate restTemplate;
    
    public Voiture getVoitureByClientId(Long clientId) {
        String url = "http://SERVICE-VOITURE/api/cars/byClient/" + clientId;
        return restTemplate.getForObject(url, Voiture.class);
    }
}
```

> [!NOTE]
> RestTemplate est simple mais considéré comme "ancien". Il reste utile pour comprendre la base.

### ✅ Validation B1

```bash
curl http://localhost:8081/api/clients/1/car/rest
```

---

### Étape B2 — Feign Client (déclaratif)

#### B2.1 Activer Feign

```java
@SpringBootApplication
@EnableFeignClients
public class ServiceClientApplication {
    // ...
}
```

#### B2.2 Définir l'interface Feign

```java
@FeignClient(name = "SERVICE-VOITURE")
public interface VoitureFeignClient {
    
    @GetMapping("/api/cars/byClient/{clientId}")
    Voiture getVoitureByClientId(@PathVariable Long clientId);
}
```

> [!TIP]
> Feign réduit fortement le code : pas de build d'URL manuel, très apprécié pour la lisibilité.

### ✅ Validation B2

```bash
curl http://localhost:8081/api/clients/1/car/feign
```

---

### Étape B3 — WebClient (mode synchrone)

#### B3.1 Créer un WebClient.Builder

```java
@Configuration
public class WebClientConfig {
    
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
```

#### B3.2 Appeler le service

```java
@Service
public class VoitureClientWeb {
    
    private final WebClient webClient;
    
    public VoitureClientWeb(WebClient.Builder builder) {
        this.webClient = builder.build();
    }
    
    public Voiture getVoitureByClientId(Long clientId) {
        return webClient.get()
                .uri("http://SERVICE-VOITURE/api/cars/byClient/" + clientId)
                .retrieve()
                .bodyToMono(Voiture.class)
                .block();  // Mode synchrone pour ce TP
    }
}
```

> [!IMPORTANT]
> WebClient est réactif. Le lab l'utilise en "synchrone" via `block()` pour comparer à armes égales.

### ✅ Validation B3

```bash
curl http://localhost:8081/api/clients/1/car/webclient
```

---

## 🔍 Partie C — Découverte de services

### Étape C1 — Mode Eureka

#### C1.1 Lancer Eureka Server

```bash
# Port standard : 8761
mvn spring-boot:run
```

UI Eureka : http://localhost:8761

#### C1.2 Configuration des services

**Service Voiture & Service Client :**

```yaml
spring:
  application:
    name: SERVICE-VOITURE  # ou SERVICE-CLIENT

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
```

### ✅ Validation C1

- Eureka UI affiche `SERVICE-CLIENT` et `SERVICE-VOITURE`
- Les endpoints `/rest`, `/feign`, `/webclient` fonctionnent

---

### Étape C2 — Mode Consul (migration)

#### C2.1 Lancer Consul

```bash
consul agent -dev
```

UI Consul : http://localhost:8500

#### C2.2 Migration Eureka → Consul

**pom.xml :**
```xml
<!-- Supprimer -->
<!-- <dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency> -->

<!-- Ajouter -->
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-consul-discovery</artifactId>
</dependency>
```

**application.yml :**
```yaml
spring:
  cloud:
    consul:
      host: localhost
      port: 8500
      discovery:
        health-check-interval: 10s
```

### ✅ Validation C2

- UI Consul affiche les 2 services en état "passing"
- Les endpoints fonctionnent exactement pareil

---

## 📊 Partie D — Tests de performance (JMeter)

### Étape D1 — Préparer le scénario de test

**Endpoints à tester :**
```
/api/clients/1/car/rest
/api/clients/1/car/feign
/api/clients/1/car/webclient
```

**Charges recommandées :**
- 10 utilisateurs simultanés
- 50
- 100
- 200
- 500

> [!WARNING]
> En local, 500 threads peut saturer la machine. Réduire à 300 si nécessaire.

### Étape D2 — Exécuter les tests (Eureka)

Pour chaque méthode, noter :
- Temps moyen (ms)
- P95 (percentile 95)
- Débit (req/s)

### Étape D3 — Exécuter les tests (Consul)

Même protocole après migration.

---

## 💻 Partie E — Mesures CPU / Mémoire

### Étape E1 — Mesure simple

**Option 1 : Task Manager / htop**
- Observer les processus Java
- Noter CPU% et RAM (MB) pendant les tests

**Option 2 : Spring Boot Actuator + Prometheus/Grafana**

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
```

---

## 🛡️ Partie F — Résilience et tolérance aux pannes

### Étape F1 — Panne du service voiture

**Scénario :**
1. Lancer test charge 100 req/s
2. Arrêter Service Voiture pendant 10-20s
3. Redémarrer

**À noter :**
- Taux d'échec (%)
- Temps de reprise
- Comportement de chaque client

### Étape F2 — Panne du serveur de découverte

**Scénario :**
1. Services démarrés et enregistrés
2. Arrêter Eureka/Consul pendant le test
3. Observer si les appels continuent (cache local)

### Étape F3 — Panne du service client

**Scénario :**
1. Arrêter Service Client
2. Relancer
3. Vérifier la re-registration

---

## 📈 Partie G — Récapitulatif des résultats

### Tableau 1 — Performance (latence et débit)

| Client | Charge | Temps moyen (ms) | P95 (ms) | Débit (req/s) |
|--------|--------|------------------|----------|---------------|
| **RestTemplate** (Eureka) | 100 | | | |
| **Feign** (Eureka) | 100 | | | |
| **WebClient** (Eureka) | 100 | | | |
| **RestTemplate** (Consul) | 100 | | | |
| **Feign** (Consul) | 100 | | | |
| **WebClient** (Consul) | 100 | | | |

### Tableau 2 — CPU / Mémoire

| Client | CPU (%) | RAM (MB) |
|--------|---------|----------|
| **RestTemplate** | | |
| **Feign** | | |
| **WebClient** | | |

### Tableau 3 — Résilience

| Scénario | RestTemplate | Feign | WebClient |
|----------|--------------|-------|-----------|
| Panne Service Voiture | | | |
| Panne Discovery | | | |
| Temps de reprise | | | |

### Tableau 4 — Simplicité

| Critère | RestTemplate | Feign | WebClient |
|---------|--------------|-------|-----------|
| Lignes de code | | | |
| Complexité (1-5) | | | |
| Maintenabilité | | | |

---

## 🎓 Partie H — Analyse et discussion

### Points d'analyse obligatoires

1. **Latence :** Quelle méthode donne la meilleure latence en charge ?
2. **Débit :** Le débit maximal observé pour chaque méthode ?
3. **Simplicité :** Quelle méthode est la plus simple à maintenir ?
4. **Impact Discovery :** Eureka vs Consul sur latence et stabilité ?
5. **Résilience :** Que se passe-t-il pendant une panne ?

### Conseils de rédaction

- Décrire ce qui a été mesuré
- Justifier les valeurs (charge, machine)
- Comparer et conclure (forces/faiblesses)

---

## 📦 Livrables attendus

1. ✅ Code des 2 services (client + voiture)
2. ✅ Preuve d'enregistrement (capture Eureka/Consul)
3. ✅ Résultats de tests (latence, débit, CPU/RAM)
4. ✅ Analyse comparée (1–2 pages)

---

## 👨‍💻 Auteur

**Imad ADAOUMOUM**

## 📄 License

Ce projet est réalisé dans un cadre académique.
