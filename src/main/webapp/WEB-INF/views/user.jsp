<%@ page contentType="text/html;charset=UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="pw" tagdir="/WEB-INF/tags" %>
<%@ include file="/WEB-INF/views/_top.jspf" %>
<c:choose>

<c:when test="${tab == 'dashboard'}">
  <h2>My dashboard</h2>
  <div class="grid">
    <div class="card"><div class="stat"><pw:money value="${summary.income}"/></div><div class="mute">Monthly income</div></div>
    <div class="card"><div class="stat"><pw:money value="${summary.spent}"/></div><div class="mute">Spent this month</div></div>
    <div class="card"><div class="stat ${summary.deficit ? 'bad' : 'good'}"><pw:money value="${summary.left}"/></div><div class="mute">Left to save</div></div>
  </div>
  <c:forEach var="a" items="${alerts}"><div class="card err-box" role="alert"><c:out value="${a}"/></div></c:forEach>
  <div class="card"><h3>Budget status</h3><pw:budgets items="${budgets}"/></div>
</c:when>

<c:when test="${tab == 'expenses'}">
  <h2>Expense tracker</h2>
  <div class="card">
    <form method="post" action="${ctx}/user">
      <input type="hidden" name="_csrf" value="${sessionScope.csrf}"><input type="hidden" name="action" value="addExpense"><input type="hidden" name="tab" value="expenses">
      <div class="row">
        <select name="category" aria-label="Category"><c:forEach var="c" items="${categories}"><option><c:out value="${c}"/></option></c:forEach></select>
        <input name="amount" type="number" min="1" step="0.01" placeholder="Amount" aria-label="Amount" required>
        <input name="date" type="date" value="${today}" aria-label="Date">
        <button class="b">Add expense</button>
      </div>
    </form>
  </div>
  <div class="card"><h3>Spending by category (this month)</h3><pw:bars items="${bars}"/></div>
  <div class="card sc">
    <table><tr><th>Date</th><th>Category</th><th>Amount</th><th></th></tr>
      <c:forEach var="e" items="${expenses}">
        <tr><td>${e.date}</td><td><c:out value="${e.category}"/></td><td><pw:money value="${e.amount}"/></td>
          <td><form class="inline" method="post" action="${ctx}/user">
            <input type="hidden" name="_csrf" value="${sessionScope.csrf}"><input type="hidden" name="action" value="deleteExpense"><input type="hidden" name="tab" value="expenses"><input type="hidden" name="id" value="${e.id}">
            <button class="g d">Delete</button></form></td></tr>
      </c:forEach>
    </table>
    <c:if test="${empty expenses}"><p class="mute">No expenses yet.</p></c:if>
  </div>
</c:when>

<c:when test="${tab == 'budgets'}">
  <h2>Budget overview</h2>
  <div class="card">
    <form method="post" action="${ctx}/user">
      <input type="hidden" name="_csrf" value="${sessionScope.csrf}"><input type="hidden" name="action" value="setBudget"><input type="hidden" name="tab" value="budgets">
      <div class="row">
        <select name="category" aria-label="Category"><c:forEach var="c" items="${categories}"><option><c:out value="${c}"/></option></c:forEach></select>
        <input name="amount" type="number" min="1" step="0.01" placeholder="Monthly budget" aria-label="Budget amount" required>
        <button class="b">Set budget</button>
      </div>
    </form>
  </div>
  <div class="card"><pw:budgets items="${budgets}"/></div>
</c:when>

<c:when test="${tab == 'advice'}">
  <h2>Financial advice</h2>
  <div class="card">
    <h3>Ask your advisor</h3>
    <form method="post" action="${ctx}/user">
      <input type="hidden" name="_csrf" value="${sessionScope.csrf}"><input type="hidden" name="action" value="askAdvice"><input type="hidden" name="tab" value="advice">
      <textarea name="content" maxlength="1000" placeholder="Describe your goal or question" required></textarea>
      <button class="b">Request advice</button>
    </form>
  </div>
  <div class="card">
    <h3>Advice for you</h3>
    <c:forEach var="a" items="${adviceList}"><p><span class="tag"><c:out value="${a.createdAt}"/></span> <c:out value="${a.content}"/></p></c:forEach>
    <c:if test="${empty adviceList}"><p class="mute">No advice yet. Send a request above.</p></c:if>
  </div>
  <div class="card">
    <h3>Quick tips from your numbers</h3>
    <ul><c:forEach var="t" items="${tips}"><li><c:out value="${t}"/></li></c:forEach></ul>
  </div>
  <div class="card sc">
    <h3>Your conversation</h3>
    <table><tr><th>When</th><th>From</th><th>Message</th></tr>
      <c:forEach var="m" items="${messages}"><tr><td><c:out value="${m.createdAt}"/></td><td><c:out value="${m.senderRole}"/></td><td><c:out value="${m.content}"/></td></tr></c:forEach>
    </table>
    <c:if test="${empty messages}"><p class="mute">No messages yet.</p></c:if>
  </div>
</c:when>

<c:when test="${tab == 'reports'}">
  <h2>Financial reports</h2>
  <div class="card">
    <p>This month: income <pw:money value="${summary.income}"/>, expenses <pw:money value="${summary.spent}"/>, savings rate <b>${summary.savingsRate}%</b>.</p>
    <h3>Expenses by category</h3>
    <pw:bars items="${bars}"/>
    <div class="row"><a class="b" style="text-decoration:none;display:inline-block;color:#fff;padding:8px 14px;border-radius:6px;background:var(--acc)" href="${ctx}/user?tab=reports&amp;export=csv">Download all expenses (CSV)</a></div>
  </div>
</c:when>

<c:when test="${tab == 'profile'}">
  <h2>Profile</h2>
  <div class="card">
    <form method="post" action="${ctx}/user">
      <input type="hidden" name="_csrf" value="${sessionScope.csrf}"><input type="hidden" name="action" value="saveProfile"><input type="hidden" name="tab" value="profile">
      <div class="row">
        <label>Name <input name="name" value="<c:out value='${me.name}'/>" maxlength="80" required></label>
        <label>Email <input name="email" type="email" value="<c:out value='${me.email}'/>" required></label>
        <label>Monthly income <input name="income" type="number" min="0" step="0.01" value="${me.monthlyIncome}"></label>
      </div>
      <button class="b">Save profile</button>
    </form>
  </div>
</c:when>

<c:when test="${tab == 'feedback'}">
  <h2>Send feedback</h2>
  <div class="card">
    <form method="post" action="${ctx}/user">
      <input type="hidden" name="_csrf" value="${sessionScope.csrf}"><input type="hidden" name="action" value="sendFeedback"><input type="hidden" name="tab" value="feedback">
      <textarea name="content" maxlength="1000" placeholder="Tell the admin what to improve, or report a problem" required></textarea>
      <button class="b">Send feedback</button>
    </form>
  </div>
</c:when>

</c:choose>
<%@ include file="/WEB-INF/views/_bottom.jspf" %>
