/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.Manager;

import services.DBManager;
import services.DBVerbindung;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Klasse mit der Menuestruktur des Turniermenues (Zugriff nur fuer den Manager)
 *
 * @author Annika Petry
 * @author Timo Flake
 */
public class Turniermenue extends HttpServlet {
    protected DBManager managerDB;
    protected DBVerbindung verbindungDB;
    
    @Override
    public void init(){
        verbindungDB=DBVerbindung.dbVerbindung;
        managerDB=new DBManager();
    }
    
    
    
    /**
     * Leitet die Anfragen weiter fuer beide HTTP <code>GET</code> und <code>POST</code>
     * Methoden.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException, ClassNotFoundException {
        //prueft ob Session noch aktiv ist
        HttpSession session = request.getSession(false);
        if( session == null ) fehlermeldungSession(request, response);
        
        if(request.getParameter("turnier_hinzufuegen")!= null){
            turnierHinzufuegen(request, response);
        }
        else if(request.getParameter("speichern_Turnier")!=null){
            String temp = request.getParameter("turnier");
            managerDB.turnierHinzufuegen(verbindungDB, temp);
            turniermenue( request, response);         
        }
        else if(request.getParameter("turnier_bearbeiten")!= null){
            String id = request.getParameter("turnierauswahl");
            if(id==null)fehlermeldung(request, response);
            turnierBearbeiten(request, response, id);
        }
        else if(request.getParameter("aendern_Turnier")!=null){
            String temp = request.getParameter("turnier");
            String id = request.getParameter("turnier_geaendert");
            managerDB.turnierBearbeiten(verbindungDB, temp, id);
            turniermenue( request, response);         
        }
        else if(request.getParameter("turnier_loeschen")!= null){
            String id = request.getParameter("turnierauswahl");
            //prueft ob Turniere ausgewaehlt wurden
            if(id==null)fehlermeldung(request, response);
            if(managerDB.turnierLoeschenPruefen(verbindungDB, id)){
                managerDB.turnierLoeschen(verbindungDB, id);
                turniermenue( request, response);   
            }
            else{
                fehlermeldungLoeschen(request, response);
            }  
        }  
        else if(request.getParameter("zurueck") != null){
            turniermenue(request, response);
        }
        else if(request.getParameter("zurueck_managermenue") != null){
            Managermenue manager = new Managermenue();
            manager.managermenue(request, response);
        }
    }

    
    
    /**
     * Menuestruktur fuer das Turniermenue
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws SQLException 
     */
    protected void turniermenue(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        
        //Liste mit Turnieren, fuer die Ausgabe, Reihenfolge: TurnierID-Turniername
        ArrayList<ArrayList<String>> list;
        list = new ArrayList<>();
        list= managerDB.turnierListe(verbindungDB);
        String id;
    
         response.setContentType("text/html;charset=UTF-8");
         try (PrintWriter out = response.getWriter()) {
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Turniermen&uuml;</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<form action=\"http://localhost:8080/Projektarbeit_V2/Turniermenue\" method = \"POST\">");
            
            out.println("<h2>Turniere</h2>");
            for(int i=0; i<list.size();i++){
                id = list.get(i).get(0);
                out.println("<input type=\"radio\" name=\"turnierauswahl\" value=\""+id+"\">");
                out.println(list.get(i).get(1) + "<BR>");
            }
            
            out.println("<BR><input type=\"submit\" name=\"turnier_loeschen\" value=\"Turnier l&ouml;schen \">");
            out.println("<input type=\"submit\" name=\"turnier_bearbeiten\" value=\"Turnier bearbeiten \">");
            out.println("<input type=\"submit\" name=\"turnier_hinzufuegen\" value=\"neues Turnier hinzuf&uuml;gen \">");
            out.println("<input type=\"submit\" name=\"zurueck_managermenue\" value=\"zur&uuml;ck\"><BR><BR>");
            
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    
    
    /**
     * Methode mit der Darstellung der HTML-Seite zum speichern neuer Turneire
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws SQLException 
     */
    protected void turnierHinzufuegen(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

         response.setContentType("text/html;charset=UTF-8");
         try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Turnier hinzuf&uuml;gen</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<form action=\"http://localhost:8080/Projektarbeit_V2/Turniermenue\" method = \"POST\">");
            
            out.println("<h2>Turnier hinzuf&uuml;gen</h2><BR>");
            out.println("<input type=\"text\" id=\"turnier\" name=\"turnier\" ><br>");
            
            out.println("<input type=\"submit\" name=\"speichern_Turnier\" value=\"speichern\"><BR><BR>");
            out.println("<input type=\"submit\" name=\"zurueck\" value=\"zur&uuml;ck\"><BR><BR>");
            
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    
    
    /**
     * Methode mit der Darstellung der HTML-Seite mit der Moeglichkeit zur Bearbeitung von
     * Turnieren
     * 
     * @param request
     * @param response
     * @param id
     * @throws ServletException
     * @throws IOException
     * @throws SQLException 
     */
    protected void turnierBearbeiten(HttpServletRequest request, HttpServletResponse response, String id)
            throws ServletException, IOException, SQLException {

         response.setContentType("text/html;charset=UTF-8");
         try (PrintWriter out = response.getWriter()) {
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Turnier bearbeiten</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<form action=\"http://localhost:8080/Projektarbeit_V2/Turniermenue\" method = \"POST\">");
            
            out.println("<input type=\"hidden\" name=\"turnier_geaendert\" value=\""+id+"\">");
            out.println("<h2>Turnier bearbeiten</h2><BR>");
            out.println("<input type=\"text\" id=\"turnier\" name=\"turnier\" ><br>");
            
            out.println("<input type=\"submit\" name=\"aendern_Turnier\" value=\"&auml;ndern\"><BR><BR>");
            out.println("<input type=\"submit\" name=\"zurueck\" value=\"zur&uuml;ck\"><BR><BR>");
            
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    
    
    /**
     * Methode zur Darstellung der allgemeinen HTML-Fehlerseite
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws SQLException 
     */
    protected void fehlermeldung(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException{
        response.setContentType("text/html;charset=UTF-8");
        try(PrintWriter out = response.getWriter()){
            out.println("<DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Fehler Turniermenue</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<form action=\"http://localhost:8080/Projektarbeit_V2/Turniermenue\" method = \"POST\">");
            
            out.println("<h2>Fehler</h2><BR>");
            out.println("Leider gab es ein Problem, bitte versuchen Sie es erneut!<BR>");
            
            out.println("<input type=\"submit\" name=\"zurueck\" value=\"zur&uuml;ck\"><BR><BR>");
            
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    
    
    /**
     * Methode zur Darstellung der speziellen HTML-Fehlerseite
     * Spezielle Fehlerseite, fuer den fall, das ein Turnier geloescht werden soll,
     * das bereits in Spielen verwendet wird
     * 
     * @param request
     * @param response
     * @throws IOException 
     */
    protected void fehlermeldungLoeschen(HttpServletRequest request, HttpServletResponse response) 
            throws IOException{
        response.setContentType("text/html;charset=UTF-8");
        try(PrintWriter out = response.getWriter()){
            out.println("<DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Fehler beim L&ouml;schen des Turniers</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<form action=\"http://localhost:8080/Projektarbeit_V2/Turniermenue\" method = \"POST\">");
            
            out.println("<h2>Fehler</h2><BR>");
            out.println("Das Turnier ist bereits in einem oder mehreren Spielen gespeichert<BR>");
            out.println("wenn Sie das Turnier trotzdem l&ouml;schen wollen, bitte erst die Spiele l&ouml;schen.<BR>");
            
            out.println("<input type=\"submit\" name=\"zurueck\" value=\"zur&uuml;ck\"><BR><BR>");
            
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    
    
    /**
     * Methode, zur Darstellung der HTML-Fehlerseite wenn die Session abgelaufen ist
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    protected void fehlermeldungSession(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Fehlermeldung Session</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<form action=\"http://localhost:8080/Projektarbeit_V2/SessionFehler\" method=\"POST\">");
            out.println("<h2>Fehler</h2><BR>");
            out.println("Leider ist Ihre Session ausgelaufen<BR>");
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
}
    
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Turniermenue.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Turniermenue.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}