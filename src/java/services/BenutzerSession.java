/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

/**
 * Klasse zur Session-Handhabung, mit pruefung auf doppelte Anmeldung oder den
 * Versuch eines zweiten Managers sich anzumelden.
 *
 * @author Annika Petry
 * @author Timo Flake
 */
@WebListener
public class BenutzerSession implements HttpSessionBindingListener {
    private static Map<BenutzerSession,HttpSession> logins = new HashMap();
    private String benutzer;
    private ArrayList<String> manager=new ArrayList();

    
  /**
   * Methode, die aufgerufen wird, wenn eine Session gestartet wird.
   * 
   * schreibt einen Eintrag für die user data in die map logins
   * prueft ob bereits eine Session für den gleichen Benutzer gestartet wurde
   * loggt diesen aus
   * Prueft wenn sich ein Manager anmeldet ob irgendein anderer Manager bereits eine
   * Session gestartet hat, loggt diesen aus
   * Setzt die maximale Inaktivitätszeit für die Session auf eine Minute 
   * 
   * @param event 
   */
  @Override
  public void valueBound(HttpSessionBindingEvent event) {

    HttpSession session = event.getSession();
    benutzer=(String) session.getAttribute("user");
    manager=(ArrayList<String>) session.getAttribute("manager");      
    
    //Sollte bereits ein anderer Manager eingelogt sein wird dieser ausgeloggt, 
    //wenn sich ein anderer Manager einloggt
    if(manager!=null){
        for (BenutzerSession i : logins.keySet()) {
            for(int j=0; j<manager.size(); j++){
                System.out.println(manager.get(j));
                if(i.benutzer.equals(manager.get(j))){
                    System.out.println("Es ist bereits ein anderer Manager "
                                        + "eingeloggt....wird ausgeloggt");
                    logins.get(i).invalidate();
                    }
                }
            }
        }
      
    //sollte ein Benutzer bereits eine aktive Session haben und versuchen 
    //sich noch einmal einzuloggen wird die aeltere Session beendet
    else{
        for (BenutzerSession i : logins.keySet()) {
            if(i.benutzer.equals(benutzer)){
                System.out.println("User bereits eingeloggt...."
                                    + "wird ausgeloggt");
                logins.get(i).invalidate();
            }
        }
    }
      
           //fügt die aktuelle Verbindung in die Session-Map
          logins.put(this, event.getSession());
          // Setzt die maximale Inaktivitaetszeit auf 60 Sekunden,
          //wird diese ueberschritten wird die Session beendet
          session.setMaxInactiveInterval(60);
          
          System.out.println(" Größe logins:   "+ logins.size());
          for (BenutzerSession k : logins.keySet()) {
            System.out.println("Eingeloggte User: " + k.benutzer );
            }

  }

  
  /**
   * Diese Methode wird aufgerufen, wenn eine Session beendet wird
   * 
   * loescht Benutzerdaten aus der Map
   * 
   * @param event 
   */
  @Override
  public void valueUnbound(HttpSessionBindingEvent event) {
      
      logins.remove(this);
       System.out.println(" Größe logins:   "+ logins.size());
                 for (BenutzerSession k : logins.keySet()) {
            System.out.println("Eingeloggte User: " + k.benutzer );
            }
  }
}