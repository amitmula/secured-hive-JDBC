package com.some.jdbc;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class App {

    private static final String DRIVER_NAME = "org.apache.hive.jdbc.HiveDriver";
    private static final String REALM_NAME = "IDW.COM";
    private static final String KDC_SERVER = "192.168.145.144";
    private static final String USER_NAME = "impadmin/impetus-d917v1.impetus.co.in@IDW.COM";
    private static final String PASSWORD = "impetus";
    private static final String JDBC_DB_URL = "jdbc:hive2://impetus-d917v3.impetus.co.in:10000/default;principal=hive/impetus-d917v3.impetus.co.in@IDW.COM;auth=kerberos;kerberosAuthType=fromSubject";

    public App() {}

    public List<String> getTableNames() throws LoginException, PrivilegedActionException, SQLException {
        Subject blendUser = doSecureLogin();
        Connection connection = getConnection(blendUser);
        Statement stmt = connection.createStatement();
        String query = "show tables";
        ResultSet resultSet = stmt.executeQuery(query);
        List<String> tablesNames = new ArrayList();
        while(resultSet.next())
            tablesNames.add(resultSet.getString(1));
        stmt.close();
        resultSet.close();
        connection.close();
        return tablesNames;
    }

    private static Connection getConnection(Subject signedOnUserSubject) throws PrivilegedActionException {
        return (Connection) Subject.doAs(signedOnUserSubject, new PrivilegedExceptionAction<Object>()
        {
            public Object run()
            {
                Connection con = null;
                try {
                    Class.forName(DRIVER_NAME);
                    con =  DriverManager.getConnection(JDBC_DB_URL);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return con;
            }
        });
    }

    private static Subject doSecureLogin() throws LoginException {
        /* Set sun.security.krb5.debug to true for KDC verbose mode.
        System.setProperty("sun.security.krb5.debug", "true");*/
        System.setProperty("java.security.krb5.realm", REALM_NAME);
        System.setProperty("java.security.krb5.kdc", KDC_SERVER);
        System.setProperty("java.security.auth.login.config",
             Thread.currentThread().getContextClassLoader().getResource("jaas.conf").getFile());
        System.setProperty("javax.security.auth.useSubjectCredsOnly", "true");
        System.setProperty("sun.security.krb5.debug", "true");
        LoginContext loginContext = new LoginContext("Client", new LoginCallbackHandler(USER_NAME, PASSWORD));
        loginContext.login();
        return loginContext.getSubject();
    }
}
