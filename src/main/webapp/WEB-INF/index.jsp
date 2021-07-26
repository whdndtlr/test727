<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Insert title here</title>
<link href="/css/style.css" type="text/css" rel="stylesheet" />
</head>

<body>
<jsp:include page="/WEB-INF/home/home.jsp"/>
<jsp:include page="/WEB-INF/home/nav.jsp"/>
<jsp:include page="/WEB-INF${section}"/>
</body>
</html>