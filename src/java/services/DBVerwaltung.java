/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Datenbankfunktionen zum Anmelden und Registrieren von Benutzern und zum 
 * Anmelden des Managers 
 * 
 * @author Annika Petry
 * @author Timo Flake
 */
public class DBVerwaltung {
    
    /**
     * Methode zur Pruefung, ob ein Benutzername entweder von einem anderen Benutzer oder
     * von einem Manager verwendet wird 
     * true, wenn der Benutzername noch nicht verwendet ist
     * 
     * @param dbVerbindung
     * @param benutzername
     * @return boolean
     * @throws SQLException 
     */
    public boolean benutzernamePruefen (DBVerbindung dbVerbindung, String benutzername) 
            throws SQLException{
        
        dbVerbindung.setResultSet(dbVerbindung.getStatement().executeQuery(
            "SELECT Benutzername FROM Benutzer"));
        
        while(dbVerbindung.getResultSet().next()){
            if(benutzername.equals(dbVerbindung.getResultSet().getString("Benutzername"))){
                return false;
            }  
        }
        dbVerbindung.setResultSet(dbVerbindung.getStatement().executeQuery(
            "SELECT Benutzername FROM Manager"));
        
        while(dbVerbindung.getResultSet().next()){
            if(benutzername.equals(dbVerbindung.getResultSet().getString("Benutzername"))){
                return false;
            }
        }
        return true;
    }
    
    
    
    /**
     * Methode zur Pruefung, ob Benutzername und Passwort zu einem registrierten
     * Benutzer passen
     * true, wenn Benutzername und Passwort zueinander passen (Benutzer)
     * 
     * @param dbVerbindung
     * @param benutzername
     * @param passwort
     * @return boolean
     * @throws SQLException 
     */
    public boolean anmeldungBenutzer(DBVerbindung dbVerbindung, String benutzername, String passwort) 
            throws SQLException{
        
        dbVerbindung.setResultSet(dbVerbindung.getStatement().executeQuery(
            "SELECT Benutzername, Passwort FROM Benutzer"));
        
        while(dbVerbindung.getResultSet().next()){
            if(benutzername.equals(dbVerbindung.getResultSet().getString("Benutzername"))){
                if(passwort.equals(dbVerbindung.getResultSet().getString("Passwort"))){
                    return true;
                }    
            }
        }
        return false;
    }
    
    
    
    /**
     * Methode zur Pruefung, ob Benutzername und Passwort zu einem registrierten
     * Manager passen
     * true, wenn Benutzername und Passwort zueinander passen (Manager)
     * 
     * @param dbVerbindung
     * @param benutzername
     * @param passwort
     * @return boolean
     * @throws SQLException 
     */
    public boolean anmeldungManager(DBVerbindung dbVerbindung, String benutzername, String passwort) 
            throws SQLException{
        
        dbVerbindung.setResultSet(dbVerbindung.getStatement().executeQuery(
            "SELECT Benutzername, Passwort FROM Manager"));
        
        while(dbVerbindung.getResultSet().next()){
            if(benutzername.equals(dbVerbindung.getResultSet().getString("Benutzername"))){
                if(passwort.equals(dbVerbindung.getResultSet().getString("Passwort"))){
                    return true;
                }
            }
        }
        
        return false;
    }
    
    
    
    /**
     * Methode, um einen Benutzer, der sich neu registriert hat, mit Passwort
     * in der Datenbank zu speichern
     * 
     * @param dbVerbindung
     * @param benutzername
     * @param passwort
     * @throws SQLException 
     */
    public void benutzerHinzufuegen(DBVerbindung dbVerbindung, String benutzername, String passwort) 
            throws SQLException{
        dbVerbindung.getStatement().executeUpdate(
                "insert into benutzer values("
                    + "'" + benutzername + "','" + passwort + "')");
        
    }
    
    
    
    /**
     * Methode, die eine Liste aller Manager in eine ArrayList speichert und 
     * zurueck gibt
     * 
     * @param dbVerbindung
     * @return ArrayList<String>
     * @throws SQLException 
     */
    public ArrayList<String> listeManager(DBVerbindung dbVerbindung) 
            throws SQLException{
        ArrayList<String> list;
        list = new ArrayList<>();

        dbVerbindung.setResultSet(dbVerbindung.getStatement().executeQuery(
            "SELECT Benutzername FROM Manager"));
               
        while(dbVerbindung.getResultSet().next()){
                list.add(dbVerbindung.getResultSet().getString("Benutzername"));

        }
        return list;
    }
    
}
