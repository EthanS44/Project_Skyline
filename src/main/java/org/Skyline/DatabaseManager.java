package org.Skyline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.sql.*;


@Controller
public class DatabaseManager {

    private static final String url = "postgresql://viaduct.proxy.rlwy.net:17205/railway";
    private static final String user = "postgres";
    private static final String password = "SqyAeJuIfQNzpFFxyxczlwTlrcYCwUEc";


    public void saveModel(Model model){
        // save model
        try{
            Connection connection = DriverManager.getConnection(url, user, password);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public saveUser()
}
