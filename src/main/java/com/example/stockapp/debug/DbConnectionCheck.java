package com.example.stockapp.debug;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class DbConnectionCheck {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void checkDb() throws Exception {
        try (var c = dataSource.getConnection();
             var s = c.createStatement();
             var rs = s.executeQuery(
                     "select inet_server_addr(), inet_server_port()")) {

            while (rs.next()) {
                System.out.println(
                        "Spring Boot DB = "
                                + rs.getString(1)
                                + ":" + rs.getInt(2)
                );
            }
        }
    }
}
