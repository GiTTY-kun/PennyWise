package com.pennywise.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Opens JDBC connections using the settings in db.properties. */
public final class DBUtil {
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new ExceptionInInitializerError("db.properties not found on the classpath");
            }
            PROPS.load(in);
            Class.forName("com.mysql.cj.jdbc.Driver");   // load the MySQL JDBC driver
        } catch (IOException | ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private DBUtil() { }

    /** Callers must close the connection (use try-with-resources). */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                PROPS.getProperty("db.url"), PROPS.getProperty("db.user"), PROPS.getProperty("db.password"));
    }
}
