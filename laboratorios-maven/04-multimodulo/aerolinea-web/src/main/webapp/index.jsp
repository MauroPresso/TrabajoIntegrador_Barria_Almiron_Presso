<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ar.edu.ifes.aerolinea.core.ResumenAerolinea" %>
<%
    ResumenAerolinea resumen = new ResumenAerolinea();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>SistemaDeAerolinea - Maven WAR</title>
</head>
<body>
    <h1>SistemaDeAerolinea</h1>
    <p><%= resumen.estadoGeneral() %></p>
    <p>Modulo web empaquetado como WAR y ejecutado con Jetty.</p>
</body>
</html>