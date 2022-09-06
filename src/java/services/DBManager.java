/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Klasse zur Verwaltung der Datenbankfunktionen des Managers
 *
 * @author Annika Petry
 * @author Timo Flake
 */
public class DBManager {
    
    //-------------------------Mannschaftsmenue---------------------------------
    
    /**
     * Methode zum Erhalten einer Liste aller Mannschaften, mit ID und Name
     * 
     * @param dbVerbindung
     * @return ArrayList<ArrayList<String>>
     * @throws SQLException 
     */
    public ArrayList<ArrayList<String>> mannschaftsListe (DBVerbindung dbVerbindung) 
            throws SQLException{
        ArrayList<ArrayList<String>> list;
        list = new ArrayList<>();

        dbVerbindung.setResultSet(dbVerbindung.getStatement().executeQuery(
            "SELECT Mannschaftsid, Mannschaftsname FROM Mannschaften"));
               
        while(dbVerbindung.getResultSet().next()){
            ArrayList<String> temp = new ArrayList<>();
                temp.add(dbVerbindung.getResultSet().getString(1));
                temp.add(dbVerbindung.getResultSet().getString(2));
                list.add(temp);
        }
        return list;
    }

    
    /**
     * Methode zum speichern neuer Mannschaften in die Tabelle Mannschaften
     * (MannschaftsID ist ein automatisch generiertes Feld, mit aufsteigenden IDs)
     * 
     * @param dbVerbindung
     * @param name
     * @throws SQLException
     * @throws ClassNotFoundException 
     */
    public void mannschaftHinzufuegen(DBVerbindung dbVerbindung, String name) 
            throws SQLException, ClassNotFoundException{
        dbVerbindung.getStatement().executeUpdate(
                "INSERT INTO Mannschaften (Mannschaftsname) "
                        + "VALUES ('"+name+"')");
    }
    
    
    /**
     * Methode zum speichern der Aenderungen an einem Spiel
     * 
     * @param dbVerbindung
     * @param name
     * @param id
     * @throws SQLException
     * @throws ClassNotFoundException 
     */
    public void mannschaftBearbeiten(DBVerbindung dbVerbindung, String name, String id) 
            throws SQLException, ClassNotFoundException{
        dbVerbindung.getStatement().executeUpdate(
                "UPDATE Mannschaften SET Mannschaftsname='"+name+"' "
                    + "WHERE Mannschaftsid="+id+"");
    }
    
    
    /**
     * Methode zum Pruefen, ob eine Mannschaft bereits in einem oder mehreren Spielen
     * verwendet wurde
     * true, wenn die Mannschaft in keinem Spiel verwendet wird
     * 
     * @param dbVerbindung
     * @param id
     * @return boolean
     * @throws SQLException 
     */
    public boolean mannschaftLoeschenPruefen (DBVerbindung dbVerbindung, String id) 
            throws SQLException{
        dbVerbindung.setResultSet(dbVerbindung.getStatement().executeQuery(
                "SELECT Spielid FROM Spiele "
                    + "WHERE Team1 = "+id+" "
                    + "OR Team2 = "+id+""));

        return !dbVerbindung.getResultSet().next();
    }
    
    
    /**
     * Mehtode zum Loeschen von Mannschaften
     * 
     * @param dbVerbindung
     * @param id
     * @throws SQLException
     * @throws ClassNotFoundException 
     */
    public void mannschaftLoeschen(DBVerbindung dbVerbindung, String id) 
            throws SQLException, ClassNotFoundException{
        dbVerbindung.getStatement().executeUpdate(
                "DELETE FROM Mannschaften "
                    + "WHERE Mannschaftsid="+id+"");
    }
    
    
    //-----------------------------Turniermenue---------------------------------
    
    /**
     * Methode zum Erhalten einer Liste aller Turniere, mit ID und Name
     * 
     * @param dbVerbindung
     * @return ArrayList<ArrayList<String>>
     * @throws SQLException 
     */
    public ArrayList<ArrayList<String>>turnierListe (DBVerbindung dbVerbindung) 
            throws SQLException{
        ArrayList<ArrayList<String>> list;
        list = new ArrayList<>();

        dbVerbindung.setResultSet(dbVerbindung.getStatement().executeQuery(
            "SELECT TurnierID, Turniername FROM Turniere"));
               
        while(dbVerbindung.getResultSet().next()){
            ArrayList<String> temp = new ArrayList<>();
                temp.add(dbVerbindung.getResultSet().getString(1));
                temp.add(dbVerbindung.getResultSet().getString(2));
                list.add(temp);
        }
        return list;
    }
    
    
    /**
     * Methode zum Speichern neuer Turniere in der Tabelle Turniere
     * 
     * @param dbVerbindung
     * @param name
     * @throws SQLException
     * @throws ClassNotFoundException 
     */
    public void turnierHinzufuegen(DBVerbindung dbVerbindung, String name) 
            throws SQLException, ClassNotFoundException{
        dbVerbindung.getStatement().executeUpdate(
                "INSERT INTO Turniere (Turniername) "
                    + "VALUES ('"+name+"')");
    }
    
    
    /**
     * Methode zum Speichern der Aenderungen an einem Turnier
     * 
     * @param dbVerbindung
     * @param name
     * @param id
     * @throws SQLException
     * @throws ClassNotFoundException 
     */
    public void turnierBearbeiten(DBVerbindung dbVerbindung, String name, String id) 
            throws SQLException, ClassNotFoundException{
        dbVerbindung.getStatement().executeUpdate(
                "UPDATE Turniere SET Turniername='"+name+"' "
                    + "WHERE Turnierid="+id+"");
    }
    
    
    /**
     * Methode zum Pruefen, ob ein Turnier bereits in einem oder mehreren Spielen
     * verwendet wird
     * true, wenn das Turnier in keinem Spiel verwendet wird
     * 
     * @param dbVerbindung
     * @param id
     * @return boolean
     * @throws SQLException 
     */
    public boolean turnierLoeschenPruefen (DBVerbindung dbVerbindung, String id) 
            throws SQLException{
        dbVerbindung.setResultSet(dbVerbindung.getStatement().executeQuery(
                "SELECT Spielid FROM Spiele "
                    + "WHERE turnier = "+id+""));

        return !dbVerbindung.getResultSet().next();
    }
    
    
    /**
     * Methode zum Loeschen eines Turniers
     * 
     * @param dbVerbindung
     * @param id
     * @throws SQLException
     * @throws ClassNotFoundException 
     */
    public void turnierLoeschen(DBVerbindung dbVerbindung, String id) 
            throws SQLException, ClassNotFoundException{
        dbVerbindung.getStatement().executeUpdate(
                "DELETE FROM Turniere WHERE Turnierid="+id+"");
    }

    
    //-------------------------------Spielmenue---------------------------------
    
    /**
     * Methode, die eine Liste an Spielen zurueck gibt, die noch nicht gesperrt
     * wurden. Sortiert nach Anstossdatum und Anstosszeit
     * 
     * @param dbVerbindung
     * @return ArrayList<ArrayList<String>>
     * @throws SQLException 
     */
    public ArrayList<ArrayList<String>> listeSpieleOffen (DBVerbindung dbVerbindung) 
            throws SQLException{
        ArrayList<ArrayList<String>> list;
        list = new ArrayList<>();
    
        dbVerbindung.setResultSet(dbVerbindung.getStatement().executeQuery(
                "SELECT t1.MANNSCHAFTSNAME, t2.MANNSCHAFTSNAME, Spiele.Anstossdatum, Spiele.Anstosszeit, Turniere.Turniername, Spiele.SpielID FROM Spiele "
                    + "JOIN Mannschaften AS t1 ON t1.MANNSCHAFTSID =Spiele.Team1 "
                    + "JOIN Mannschaften AS t2 ON t2.MANNSCHAFTSID=Spiele.Team2 "
                    + "JOIN Turniere ON Turniere.Turnierid=Spiele.Turnier "
                    + "Where Spiele.Status_Ergebnis=false "
                    + "AND Spiele.Status_Offen = true "
                    + "ORDER BY Spiele.Anstossdatum, Spiele.Anstosszeit"));
        
        while(dbVerbindung.getResultSet().next()){
            ArrayList<String> temp = new ArrayList<>();
                temp.add(dbVerbindung.getResultSet().getString(1));
                temp.add(dbVerbindung.getResultSet().getString(2));
                temp.add(dbVerbindung.getResultSet().getString("Anstossdatum"));
                temp.add(dbVerbindung.getResultSet().getString("Anstosszeit"));
                temp.add(dbVerbindung.getResultSet().getString("Turniername"));
                temp.add(dbVerbindung.getResultSet().getString("SpielID"));
                list.add(temp);
        }
        return list;
    }
    
    
    /**
     * Methode, die eine Liste an Spielen zurueck gibt, die bereits gesperrt wurden,
     * fuer die aber noch kein Ergebnis gespeichert wurde.
     * Sortiert nach Anstossdatum und Anstosszeit
     * 
     * @param dbVerbindung
     * @return ArrayList<ArrayList<String>>
     * @throws SQLException 
     */
    public ArrayList<ArrayList<String>> listeSpieleGesperrt (DBVerbindung dbVerbindung) 
            throws SQLException{
        ArrayList<ArrayList<String>> list;
        list = new ArrayList<>();
        
        dbVerbindung.setResultSet(dbVerbindung.getStatement().executeQuery(
                "SELECT t1.MANNSCHAFTSNAME, t2.MANNSCHAFTSNAME, Spiele.Anstossdatum, Spiele.Anstosszeit, Turniere.Turniername, Spiele.SpielID FROM Spiele "
                    + "JOIN Mannschaften AS t1 ON t1.MANNSCHAFTSID =Spiele.Team1 "
                    + "JOIN Mannschaften AS t2 ON t2.MANNSCHAFTSID=Spiele.Team2 "
                    + "JOIN Turniere ON Turniere.Turnierid=Spiele.Turnier "
                    + "Where Spiele.Status_Ergebnis=false "
                    + "AND Spiele.Status_Offen=false "
                    + "ORDER BY Spiele.Anstossdatum, Spiele.Anstosszeit"));
        
        while(dbVerbindung.getResultSet().next()){
            ArrayList<String> temp = new ArrayList<>();
                temp.add(dbVerbindung.getResultSet().getString(1));
                temp.add(dbVerbindung.getResultSet().getString(2));
                temp.add(dbVerbindung.getResultSet().getString("Anstossdatum"));
                temp.add(dbVerbindung.getResultSet().getString("Anstosszeit"));
                temp.add(dbVerbindung.getResultSet().getString("Turniername"));
                temp.add(dbVerbindung.getResultSet().getString("SpielID"));
                list.add(temp);
        }
        return list;
    }
    
    
    /**
     * Methode, um Spiele zu sperren
     * 
     * @param dbVerbindung
     * @param id
     * @throws SQLException 
     */
    public void spielSperren (DBVerbindung dbVerbindung, String id) 
            throws SQLException{
        dbVerbindung.getStatement().executeUpdate(
                "UPDATE Spiele SET Status_Offen=false "
                    + "WHERE SpielID="+id+"");
    }
    
    
    /**
     * Methode, um Ergebnisse fuer ein Spiel zu speichern
     * @param dbVerbindung
     * @param id
     * @param erg1
     * @param erg2
     * @throws SQLException 
     */
    public void ergebnisSpeichern(DBVerbindung dbVerbindung, String id, String erg1, String erg2) 
            throws SQLException{
        dbVerbindung.getStatement().executeUpdate(
                "UPDATE Spiele SET ToreT1="+erg1+", "
                    + "ToreT2="+erg2+", "
                    + "Status_Ergebnis = true "
                    + "WHERE SpielID="+id+"");
    }
       
    
    /**
     * Methode, um ein neues Spiel zu speichern
     * 
     * @param dbVerbindung
     * @param team1
     * @param team2
     * @param turnier
     * @param anstossDatum
     * @param anstossZeit
     * @throws SQLException 
     */
    public void spielHinzufuegen(DBVerbindung dbVerbindung, String team1, String team2, 
                            String turnier, String anstossDatum, String anstossZeit) 
            throws SQLException{

        dbVerbindung.getStatement().executeUpdate(
                "INSERT INTO Spiele (Team1, Team2, ToreT1, ToreT2, Anstossdatum, "
                    + "Anstosszeit, Turnier, Status_offen, Status_ergebnis)"
                    + "Values ("+team1+","+team2+",0,0,'"+anstossDatum+"',"
                    + "'"+anstossZeit+"',"+turnier+",'TRUE','FALSE')");
    }
    
    
    /**
     * Methode, um ein Spiel zu loeschen
     * 
     * @param dbVerbindung
     * @param id
     * @throws SQLException 
     */
    public void spielLoeschen (DBVerbindung dbVerbindung, String id) 
            throws SQLException{
        dbVerbindung.getStatement().executeUpdate(
                "DELETE FROM Spiele "
                    + "WHERE Spielid="+id+"");
    }
    
    
    /**
     * Methode, um Spiele zu entsperren
     * 
     * @param dbVerbindung
     * @param id
     * @throws SQLException 
     */
    public void spielEntsperren (DBVerbindung dbVerbindung, String id) 
            throws SQLException{
        dbVerbindung.getStatement().executeUpdate(
                "UPDATE Spiele SET Status_Offen=true "
                    + "WHERE SpielID="+id+"");
    }
    
    
    /**
     * Methode, die eine Liste an Spielen zurueck gibt, die bereits abgeschlossen sind.
     * Abgeschlossen bedeutet: Spiel ist geschlossen und ein Ergebnis wurde eingetragen
     * 
     * @param dbVerbindung
     * @return ArrayList<ArrayList<String>>
     * @throws SQLException 
     */
    public ArrayList<ArrayList<String>> listeSpieleAbgeschlossen (DBVerbindung dbVerbindung) 
            throws SQLException{
        ArrayList<ArrayList<String>> list;
        list = new ArrayList<>();
        
        dbVerbindung.setResultSet(dbVerbindung.getStatement().executeQuery(
                "SELECT t1.mannschaftsname, t2.mannschaftsname, Spiele.ToreT1, Spiele.ToreT2, Spiele.Anstossdatum, Spiele.Anstosszeit FROM Spiele "
                    + "JOIN Mannschaften AS t1 ON t1.MANNSCHAFTSID =Spiele.Team1 "
                    + "JOIN Mannschaften AS t2 ON t2.MANNSCHAFTSID=Spiele.Team2 "
                    + "JOIN Turniere ON Turniere.Turnierid=Spiele.Turnier "
                    + "Where Spiele.Status_Ergebnis=true "
                    + "AND Spiele.Status_Offen=false "
                    + "ORDER BY Spiele.Anstossdatum, Spiele.Anstosszeit"));
        
        while(dbVerbindung.getResultSet().next()){
            ArrayList<String> temp = new ArrayList<>();
                temp.add(dbVerbindung.getResultSet().getString(1));
                temp.add(dbVerbindung.getResultSet().getString(2));
                temp.add(dbVerbindung.getResultSet().getString("ToreT1"));
                temp.add(dbVerbindung.getResultSet().getString("ToreT2"));
                temp.add(dbVerbindung.getResultSet().getString("Anstossdatum"));
                temp.add(dbVerbindung.getResultSet().getString("Anstosszeit"));
                list.add(temp);
        }
        return list;
    }
    
    
    /**
     * Methode zum Aendern bereits gespeicherter Spiele
     * 
     * @param dbVerbindung
     * @param team1
     * @param team2
     * @param turnier
     * @param anstossDatum
     * @param anstossZeit
     * @param id
     * @throws SQLException 
     */
    public void spielBearbeiten(DBVerbindung dbVerbindung, String team1, String team2, 
                            String turnier, String anstossDatum, String anstossZeit, String id) 
            throws SQLException{
        dbVerbindung.getStatement().executeUpdate(
                "UPDATE Spiele SET Team1="+team1+", Team2="+team2+","
                    + " Anstoßdatum='"+anstossDatum+"', Anstosszeit='"+anstossZeit+"'"
                    + " WHERE Spielid="+id+"");
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
    

    //-------------------------------Managermenue-------------------------------
    
    /**
     * Methode, die die Rangliste der Tipper zurueck gibt, mit berechneter Punktzahl.
     * Sortiert absteigend nach Punkten
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
                    + "JOIN Spiele ON Spiele.SPIELID=Tipps.spielid " 
                    + "WHERE (((spiele.TORET1-Spiele.TORET2)>0 " 
                    + "AND (tipps.TIPPTORE1-Tipps.TIPPTORE2)>0) " 

                    + "OR ((spiele.TORET1-Spiele.TORET2)=0 "  
                    + "AND (tipps.TIPPTORE1-Tipps.TIPPTORE2)=0) "  

                    + "OR ((spiele.TORET1-Spiele.TORET2)<0 "  
                    + "AND (tipps.TIPPTORE1-Tipps.TIPPTORE2)<0)) "  
                    + "AND Spiele.STATUS_ERGEBNIS=true "  

                    + "UNION ALL "  

                    + "SELECT Benutzer, 2 AS test FROM Tipps "  
                    + "JOIN Spiele ON Spiele.SPIELID=Tipps.spielid "  
                    + "WHERE Spiele.TORET1=tipps.tipptore1 "  
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

}