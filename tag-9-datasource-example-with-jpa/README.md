# Tag 9 - Java Web und Datenbanken - Datasource & Connection Pools

## 📋 Projektübersicht

Dies ist das vollständige Maven-Projekt zu **Tag 9 des Java Web Basic Kurses**. Es demonstriert den professionellen Einsatz von Datasources und Connection Pools in Java Web-Anwendungen.

### Was du in diesem Projekt findest:

✅ **Datasource-Konfiguration** mit JNDI  
✅ **Connection Pooling** in Payara Server  
✅ **DAO-Pattern** für sauberen Datenbankzugriff  
✅ **CRUD-Operationen** (Create, Read, Update, Delete)  
✅ **PreparedStatements** für SQL-Injection-Schutz  
✅ **Try-With-Resources** für korrektes Resource-Management  
✅ **JSP mit JSTL** für die View-Schicht  
✅ **Model 2 Architektur** (MVC-Pattern)

---

## 🛠️ Voraussetzungen

### Software

- ✅ **JDK 21 LTS** (OpenJDK oder Amazon Corretto)
- ✅ **Apache NetBeans 22** (oder IntelliJ IDEA / Eclipse)
- ✅ **Payara Server 6.x** (Application Server)
- ✅ **MariaDB 11.6.2+** (oder MySQL 8.0.33+)
- ✅ **Maven 3.8+** (in NetBeans integriert)

### Datenbank

Stelle sicher, dass MariaDB/MySQL läuft:
```bash
# XAMPP: Starte MySQL über XAMPP Control Panel
# Oder standalone: Prüfe mit
mysql --version
```

---

## 🚀 Installation & Setup

### Schritt 1: Datenbank erstellen

1. **Öffne phpMyAdmin** oder MySQL Command Line:
   ```
   http://localhost/phpmyadmin
   ```

2. **Führe das SQL-Skript aus:**
   - Öffne die Datei `database-setup.sql`
   - Führe das komplette Skript aus
   - Es erstellt:
     - Datenbank `mywebapp`
     - Tabellen `users` und `blog_posts`
     - Test-Daten

**Oder via Command Line:**
```bash
mysql -u root -p < database-setup.sql
```

---

### Schritt 2: JDBC-Treiber installieren

**Der MySQL Connector muss auf dem Payara Server verfügbar sein:**

1. **Download:** MySQL Connector/J
   - URL: https://dev.mysql.com/downloads/connector/j/
   - Version: 8.0.33 oder neuer

2. **Installation:**
   ```
   Kopiere mysql-connector-java-8.0.33.jar nach:
   C:\payara6\glassfish\domains\domain1\lib\
   ```

3. **Payara neu starten**

---

### Schritt 3: Connection Pool konfigurieren

**Via Payara Admin Console:**

1. **Admin Console öffnen:**
   ```
   http://localhost:4848
   ```

2. **Navigiere zu:**
   ```
   Resources → JDBC → JDBC Connection Pools → New
   ```

3. **Pool-Name:** `MySQLPool`

4. **Resource Type:** `javax.sql.DataSource`

5. **Database Driver Vendor:** `MySQL`

6. **Klick:** "Next"

7. **Additional Properties:**

   | Property | Value |
   |----------|-------|
   | `ServerName` | `localhost` |
   | `Port` | `3306` |
   | `DatabaseName` | `mywebapp` |
   | `User` | `root` |
   | `Password` | `[dein Passwort]` |
   | `URL` | `jdbc:mysql://localhost:3306/mywebapp?useSSL=false&serverTimezone=Europe/Berlin` |

8. **Klick:** "Finish"

9. **Test Connection:**
   - Wähle Pool aus
   - Klick "Ping"
   - Erfolg? → Weiter zu Schritt 4!

**Empfohlene Pool-Einstellungen (via "Edit"):**

| Setting | Value | Beschreibung |
|---------|-------|--------------|
| Initial Pool Size | 5 | Connections beim Start |
| Min Pool Size | 2 | Minimum immer verfügbar |
| Max Pool Size | 20 | Maximum gleichzeitig |
| Max Wait Time | 5000 | Timeout in ms |
| Idle Timeout | 300 | Idle-Connection nach 5min schließen |

---

### Schritt 4: JDBC Resource erstellen

**Via Admin Console:**

1. **Navigiere zu:**
   ```
   Resources → JDBC → JDBC Resources → New
   ```

2. **JNDI Name:** `jdbc/MyWebAppDB`
   - ⚠️ **Wichtig:** Muss mit `jdbc/` beginnen!

3. **Pool Name:** `MySQLPool`

4. **Klick:** "OK"

**Fertig!** Deine Anwendung kann jetzt per `jdbc/MyWebAppDB` auf die Datenbank zugreifen.

---

### Schritt 5: Projekt in NetBeans öffnen

1. **NetBeans starten**

2. **File → Open Project**

3. **Wähle dieses Projekt-Verzeichnis aus**

4. **Maven lädt automatisch alle Dependencies**

---

### Schritt 6: Projekt deployen

**In NetBeans:**

1. **Rechtsklick auf Projekt → Properties**

2. **Run:**
   - Server: `Payara Server`
   - Context Path: `/`

3. **Klick:** "OK"

4. **Rechtsklick auf Projekt → Run**

Payara startet automatisch und deployed die Anwendung!

---

## 🌐 Anwendung nutzen

Nach erfolgreichem Deployment:

### User-Verwaltung

```
http://localhost:8080/users
```

**Features:**
- Liste aller User anzeigen
- User-Details anzeigen (`/users?id=1`)
- User suchen (`/users?search=elyndra`)

### Blog-System

```
http://localhost:8080/blog
```

**Features:**
- Liste aller Blog-Posts
- Post-Details anzeigen (`/blog?id=1`)
- Neuen Post erstellen (`/blog?action=new`)
- Post bearbeiten (`/blog?action=edit&id=1`)
- Post löschen (POST: `/blog?action=delete&id=1`)
- Posts suchen (`/blog?search=connection`)
- Pagination (`/blog?page=2`)

---

## 📁 Projekt-Struktur

```
Tag-9-Java-Web-Aufbau/
│
├── pom.xml                              # Maven-Konfiguration
├── database-setup.sql                   # SQL-Initialisierungsskript
├── README.md                            # Diese Datei
├── DEPLOYMENT.md                        # Deployment-Anleitung
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── de/javafleet/web/
│   │   │       ├── model/               # POJOs/Beans
│   │   │       │   ├── User.java
│   │   │       │   └── BlogPost.java
│   │   │       │
│   │   │       ├── dao/                 # Data Access Objects
│   │   │       │   ├── UserDAO.java
│   │   │       │   └── BlogPostDAO.java
│   │   │       │
│   │   │       └── servlets/            # Controller
│   │   │           ├── UserListServlet.java
│   │   │           └── BlogServlet.java
│   │   │
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── web.xml              # Deployment Descriptor (optional)
│   │       │   ├── payara-web.xml       # Payara-Konfiguration
│   │       │   │
│   │       │   └── views/               # JSP Views
│   │       │       ├── user-list.jsp
│   │       │       ├── user-detail.jsp
│   │       │       ├── blog-list.jsp
│   │       │       ├── blog-post.jsp
│   │       │       └── blog-form.jsp
│   │       │
│   │       ├── css/
│   │       │   └── style.css
│   │       │
│   │       └── index.jsp                # Landing Page
│   │
│   └── test/                            # Unit Tests (optional)
│
└── target/                              # Build-Output (von Maven erstellt)
```

---

## 🔧 Troubleshooting

### Problem: "Cannot find DataSource"

**Fehlermeldung:**
```
javax.naming.NameNotFoundException: jdbc/MyWebAppDB not found
```

**Lösung:**
1. Prüfe, ob JDBC Resource erstellt wurde (Admin Console)
2. Prüfe JNDI-Name (muss mit `jdbc/` beginnen)
3. Payara Server neu starten

---

### Problem: "Driver not found"

**Fehlermeldung:**
```
ClassNotFoundException: com.mysql.cj.jdbc.Driver
```

**Lösung:**
1. Kopiere MySQL Connector nach `payara6/glassfish/domains/domain1/lib/`
2. Payara neu starten

---

### Problem: "Connection pool is exhausted"

**Fehlermeldung:**
```
SQLException: Unable to get a connection from pool
```

**Lösung:**
1. Erhöhe Max Pool Size in Admin Console
2. Prüfe Code auf Connection Leaks (fehlende `conn.close()`)
3. Aktiviere Connection Leak Detection:
   ```
   Admin Console → Pool → Advanced → Connection Leak Detection: true
   ```

---

### Problem: "Access denied for user"

**Fehlermeldung:**
```
SQLException: Access denied for user 'root'@'localhost'
```

**Lösung:**
1. Prüfe Username/Password in Pool-Konfiguration
2. Teste manuell: `mysql -u root -p`

---

## 📚 Was du gelernt hast

Nach diesem Projekt kannst du:

✅ **Datasources** auf dem Application Server konfigurieren  
✅ **Connection Pools** einrichten und tunen  
✅ **JNDI-Lookups** durchführen  
✅ **DAO-Pattern** für sauberen Datenbankzugriff nutzen  
✅ **CRUD-Operationen** mit PreparedStatements implementieren  
✅ **SQL-Injection** vermeiden  
✅ **Try-With-Resources** für korrektes Cleanup nutzen  
✅ **Model 2 Architektur** in der Praxis anwenden  

---

## 🎯 Nächste Schritte

**Tag 10 wartet auf dich!**

Themen:
- Connection Pool Tuning (optimale Werte finden)
- Monitoring & Troubleshooting
- Transaction Management im Detail
- Batch-Operations für Performance
- Connection Leak Detection

---

## 💬 Feedback

Fragen oder Probleme?

📧 **E-Mail:** feedback@java-developer.online  
🌐 **Website:** https://java-developer.online

---

**Java Web Basic - Tag 9**  
*Teil der Java Fleet Learning-Serie*  
*© 2025 Java Fleet Systems Consulting*
