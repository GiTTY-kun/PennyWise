<%@ page contentType="text/html;charset=UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Under maintenance</title><link rel="stylesheet" href="${ctx}/static/style.css"></head>
<body><div class="login"><div class="card">
<h2>Under maintenance</h2>
<p>The platform is paused by the admin. Please check back soon.</p>
<form method="post" action="${ctx}/logout"><button class="g">Sign out</button></form>
</div></div></body></html>
