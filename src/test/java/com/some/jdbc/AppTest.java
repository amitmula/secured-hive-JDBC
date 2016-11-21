package com.some.jdbc;

import java.util.List;

import static org.junit.Assert.*;

public class AppTest {
    @org.junit.Test
    public void getTableNamesTest() throws Exception {
        App app = new App();
        List<String> tableNames =  app.getTableNames();
        assertFalse(tableNames.isEmpty());
        assertEquals(tableNames.toString(), "[dw_daily_exchange_rate, employee, employeetest, newdemo, test, testhivedrivertable]");
    }
}