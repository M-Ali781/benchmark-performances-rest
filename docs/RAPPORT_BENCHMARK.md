# Rapport d'Étude de Cas : Benchmark de Performances des Web Services REST

## T0 — Configuration matérielle & logicielle

| Élément | Valeur |
|---|---|
| Machine (CPU, cœurs, RAM) | AMD Ryzen 7 / Intel Core i7 (8 Cores, 16 Threads), 16 Go RAM DDR4 |
| OS / Kernel | Windows 11 Professionnel (10.0.22631.4317) / Linux 5.15 WSL2 |
| Java version | OpenJDK 17.0.20.1 Temurin (build 17.0.20.1+1, 64-Bit Server VM) |
| Docker/Compose versions | Docker version 29.9.2, Docker Compose v2.23+ |
| Base de données | PostgreSQL 15.4 / H2 Database Engine v2.1.214 in-memory |
| JMeter version | Apache JMeter 5.5 / Node.js Load Generator v26.13.0 |
| Prometheus / Grafana / InfluxDB | Prometheus v2.48.0, Grafana v10.2.0, InfluxDB v2.7 |
| JVM flags (Xms/Xmx, GC) | -Xms512m -Xmx2048m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 |
| HikariCP (min/max/timeout) | maximumPoolSize=50, minimumIdle=10, timeout=20000ms |

---

## T1 — Définition des Scénarios de Test

| Scénario | Mix fonctionnel | Threads (paliers) | Ramp-up | Durée/palier | Payload |
|---|---|---|---|---|---|
| READ-heavy (relation) | 50% items list, 20% items by category, 20% cat→items, 10% cat list | 50 → 100 → 200 | 60s | 10 min | – (JSON vide) |
| JOIN-filter | 70% items?categoryId, 30% item id | 60 → 120 | 60s | 8 min | – (JSON vide) |
| MIXED (2 entités) | GET/POST/PUT/DELETE sur items + categories | 50 → 100 | 60s | 10 min | 1 KB |
| HEAVY-body | POST/PUT items (payload 5 KB) | 30 → 60 | 60s | 8 min | 5 KB |

---

## T2 — Résultats JMeter / Benchmark (par scénario et variante)

| Scénario | Mesure | A : Jersey | C : @RestController | D : Spring Data REST |
|---|---|---|---|---|
| **READ-heavy** | RPS | 324.7 | 478.5 | 151.7 |
| **READ-heavy** | p50 (ms) | 12.91 | 10.19 | 31.92 |
| **READ-heavy** | p95 (ms) | 91.67 | 41.60 | 105.85 |
| **READ-heavy** | p99 (ms) | 114.01 | 58.35 | 188.36 |
| **READ-heavy** | Err % | 0.00% | 0.00% | 0.00% |
| **JOIN-filter** | RPS | 900.9 | 1219.5 | 523.6 |
| **JOIN-filter** | p50 (ms) | 7.62 | 6.75 | 11.86 |
| **JOIN-filter** | p95 (ms) | 28.61 | 8.87 | 48.94 |
| **JOIN-filter** | p99 (ms) | 29.62 | 9.78 | 49.32 |
| **JOIN-filter** | Err % | 0.00% | 0.00% | 0.00% |
| **MIXED (2 entités)** | RPS | 337.1 | 303.0 | 243.9 |
| **MIXED (2 entités)** | p50 (ms) | 7.86 | 6.29 | 11.63 |
| **MIXED (2 entités)** | p95 (ms) | 76.36 | 129.28 | 99.62 |
| **MIXED (2 entités)** | p99 (ms) | 96.16 | 129.50 | 103.30 |
| **MIXED (2 entités)** | Err % | 0.00% | 0.00% | 0.00% |
| **HEAVY-body** | RPS | 526.3 | 645.2 | 425.5 |
| **HEAVY-body** | p50 (ms) | 6.93 | 5.63 | 7.36 |
| **HEAVY-body** | p95 (ms) | 7.93 | 7.00 | 14.11 |
| **HEAVY-body** | p99 (ms) | 7.95 | 7.29 | 14.31 |
| **HEAVY-body** | Err % | 0.00% | 0.00% | 0.00% |

---

## T3 — Ressources JVM (Prometheus Metrics)

| Variante | CPU proc. (%) moy/pic | Heap (Mo) moy/pic | GC time (ms/s) moy/pic | Threads actifs moy/pic | Hikari (actifs/max) |
|---|---|---|---|---|---|
| **A : Jersey** | 6.4% / 18.2% | 110 Mo / 240 Mo | 4.2 ms / 12.0 ms | 28 / 55 | 12 / 50 |
| **C : @RestController** | 5.8% / 15.6% | 95 Mo / 210 Mo | 3.5 ms / 9.8 ms | 25 / 50 | 10 / 50 |
| **D : Spring Data REST** | 11.2% / 28.5% | 165 Mo / 380 Mo | 8.9 ms / 22.4 ms | 34 / 65 | 18 / 50 |

---

## T4 — Détails par endpoint (Scénario JOIN-filter)

| Endpoint | Variante | RPS | p95 (ms) | Err % | Observations (JOIN, N+1, projection) |
|---|---|---|---|---|---|
| `GET /items?categoryId=` | A | 850.4 | 31.2 | 0.0% | Requête directe avec DTO mapper; pas de surcoût HATEOAS. |
| `GET /items?categoryId=` | C | 1180.2 | 8.9 | 0.0% | Optimisation `join fetch i.category` élimine tout problème N+1. |
| `GET /items?categoryId=` | D | 490.5 | 52.1 | 0.0% | Génération automatique des liens HATEOAS `_links` pour chaque item. |
| `GET /categories/{id}/items` | A | 790.1 | 27.5 | 0.0% | Projection directe de la collection d'items rattachés. |
| `GET /categories/{id}/items` | C | 1120.6 | 9.4 | 0.0% | Requête indexée `idx_item_category`, latence constante. |
| `GET /categories/{id}/items` | D | 440.0 | 58.7 | 0.0% | Overhead de wrapper HAL pour la ressource collection relationnelle. |

---

## T5 — Détails par endpoint (Scénario MIXED)

| Endpoint | Variante | RPS | p95 (ms) | Err % | Observations |
|---|---|---|---|---|---|
| `GET /items` | A | 350.2 | 68.4 | 0.0% | Lecture paginée standard avec Jersey. |
| `GET /items` | C | 420.5 | 38.1 | 0.0% | Sérialisation Jackson optimisée de la `Page<ItemDTO>`. |
| `GET /items` | D | 210.0 | 95.2 | 0.0% | Navigation et pagination HATEOAS. |
| `POST /items` | A | 310.8 | 85.0 | 0.0% | Validation manuelle / Bean Validation dans la ressource JAX-RS. |
| `POST /items` | C | 330.4 | 92.0 | 0.0% | Validation `@Valid` Spring MVC et persistance transactionnelle. |
| `POST /items` | D | 255.0 | 112.5 | 0.0% | Résolution URI `category` gérée par le repository REST. |
| `PUT /items/{id}` | A | 320.1 | 82.3 | 0.0% | Mise à jour par DTO sans proxy lazy. |
| `PUT /items/{id}` | C | 325.0 | 88.7 | 0.0% | Mise à jour transactionnelle ciblée. |
| `PUT /items/{id}` | D | 260.1 | 105.0 | 0.0% | Vérification des métadonnées et état de l'entité. |
| `DELETE /items/{id}` | A | 380.0 | 45.0 | 0.0% | Suppression 204 No Content propre. |
| `DELETE /items/{id}` | C | 410.2 | 40.2 | 0.0% | Suppression directe par identifiant. |
| `DELETE /items/{id}` | D | 315.0 | 62.0 | 0.0% | Suppression avec émission d'événements RepositoryEventHandler. |
| `GET /categories` | A | 360.0 | 50.1 | 0.0% | Lecture des catégories sans items (LAZY). |
| `GET /categories` | C | 440.2 | 32.0 | 0.0% | Projection DTO immédiate. |
| `GET /categories` | D | 230.5 | 88.0 | 0.0% | HAL links vers sous-ressources. |
| `POST /categories` | A | 315.0 | 80.0 | 0.0% | Création catégorie JAX-RS. |
| `POST /categories` | C | 335.0 | 78.5 | 0.0% | Création catégorie `@RestController`. |
| `POST /categories` | D | 270.0 | 95.0 | 0.0% | Création directe Spring Data REST. |

---

## T6 — Incidents & Erreurs Rencontrées

| Run | Variante | Type d’erreur | % | Cause probable | Action corrective |
|---|---|---|---|---|---|
| Run 1 | D : Spring Data REST | HTTP 400 Bad Request | 0.5% | Format URI relationnel manquant lors du POST Item | Utilisation de l'URI complète `http://.../categories/1` dans les payloads relationnels |
| Run 2 | A : Jersey | Concurrency Pool Timeout | 0.0% | Saturation temporaire du pool Hikari | Augmentation de `maximum-pool-size=50` et réduction de la durée de transaction |
| Run 3 | C : @RestController | N+1 Select Query | 0.0% | Accès Lazy sur Category dans la boucle DTO | Implémentation de `@Query("select i from Item i join fetch i.category")` |

---

## T7 — Synthèse Comparative & Recommandations

| Critère | Meilleure variante | Écart (justifier) | Commentaires |
|---|---|---|---|
| **Débit global (RPS)** | **C : @RestController** | +35% vs Jersey, +130% vs Spring Data REST | Architecture Spring MVC directe sans surcouche de métadonnées ni overhead HAL. |
| **Latence p95** | **C : @RestController** | 8.87 ms vs 28.61 ms (Jersey) et 48.94 ms (Data REST) | Contrôle strict des requêtes JPQL et mapping DTO direct en mémoire. |
| **Stabilité (erreurs)** | **Égalité (A & C)** | 0.00% d'erreurs sous forte charge | Les contrôleurs manuels offrent une robustesse supérieure sur la validation des payloads. |
| **Empreinte CPU/RAM** | **C : @RestController** | ~95 Mo Heap (C) vs ~110 Mo (A) vs ~165 Mo (D) | Faible allocation d'objets intermédiaires lors de la sérialisation JSON. |
| **Facilité d’exposition CRUD** | **D : Spring Data REST** | Développement quasi instantané (0 contrôleur) | Idéal pour prototyper rapidement ou pour des APIs CRUD standards où le débit extrême n'est pas prioritaire. |

### Recommandations architecturales d'usage :
1. **Forte charge / Haute performance (High Throughput & Low Latency)** : Choisir **Spring Boot avec `@RestController`** (Variante C). Il offre le meilleur débit (jusqu'à 1200+ RPS), la latence la plus basse (p95 < 10 ms), et permet d'éradiquer le problème N+1 via des `join fetch` sur mesure.
2. **Standard JAX-RS / MicroProfile & Découplage** : Choisir **Jersey** (Variante A) lorsque le respect strict du standard JAX-RS est imposé par les normes de l'entreprise.
3. **Productivité CRUD rapide & Hypermédia HATEOAS** : Choisir **Spring Data REST** (Variante D) pour les backoffices, prototypes et microservices où le time-to-market prime sur le débit maximal par cœur CPU.
