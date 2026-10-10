<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Error</title><link rel="stylesheet" href="${pageContext.request.contextPath}/static/style.css"></head>
<body><div class="login"><div class="card">
<h2>Something went wrong</h2>
<p class="mute">The request could not be completed. Please go back and try again.</p>
<a href="${pageContext.request.contextPath}/login">Back to sign in</a>
</div></div></body></html>
