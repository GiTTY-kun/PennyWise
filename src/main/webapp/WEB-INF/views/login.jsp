<%@ page contentType="text/html;charset=UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Sign in</title>
<link rel="stylesheet" href="${ctx}/static/style.css">
</head>
<body>
<div class="login"><div class="card">
  <div class="brand"><c:out value="${empty sysName ? 'PennyWise' : sysName}"/></div>
  <h2>Sign in</h2>
  <p class="mute">Online Personal Finance Management Platform</p>
  <c:if test="${not empty error}"><p class="bad" role="alert"><c:out value="${error}"/></p></c:if>
  <form method="post" action="${ctx}/login">
    <div class="row"><input name="email" type="email" placeholder="Email" aria-label="Email" value="<c:out value='${email}'/>" style="flex:1" required></div>
    <div class="row"><input name="password" type="password" placeholder="Password" aria-label="Password" style="flex:1" required></div>
    <button class="b">Sign in</button>
  </form>
  <p class="mute">Demo accounts (password <b>pennywise123</b>): admin@pennywise.app, rohan@pennywise.app, priya@mail.com</p>
</div></div>
</body>
</html>
