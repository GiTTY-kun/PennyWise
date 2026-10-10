<%@ page contentType="text/html;charset=UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="pw" tagdir="/WEB-INF/tags" %>
<%@ include file="/WEB-INF/views/_top.jspf" %>
<c:choose>

<c:when test="${tab == 'dashboard'}">
  <h2>Advisor dashboard</h2>
  <div class="grid">
    <div class="card"><div class="stat">${clientCount}</div><div class="mute">Clients</div></div>
    <div class="card"><div class="stat">${adviceCount}</div><div class="mute">Advice given</div></div>
    <div class="card"><div class="stat">${messageCount}</div><div class="mute">Messages</div></div>
  </div>
  <div class="card"><h3>Interaction statistics (messages per client)</h3><pw:bars items="${stats}" counts="${true}"/></div>
</c:when>

<c:when test="${tab == 'advice'}">
  <h2>Provide financial advice</h2>
  <div class="card">
    <form method="post" action="${ctx}/advisor">
      <input type="hidden" name="_csrf" value="${sessionScope.csrf}"><input type="hidden" name="action" value="sendAdvice"><input type="hidden" name="tab" value="advice">
      <div class="row"><select name="userId" aria-label="Client"><c:forEach var="c" items="${clients}"><option value="${c.id}"><c:out value="${c.name}"/></option></c:forEach></select></div>
      <textarea name="content" maxlength="1000" placeholder="Write personalised advice based on the client's data" required></textarea>
      <button class="b">Send advice</button>
    </form>
  </div>
  <div class="card">
    <h3>Client snapshot (this month)</h3>
    <c:forEach var="c" items="${clients}">
      <c:set var="s" value="${summaries[c.id]}"/>
      <p><b><c:out value="${c.name}"/></b>: income <pw:money value="${s.income}"/>, spent <pw:money value="${s.spent}"/>,
        left <span class="${s.deficit ? 'bad' : 'good'}"><pw:money value="${s.left}"/></span>, savings rate ${s.savingsRate}%</p>
      <ul class="mute"><c:forEach var="t" items="${tips[c.id]}"><li><c:out value="${t}"/></li></c:forEach></ul>
    </c:forEach>
  </div>
</c:when>

<c:when test="${tab == 'interaction'}">
  <h2>User interaction</h2>
  <div class="card">
    <form method="post" action="${ctx}/advisor">
      <input type="hidden" name="_csrf" value="${sessionScope.csrf}"><input type="hidden" name="action" value="sendMessage"><input type="hidden" name="tab" value="interaction">
      <div class="row"><select name="userId" aria-label="Client"><c:forEach var="c" items="${clients}"><option value="${c.id}"><c:out value="${c.name}"/></option></c:forEach></select></div>
      <textarea name="content" maxlength="1000" placeholder="Reply to a client" required></textarea>
      <button class="b">Send message</button>
    </form>
  </div>
  <div class="card sc">
    <table><tr><th>When</th><th>Client</th><th>From</th><th>Message</th></tr>
      <c:forEach var="m" items="${messages}"><tr><td><c:out value="${m.createdAt}"/></td><td><c:out value="${names[m.userId]}"/></td><td><c:out value="${m.senderRole}"/></td><td><c:out value="${m.content}"/></td></tr></c:forEach>
    </table>
    <c:if test="${empty messages}"><p class="mute">No messages yet.</p></c:if>
  </div>
</c:when>

<c:when test="${tab == 'progress'}">
  <h2>User progress</h2>
  <c:forEach var="c" items="${clients}">
    <div class="card"><h3><c:out value="${c.name}"/></h3><pw:budgets items="${progress[c.id]}"/></div>
  </c:forEach>
  <c:if test="${empty clients}"><p class="mute">No clients yet.</p></c:if>
</c:when>

<c:when test="${tab == 'history'}">
  <h2>Advice history</h2>
  <div class="card sc">
    <table><tr><th>When</th><th>Client</th><th>Advice</th></tr>
      <c:forEach var="a" items="${history}"><tr><td><c:out value="${a.createdAt}"/></td><td><c:out value="${names[a.userId]}"/></td><td><c:out value="${a.content}"/></td></tr></c:forEach>
    </table>
    <c:if test="${empty history}"><p class="mute">No advice given yet.</p></c:if>
  </div>
</c:when>

</c:choose>
<%@ include file="/WEB-INF/views/_bottom.jspf" %>
