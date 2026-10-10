<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="items" required="true" type="java.util.List" %>
<%@ attribute name="counts" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="pw" tagdir="/WEB-INF/tags" %>
<c:choose>
  <c:when test="${empty items}"><p class="mute">Nothing to show yet.</p></c:when>
  <c:otherwise>
    <c:forEach var="b" items="${items}">
      <div class="br"><span><c:out value="${b.label}"/></span>
        <div class="bar"><i style="width:${b.percent}%"></i></div>
        <b><c:choose><c:when test="${counts}">${b.value}</c:when><c:otherwise><pw:money value="${b.value}"/></c:otherwise></c:choose></b></div>
    </c:forEach>
  </c:otherwise>
</c:choose>
