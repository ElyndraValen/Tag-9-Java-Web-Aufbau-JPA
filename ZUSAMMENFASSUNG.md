# Java Web Aufbau Tag 9 - Maven Projekt

**JPA Relationen: @OneToOne & @ManyToOne**  
*Erstellt für java-developer.online im Dark-Orange-White Design*

---

## ✅ Was wurde erstellt?

Ein vollständiges Maven-Projekt mit allen Code-Beispielen aus dem Artikel **Tag 9: Java Web und Datenbanken** von java-developer.online.

### 📦 Lieferumfang

1. **JavaWebAufbauTag9.zip** - Vollständiges Maven-Projekt
2. **Projekt-Uebersicht.html** - Visuelle Übersicht mit allen Details
3. **Diese Zusammenfassung** - Quick Reference

---

## 🎯 Projekt-Highlights

### Entity-Klassen (4)
- ✅ **User.java** - @OneToOne zu UserProfile, @OneToMany zu Orders
- ✅ **UserProfile.java** - Detail-Entity mit Geburtsdatum, Bio, Telefon
- ✅ **Order.java** - @ManyToOne zu User mit LAZY Loading
- ✅ **OrderStatus.java** - Enum (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)

### Service Layer (1)
- ✅ **OrderManagementService.java** - EJB mit vollständiger CRUD-Logik
  - User mit Profile erstellen
  - Orders erstellen und zuordnen
  - JOIN FETCH für Performance
  - Statistiken berechnen (COUNT, SUM)
  - Bidirektionale Relationen pflegen

### Web Layer (1)
- ✅ **OrderServlet.java** - Interaktive Demo mit 3 Funktionen:
  - Test-Daten erstellen
  - Orders auflisten
  - Statistiken anzeigen

### Design & Styling
- ✅ **style.css** - Dark-Orange-White Theme im java-developer.online Stil
  - Dunkler Hintergrund (#1a1a1a)
  - Orange Überschriften (#ff6b35, #ff8c42)
  - White Text (#ffffff)
  - Responsive Design
  - Status-Badges für Orders

### Konfiguration
- ✅ **pom.xml** - Maven Build mit Jakarta EE 10
- ✅ **persistence.xml** - JPA Configuration mit Hibernate
- ✅ **index.html** - Landing Page mit Projekt-Info
- ✅ **README.md** - Vollständige Dokumentation

---

## 🛠️ Technologie-Stack

| Komponente | Version | Zweck |
|------------|---------|-------|
| Java | JDK 21 LTS | Programmiersprache |
| Jakarta EE | 10.0.0 | Enterprise Framework |
| JPA | 3.1 | Object-Relational Mapping |
| Payara Server | 6.x | Application Server |
| MySQL | 8.x | Datenbank |
| Maven | 3.9+ | Build Management |

---

## 📂 Verzeichnis-Struktur

```
JavaWebAufbauTag9/
├── pom.xml
├── README.md
├── .gitignore
└── src/
    └── main/
        ├── java/com/javafleet/
        │   ├── model/
        │   │   ├── User.java
        │   │   ├── UserProfile.java
        │   │   ├── Order.java
        │   │   └── OrderStatus.java
        │   ├── service/
        │   │   └── OrderManagementService.java
        │   └── web/
        │       └── OrderServlet.java
        ├── resources/META-INF/
        │   └── persistence.xml
        └── webapp/
            ├── WEB-INF/
            ├── css/
            │   └── style.css
            └── index.html
```

---

## ⚙️ Quick Start

### 1. Voraussetzungen prüfen
- [ ] JDK 21 LTS installiert
- [ ] NetBeans 22 oder IntelliJ IDEA
- [ ] Payara Server 6.x konfiguriert
- [ ] MySQL 8.x läuft

### 2. Datenbank starten
```bash
docker run --name mysql-jpa \
  -e MYSQL_ROOT_PASSWORD=secret \
  -e MYSQL_DATABASE=jpadb \
  -p 3306:3306 \
  -d mysql:8
```

### 3. DataSource konfigurieren
1. Payara Admin Console öffnen: http://localhost:4848
2. Resources → JDBC → JDBC Connection Pools → New...
   - Pool Name: `MySQLPool`
   - ServerName: `localhost`
   - Port: `3306`
   - DatabaseName: `jpadb`
   - User: `root`
   - Password: `secret`
3. Resources → JDBC → JDBC Resources → New...
   - JNDI Name: `jdbc/jpadb`
   - Pool Name: `MySQLPool`

### 4. Projekt öffnen & deployen
```bash
# Entpacken
unzip JavaWebAufbauTag9.zip

# In IDE öffnen (NetBeans oder IntelliJ)
# Oder via Maven CLI:
cd JavaWebAufbauTag9
mvn clean package
# Deploy: target/javawebaufbau-tag9.war
```

### 5. Testen
- Landing Page: http://localhost:8080/javawebaufbau-tag9/
- Order Demo: http://localhost:8080/javawebaufbau-tag9/orders

---

## 🎨 Design-Features

Das Projekt verwendet den **authentischen java-developer.online Stil**:

### Farben
- **Hintergrund:** #1a1a1a (Dark)
- **Überschriften:** #ff6b35, #ff8c42 (Orange Gradient)
- **Text:** #ffffff, #e0e0e0 (White)
- **Akzente:** #2196f3 (Info), #4caf50 (Success), #f44336 (Error)

### Features
- Gradient-Header im Orange-Stil
- Responsive Tables mit Hover-Effekten
- Status-Badges für Order-Status
- Info-Boxen mit farbigen Borders
- Statistik-Karten im Grid-Layout
- Mobile-First Design

---

## 💡 Was du lernst

### JPA Relationen
- ✅ @OneToOne mit CascadeType.ALL
- ✅ @ManyToOne mit FetchType.LAZY
- ✅ Bidirektionale Relationen pflegen
- ✅ orphanRemoval für automatisches Cleanup

### Performance
- ✅ JOIN FETCH für effizientes Laden
- ✅ N+1 Problem vermeiden
- ✅ LAZY vs. EAGER Loading
- ✅ Batch Fetching

### Best Practices
- ✅ Service Layer mit EJB
- ✅ Convenience Methods für bidirektionale Relations
- ✅ Transaction Management
- ✅ Error Handling

---

## 🔧 Häufige Probleme & Lösungen

### Problem: LazyInitializationException
**Lösung:** JOIN FETCH nutzen
```java
em.createQuery("SELECT o FROM Order o JOIN FETCH o.user", Order.class)
```

### Problem: DataSource nicht gefunden
**Lösung:** 
1. Admin Console öffnen: http://localhost:4848
2. Resources → JDBC → JDBC Resources prüfen
3. `jdbc/jpadb` muss vorhanden sein

### Problem: MySQL Connection refused
**Lösung:** Docker Container prüfen
```bash
docker ps
docker logs mysql-jpa
```

---

## 📚 Dokumentation

Das Projekt enthält:
- ✅ Vollständiges README.md mit Setup-Anleitung
- ✅ Kommentierte Code-Beispiele
- ✅ Troubleshooting Guide
- ✅ Performance-Tipps
- ✅ Links zu weiterführenden Ressourcen

---

## 🚀 Nächste Schritte

### Projekt erweitern
1. **Category Entity** hinzufügen (Products → Category)
2. **Address Entity** erstellen (User @OneToOne Address)
3. **Order-Status Transitions** implementieren
4. **Suche nach Orders** bauen
5. **Dashboard mit Charts** erstellen

### Mehr lernen
- Tag 10: @OneToMany & @ManyToMany Relationen
- Join Tables konfigurieren
- Advanced Queries mit JPQL
- Criteria API nutzen

---

## 📧 Support

**Fragen? Probleme?**
- Email: support@java-developer.online
- Website: https://www.java-developer.online

---

## 📜 Credits

**Autor:** Elyndra Valen, Senior Developer  
**Unternehmen:** Java Fleet Systems Consulting  
**Website:** java-developer.online  
**Kurs:** Java Web Aufbau - Tag 9 von 10

---

**Made with ☕ and ❤️**

© 2025 Java Fleet Systems Consulting
