<%--
  Created by IntelliJ IDEA.
  User: Josue
  Date: 02/07/2016
  Time: 01:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <meta http-equiv="content-language" content="en"/>
    <meta name="robots" content="noindex,nofollow"/>
    <link rel="stylesheet" media="screen,projection" type="text/css" href="css/main.css"/>
    <link rel="stylesheet" media="screen,projection" type="text/css" href="css/style.css"/>
    <!-- WRITE YOUR CSS CODE HERE -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/react/15.1.0/react.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/react/15.1.0/react-dom.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/babel-core/5.6.16/browser.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/2.2.2/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/remarkable/1.6.2/remarkable.min.js"></script>

    <title>Service registry</title>

    <script>var root = "${pageContext.request.contextPath}"</script>
</head>
<body>
    <div id="mainComponent"></div>
    <script type="text/babel" src="app/app.jsx"></script>
</body>
</html>
