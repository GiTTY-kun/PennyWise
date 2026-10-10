<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="value" required="true" type="java.lang.Object" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:out value="${applicationScope.currency}"/><fmt:formatNumber value="${value}" minFractionDigits="0" maxFractionDigits="2"/>