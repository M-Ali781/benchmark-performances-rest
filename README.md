# Étude de cas – Benchmark de performances des Web Services REST

## 1. Compilation du projet sous CMD (Maven Build)
![Compilation du projet sous CMD](screenshots/01_cmd_build_maven.png)

## 2. Génération du jeu de données sous CMD (2000 Catégories, 100000 Items & Datasets JMeter)
![Génération du jeu de données](screenshots/02_cmd_data_generator.png)

## 3. Démarrage du serveur Benchmark sous CMD (Variantes Jersey, Spring MVC, Spring Data REST)
![Démarrage du serveur Benchmark](screenshots/03_cmd_run_benchmark_server.png)

## 4. Requête CMD - Test des endpoints Variante A (Jersey JAX-RS)
![Variante A Jersey JAX-RS](screenshots/04_cmd_test_variant_a_jersey.png)

## 5. Requête CMD - Test des endpoints Variante C (Spring Boot @RestController)
![Variante C Spring Boot @RestController](screenshots/05_cmd_test_variant_c_mvc.png)

## 6. Requête CMD - Test des endpoints Variante D (Spring Data REST)
![Variante D Spring Data REST](screenshots/06_cmd_test_variant_d_data_rest.png)

## 7. Exécution sous CMD - Scénario 1 : READ-heavy (JMeter 50 à 200 Threads)
![Scénario 1 READ-heavy](screenshots/07_cmd_jmeter_read_heavy.png)

## 8. Exécution sous CMD - Scénario 2 : JOIN-filter ciblé (JMeter 60 à 120 Threads)
![Scénario 2 JOIN-filter](screenshots/08_cmd_jmeter_join_filter.png)

## 9. Exécution sous CMD - Scénario 3 : MIXED CRUD sur 2 entités (JMeter 50 à 100 Threads)
![Scénario 3 MIXED CRUD](screenshots/09_cmd_jmeter_mixed_crud.png)

## 10. Exécution sous CMD - Scénario 4 : HEAVY-body avec payload de 5 KB (JMeter 30 à 60 Threads)
![Scénario 4 HEAVY-body](screenshots/10_cmd_jmeter_heavy_body.png)

## 11. Exécution sous CMD - Synthèse comparative des performances (Tableau T2 : RPS, Latences p50/p95/p99, Erreurs)
![Tableau comparatif des performances](screenshots/11_cmd_benchmark_summary_table.png)

## 12. Requête CMD - Collecte des métriques JVM Prometheus (/actuator/prometheus)
![Métriques JVM Prometheus](screenshots/12_cmd_prometheus_metrics.png)

## 13. Exécution sous CMD - Déploiement de l'infrastructure Docker (PostgreSQL, InfluxDB, Prometheus, Grafana)
![Déploiement Docker Compose](screenshots/13_cmd_docker_compose.png)
