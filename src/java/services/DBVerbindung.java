/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import java.sql.*;


/**
 * Klasse zur Verwaltung der Verbindung mit der Datenbank
 * 
 * @author Annika Petry
 * @author Timo Flake
 */
public class DBVerbindung {
    public static DBVerbindung dbVerbindung = new DBVerbindung();
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    
    /**
     * Methode fuer den Verbindungsaufbau mit der Datenbank
     * 
     */
    public DBVerbindung(){
        try{
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            connection = DriverManager.getConnection("jdbc:derby://localhost:1527/Tippspiel");
            statement = connection.createStatement();
        } 
        catch(SQLException | ClassNotFoundException e){
            System.out.println(e);
        }
    }
    
    
    
    /**
     * Getter Statement
     * @return Statement
     */
    public Statement getStatement(){
        return statement;
    }
    
    
    
    /**
     * Getter ResultSet
     * @return 
     */
    public ResultSet getResultSet(){
        return resultSet;
    }
    
    
    
    /**
     * Setter ResultSet
     * @param setter 
     */
    public void setResultSet(ResultSet setter){
        resultSet = setter;
    }
    
}
