# ThreatMap - Aplicatie de vizualizare a atacurilor cibernetice
### Student: Chindris Andrei

## Descriere
Aplicație desktop pentru vizualizarea și analiza amenințărilor cibernetice pe bază de IP, cu backend Java și frontend React.

## Obiective
* Backend Java pentru procesare log-uri și detectare atacuri.
* Frontend cu hartă 3D interactivă.
* Integrare AI pentru evaluare riscuri.
* Interfață modernă cu statistici în timp real.

## Arhitectura
Împărțit în backend (Java/Spring Boot) și frontend (Electron/React).

Flux: Log-uri → Backend → Analiză → Frontend → Hartă 3D.

## Arhitectura Claselor Java (Diagrama UML Simplificată)
Reprezentare UML în ASCII a principalelor clase și relații de dependență:

```
+---------------------+       +---------------------+
| AttackController    |       | LogParser           |
| (Controller)        | ----> | (Service)           |
| +getLatestAttacks() |       | +parseLine()         |
| +processLogMessage()|       | +parseLogFile()     |
+---------------------+       +---------------------+
           |                           ^
           | uses                      |
           v                           |
+---------------------+       +---------------------+
| AttackDetector      | ----> | IpAnalysisService   |
| (Service)           |       | (Service)           |
| +detectAttacks()    |       | +analyzeIp()        |
| +predictDangerPercent()|   | +analyzeIpAsync()   |
+---------------------+       +---------------------+
           |                           ^
           | uses                      |
           v                           |
+---------------------+       +---------------------+
| SimulationService   |       | AttackStorage       |
| (Service)           | ----> | (Service)           |
| +startSimulation()  |       | +saveEvents()       |
| +stopSimulation()   |       | +loadEvents()       |
+---------------------+       +---------------------+
           |                           ^
           | persists                 |
           v                           |
+---------------------+       +---------------------+
| EvenimenteSecuritate|       | AttackEvent         |
| (Entity)            | ----> | (Model)             |
| +eventId (PK)       |       | +adresaIP           |
| +rawLog             |       | +failedAttempts     |
| +dataEveniment      |       | +location           |
+---------------------+       +---------------------+
```

- **Relații**:
  - `AttackController` utilizează `AttackDetector` și `LogParser` pentru procesare.
  - `AttackDetector` depinde de `IpAnalysisService` pentru geolocație.
  - `SimulationService` folosește `AttackStorage` pentru persistență.
  - `AttackStorage` mapează între `EvenimenteSecuritate` (DB) și `AttackEvent` (frontend).

## Functionalitati/Exemple utilizare
- **Hartă 3D Interactivă**: Vizualizează atacurile pe o hartă Mapbox 3D, cu markere pentru locații IP detectate.
- **Detectare Automată a Atacurilor**: Algoritmi pentru brute-force/DDoS bazate pe praguri (minim 4 încercări eșuate per IP).
- **Geolocație IP**: Folosește MaxMind DB pentru a localiza IP-uri și afișa țări/orase.
- **Analiză AI pentru Risc**: Evaluare procentuală a riscului folosind OpenRouter (fallback la heuristică).
- **Simulare Atacuri**: Generează evenimente simulate pentru testare, cu salvare în DB.
- **Notificări**: Sunete la detectare, pop-up-uri periodice, email-uri la fiecare 10 minute după conectare.
- **Teme Personalizate**: Schimbă între teme albastre și mov pentru UI.
- **Dashboard cu Statistici**: Număr total atacuri, top vectori, tabel IP detaliat.
- **Reset Sistem**: Șterge toate datele și resetează simularea.

## Cum să utilizezi
### Pornire Aplicație
1. Backend: `cd backend/src && mvn spring-boot:run` (port 8080).
2. Frontend: `cd frontend/electron-vite-react && npm install && npm run dev` (port 5173).

### Exemplu Utilizare
- **Pornire Simulare**: Mergi în Setări > Data Management > Apasă "ACTIVATE" pentru a genera atacuri simulate.
- **Vizualizare Atacuri**: În Dashboard, vezi markere pe hartă pentru IP-uri detectate (minim 4 failures).
- **Statistici IP**: Mergi în Statistics pentru tabel cu locații IP, geolocație și analiză AI.
- **Notificări**: Activează sunet/pop-up în Setări pentru alerte la evenimente noi.
- **Schimbare Temă**: În Setări > Display Settings > "Change the cyber theme app" pentru albastru/mov.
- **Reset**: Apasă "RESET" pentru a șterge toate datele și reseta aplicația.

### Alte Funcții
- **Încărcare Log-uri**: Folosește butonul "Load log" pentru a analiza fișiere log externe.
- **Conectare Email**: În Setări, conectează email pentru alerte periodice.
- **Testare**: Rulează `mvn test` pentru teste unitare.
