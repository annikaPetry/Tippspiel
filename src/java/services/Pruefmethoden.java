/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Annika Petry
 * @author Timo Flake
 */
public class Pruefmethoden {
    
    /**
     * Methode, die Prueft, ob eine Anstosszeit nach der jetztzeit liegt
     * 
     * @param anstossDatum
     * @param anstossZeit
     * @return boolean
     * @throws IOException
     * @throws ParseException 
     */
    public boolean anstosszeitPruefen(String anstossDatum, String anstossZeit) 
            throws IOException, ParseException{

        
        SimpleDateFormat datumFormat = new SimpleDateFormat("yyyy-MM-dd");
        String stringHeute = datumFormat.format(new Date());
        
        Date heute = datumFormat.parse(stringHeute);
        Date parseAnstossDatum = datumFormat.parse(anstossDatum);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String stringZeit = timeFormat.format(new Date());
        
        Date jetzt = timeFormat.parse(stringZeit);
        Date parseAnstossZeit = timeFormat.parse(anstossZeit);
        
       if(parseAnstossDatum.before(heute)){
           return false;
       }
       else if(parseAnstossDatum.equals(heute)
               && parseAnstossZeit.before(jetzt)){
            return false;
       }
       return true;
    }
   
     
    /**
     * Methode zum pruefen, ob ein eingegebenes Spiel korrekt eingegeben wurde
     * die Kriterien sind:
     * zwei unterschiedliche Teams, Spielbeginn nach Jetztzeit
     * true, wenn die Eingabe korrekt ist
     * 
     * @param team1
     * @param team2
     * @param anstossDatum
     * @param anstossZeit
     * @return boolean
     * @throws IOException
     * @throws ParseException 
     */
    public boolean spielSpeichernPruefen(String team1, String team2, String anstossDatum, String anstossZeit) 
            throws IOException, ParseException{

        if(team1==null || team2==null || anstossDatum==null || anstossZeit==null){
            return false;
        }
        
        SimpleDateFormat datumFormat = new SimpleDateFormat("yyyy-MM-dd");
        String stringHeute = datumFormat.format(new Date());
        
        Date heute = datumFormat.parse(stringHeute);
        Date parseAnstossDatum = datumFormat.parse(anstossDatum);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String stringZeit = timeFormat.format(new Date());
        
        Date jetzt = timeFormat.parse(stringZeit);
        Date parseAnstossZeit = timeFormat.parse(anstossZeit);
        
        if(team1.equals(team2)){
            return false;
        }
       if(parseAnstossDatum.before(heute)){
           return false;
       }
       else if(parseAnstossDatum.equals(heute)
               && parseAnstossZeit.before(jetzt)){
            return false;
       }
       return true;
    }
        

}
