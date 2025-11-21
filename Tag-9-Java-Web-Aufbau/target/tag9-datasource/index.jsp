<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tag 9 - Datasource Beispiel</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }
        
        .container {
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            padding: 50px;
            max-width: 800px;
            width: 100%;
        }
        
        h1 {
            color: #333;
            font-size: 2.5em;
            margin-bottom: 10px;
            text-align: center;
        }
        
        .subtitle {
            color: #666;
            text-align: center;
            margin-bottom: 40px;
            font-size: 1.1em;
        }
        
        .features {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 40px;
        }
        
        .feature {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 10px;
            text-align: center;
        }
        
        .feature h3 {
            color: #667eea;
            margin-bottom: 10px;
        }
        
        .feature p {
            color: #666;
            font-size: 0.9em;
        }
        
        .links {
            display: flex;
            gap: 20px;
            justify-content: center;
            flex-wrap: wrap;
        }
        
        .btn {
            display: inline-block;
            padding: 15px 30px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            text-decoration: none;
            border-radius: 50px;
            font-weight: bold;
            transition: transform 0.3s, box-shadow 0.3s;
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
        }
        
        .btn:hover {
            transform: translateY(-3px);
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
        }
        
        .footer {
            text-align: center;
            margin-top: 40px;
            color: #999;
            font-size: 0.9em;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🚀 Tag 9 - Datasource & Connection Pools</h1>
        <p class="subtitle">Java Web Basic Kurs - Praktisches Beispiel</p>
        
        <div class="features">
            <div class="feature">
                <h3>✅ Connection Pooling</h3>
                <p>Professionelle Wiederverwendung von DB-Connections</p>
            </div>
            <div class="feature">
                <h3>🎯 DAO-Pattern</h3>
                <p>Saubere Trennung von Business-Logik und Datenzugriff</p>
            </div>
            <div class="feature">
                <h3>🔒 PreparedStatements</h3>
                <p>SQL-Injection-Schutz</p>
            </div>
            <div class="feature">
                <h3>🏗️ Model 2 Architecture</h3>
                <p>MVC-Pattern in der Praxis</p>
            </div>
        </div>
        
        <div class="links">
            <a href="users" class="btn">👥 User-Verwaltung</a>
            <a href="blog" class="btn">📝 Blog-System</a>
        </div>
        
        <div class="footer">
            <p><strong>Java Web Basic - Tag 9 von 10</strong></p>
            <p>Von Elyndra Valen, Senior Developer bei Java Fleet Systems Consulting</p>
            <p style="margin-top: 10px;">© 2025 Java Fleet Systems Consulting</p>
        </div>
    </div>
</body>
</html>
