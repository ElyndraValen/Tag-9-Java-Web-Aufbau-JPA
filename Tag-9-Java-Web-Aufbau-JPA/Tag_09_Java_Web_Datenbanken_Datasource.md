# Java Web und Datenbanken - Datasource und Connection Pools verstehen - Tag 9 von 10

**Java Web Basic - Tag 9 von 10**  
*Von Elyndra Valen, Senior Developer bei Java Fleet Systems Consulting*

---

## 🗺️ Deine Position im Kurs

| Tag | Thema | Status |
|-----|-------|--------|
| 1 | Java EE Überblick & HTTP (Teil 1) | ✅ Abgeschlossen |
| 2 | HTTP-Protokoll Vertiefung & Zustandslosigkeit | ✅ Abgeschlossen |
| 3 | Servlets & Servlet API | ✅ Abgeschlossen |
| 4 | Deployment Descriptor & MVC vs Model 2 | ✅ Abgeschlossen |
| 5 | JSP & Expression Languages (Teil 1) | ✅ Abgeschlossen |
| 6 | Java Beans, Actions, Scopes & Direktiven | ✅ Abgeschlossen |
| 7 | Include-Action vs Include-Direktive | ✅ Abgeschlossen |
| 8 | JSTL - Java Standard Tag Libraries | ✅ Abgeschlossen |
| **→ 9** | **Java Web und Datenbanken - Datasource** | **👉 DU BIST HIER!** |
| 10 | Connection Pools & JDBC in Web-Umgebungen | 🔒 Noch nicht freigeschaltet |

**Modul:** Java Web Basic  
**Gesamt-Dauer:** 10 Arbeitstage (je 8 Stunden)  
**Dauer heute:** 8 Stunden  
**Dein Ziel:** Datasource-Objekte verstehen und Connection Pools professionell einsetzen

---

## 📋 Voraussetzungen

**Was du schon können solltest:**
- ✅ JDBC-Grundlagen aus Java SE (Connection, Statement, ResultSet)
- ✅ SQL-Kenntnisse (SELECT, INSERT, UPDATE, DELETE)
- ✅ Servlets und JSPs beherrschen
- ✅ JSTL für Datenbankzugriff in JSPs nutzen können
- ✅ Maven-Projekte aufsetzen können
- ✅ Payara Server konfigurieren können

**Was du heute lernst:**
- ✅ Unterschied zwischen JDBC in Java SE und Java Web
- ✅ Was Datasource-Objekte sind und wie sie funktionieren
- ✅ Connection Pools verstehen und konfigurieren
- ✅ JNDI-Namen einsetzen
- ✅ JDBC-URLs für verschiedene Datenbanken aufbauen
- ✅ Production-Ready Datenbankzugriff implementieren

---

## ⚡ 30-Sekunden-Überblick

**Was sind Datasources?**
In Java Web nutzt du KEINE direkte JDBC-Connection wie in Java SE (`DriverManager.getConnection()`). Stattdessen holst du dir Connections aus einem **Connection Pool** über ein **Datasource-Objekt**. Das ist effizienter, skalierbarer und production-ready.

**Was lernst du heute?**
Du verstehst, warum Connection Pools kritisch für Webanwendungen sind, wie du Datasources auf dem Application Server konfigurierst, und wie du sie per JNDI in deinen Servlets nutzt.

**Warum ist das wichtig?**
Ohne Connection Pooling bricht deine Webanwendung unter Last zusammen. Jeder Request würde eine neue DB-Connection aufbauen (langsam!) und wieder schließen. Connection Pools recyceln Connections und machen deine App um ein Vielfaches schneller!

---

## 👋 Willkommen zu Tag 9!

Hi! 👋

Elyndra hier. Wir sind fast am Ziel!

Nach 8 Tagen hast du fast alle Grundlagen von Java Web gelernt. Heute kommt der letzte große Baustein: **Datenbanken**.

Du weißt schon, wie man in Java SE mit JDBC arbeitet. Aber in Webanwendungen funktioniert das ANDERS - und viel besser!

**Warum anders?**

In Java SE:
```java
// Jedes Mal neue Connection
Connection conn = DriverManager.getConnection(url, user, password);
// ... arbeiten
conn.close();
```

Das ist für eine kleine Desktop-App ok. Aber für eine Webanwendung mit **1000 gleichzeitigen Usern**? Katastrophe! 🔥

Jeder Request würde:
1. Neue DB-Connection aufbauen (~100ms)
2. Arbeiten
3. Connection schließen

Das ist **langsam** und **ressourcenfressend**!

**Die Lösung: Connection Pooling**

Connections werden **wiederverwendet**:
1. Pool erstellt 10 Connections beim Server-Start
2. Request holt sich eine Connection aus dem Pool
3. Nach der Arbeit: Connection zurück in den Pool (NICHT schließen!)
4. Nächster Request nutzt dieselbe Connection

**Ergebnis:** 100x schneller! ⚡

Heute lernst du, wie das funktioniert.

**Los geht's!** 🚀

---

## 🟢 GRUNDLAGEN: Java SE vs. Java Web - Datenbankzugriff

### Der klassische Weg in Java SE

**Erinnerst du dich an JDBC in Java SE?**

```java
public class JavaSEDatabase {
    
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/mydb";
        String user = "root";
        String password = "secret";
        
        try {
            // 1. Treiber laden (optional seit JDBC 4.0)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 2. Connection aufbauen
            Connection conn = DriverManager.getConnection(url, user, password);
            
            // 3. Statement erstellen
            Statement stmt = conn.createStatement();
            
            // 4. Query ausführen
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");
            
            // 5. Ergebnisse verarbeiten
            while (rs.next()) {
                System.out.println(rs.getString("name"));
            }
            
            // 6. Ressourcen schließen
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

**Das Problem für Webanwendungen:**

1. **Performance:** Jede Connection dauert ~100ms
2. **Skalierung:** 1000 Requests = 1000 neue Connections
3. **Ressourcen:** Datenbank hat begrenzte Connections (z.B. max 100)
4. **Security:** Credentials im Code (schlecht!)
5. **Flexibilität:** DB-Wechsel = Code ändern

**Real talk:** Dieser Ansatz funktioniert NICHT für Production!

---

### Der Java Web Weg: Datasource & Connection Pool

**In Java Web machen wir es besser:**

```java
@WebServlet("/users")
public class UserServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, 
                        HttpServletResponse response) 
                        throws ServletException, IOException {
        
        try {
            // 1. Context für JNDI-Lookup
            Context ctx = new InitialContext();
            
            // 2. Datasource über JNDI holen
            DataSource ds = (DataSource) ctx.lookup("jdbc/MyDB");
            
            // 3. Connection aus dem Pool holen
            Connection conn = ds.getConnection();
            
            // 4. Arbeiten (wie gewohnt)
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");
            
            // ... Ergebnisse verarbeiten
            
            // 5. Connection zurück in den Pool (NICHT schließen!)
            conn.close();  // Gibt Connection zurück an Pool
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

**Die Unterschiede:**

| Aspekt | Java SE | Java Web |
|--------|---------|----------|
| **Connection-Quelle** | `DriverManager` | `DataSource` |
| **Lookup-Methode** | Direkt im Code | JNDI |
| **Connection-Lebensdauer** | Neue Connection jedes Mal | Wiederverwendet aus Pool |
| **Credentials** | Im Code | In Server-Config |
| **Performance** | Langsam | Schnell |
| **Skalierung** | Schlecht | Gut |

---

### Was ist ein Datasource-Objekt?

**Definition:**

Ein `DataSource` ist eine Factory für Datenbank-Connections. Es ist das Java Web Äquivalent zu `DriverManager`, aber viel mächtiger!

**Interface:**
```java
public interface DataSource {
    Connection getConnection() throws SQLException;
    Connection getConnection(String username, String password) throws SQLException;
}
```

**Vorteile:**

1. **Connection Pooling:** Connections werden wiederverwendet
2. **JNDI-Integration:** Lookup über Namen statt Hardcoded-URLs
3. **Transaction-Support:** Für verteilte Transaktionen (XA)
4. **Monitoring:** Server überwacht Connection-Status
5. **Hot-Reload:** DB-Config ändern ohne Code-Änderung

**Wichtig zu verstehen:**

Das `DataSource`-Objekt selbst wird vom Application Server bereitgestellt. Du konfigurierst es einmal in der Server-Admin-Konsole und nutzt es dann in allen Servlets!

---

### Was ist ein Connection Pool?

**Definition:**

Ein Connection Pool ist eine Sammlung von vorgefertigten, wiederverwendbaren Datenbank-Connections.

**Wie es funktioniert:**

```
┌─────────────────────────────────────┐
│      Application Server             │
│                                     │
│  ┌───────────────────────────────┐ │
│  │   Connection Pool             │ │
│  │                               │ │
│  │  [Conn1] [Conn2] [Conn3]     │ │
│  │  [Conn4] [Conn5] [Conn6]     │ │
│  │  [Conn7] [Conn8] [Conn9]     │ │
│  │  [Conn10]                     │ │
│  │                               │ │
│  │  Initial: 5 Connections       │ │
│  │  Max: 20 Connections          │ │
│  │  Min: 2 Connections           │ │
│  └───────────────────────────────┘ │
│            ↓                        │
│       DataSource                    │
│            ↓                        │
│       Your Servlet                  │
└─────────────────────────────────────┘
```

**Lifecycle einer Connection:**

1. **Initialization:** Server startet → Pool erstellt X Connections
2. **Borrow:** Servlet braucht Connection → Pool gibt eine her
3. **Use:** Servlet arbeitet mit Connection
4. **Return:** `conn.close()` → Connection zurück in Pool (NICHT geschlossen!)
5. **Reuse:** Nächster Request nutzt dieselbe Connection

**Wichtig:**

Wenn du `conn.close()` aufrufst, wird die Connection NICHT wirklich geschlossen, sondern nur zurück in den Pool gegeben!

---

### Pool-Konfiguration verstehen

**Typische Parameter:**

```
Initial Pool Size:     5    → Connections beim Start
Min Pool Size:         2    → Minimum immer verfügbar
Max Pool Size:        20    → Maximum gleichzeitig
Max Wait Time:      5000ms  → Timeout wenn Pool voll
Idle Timeout:     300000ms  → Connection schließen nach 5min Inaktivität
```

**Beispiel-Szenario:**

```
09:00 - Server startet
        → Pool erstellt 5 Connections
        
09:05 - 10 Requests kommen
        → Pool nutzt alle 5 Connections
        → Pool erstellt 5 neue (jetzt 10 total)
        
09:10 - 25 Requests kommen
        → Pool nutzt alle 10 Connections
        → Pool erstellt 10 neue (jetzt 20 total = MAX)
        → 5 Requests müssen warten!
        
09:15 - Nur noch 3 aktive Requests
        → 17 Connections sind idle
        → Nach 5min Idle: Pool schließt einige (zurück auf Min)
```

**Real talk:**

Die richtigen Pool-Werte zu finden ist eine Kunst! Zu wenig = Requests warten. Zu viel = Ressourcen-Verschwendung.

**Faustregel:**
- **Min Size:** 2-5 (für Low-Traffic-Zeiten)
- **Max Size:** 10-50 (abhängig von DB-Limits und Load)
- **Initial Size:** Gleich Min Size

---

## 🟡 PROFESSIONALS: Datasource in Payara konfigurieren

### Schritt 1: JDBC-Treiber bereitstellen

**Warum?**

Payara braucht den JDBC-Treiber deiner Datenbank, um Connections herzustellen.

**Für MySQL/MariaDB:**

1. **Download:** Connector/J von MySQL-Website
   - URL: https://dev.mysql.com/downloads/connector/j/
   - Version: 8.0.33 oder neuer
   
2. **Installation:**
   - Kopiere `mysql-connector-java-8.0.33.jar` nach:
     ```
     C:\payara6\glassfish\domains\domain1\lib\
     ```
   
3. **Server neu starten**

**Alternative: Maven Dependency**

Du kannst den Treiber auch in deine WAR-Datei packen:

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.0.33</version>
    <scope>runtime</scope>
</dependency>
```

Aber: Besser ist es, den Treiber global auf dem Server zu installieren!

---

### Schritt 2: Connection Pool erstellen

**Via Admin Console (GUI):**

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

7. **Additional Properties konfigurieren:**
   
   | Property | Value |
   |----------|-------|
   | `ServerName` | `localhost` |
   | `Port` | `3306` |
   | `DatabaseName` | `mywebapp` |
   | `User` | `root` |
   | `Password` | `secret` |
   | `URL` | `jdbc:mysql://localhost:3306/mywebapp?useSSL=false&serverTimezone=Europe/Berlin` |

8. **Klick:** "Finish"

9. **Test Connection:**
   - Wähle den Pool aus
   - Klick "Ping"
   - Erfolgsmeldung? → Pool funktioniert!

---

### Schritt 3: JDBC Resource (Datasource) erstellen

**Was ist der Unterschied?**

- **Connection Pool:** Technische Implementierung (wie Connections verwaltet werden)
- **JDBC Resource:** Logischer Name (JNDI-Name) für deine Anwendung

**Via Admin Console:**

1. **Navigiere zu:**
   ```
   Resources → JDBC → JDBC Resources → New
   ```

2. **JNDI Name:** `jdbc/MyWebAppDB`
   - ⚠️ **Wichtig:** Muss mit `jdbc/` beginnen!

3. **Pool Name:** `MySQLPool` (der Pool von eben)

4. **Klick:** "OK"

**Fertig!**

Deine Anwendung kann jetzt per `jdbc/MyWebAppDB` auf die Datenbank zugreifen!

---

### Schritt 4: Datasource in Servlet nutzen

**Schritt-für-Schritt:**

```java
package de.javafleet.web.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/users")
public class UserListServlet extends HttpServlet {
    
    private DataSource dataSource;
    
    @Override
    public void init() throws ServletException {
        try {
            // JNDI Context erstellen
            Context ctx = new InitialContext();
            
            // Datasource holen (EINMAL beim Servlet-Start)
            dataSource = (DataSource) ctx.lookup("jdbc/MyWebAppDB");
            
        } catch (Exception e) {
            throw new ServletException("Datasource lookup failed", e);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, 
                        HttpServletResponse response) 
                        throws ServletException, IOException {
        
        List<User> users = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT id, username, email FROM users");
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                users.add(user);
            }
            
        } catch (Exception e) {
            throw new ServletException("Database error", e);
        }
        
        // Daten an JSP weiterreichen
        request.setAttribute("users", users);
        request.getRequestDispatcher("/WEB-INF/views/user-list.jsp")
               .forward(request, response);
    }
}
```

**Die wichtigsten Punkte:**

1. **DataSource als Instanzvariable:**
   ```java
   private DataSource dataSource;
   ```
   Nur EINMAL holen (in `init()`), dann wiederverwenden!

2. **JNDI-Lookup in `init()`:**
   ```java
   Context ctx = new InitialContext();
   dataSource = (DataSource) ctx.lookup("jdbc/MyWebAppDB");
   ```
   Der Name `jdbc/MyWebAppDB` muss mit dem JNDI-Namen übereinstimmen!

3. **Connection aus Pool holen:**
   ```java
   Connection conn = dataSource.getConnection()
   ```
   Das ist SCHNELL - Connection kommt aus dem Pool!

4. **Try-With-Resources:**
   ```java
   try (Connection conn = ...; PreparedStatement stmt = ...; ResultSet rs = ...) {
       // arbeiten
   }
   ```
   → Automatisches Cleanup! `conn.close()` gibt Connection zurück an Pool.

---

### Best Practice: DAO-Pattern für Datenbankzugriff

**Problem:**

Datenbankzugriff direkt im Servlet ist unübersichtlich und nicht wiederverwendbar.

**Lösung: Data Access Object (DAO)**

```java
package de.javafleet.web.dao;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    
    private DataSource dataSource;
    
    public UserDAO() {
        try {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("jdbc/MyWebAppDB");
        } catch (Exception e) {
            throw new RuntimeException("Datasource init failed", e);
        }
    }
    
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        
        String sql = "SELECT id, username, email FROM users";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        }
        
        return users;
    }
    
    public User findById(int id) throws SQLException {
        String sql = "SELECT id, username, email FROM users WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        }
        
        return null;
    }
    
    public void create(User user) throws SQLException {
        String sql = "INSERT INTO users (username, email) VALUES (?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, 
                 Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.executeUpdate();
            
            // Generated ID holen
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }
        }
    }
    
    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, email = ? WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setInt(3, user.getId());
            stmt.executeUpdate();
        }
    }
    
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        return user;
    }
}
```

**Servlet wird einfacher:**

```java
@WebServlet("/users")
public class UserListServlet extends HttpServlet {
    
    private UserDAO userDAO = new UserDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, 
                        HttpServletResponse response) 
                        throws ServletException, IOException {
        try {
            List<User> users = userDAO.findAll();
            request.setAttribute("users", users);
            request.getRequestDispatcher("/WEB-INF/views/user-list.jsp")
                   .forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
}
```

**Viel besser!** 🎯

---

### JDBC-URLs für verschiedene Datenbanken

**Wichtig zu wissen:**

Jede Datenbank hat ihr eigenes URL-Format!

**MySQL/MariaDB:**
```
jdbc:mysql://localhost:3306/mydb?useSSL=false&serverTimezone=Europe/Berlin
```

**PostgreSQL:**
```
jdbc:postgresql://localhost:5432/mydb
```

**Oracle:**
```
jdbc:oracle:thin:@localhost:1521:orcl
```

**SQL Server:**
```
jdbc:sqlserver://localhost:1433;databaseName=mydb
```

**H2 (In-Memory für Tests):**
```
jdbc:h2:mem:testdb
```

**H2 (File-Based):**
```
jdbc:h2:file:~/mydb
```

**Struktur einer JDBC-URL:**

```
jdbc:<subprotocol>://<host>:<port>/<database>?<parameters>
  ↑         ↑          ↑      ↑        ↑            ↑
  |      Datenbank-  Server  Port   DB-Name   Optionale
  |        Typ                                Parameter
  |
Immer "jdbc:"
```

---

## 🔵 BONUS: Erweiterte Themen

### Connection Pool Monitoring

**Warum wichtig?**

Du willst wissen:
- Wie viele Connections sind gerade aktiv?
- Wie oft wartet ein Request auf eine Connection?
- Gibt es Connection Leaks (Connections, die nicht zurückgegeben werden)?

**In Payara Admin Console:**

```
Resources → JDBC → JDBC Connection Pools → [DeinPool] → Monitor
```

**Wichtige Metriken:**

| Metrik | Bedeutung | Alarm wenn |
|--------|-----------|------------|
| `NumConnUsed` | Aktiv genutzte Connections | Nahe Max Pool Size |
| `NumConnFree` | Verfügbare Connections im Pool | Oft bei 0 |
| `NumConnFailedValidation` | Ungültige Connections | > 0 |
| `NumConnTimedOut` | Requests, die timeout hatten | > 0 |

**Real talk:** Wenn `NumConnUsed` ständig am Limit ist, erhöhe die Max Pool Size!

---

### Connection Leak Detection

**Problem:**

Entwickler vergisst `conn.close()` → Connection bleibt "borrowed" → Pool läuft leer!

**Symptom:**
```
WARNING: A resource failed to close properly
```

**Lösung 1: Try-With-Resources (immer nutzen!)**
```java
try (Connection conn = dataSource.getConnection()) {
    // arbeiten
}
// conn.close() wird AUTOMATISCH aufgerufen!
```

**Lösung 2: Connection Leak Detection in Pool konfigurieren**

In Admin Console:
```
Connection Pool → Advanced → Connection Leak Detection: true
Connection Leak Timeout: 60 (seconds)
```

Wenn eine Connection länger als 60 Sekunden "borrowed" ist, loggt Payara eine Warnung mit Stack Trace!

---

### Transaktionen über Datasource

**Erinnerung:** Standardmäßig ist jedes SQL-Statement eine eigene Transaktion (Auto-Commit).

**Für mehrere Statements in einer Transaktion:**

```java
Connection conn = null;
try {
    conn = dataSource.getConnection();
    conn.setAutoCommit(false);  // Auto-Commit deaktivieren
    
    // Statement 1
    PreparedStatement stmt1 = conn.prepareStatement(
        "INSERT INTO orders (user_id, total) VALUES (?, ?)");
    stmt1.setInt(1, userId);
    stmt1.setDouble(2, total);
    stmt1.executeUpdate();
    
    // Statement 2
    PreparedStatement stmt2 = conn.prepareStatement(
        "UPDATE users SET balance = balance - ? WHERE id = ?");
    stmt2.setDouble(1, total);
    stmt2.setInt(2, userId);
    stmt2.executeUpdate();
    
    // Alles erfolgreich → Commit
    conn.commit();
    
} catch (SQLException e) {
    if (conn != null) {
        try {
            conn.rollback();  // Fehler → Alles rückgängig machen
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    throw e;
} finally {
    if (conn != null) {
        conn.setAutoCommit(true);  // Wieder aktivieren
        conn.close();
    }
}
```

**Wichtig:**
- `setAutoCommit(false)` → Transaktion starten
- `commit()` → Änderungen speichern
- `rollback()` → Bei Fehler alles rückgängig machen
- `setAutoCommit(true)` → Wieder normal

---

### Prepared Statements vs. Statements

**NIEMALS normale Statements für User-Input nutzen!**

**❌ FALSCH (SQL-Injection-Gefahr!):**
```java
String username = request.getParameter("username");
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery(
    "SELECT * FROM users WHERE username = '" + username + "'");
```

**Angriff:**
```
username = "admin' OR '1'='1"
→ SQL: SELECT * FROM users WHERE username = 'admin' OR '1'='1'
→ Gibt ALLE User zurück!
```

**✅ RICHTIG (SQL-Injection-sicher):**
```java
String username = request.getParameter("username");
PreparedStatement stmt = conn.prepareStatement(
    "SELECT * FROM users WHERE username = ?");
stmt.setString(1, username);
ResultSet rs = stmt.executeQuery();
```

**Warum sicher?**

Der Parameter wird **escaped** - `'` wird zu `\'`, sodass kein SQL-Code injected werden kann!

**Faustregel:** IMMER `PreparedStatement` nutzen, NIEMALS normale `Statement` mit String-Concatenation!

---

### DataSource per Dependency Injection (CDI)

**Moderne Alternative zu JNDI-Lookup:**

```java
@WebServlet("/users")
public class UserListServlet extends HttpServlet {
    
    @Resource(name = "jdbc/MyWebAppDB")
    private DataSource dataSource;
    
    // Kein init() nötig - Container injiziert DataSource!
    
    @Override
    protected void doGet(HttpServletRequest request, 
                        HttpServletResponse response) 
                        throws ServletException, IOException {
        
        try (Connection conn = dataSource.getConnection()) {
            // arbeiten
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
}
```

**Vorteile:**
- ✅ Weniger Code
- ✅ Keine Exception-Handling für JNDI
- ✅ Container managed

**Das ist die moderne Best Practice!**

---

## 💬 Real Talk

**Elyndra:** "Nova, wie läuft's mit dem Datasource-Setup?"

**Nova:** "Funktioniert! Aber ich verstehe nicht, warum wir Connection Pooling brauchen. Connections aufbauen geht doch schnell?"

**Elyndra:** "Lass mich dir was zeigen."

*Elyndra öffnet Performance-Profiler*

**Elyndra:** "Schau - ein `DriverManager.getConnection()` dauert im Schnitt 80-120ms. Dein Servlet verarbeitet den Request in 5ms. Du verbringst also 95% der Zeit mit Connection-Setup!"

**Nova:** "Oh wow, das ist... langsam."

**Elyndra:** "Genau. Jetzt schau dir das mit Connection Pool an:"

```
Connection Pool (mit 10 Connections):
- Connection holen aus Pool: ~1ms
- Servlet-Verarbeitung: ~5ms
- Gesamt: ~6ms

Ohne Pool:
- Connection aufbauen: ~100ms
- Servlet-Verarbeitung: ~5ms
- Gesamt: ~105ms
```

**Nova:** "17x schneller! Aber was ist mit den Ressourcen? 10 permanente Connections zur Datenbank..."

**Elyndra:** "Guter Punkt! Aber überleg mal: Ohne Pool erstellt jeder Request eine neue Connection. Bei 100 gleichzeitigen Requests sind das 100 Connections zur DB. Mit Pool? Maximal 10."

**Nova:** "Ah, der Pool ist also auch ressourcensparender!"

**Elyndra:** "Exakt. Und noch ein Punkt: Connection-Wiederverwendung bedeutet, dass die DB nicht ständig neue Sessions initialisieren muss. Das spart auf DB-Seite auch massiv Ressourcen."

**Nova:** "Ok, ich bin überzeugt. Connection Pooling ist ein Must-Have."

**Elyndra:** "Richtig! In Production ohne Connection Pool zu arbeiten ist wie Netflix ohne CDN zu betreiben - technisch möglich, aber wirtschaftlich Wahnsinn."

---

## ✅ Checkpoint: Hast du es verstanden?

### Quiz:

**Frage 1: Was ist der Hauptunterschied zwischen Datenbankzugriff in Java SE und Java Web?**

**Frage 2: Was ist ein Datasource-Objekt und wozu dient es?**

**Frage 3: Was passiert, wenn du `conn.close()` auf eine Connection aus einem Pool aufrufst?**

**Frage 4: Warum solltest du PreparedStatement statt Statement nutzen?**

**Frage 5: Wie holst du dir eine Datasource per JNDI in einem Servlet?**

---

### Mini-Challenge:

**Aufgabe:** Erstelle ein vollständiges CRUD-System für Blog-Posts!

**Features:**
- Liste aller Posts anzeigen (`/blog`)
- Einzelnen Post anzeigen (`/blog?id=X`)
- Neuen Post erstellen (Formular + Verarbeitung)
- Post bearbeiten
- Post löschen

**Technische Anforderungen:**
1. Datasource über JNDI: `jdbc/BlogDB`
2. Connection Pool mit Min=2, Max=10
3. DAO-Pattern für Datenbankzugriff
4. PreparedStatements (kein Statement!)
5. Try-With-Resources für alle DB-Zugriffe
6. Transaction für Multi-Statement-Operations

**Datenbank-Schema:**
```sql
CREATE TABLE blog_posts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    author VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**Bonus:**
- Paging (z.B. 10 Posts pro Seite)
- Suche (nach Titel oder Author)
- Kommentare zu Posts (eigene Tabelle!)

**Hinweise:**
1. Erstelle zunächst die Datenbank und Tabelle in MariaDB
2. Konfiguriere Connection Pool in Payara
3. Erstelle JDBC Resource mit JNDI-Name
4. Implementiere BlogPost-Bean (POJO)
5. Implementiere BlogPostDAO mit allen CRUD-Methoden
6. Erstelle Servlets für jede Operation
7. Erstelle JSPs mit JSTL für Views

---

**Lösung:**

Die Lösung zu dieser Challenge findest du im Maven-Projekt, das zusammen mit diesem Blogpost bereitgestellt wird! 🚀

---

**Geschafft?** 🎉

Dann bist du bereit für die FAQ-Sektion!

---

## ❓ Häufig gestellte Fragen

**Frage 1: Kann ich mehrere Datasources in einer Anwendung nutzen?**

Ja! Du kannst beliebig viele Datasources konfigurieren:

```java
@Resource(name = "jdbc/MainDB")
private DataSource mainDB;

@Resource(name = "jdbc/AnalyticsDB")
private DataSource analyticsDB;

@Resource(name = "jdbc/LogDB")
private DataSource logDB;
```

Das ist üblich in größeren Anwendungen, wo verschiedene Datenbanken für verschiedene Zwecke genutzt werden (z.B. Main DB für Business-Daten, Analytics DB für Reporting, Log DB für Logs).

---

**Frage 2: Was passiert, wenn der Connection Pool leer ist?**

Der Request **wartet** bis eine Connection verfügbar ist - maximal für die `Max Wait Time` (z.B. 5 Sekunden).

```
Request kommt → Pool ist voll (alle Connections in Use)
→ Request wartet
→ Nach 5s immer noch keine Connection verfügbar
→ SQLException: "Connection pool is exhausted"
```

**Lösung:** Max Pool Size erhöhen oder Code optimieren (Connections schneller zurückgeben).

---

**Frage 3: Wie teste ich meine Anwendung ohne echte Datenbank?**

**Option 1: H2 In-Memory Database**

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>2.2.224</version>
    <scope>test</scope>
</dependency>
```

H2 ist eine Java-Datenbank, die im RAM läuft - perfekt für Tests!

**Option 2: Mock-DAO**

Erstelle eine Mock-Version deines DAOs für Tests:

```java
public class UserDAOMock extends UserDAO {
    @Override
    public List<User> findAll() {
        return Arrays.asList(
            new User(1, "testuser1", "test1@example.com"),
            new User(2, "testuser2", "test2@example.com")
        );
    }
}
```

---

**Frage 4: Muss ich die Connection immer selbst schließen?**

**Mit Try-With-Resources: NEIN!**

```java
try (Connection conn = dataSource.getConnection()) {
    // arbeiten
}
// conn.close() wird automatisch aufgerufen!
```

**Ohne Try-With-Resources: JA!**

```java
Connection conn = null;
try {
    conn = dataSource.getConnection();
    // arbeiten
} finally {
    if (conn != null) {
        conn.close();  // MUSS manuell gemacht werden!
    }
}
```

**Best Practice:** IMMER Try-With-Resources nutzen!

---

**Frage 5: Kann ich die Pool-Konfiguration zur Laufzeit ändern?**

Ja! In der Payara Admin Console:

```
Resources → JDBC → JDBC Connection Pools → [DeinPool] → Edit
```

Du kannst die Werte ändern und speichern. Payara wendet die Änderungen sofort an - kein Server-Restart nötig!

**Aber:** Für Production solltest du Pool-Werte vorher testen und nicht zur Laufzeit experimentieren!

---

**Frage 6: Was ist der Unterschied zwischen Connection Pool und Datasource?**

**Connection Pool:**
- Technische Implementierung
- Verwaltet die physischen Connections
- Konfiguriert auf Server-Ebene

**Datasource:**
- Logische Schnittstelle
- JNDI-Name für deine Anwendung
- Nutzt intern einen Connection Pool

**Analogie:**
- Connection Pool = Garage mit Autos
- Datasource = Schlüssel zu einem Auto

Du nutzt den Schlüssel (Datasource), aber das Auto kommt aus der Garage (Pool).

---

**Frage 7: Bernd meinte, "Connection Pooling ist doch old-school, moderne Apps nutzen NoSQL oder ORMs". Hat er recht?**

Ngl, Bernd hat teilweise recht - aber er übersieht was! 😄

**Real talk:**

1. **NoSQL:** Auch NoSQL-Datenbanken (MongoDB, Cassandra) nutzen Connection Pooling! Es ist kein "SQL-Ding", sondern eine generelle Best Practice für Netzwerk-Connections.

2. **ORMs (JPA/Hibernate):** Diese Tools nutzen AUCH Connection Pools! Hibernate z.B. nutzt intern HikariCP (einen sehr performanten Connection Pool). Du siehst es nur nicht direkt.

3. **Moderne Frameworks:** Spring Boot konfiguriert Connection Pooling automatisch - aber es ist TROTZDEM da!

**Bernd's Perspektive:** Er arbeitet viel mit Spring Boot, wo Connection Pooling "under the hood" passiert. Er merkt es nicht, aber es ist da.

**Die Wahrheit:** Connection Pooling ist KEIN old-school Konzept, sondern eine fundamentale Performance-Optimierung, die in ALLEN modernen Systemen genutzt wird - ob SQL, NoSQL, REST-APIs oder Message Queues.

**Meine Empfehlung:** Verstehe, wie Connection Pooling funktioniert, auch wenn dein Framework es automatisch managed. Bei Performance-Problemen musst du die Konfiguration verstehen können!

---

## 📚 Quiz-Lösungen

**Hier sind die Antworten zum Quiz von oben:**

---

### Frage 1: Was ist der Hauptunterschied zwischen Datenbankzugriff in Java SE und Java Web?

**Antwort:**

In **Java SE** nutzt du `DriverManager.getConnection()`, um jedes Mal eine neue Datenbank-Connection aufzubauen. Nach der Arbeit wird die Connection mit `conn.close()` geschlossen und verworfen.

In **Java Web** nutzt du ein `DataSource`-Objekt, um Connections aus einem **Connection Pool** zu holen. Diese Connections werden NICHT wirklich geschlossen, sondern nach `conn.close()` zurück in den Pool gegeben und wiederverwendet.

**Vorteile in Java Web:**
- **Performance:** Keine Zeit für Connection-Aufbau (Pool-Connection in ~1ms statt 100ms)
- **Skalierung:** Begrenzte Anzahl von Connections zur DB (z.B. max 20), egal wie viele Requests kommen
- **Ressourcen:** DB muss nicht ständig neue Sessions initialisieren
- **Konfiguration:** DB-Credentials in Server-Config statt im Code
- **Flexibilität:** DB-Wechsel ohne Code-Änderung möglich

---

### Frage 2: Was ist ein Datasource-Objekt und wozu dient es?

**Antwort:**

Ein `DataSource` ist eine Factory für Datenbank-Connections in Java Web. Es implementiert das Interface `javax.sql.DataSource` und bietet die Methode `getConnection()`.

**Zweck:**
1. **Abstraktion:** Du greifst nicht direkt auf `DriverManager` zu, sondern holst Connections von der Datasource
2. **Connection Pooling:** Die Datasource gibt Connections aus einem Pool zurück, statt neue zu erstellen
3. **JNDI-Integration:** Datasource wird über einen logischen Namen (`jdbc/MyDB`) statt einer URL geholt
4. **Zentrale Konfiguration:** DB-Credentials und Pool-Settings sind in der Server-Config, nicht im Code
5. **Container-Management:** Der Application Server verwaltet die Datasource und überwacht ihre Performance

**Verwendung:**
```java
// JNDI-Lookup (alt)
Context ctx = new InitialContext();
DataSource ds = (DataSource) ctx.lookup("jdbc/MyDB");

// Dependency Injection (modern)
@Resource(name = "jdbc/MyDB")
private DataSource dataSource;
```

---

### Frage 3: Was passiert, wenn du `conn.close()` auf eine Connection aus einem Pool aufrufst?

**Antwort:**

Die Connection wird NICHT wirklich geschlossen, sondern **zurück in den Pool gegeben**!

**Der Prozess:**

1. **Request holt Connection:** `Connection conn = dataSource.getConnection()`
   - Pool markiert Connection als "in use"
   - Request nutzt die Connection

2. **Request ruft `conn.close()` auf:**
   - Pool markiert Connection als "available"
   - Connection bleibt offen und bereit für den nächsten Request
   - KEINE neue Connection zur DB nötig

3. **Nächster Request holt Connection:**
   - Pool gibt dieselbe Connection zurück
   - Request nutzt sie sofort (kein Setup nötig!)

**Wichtig zu verstehen:**

Das `close()` ist ein "logisches" Close (= zurück in Pool), kein "physisches" Close (= Connection zur DB schließen).

**Physisches Close** passiert nur:
- Wenn der Pool zu groß wird (mehr als Max Idle Connections)
- Wenn die Connection kaputt ist (Failed Validation)
- Wenn der Server heruntergefahren wird

---

### Frage 4: Warum solltest du PreparedStatement statt Statement nutzen?

**Antwort:**

Es gibt zwei Hauptgründe:

**1. Sicherheit: SQL-Injection-Schutz**

Mit `Statement` baust du SQL-Queries per String-Concatenation:
```java
// ❌ GEFÄHRLICH!
String username = request.getParameter("username");
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery(
    "SELECT * FROM users WHERE username = '" + username + "'");
```

**Angriff:**
```
username = "admin' OR '1'='1"
→ SQL: SELECT * FROM users WHERE username = 'admin' OR '1'='1'
→ Gibt ALLE User zurück!
```

Mit `PreparedStatement` werden Parameter escaped:
```java
// ✅ SICHER!
PreparedStatement stmt = conn.prepareStatement(
    "SELECT * FROM users WHERE username = ?");
stmt.setString(1, username);
```

Selbst wenn `username = "admin' OR '1'='1"`, wird das als String behandelt, nicht als SQL-Code!

**2. Performance: Query-Caching**

`PreparedStatement` wird von der Datenbank gecached. Bei wiederholter Ausführung (z.B. in einer Schleife oder bei vielen Requests) ist es schneller, weil die DB den Query-Plan nicht neu erstellen muss.

**Faustregel:** IMMER `PreparedStatement` nutzen!

---

### Frage 5: Wie holst du dir eine Datasource per JNDI in einem Servlet?

**Antwort:**

Es gibt zwei Methoden:

**Methode 1: JNDI-Lookup (klassisch)**

```java
@WebServlet("/users")
public class UserServlet extends HttpServlet {
    
    private DataSource dataSource;
    
    @Override
    public void init() throws ServletException {
        try {
            // 1. JNDI Context erstellen
            Context ctx = new InitialContext();
            
            // 2. Datasource über JNDI-Name holen
            dataSource = (DataSource) ctx.lookup("jdbc/MyDB");
            
        } catch (NamingException e) {
            throw new ServletException("Datasource lookup failed", e);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, 
                        HttpServletResponse response) 
                        throws ServletException, IOException {
        
        try (Connection conn = dataSource.getConnection()) {
            // arbeiten mit Connection
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
}
```

**Methode 2: Dependency Injection (modern)**

```java
@WebServlet("/users")
public class UserServlet extends HttpServlet {
    
    @Resource(name = "jdbc/MyDB")
    private DataSource dataSource;
    
    // Kein init() nötig - Container injiziert Datasource!
    
    @Override
    protected void doGet(HttpServletRequest request, 
                        HttpServletResponse response) 
                        throws ServletException, IOException {
        
        try (Connection conn = dataSource.getConnection()) {
            // arbeiten mit Connection
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
}
```

**Wichtig:**
- Der JNDI-Name (`jdbc/MyDB`) muss mit dem Namen in der Server-Konfiguration übereinstimmen!
- Hole die Datasource EINMAL (in `init()` oder per Injection) und speichere sie als Instanzvariable
- Nutze dann `dataSource.getConnection()` in jeder Request-Methode

---

## 🎉 Tag 9 geschafft!

**WOW! Du hast es fast geschafft!** 💪

Du bist jetzt bei Tag 9 von 10 - du hast heute gelernt:
- ✅ Connection Pooling verstanden
- ✅ Datasources konfiguriert
- ✅ JNDI-Lookups gemeistert
- ✅ DAO-Pattern angewendet
- ✅ Production-Ready Datenbankzugriff implementiert

**Real talk:** Datenbankzugriff in Java Web ist ANDERS als in Java SE - aber viel besser! Connection Pooling ist eine der wichtigsten Performance-Optimierungen überhaupt.

**Slay!** Du bist jetzt bereit für Production-Datenbanken! 🎯

---

## 🚀 Wie geht's weiter?

**Morgen (Tag 10):** Connection Pools & JDBC in Web-Umgebungen - Advanced Topics

**Was dich erwartet:**
- Connection Pool Tuning (optimale Werte finden)
- Monitoring und Troubleshooting
- Transaction Management im Detail
- Batch-Operations für Performance
- Connection Leak Detection
- **Das wird dein Final Boss für Java Web Basic!** 🔥

**Besonderheit:**

Tag 10 ist der **Abschluss** des Java Web Basic Kurses! Du lernst fortgeschrittene Techniken und Best Practices für Production-Einsatz. Danach bist du ready für echte Enterprise-Projekte!

---

**Brauchst du eine Pause?**

Gönn sie dir! Datasource-Konfiguration kann tricky sein. Lass es sacken.

**Tipp für heute Abend:**

Spiel mit der Mini-Challenge! Bau das Blog-System. Teste verschiedene Szenarien:
- Was passiert, wenn der Pool voll ist?
- Wie verhält sich die App unter Last?
- Was passiert bei Connection-Leaks?

Learning by monitoring! 📊

---

## 🔧 Troubleshooting

**Problem 1: "Cannot find DataSource"**

**Fehlermeldung:**
```
javax.naming.NameNotFoundException: jdbc/MyDB not found
```

**Ursachen:**
1. JNDI-Name falsch geschrieben
2. JDBC Resource nicht erstellt
3. Connection Pool nicht konfiguriert

**Lösung:**

Prüfe in Admin Console:
```
Resources → JDBC → JDBC Resources
```

Ist `jdbc/MyDB` da? Falls nein, erstelle es.

Prüfe auch:
```
Resources → JDBC → JDBC Connection Pools
```

Ist der zugehörige Pool da?

---

**Problem 2: "Connection pool is exhausted"**

**Fehlermeldung:**
```
SQLException: Unable to get a connection from pool
```

**Ursachen:**
1. Zu viele gleichzeitige Requests
2. Connections werden nicht zurückgegeben (Leak!)
3. Max Pool Size zu klein

**Lösung:**

**Schritt 1:** Erhöhe Max Pool Size temporär:
```
Admin Console → Resources → JDBC → JDBC Connection Pools → [Pool] → Edit
Max Pool Size: 50 (statt 20)
```

**Schritt 2:** Aktiviere Connection Leak Detection:
```
Advanced → Connection Leak Detection: true
Connection Leak Timeout: 60
```

**Schritt 3:** Prüfe deinen Code - nutzt du Try-With-Resources?

```java
// ✅ RICHTIG
try (Connection conn = dataSource.getConnection()) {
    // arbeiten
}

// ❌ FALSCH - Leak-Gefahr!
Connection conn = dataSource.getConnection();
// arbeiten
// conn.close() vergessen!
```

---

**Problem 3: "Driver not found"**

**Fehlermeldung:**
```
ClassNotFoundException: com.mysql.cj.jdbc.Driver
```

**Ursache:**

JDBC-Treiber nicht installiert.

**Lösung:**

Kopiere `mysql-connector-java-8.0.33.jar` nach:
```
C:\payara6\glassfish\domains\domain1\lib\
```

Dann Payara neu starten!

---

**Problem 4: "Connection timed out"**

**Fehlermeldung:**
```
SQLException: Communications link failure
```

**Ursachen:**
1. Datenbank läuft nicht
2. Falsche Host/Port
3. Firewall blockiert Port 3306

**Lösung:**

**Schritt 1:** Prüfe, ob MariaDB läuft:
```bash
# XAMPP Control Panel → MySQL → Start
```

**Schritt 2:** Teste Connection manuell:
```bash
mysql -h localhost -P 3306 -u root -p
```

**Schritt 3:** Prüfe Pool-Konfiguration:
```
Admin Console → Resources → JDBC → JDBC Connection Pools → [Pool]
→ Ping
```

---

**Problem 5: "Access denied for user"**

**Fehlermeldung:**
```
SQLException: Access denied for user 'root'@'localhost'
```

**Ursache:**

Falscher Username/Password in Pool-Config.

**Lösung:**

```
Admin Console → Resources → JDBC → JDBC Connection Pools → [Pool] → Edit
→ Additional Properties
→ User: [korrekter Username]
→ Password: [korrektes Password]
```

Dann "Save" und "Ping" zum Testen.

---

**Problem 6: "Unknown database"**

**Fehlermeldung:**
```
SQLException: Unknown database 'mywebapp'
```

**Ursache:**

Datenbank existiert nicht.

**Lösung:**

Erstelle die Datenbank in MariaDB:

```sql
CREATE DATABASE mywebapp CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Oder in phpMyAdmin:
```
http://localhost/phpmyadmin → New → Database name: mywebapp
```

---

## 📚 Resources & Links

**JDBC & Datasource:**
- JDBC Tutorial: https://docs.oracle.com/javase/tutorial/jdbc/
- DataSource JavaDoc: https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/javax/sql/DataSource.html
- JNDI Tutorial: https://docs.oracle.com/javase/tutorial/jndi/

**Connection Pooling:**
- HikariCP (beliebte Pool-Implementierung): https://github.com/brettwooldridge/HikariCP
- Apache Commons DBCP: https://commons.apache.org/proper/commons-dbcp/
- Connection Pool Best Practices: https://vladmihalcea.com/the-anatomy-of-connection-pooling/

**Payara Server:**
- Payara JDBC Configuration: https://docs.payara.fish/community/docs/documentation/payara-server/jdbc/
- Monitoring JDBC: https://docs.payara.fish/community/docs/documentation/payara-server/monitoring/

**Security:**
- SQL Injection Prevention: https://owasp.org/www-community/attacks/SQL_Injection
- Prepared Statements: https://www.baeldung.com/java-statement-preparedstatement

---

## 💬 Feedback?

War Tag 9 hilfreich? Zu viel Theorie? Mehr Praxis-Beispiele gewünscht?

Schreib uns: feedback@java-developer.online

Wir wollen, dass du erfolgreich lernst!

---

**Bis morgen - zum finalen Tag!** 👋

Elyndra

*elyndra@java-developer.online*  
*Senior Developer bei Java Fleet Systems Consulting*

---

**Java Web Basic - Tag 9 von 10**  
*Teil der Java Fleet Learning-Serie*  
*© 2025 Java Fleet Systems Consulting*
