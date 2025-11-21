<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Blog-Liste - Tag 9 Datasource</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #f5f5f5;
            padding: 20px;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            padding: 30px;
        }

        h1 {
            color: #333;
            margin-bottom: 10px;
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
            flex-wrap: wrap;
            gap: 20px;
        }

        .search-box {
            display: flex;
            gap: 10px;
        }

        .search-box input {
            padding: 10px 15px;
            border: 2px solid #ddd;
            border-radius: 5px;
            font-size: 1em;
            width: 300px;
        }

        .btn {
            padding: 10px 20px;
            background: #667eea;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            border: none;
            cursor: pointer;
            font-size: 1em;
            transition: background 0.3s;
        }

        .btn:hover {
            background: #5568d3;
        }

        .stats {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        th, td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }

        th {
            background: #667eea;
            color: white;
            font-weight: 600;
        }

        tr:hover {
            background: #f8f9fa;
        }

        .blog-link {
            color: #667eea;
            text-decoration: none;
            font-weight: 500;
        }

        .blog-link:hover {
            text-decoration: underline;
        }

        .back-link {
            display: inline-block;
            margin-top: 20px;
            color: #667eea;
            text-decoration: none;
        }

        .back-link:hover {
            text-decoration: underline;
        }

        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #999;
        }

        .empty-state h2 {
            margin-bottom: 10px;
        }

        .content-preview {
            color: #666;
            font-size: 0.9em;
            max-width: 300px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .date {
            color: #888;
            font-size: 0.9em;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <div>
                <h1>📝 Blog-Verwaltung</h1>
                <p style="color: #666;">Datasource-Beispiel mit Connection Pool</p>
            </div>

            <form action="blogs" method="get" class="search-box">
                <input type="text" name="search"
                       placeholder="Blog durchsuchen..."
                       value="${searchQuery}">
                <button type="submit" class="btn">🔍 Suchen</button>
            </form>
        </div>

        <div class="stats">
            <strong>Anzahl Beiträge:</strong> ${blogCount}
            <c:if test="${not empty searchQuery}">
                | Suche nach: <strong>${searchQuery}</strong>
            </c:if>
        </div>

        <c:choose>
            <c:when test="${empty blogs}">
                <div class="empty-state">
                    <h2>Keine Blog-Beiträge gefunden</h2>
                    <p>Es wurden keine Beiträge gefunden, die deinen Suchkriterien entsprechen.</p>
                </div>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Titel</th>
                            <th>Autor</th>
                            <th>Erstellt am</th>
                            <th>Aktion</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="blog" items="${blogs}">
                            <tr>
                                <td>${blog.id}</td>
                                <td>
                                    <a href="blogs?id=${blog.id}" class="blog-link">
                                        ${blog.title}
                                    </a>
                                </td>
                                <td>${blog.author}</td>
                                <td class="date">
                                    ${blog.createdAt}
                                </td>
                                <td>
                                    <a href="blogs?id=${blog.id}" class="btn">
                                        Details anzeigen
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>

        <a href="index.jsp" class="back-link">← Zurück zur Startseite</a>
    </div>
</body>
</html>
