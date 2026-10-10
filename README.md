# PennyWise - Online Personal Finance Management Platform

A Java web application (Servlets + JSP + JDBC + MySQL) where users track expenses and set budgets,
financial advisors give personalised advice, and admins manage users, data security and system settings.

## Features

| Role | What they can do |
|------|------------------|
| **User** | Track expenses, set monthly budgets, see budget progress and over-budget alerts, ask an advisor for advice and read replies, view reports and download expenses as CSV, edit profile, send feedback |
| **Financial Advisor** | Write personalised advice using each client's income/spending snapshot, message clients, track client budget progress, see advice history |
| **Admin** | Create / rename / change role / delete users, configure security settings and view the security report, configure system settings (name, currency, maintenance mode), resolve feedback tickets, see a platform-wide spending chart |

## Requirements

- JDK 11 or newer
- Apache Maven 3.6+
- MySQL 8
- Apache Tomcat 9 (uses `javax.servlet`; Tomcat 10+ will NOT work without changing imports to `jakarta.*`)

## How to run

1. **Create the database**
   ```
   mysql -u root -p < sql/schema.sql
   ```
2. **Set your MySQL login** in `src/main/resources/db.properties` (`db.user`, `db.password`).
3. **Build the WAR**
   ```
   mvn clean package
   ```
4. **Deploy**: copy `target/pennywise.war` into Tomcat's `webapps/` folder and start Tomcat
   (or add the project to Eclipse / IntelliJ with a Tomcat 9 run configuration).
5. Open <http://localhost:8080/pennywise/>

On the first start the app inserts demo accounts and sample data automatically.

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@pennywise.app | pennywise123 |
| Financial Advisor | rohan@pennywise.app | pennywise123 |
| User | priya@mail.com | pennywise123 |
| User | karan@mail.com | pennywise123 |

## Project structure

```
src/main/java/com/pennywise/
  model/      User (abstract) -> AdminUser, AdvisorUser, RegularUser; Role enum; Dashboardable interface;
              Expense, Budget, Advice, Message, Feedback, ...
  exception/  AppException -> ValidationException, AuthenticationException, DataAccessException
  dao/        Dao<T,ID> (generic interface), AbstractJdbcDao<T> (all JDBC code), one DAO per table
  service/    UserService, FinanceService, AdvisoryService, SystemService, BudgetMonitor (thread), DemoData
  util/       DBUtil, PasswordUtil, Validator, Bars, Categories
  web/        BaseServlet -> AdminServlet, AdvisorServlet, UserServlet; LoginServlet, LogoutServlet,
              AuthFilter, AppInitListener, AppContext
src/main/webapp/
  WEB-INF/views/   JSP pages (admin, advisor, user, login, maintenance) - not directly reachable by URL
  WEB-INF/tags/    reusable JSP tag files (money, bars, budgets)
  static/style.css
sql/schema.sql
```

Architecture (MVC with layers): **JSP (view) <- Servlet (controller) <- Service (business rules) <- DAO (JDBC) <- MySQL**.

## Where each Java concept is used

- **OOP**: abstract `User` with three subclasses; each overrides `getRole()`, `getDashboardPath()` and `getMenu()` (polymorphism);
  `User.create()` factory; `BaseServlet` is a template with three servlet subclasses; encapsulation with private fields and getters.
- **Interfaces**: `Dashboardable` (implemented by every user type), generic `Dao<T,ID>`, `ResultReader<R>`.
- **Generics and collections**: `Dao<T,ID>`, `AbstractJdbcDao<T>`, `Bars.of(Map<String,? extends Number>)`;
  `List`, `Map`, `LinkedHashMap`, `TreeMap`, `Optional`, streams and `Collectors.groupingBy`.
- **Exception handling**: custom exception hierarchy; JDBC `SQLException` wrapped in `DataAccessException`;
  servlets catch `AppException` and show the message to the user; try-with-resources closes connections.
- **Multithreading and synchronization**: `BudgetMonitor` runs on its own thread (`ScheduledExecutorService`),
  re-checks all budgets every 30 s and after each expense/budget change; the shared alert map is guarded with
  `synchronized` methods, and a run counter uses `AtomicInteger`.
- **JDBC**: `DBUtil` + `AbstractJdbcDao` use `PreparedStatement` for every query (no SQL injection),
  generated keys, aggregate queries (`SUM ... GROUP BY`), `ON DUPLICATE KEY UPDATE`, foreign keys with cascade.
- **Servlets and web integration**: `@WebServlet` controllers, `AuthFilter` (login check, role check, maintenance mode, CSRF token),
  `@WebListener` start-up, sessions (new session id on login, timeout from settings), Post/Redirect/Get with flash messages,
  JSP + JSTL views, CSV download.

## Security notes

- Passwords are stored as salted PBKDF2 hashes.
- All output is escaped with `<c:out>`; all SQL uses prepared statements; every POST needs a CSRF token.
- A user can only open the pages of their own role, and can only delete their own expenses.
- The admin can set the minimum password length and the session timeout, and both are enforced.
  The "two-factor" and "encrypt stored data" switches are saved as settings and appear in the security report,
  but this demo does not implement a second login factor or database encryption.

## Notes

- "Spent", "left to save" and budget progress always refer to the **current month**.
- Alerts on the user dashboard come from the background thread, so a new alert can take a moment to appear
  after you add an expense (the budget bars update immediately).
