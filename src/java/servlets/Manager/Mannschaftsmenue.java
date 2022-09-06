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
 * Klasse mit der Menuestruktur des Mannschaftsmenues (Zugriff nur fuer den Manager)
 *
 * @author Annika Petry
 * @author Timo Flake
 */
public class Mannschaftsmenue extends HttpServlet {
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
        
        if(request.getParameter("mannschaft_hinzufuegen")!= null){
            mannschaftHinzufuegen(request, response);
        }
        else if(request.getParameter("speichern_Mannschaft")!=null){
            String temp = request.getParameter("mannschaft");
            managerDB.mannschaftHinzufuegen(verbindungDB, temp);
            mannschaftsmenue( request, response);         
        }
        else if(request.getParameter("mannschaft_bearbeiten")!= null){
            String id = request.getParameter("mannschaftsauswahl");
            //prueft ob eine Mannschaft ausgewaehlt wurde
            if(id==null)fehlermeldung(request, response);
            mannschaftBearbeiten(request, response, id);
        }
        else if(request.getParameter("aendern_Mannschaft")!=null){
            String temp = request.getParameter("mannschaft");
            String id = request.getParameter("mannschaft_geaendert");
            managerDB.mannschaftBearbeiten(verbindungDB, temp, id);
            mannschaftsmenue( request, response);         
        }
        else if(request.getParameter("mannschaft_loeschen")!= null){
            String id = request.getParameter("mannschaftsauswahl");
            //prueft ob eine Mannschaft ausgewaehlt wurde
            if(id==null)fehlermeldung(request, response);
            //prueft ob die Mannschaft bereits in Spiele verplant wurdes
            if(managerDB.mannschaftLoeschenPruefen(verbindungDB, id)){
                managerDB.mannschaftLoeschen(verbindungDB, id);
                mannschaftsmenue( request, response);         
            }
            else{
                fehlermeldungLoeschen(request, response);
            }    
        }
        else if(request.getParameter("zurueck") != null){
            mannschaftsmenue(request, response);
        }
        else if(request.getParameter("zurueck_Managermenue") != null){
            Managermenue manager = new Managermenue();
            manager.managermenue(request, response);
        }
    }

    
    
    /**
     * Methode, die die Menuestruktur bildet, Ausgabe der existierenden Mannschaften.
     * Moglichkeiten zum hinzufuegen, bearbeiten und loeschen von Mannschaften
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws SQLException 
     */
    protected void mannschaftsmenue(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        
        //Liste mit Existierenden Mannschaften
        //Reihenfolge: MannschaftsID-Mannschaftsname
        ArrayList<ArrayList<String>> list;
        list = new ArrayList<>();
        list=managerDB.mannschaftsListe(verbindungDB);
        String id;
    
         response.setContentType("text/html;charset=UTF-8");
         try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Mannschaftsmen&uuml;</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<form action=\"http://localhost:8080/Projektarbeit_V2/Mannschaftsmenue\" method = \"POST\">");
            out.println("<h2>Mannschaften</h2>");
            for(int i=0; i<list.size();i++){
                id = list.get(i).get(0);
                out.println("<input type=\"radio\" name=\"mannschaftsauswahl\" value=\""+id+"\">");
                out.println(list.get(i).get(1) + "<BR>");
            }
            out.println("<BR><input type=\"submit\" name=\"mannschaft_loeschen\" value=\"Mannschaft l&ouml;schen \">");
            out.println("<input type=\"submit\" name=\"mannschaft_bearbeiten\" value=\"Mannschaft bearbeiten \">");
            out.println("<input type=\"submit\" name=\"mannschaft_hinzufuegen\" value=\"neue Mannschaft hinzuf&uuml;gen \">");
            out.println("<input type=\"submit\" name=\"zurueck_Managermenue\" value=\"zur&uuml;ck\"><BR><BR>");
            
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    
    
    /**
     * Mehtode mit der Darstellung der HTML-Seite zum bearbeiten von Mannschaften
     * 
     * @param request
     * @param response
     * @param id
     * @throws ServletException
     * @throws IOException
     * @throws SQLException 
     */
    protected void mannschaftBearbeiten(HttpServletRequest request, HttpServletResponse response, String id)
            throws ServletException, IOException, SQLException { 
   
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Mannschaft bearbeiten</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<form action=\"http://localhost:8080/Projektarbeit_V2/Mannschaftsmenue\" method = \"POST\">");
            
            out.println("<input type=\"hidden\" name=\"mannschaft_geaendert\" value=\""+id+"\">");
            out.println("<h2>Mannschaft bearbeiten</h2><BR>");
            
            out.println("<input type=\"text\" id=\"mannschaft\" name=\"mannschaft\" ><br>");
            out.println("<input type=\"submit\" name=\"aendern_Mannschaft\" value=\"&auml;ndern\"><BR><BR>");
            out.println("<input type=\"submit\" name=\"zurueck\" value=\"zur&uuml;ck\"><BR><BR>");
            
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    
    
    /**
     * Methode mit der Darstellung der HTML-Seite zum erstellen einer neuen Mannschaft
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws SQLException 
     */
    protected void mannschaftHinzufuegen(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException { 
   
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Mannschaft hinzuf&uuml;gen</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<form action=\"http://localhost:8080/Projektarbeit_V2/Mannschaftsmenue\" method = \"POST\">");
            
            out.println("<h2>Mannschaft hinzuf&uuml;gen</h2><BR>");
            out.println("<input type=\"text\" id=\"mannschaft\" name=\"mannschaft\" ><br>");
            
            out.println("<input type=\"submit\" name=\"speichern_Mannschaft\" value=\"speichern\"><BR><BR>");
            out.println("<input type=\"submit\" name=\"zurueck\" value=\"zur&uuml;ck\"><BR><BR>");
            
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    
    
    /**
     * Methode mit der Datstellung der Fehlermeldung-HTML-Seite des Mannschaftsmenues
     * Allgemeine Fehlerseite 
     * 
     * @param request
     * @param response
     * @throws IOException 
     */
    protected void fehlermeldung(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setContentType("text/html;charset=UTF-8");
        try(PrintWriter out = response.getWriter()){
            out.println("<DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Fehler Mannschaftsmenue</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<form action=\"http://localhost:8080/Projektarbeit_V2/Mannschaftsmenue\" method = \"POST\">");
            
            out.println("<h2>Fehler</h2><BR>");
            out.println("Leider gab es ein Problem, bitte versuchen Sie es erneut!<BR>");
            
            out.println("<input type=\"submit\" name=\"zurueck\" value=\"zur&uuml;ck\"><BR><BR>");
            
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    
    
    /**
     * Methode mit der Darstellung der speziellen Fehlermeldungs-HTML-Seite des Mannschaftsmenues
     * Fehlerseite, wenn eine Mannschaft geloescht werden moechte, die bereits in Spielen gespeichert ist
     * 
     * @param request
     * @param response
     * @throws IOException 
     */
    protected void fehlermeldungLoeschen(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setContentType("text/html;charset=UTF-8");
        try(PrintWriter out = response.getWriter()){
            out.println("<DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Fehler beim L&ouml;schen der Mannschaft</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<form action=\"http://localhost:8080/Projektarbeit_V2/Mannschaftsmenue\" method = \"POST\">");
            
            out.println("<h2>Fehler</h2><BR>");
            out.println("Die Manschaft ist bereits in einem oder mehreren Spielen gespeichert<BR>");
            out.println("wenn Sie die Mannschaft trotzdem l&ouml;schen wollen, bitte erst die Spiele l&ouml;schen.<BR>");
            
            out.println("<input type=\"submit\" name=\"zurueck\" value=\"zur&uuml;ck\"><BR><BR>");
            out.println("</body>");
            out.println("</html>");
        }
    }
     
    
    
    /**
     * Methode zur Darstellung der HTML-Fehlerseite wenn die Session abgelaufen ist
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
            Logger.getLogger(Mannschaftsmenue.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(Mannschaftsmenue.class.getName()).log(Level.SEVERE, null, ex);
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
