/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Klasse zur Verwaltung der Datenbankfunktionen des Benutzers
 *
 * @author Annika Petry
 * @author Timo Flake
 */
public class DBBenutzer {
    
    //----------------------------Punktemenue-----------------------------------
    
    /**
     * Methode, die eine Liste an Spielen zurueck gibt, die sowohl geschlossen sind, als auch
     * ein eigetragenes Ergebnis haben
     * 
     * @param dbVerbindung
     * @param benutzername
     * @return ArrayList<ArrayList<String>>
     * @throws SQLException 
     */
    public ArrayList<ArrayList<String>> spieleAbgeschlossen (DBVerbindung dbVerbindung, String benutzername) 
            throws SQLException{
        ArrayList<ArrayList<String>> list;
        list = new ArrayList<>();
        
        dbVerbindung.setResultSet(dbVerbindung.getStatement().executeQuery(
                "(SELECT t1.Mannschaftsname, t2.Mannschaftsname, Spiele.ToreT1, Spiele.ToreT2, "
                    + "Spiele.Anstossdatum, Spiele.Anstosszeit, tipps.Tipptore1, tipps.Tipptore2 FROM Spiele " 
                    + "JOIN Mannschaften AS t1 ON t1.Mannschaftsid = Spiele.Team1 "
                    + "JOIN Mannschaften AS t2 ON t2.Mannschaftsid = Spiele.Team2 "
                    + "JOIN Tipps ON tipps.spielid = Spiele.spielid "
                    + "WHERE Spiele.status_ergebnis = true "
                    + "AND Tipps.Benutzer = '"+benutzername+"' "

                    + "UNION ALL "

                + "SELECT t1.Mannschaftsname, t2.Mannschaftsname, Spiele.ToreT1, "
                    + "Spiele.ToreT2, Spiele.Anstossdatum, Spiele.Anstosszeit, -1 as notipp1, "
                    + "-1 AS nottipp2 FROM Spiele " 
                    + "JOIN Mannschaften AS t1 ON t1.Mannschaftsid = Spiele.Team1 "
                    + "JOIN Mannschaften AS t2 ON t2.Mannschaftsid = Spiele.Team2 "
                    + "WHERE Spiele.Status_Ergebnis = true "
                    + "AND Spiele.spielid NOT IN (SELECT Spielid FROM tipps WHERE Benutzer = '"+benutzername+"')) "
                    + "ORDER BY Anstossdatum, Anstosszeit "));
        
        while(dbVerbindung.getResultSet().next()){
            ArrayList<String> temp = new ArrayList<>();
            temp.add(dbVerbindung.getResultSet().getString(1));
            temp.add(dbVerbindung.getResultSet().getString(2));
            temp.add(dbVerbindung.getResultSet().getString("Anstossdatum"));
            temp.add(dbVerbindung.getResultSet().getString("Anstosszeit"));
            temp.add(dbVerbindung.getResultSet().getString("ToreT1"));
            temp.add(dbVerbindung.getResultSet().getString("ToreT2"));
            temp.add(dbVerbindung.getResultSet().getString(7));
            temp.add(dbVerbindung.getResultSet().getString(8));
            list.add(temp);
        }
        return list;
    }
    
    
    /**
     * Methode, die eine Liste an Spielen zurueck gibt, die geschlossen sind, fuer die aber 
     * noch kein Ergebnis eingetragen wurde
     * 
     * @param dbVerbindung
     * @param benutzername
     * @return ArrayList<ArrayList<String>>
     * @throws SQLException 
     */
    public ArrayList<ArrayList<String>> spieleOhneErgebnis (DBVerbindung dbVerbindung, String benutzername) 
            throws SQLException{
        ArrayList<ArrayList<String>> list;
        list = new ArrayList<>();
        
        dbVerbindung.setResultSet(dbVerbindung.getStatement().executeQuery(
                "(SELECT t1.Mannschaftsname, t2.Mannschaftsname, "
                    + "Spiele.Anstossdatum, Spiele.Anstosszeit, tipps.Tipptore1, tipps.Tipptore2 FROM Spiele " 
                    + "JOIN Mannschaften AS t1 ON t1.Mannschaftsid = Spiele.Team1 "
                    + "JOIN Mannschaften AS t2 ON t2.Mannschaftsid = Spiele.Team2 "
                    + "JOIN Tipps ON tipps.spielid = Spiele.spielid "
                    + "WHERE Spiele.status_ergebnis = false "
                    + "AND Spiele.status_offen = false "
                    + "AND Tipps.Benutzer = '"+benutzername+"' "

                    + "UNION ALL "

                + "SELECT t1.Mannschaftsname, t2.Mannschaftsname, "
                    + "Spiele.Anstossdatum, Spiele.Anstosszeit, -1 as notipp1, "
                    + "-1 AS nottipp2 FROM Spiele " 
                    + "JOIN Mannschaften AS t1 ON t1.Mannschaftsid = Spiele.Team1 "
                    + "JOIN Mannschaften AS t2 ON t2.Mannschaftsid = Spiele.Team2 "
                    + "WHERE Spiele.Status_Ergebnis = false "
                    + "AND Spiele.Status_offen = false "
                    + "AND Spiele.spielid NOT IN (SELECT Spielid FROM tipps WHERE Benutzer = '"+benutzername+"')) "
                    + "ORDER BY Anstossdatum, Anstosszeit "));
        
        while(dbVerbindung.getResultSet().next()){
            ArrayList<String> temp = new ArrayList<>();
            temp.add(dbVerbindung.getResultSet().getString(1));
            temp.add(dbVerbindung.getResultSet().getString(2));
            temp.add(dbVerbindung.getResultSet().getString("Anstossdatum"));
            temp.add(dbVerbindung.getResultSet().getString("Anstosszeit"));
            temp.add(dbVerbindung.getResultSet().getString(5));
            temp.add(dbVerbindung.getResultSet().getString(6));
            list.add(temp);
        }
        
        return list;
    }
    
    
    /**
     * Methode, die die Rangliste aller Tippenden zurueck gibt, sortiert absteigend
     * nach erreichten Punkten
     * 
     * @param dbVerbindung
     * @return ArrayList<ArrayList<String>>
     * @throws SQLException 
     */
    public ArrayList<ArrayList<String>> rangliste (DBVerbindung dbVerbindung) 
            throws SQLException{
        ArrayList<ArrayList<String>> list;
        list = new ArrayList<> ();
        
        dbVerbindung.setResultSet(dbVerbindung.getStatement().executeQuery(
                "SELECT Benutzer.benutzername, Sum(tmp.test) AS Punkte FROM Benutzer " 
                    + "LEFT JOIN "
                    + "(SELECT Benutzer, 1 AS test FROM tipps " 
                    + "JOIN Spiele ON Spiele.SPIELID=Tipps.SPIELID " 
                    + "WHERE (((spiele.TORET1-Spiele.TORET2)>0 " 
                    + "AND (tipps.TIPPTORE1-Tipps.TIPPTORE2)>0) " 

                    + "OR ((spiele.TORET1-Spiele.TORET2)=0 "  
                    + "AND (tipps.TIPPTORE1-Tipps.TIPPTORE2)=0) "  

                    + "OR ((spiele.TORET1-Spiele.TORET2)<0 "  
                    + "AND (tipps.TIPPTORE1-Tipps.TIPPTORE2)<0)) "  
                    + "AND Spiele.STATUS_ERGEBNIS=true "  

                    + " UNION ALL "  

                    + "SELECT Benutzer, 2 AS test FROM Tipps "  
                    + "JOIN Spiele ON Spiele.spielid = Tipps.spielid "  
                    + "WHERE Spiele.toret1 = tipps.tipptore1 "  
                    + "AND Spiele.toret2 = tipps.tipptore2 "
                    + "AND Spiele.Status_Ergebnis=true) AS tmp ON tmp.Benutzer = Benutzer.benutzername "  
                    + "GROUP BY Benutzer.benutzername "
                    + "ORDER BY Punkte DESC NULLS LAST"));
        
        while(dbVerbindung.getResultSet().next()){
            ArrayList<String> temp = new ArrayList<>();
            temp.add(dbVerbindung.getResultSet().getString("Benutzername"));
            temp.add(dbVerbindung.getResultSet().getString("Punkte"));
            list.add(temp);
        }
        return list;
    }
    
    
    //-----------------------------Tippmenue------------------------------------
     
     /**
      * Tipp in die Datenbank speichern
      * 
      * @param dbVerbindung
      * @param id
      * @param tore1
      * @param tore2
      * @param benutzername
      * @throws SQLException 
      */   
    public void tippSpeichern(DBVerbindung dbVerbindung, String id, String tore1, String tore2, String benutzername) 
            throws SQLException{

        dbVerbindung.getStatement().executeUpdate(
                "INSERT INTO Tipps "
                    + "VALUES ("+id+", "
                    +          "'"+benutzername+"', "
                    +          ""+tore1+", "
                    +          ""+tore2+")");
    }
    
    
    /**
     * Methode zum aendern bereits gespeicherter Tipps
     * 
     * @param dbVerbindung
     * @param id
     * @param tore1
     * @param tore2
     * @param benutzername
     * @throws SQLException 
     */
    public void tippAendern (DBVerbindung dbVerbindung, String id, String tore1, String tore2, String benutzername) 
            throws SQLException{
        dbVerbindung.getStatement().executeUpdate(
                "UPDATE Tipps SET Tipptore1="+tore1+", "
                    + "Tipptore2="+tore2+" "
                    + "WHERE SpielID="+id+" "
                    + "AND Benutzer ='"+benutzername+"'");
    }
    
    
    /**
     * Methode, die eine Liste von offenen Spielen zurueck gibt, auf die der Benutzer noch nicht
     * getippt hat
     * 
     * @param dbVerbindung
     * @param benutzername
     * @return ArrayList<ArrayList<String>>
     * @throws SQLException 
     */
    public ArrayList<ArrayList<String>> spieleNichtGetippt (DBVerbindung dbVerbindung, String benutzername) 
            throws SQLException{
        ArrayList<ArrayList<String>> list;
        list = new ArrayList<>();
        
         dbVerbindung.setResultSet(dbVerbindung.getStatement().executeQuery(
                "SELECT t1.Mannschaftsname, t2.Mannschaftsname, Spiele.Anstossdatum, Spiele.Anstosszeit, Spiele.SpielID FROM Spiele "
                    + "JOIN Mannschaften AS t1 ON t1.mannschaftsid = Spiele.Team1 " 
                    + "JOIN Mannschaften AS t2 ON t2.mannschaftsid = Spiele.Team2 "
                    + "WHERE Spiele.Status_offen=true "
                    + "AND Spiele.SpielID NOT IN( "
                    + "SELECT SpielID FROM Tipps "
                    + "WHERE Benutzer='"+benutzername+"')"));
         
        while(dbVerbindung.getResultSet().next()){
            ArrayList<String> temp = new ArrayList<>();
            temp.add(dbVerbindung.getResultSet().getString(1));
            temp.add(dbVerbindung.getResultSet().getString(2));
            temp.add(dbVerbindung.getResultSet().getString("Anstossdatum"));
            temp.add(dbVerbindung.getResultSet().getString("Anstosszeit"));
            temp.add(dbVerbindung.getResultSet().getString("SpielID"));
            list.add(temp);
        }
        return list;
    }
    
    
    /**
     * Methode, die eine Liste von offenen Spielen zurueck gibt, auf die der Benutzer bereits 
     * getippt hat.
     * 
     * @param dbVerbindung
     * @param benutzername
     * @return ArrayList<ArrayList<String>>
     * @throws SQLException 
     */
    public ArrayList<ArrayList<String>> spieleGetippt (DBVerbindung dbVerbindung, String benutzername) 
            throws SQLException{
        ArrayList<ArrayList<String>> list;
        list = new ArrayList<>();
        
         dbVerbindung.setResultSet(dbVerbindung.getStatement().executeQuery(
                "SELECT t1.Mannschaftsname, t2.Mannschaftsname, Spiele.Anstossdatum, Spiele.Anstosszeit, "
                    + "tipps.Tipptore1, tipps.Tipptore2, Spiele.SpielID FROM Spiele "
                    + "JOIN Tipps ON Tipps.SpielID=Spiele.SpielID "
                    + "JOIN Mannschaften AS t1 ON t1.mannschaftsid = Spiele.Team1 " 
                    + "JOIN Mannschaften AS t2 ON t2.mannschaftsid = Spiele.Team2 "
                    + "WHERE Spiele.Status_Offen=true "
                    + "AND Benutzer='"+benutzername+"'"));
         
        while(dbVerbindung.getResultSet().next()){
            ArrayList<String> temp = new ArrayList<>();
            temp.add(dbVerbindung.getResultSet().getString(1));
            temp.add(dbVerbindung.getResultSet().getString(2));
            temp.add(dbVerbindung.getResultSet().getString("Anstossdatum"));
            temp.add(dbVerbindung.getResultSet().getString("Anstosszeit"));
            temp.add(dbVerbindung.getResultSet().getString("Tipptore1"));
            temp.add(dbVerbindung.getResultSet().getString("Tipptore2"));
            temp.add(dbVerbindung.getResultSet().getString("SpielID"));
            list.add(temp);
        }
        return list;
    }
    
    
    /**
     * Methode, die zu einer SpielID die zugehoerige Anstosszeit und das Anstossdatum 
     * zurueck gibt
     * 
     * @param dbVerbindung
     * @param id
     * @return ArrayList<String>
     * @throws SQLException 
     */
    public ArrayList<String> getZeitID (DBVerbindung dbVerbindung, String id) 
            throws SQLException{
        ArrayList<String> list;
        list = new ArrayList<>();
        
         dbVerbindung.setResultSet(dbVerbindung.getStatement().executeQuery(
                "SELECT Spiele.Anstossdatum, Spiele.Anstosszeit FROM Spiele "
                    + "WHERE Spiele.SpielID="+id+" "));
         
        while(dbVerbindung.getResultSet().next()){
            list.add(dbVerbindung.getResultSet().getString("Anstossdatum"));
            list.add(dbVerbindung.getResultSet().getString("Anstosszeit"));
        }
        return list;
    }
    
    
}
