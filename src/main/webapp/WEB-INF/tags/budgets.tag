<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="items" required="true" type="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="pw" tagdir="/WEB-INF/tags" %>
<c:choose>
  <c:when test="${empty items}"><p class="mute">No budgets set yet.</p></c:when>
  <c:otherwise>
    <c:forEach var="b" items="${items}">
      <div class="br"><span><c:out value="${b.category}"/></span>
        <div class="bar"><i class="${b.over ? 'over' : ''}" style="width:${b.percent}%"></i></div>
        <span><pw:money value="${b.spent}"/> / <pw:money value="${b.limit}"/></span></div>
    </c:forEach>
  </c:otherwise>
</c:choose>
