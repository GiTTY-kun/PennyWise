<%@ page contentType="text/html;charset=UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="pw" tagdir="/WEB-INF/tags" %>
<%@ include file="/WEB-INF/views/_top.jspf" %>
<c:choose>

<c:when test="${tab == 'dashboard'}">
  <h2>Admin dashboard</h2>
  <div class="grid">
    <div class="card"><div class="stat">${accountCount}</div><div class="mute">Accounts</div></div>
    <div class="card"><div class="stat"><pw:money value="${totalSpend}"/></div><div class="mute">Total tracked spend</div></div>
    <div class="card"><div class="stat">${openFeedback}</div><div class="mute">Open feedback tickets</div></div>
  </div>
  <div class="card"><h3>Financial overview: spend by category</h3><pw:bars items="${bars}"/></div>
</c:when>

<c:when test="${tab == 'users'}">
  <h2>User management</h2>
  <div class="card">
    <h3>Add user</h3>
    <form method="post" action="${ctx}/admin">
      <input type="hidden" name="_csrf" value="${sessionScope.csrf}"><input type="hidden" name="action" value="addUser"><input type="hidden" name="tab" value="users">
      <div class="row">
        <input name="name" placeholder="Name" aria-label="Name" required>
        <input name="email" type="email" placeholder="Email" aria-label="Email" required>
        <input name="password" type="password" placeholder="Password" aria-label="Password" required>
        <select name="role" aria-label="Role"><c:forEach var="r" items="${roles}"><option value="${r}"><c:out value="${r.label}"/></option></c:forEach></select>
        <button class="b">Add user</button>
      </div>
    </form>
  </div>
  <div class="card sc">
    <table>
      <tr><th>Name</th><th>Email</th><th>Role</th><th></th><th></th></tr>
      <c:forEach var="u" items="${users}">
        <tr>
          <td><form method="post" action="${ctx}/admin" id="u${u.id}"> <input type="hidden" name="_csrf" value="${sessionScope.csrf}"><input type="hidden" name="action" value="updateUser"><input type="hidden" name="tab" value="users"><input type="hidden" name="id" value="${u.id}"> </form><input form="u${u.id}" name="name" value="<c:out value='${u.name}'/>" aria-label="Name"></td>
          <td><c:out value="${u.email}"/></td>
          <td><select form="u${u.id}" name="role" aria-label="Role">
            <c:forEach var="r" items="${roles}"><option value="${r}" ${r == u.role ? 'selected' : ''}><c:out value="${r.label}"/></option></c:forEach>
          </select></td>
          <td><button form="u${u.id}" class="g">Save</button></td>
          <td>
            <form class="inline" method="post" action="${ctx}/admin" onsubmit="return confirm('Delete this user and all their data?')">
              <input type="hidden" name="_csrf" value="${sessionScope.csrf}"><input type="hidden" name="action" value="deleteUser"><input type="hidden" name="tab" value="users"><input type="hidden" name="id" value="${u.id}">
              <button class="g d">Delete</button>
            </form>
          </td>
        </tr>
      </c:forEach>
    </table>
  </div>
</c:when>

<c:when test="${tab == 'security'}">
  <h2>Financial data security</h2>
  <div class="card">
    <form method="post" action="${ctx}/admin">
      <input type="hidden" name="_csrf" value="${sessionScope.csrf}"><input type="hidden" name="action" value="saveSecurity"><input type="hidden" name="tab" value="security">
      <div class="sw"><input type="checkbox" id="s1" name="twofa" ${settings.twofa == 'true' ? 'checked' : ''}><label for="s1">Require two-factor sign-in</label></div>
      <div class="sw"><input type="checkbox" id="s2" name="encrypt" ${settings.encrypt == 'true' ? 'checked' : ''}><label for="s2">Encrypt stored financial data</label></div>
      <div class="row">
        <label>Session timeout (min) <input name="timeout" type="number" min="1" max="240" value="<c:out value='${settings.timeout}'/>" style="width:90px"></label>
        <label>Minimum password length <input name="minpw" type="number" min="6" max="64" value="<c:out value='${settings.minpw}'/>" style="width:90px"></label>
      </div>
      <button class="b">Save security settings</button>
    </form>
  </div>
  <div class="card sc">
    <h3>Security report</h3>
    <table><tr><th>When</th><th>Event</th></tr>
      <c:forEach var="l" items="${report}"><tr><td><c:out value="${l.createdAt}"/></td><td><c:out value="${l.event}"/></td></tr></c:forEach>
    </table>
  </div>
</c:when>

<c:when test="${tab == 'system'}">
  <h2>System settings</h2>
  <div class="card">
    <form method="post" action="${ctx}/admin">
      <input type="hidden" name="_csrf" value="${sessionScope.csrf}"><input type="hidden" name="action" value="saveSystem"><input type="hidden" name="tab" value="system">
      <div class="row">
        <label>Platform name <input name="sysname" value="<c:out value='${settings.sysname}'/>" maxlength="30"></label>
        <label>Currency
          <select name="currency"><c:forEach var="cur" items="${currencies}"><option ${cur == settings.currency ? 'selected' : ''}><c:out value="${cur}"/></option></c:forEach></select>
        </label>
      </div>
      <div class="sw"><input type="checkbox" id="y3" name="maintenance" ${settings.maintenance == 'true' ? 'checked' : ''}><label for="y3">Maintenance mode (blocks non-admin access)</label></div>
      <button class="b">Save system settings</button>
    </form>
  </div>
</c:when>

<c:when test="${tab == 'feedback'}">
  <h2>User feedback and support tickets</h2>
  <div class="card sc">
    <table>
      <tr><th>When</th><th>From</th><th>Message</th><th>Status</th><th></th></tr>
      <c:forEach var="f" items="${feedback}">
        <tr>
          <td><c:out value="${f.createdAt}"/></td>
          <td><c:out value="${names[f.userId]}"/></td>
          <td><c:out value="${f.content}"/></td>
          <td><c:out value="${f.status}"/></td>
          <td><c:if test="${f.open}">
            <form class="inline" method="post" action="${ctx}/admin">
              <input type="hidden" name="_csrf" value="${sessionScope.csrf}"><input type="hidden" name="action" value="resolveFeedback"><input type="hidden" name="tab" value="feedback"><input type="hidden" name="id" value="${f.id}">
              <button class="g">Mark resolved</button>
            </form></c:if></td>
        </tr>
      </c:forEach>
    </table>
    <c:if test="${empty feedback}"><p class="mute">No feedback yet.</p></c:if>
  </div>
</c:when>

</c:choose>
<%@ include file="/WEB-INF/views/_bottom.jspf" %>
