# 🚀 Deployment-Anleitung - Tag 9 Datasource Beispiel

Diese Anleitung führt dich Schritt-für-Schritt durch das Deployment des Tag 9 Projekts.

---

## ✅ Voraussetzungen prüfen

Bevor du startest, stelle sicher, dass folgende Software installiert ist:

```bash
# Java Version prüfen (sollte 21 sein)
java -version

# Maven Version prüfen
mvn -version

# MySQL/MariaDB prüfen
mysql --version
```

**Alles installiert?** Dann los! 🚀

---

## 📦 Schritt 1: Projekt vorbereiten

### 1.1 Projekt entpacken

```bash
# ZIP-Datei entpacken nach:
C:\dev\Tag-9-Java-Web-Aufbau
```

### 1.2 In NetBeans öffnen

1. **NetBeans starten**
2. **File → Open Project**
3. **Wähle:** `C:\dev\Tag-9-Java-Web-Aufbau`
4. **Maven lädt Dependencies** (dauert beim ersten Mal ~1 Minute)

---

## 🗄️ Schritt 2: Datenbank einrichten

### 2.1 MariaDB/MySQL starten

**Mit XAMPP:**
```
XAMPP Control Panel → MySQL → Start
```

**Oder standalone:**
```bash
# Windows Service
net start MySQL

# Oder manuell starten
C:\MariaDB\bin\mysqld.exe
```

### 2.2 Datenbank erstellen

**Via phpMyAdmin:**

1. Öffne: `http://localhost/phpmyadmin`
2. Klick: "SQL"
3. Kopiere den Inhalt von `database-setup.sql`
4. Klick: "Go"

**Via Command Line:**

```bash
# In Projekt-Verzeichnis wechseln
cd C:\dev\Tag-9-Java-Web-Aufbau

# SQL-Skript ausführen
mysql -u root -p < database-setup.sql
```

### 2.3 Datenbank testen

```bash
mysql -u root -p

USE mywebapp;
SELECT * FROM users;
SELECT * FROM blog_posts;
```

**Siehst du Test-Daten?** → Perfekt! ✅

---

## 🔌 Schritt 3: JDBC-Treiber installieren

### 3.1 MySQL Connector downloaden

1. **Website:** https://dev.mysql.com/downloads/connector/j/
2. **Version:** 8.0.33 oder neuer
3. **Download:** `mysql-connector-java-8.0.33.jar`

### 3.2 Auf Payara Server installieren

```bash
# Kopiere JAR nach:
C:\payara6\glassfish\domains\domain1\lib\mysql-connector-java-8.0.33.jar
```

**Wichtig:** Payara muss neu gestartet werden!

---

## ⚙️ Schritt 4: Connection Pool konfigurieren

### 4.1 Payara Admin Console öffnen

```
http://localhost:4848
```

**Payara läuft nicht?**
```bash
# Starten
C:\payara6\bin\asadmin start-domain
```

### 4.2 Connection Pool erstellen

**Navigation:**
```
Resources → JDBC → JDBC Connection Pools → New
```

**Konfiguration:**

| Feld | Wert |
|------|------|
| Pool Name | `MySQLPool` |
| Resource Type | `javax.sql.DataSource` |
| Database Driver Vendor | `MySQL` |

Klick: **"Next"**

**Additional Properties:**

| Property | Value |
|----------|-------|
| `ServerName` | `localhost` |
| `Port` | `3306` |
| `DatabaseName` | `mywebapp` |
| `User` | `root` |
| `Password` | `[dein Password]` |
| `URL` | `jdbc:mysql://localhost:3306/mywebapp?useSSL=false&serverTimezone=Europe/Berlin` |

Klick: **"Finish"**

### 4.3 Connection Pool testen

1. **Wähle Pool aus:** `MySQLPool`
2. **Klick:** "Ping"
3. **Erwarte:** "Ping Succeeded"

**Fehler?** → Siehe Troubleshooting unten!

### 4.4 Pool-Einstellungen optimieren (Optional)

**General Tab:**
| Setting | Value |
|---------|-------|
| Initial and Min Pool Size | 5 |
| Max Pool Size | 20 |
| Pool Resize Quantity | 2 |
| Idle Timeout | 300 |
| Max Wait Time | 5000 |

Klick: **"Save"**

---

## 🔗 Schritt 5: JDBC Resource erstellen

### 5.1 Resource anlegen

**Navigation:**
```
Resources → JDBC → JDBC Resources → New
```

**Konfiguration:**

| Feld | Wert |
|------|------|
| JNDI Name | `jdbc/MyWebAppDB` |
| Pool Name | `MySQLPool` |
| Status | ✅ Enabled |

Klick: **"OK"**

**Wichtig:** Der JNDI-Name MUSS mit `jdbc/` beginnen!

---

## 🏗️ Schritt 6: Projekt bauen

### 6.1 In NetBeans

**Rechtsklick auf Projekt → Clean and Build**

Output sollte zeigen:
```
BUILD SUCCESS
```

### 6.2 Via Command Line

```bash
cd C:\dev\Tag-9-Java-Web-Aufbau
mvn clean package
```

**Ergebnis:**
```
target/tag9-datasource.war
```

---

## 🚢 Schritt 7: Deployment

### 7.1 In NetBeans deployen

1. **Rechtsklick auf Projekt → Properties**
2. **Run → Server:** `Payara Server 6.x`
3. **Run → Context Path:** `/`
4. **Klick:** "OK"
5. **Rechtsklick auf Projekt → Run**

**Payara startet automatisch!**

### 7.2 Manuelles Deployment (Alternative)

**Via Admin Console:**
```
Applications → Deploy → Choose File → tag9-datasource.war → OK
```

**Via Command Line:**
```bash
C:\payara6\bin\asadmin deploy target\tag9-datasource.war
```

---

## ✅ Schritt 8: Testen

### 8.1 Startseite öffnen

```
http://localhost:8080/
```

**Siehst du die Landing Page?** → Deployment erfolgreich! 🎉

### 8.2 User-Verwaltung testen

```
http://localhost:8080/users
```

**Erwarte:** Liste mit 5 Test-Usern

### 8.3 Blog-System testen

```
http://localhost:8080/blog
```

**Erwarte:** Liste mit 5 Test-Blog-Posts

### 8.4 Einzelnen User anzeigen

```
http://localhost:8080/users?id=1
```

### 8.5 User suchen

```
http://localhost:8080/users?search=elyndra
```

---

## 🔍 Troubleshooting

### Problem: "Ping Failed"

**Ursachen:**
1. MariaDB läuft nicht
2. Falsche Credentials
3. Treiber nicht installiert

**Lösung:**

```bash
# 1. Prüfe ob MySQL läuft
mysql -u root -p

# 2. Prüfe Treiber
dir C:\payara6\glassfish\domains\domain1\lib\*.jar | findstr mysql

# 3. Prüfe Connection-String in Pool
# URL: jdbc:mysql://localhost:3306/mywebapp?useSSL=false&serverTimezone=Europe/Berlin
```

---

### Problem: "Cannot find DataSource"

**Fehlermeldung:**
```
javax.naming.NameNotFoundException: jdbc/MyWebAppDB not found
```

**Lösung:**
1. Prüfe JNDI-Name: `jdbc/MyWebAppDB`
2. Prüfe ob Resource erstellt: Admin Console → JDBC Resources
3. Payara neu starten

---

### Problem: "Connection refused"

**Fehlermeldung:**
```
Communications link failure
```

**Lösung:**
```bash
# Prüfe ob MySQL auf Port 3306 läuft
netstat -an | findstr 3306

# Starte MySQL neu
# XAMPP: MySQL → Stop → Start
```

---

### Problem: "Access denied for user"

**Fehlermeldung:**
```
Access denied for user 'root'@'localhost'
```

**Lösung:**
1. Prüfe Password in Pool-Config
2. Teste Login manuell: `mysql -u root -p`
3. Falls Password falsch: In phpMyAdmin User ändern

---

### Problem: "Table doesn't exist"

**Fehlermeldung:**
```
Table 'mywebapp.users' doesn't exist
```

**Lösung:**
```bash
# SQL-Skript nochmal ausführen
mysql -u root -p < database-setup.sql

# Oder manuell Tabellen erstellen in phpMyAdmin
```

---

### Problem: "Context path already in use"

**Lösung:**
1. **Admin Console → Applications**
2. **Finde alte Version** → Undeploy
3. **Neues Deployment** starten

---

## 📊 Performance-Check

### Connection Pool Monitoring

```
Admin Console → Resources → JDBC → JDBC Connection Pools → MySQLPool → Monitor
```

**Wichtige Metriken:**
- `NumConnUsed`: Sollte < Max Pool Size sein
- `NumConnFree`: Sollte > 0 sein
- `NumConnFailedValidation`: Sollte = 0 sein

---

## 🎯 Nächste Schritte

**Projekt läuft?** Gratuliere! 🎉

Jetzt kannst du:
1. **Code anschauen:** Verstehe, wie Datasources genutzt werden
2. **Erweitern:** Füge eigene Features hinzu
3. **Experimentieren:** Ändere Pool-Settings und beobachte Performance
4. **Tag 10 machen:** Lerne Advanced Topics!

---

## 💬 Support

**Probleme beim Deployment?**

📧 **E-Mail:** feedback@java-developer.online  
🌐 **Website:** https://java-developer.online

---

**Viel Erfolg!** 🚀

*Java Fleet Systems Consulting*
