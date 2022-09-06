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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 *Klasse mit der Menuestruktur fuer den Manager
 * 
 * @author Annika Petry
 * @author Timo Flake
 */
public class Managermenue extends HttpServlet {
    protected DBManager managerDB;
    protected DBVerbindung verbindungDB;
    
    @Override
    public void init(ServletConfig config) throws ServletException{
        super.init(config);
        verbindungDB= DBVerbindung.dbVerbindung;
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
     * @throws java.text.ParseException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException, ClassNotFoundException, ParseException {
        //prueft ob die Session noch aktiv ist
        HttpSession session = request.getSession(false);
        if( session == null ) fehlermeldungSession(request, response);
        
        if(request.getParameter("mannschaftsmenue")!=null){
            Mannschaftsmenue mannschaft = new Mannschaftsmenue();
            mannschaft.init();
            mannschaft.mannschaftsmenue(request, response);
        }
        else if(request.getParameter("turniermenue")!=null){
            Turniermenue turnier = new Turniermenue();
            turnier.init();
            turnier.turniermenue(request, response);
        }
        else if(request.getParameter("spielemenue")!=null){            
            Spielmenue spiel = new Spielmenue();
            spiel.init();
            spiel.spielemenue(request, response);
        }
        else if(request.getParameter("punkte") != null){            
            punkteliste(request, response);
        }
        else if(request.getParameter("zurueck")!=null){ 
            managermenue(request, response);
        }
        else if(request.getParameter("abmelden")!= null){
            //beender der Session (aufruf valueUnbound)
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect("index.html");
        }
    }
    
    
    
    /**
     * Menuestruktur des Managers, mit Weiterleitung zum Mannschats- Spiel- und
     * Turniermenue. 
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws SQLException 
     */
    public void managermenue(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Managermen&uuml;</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<form action=\"http://localhost:8080/Projektarbeit_V2/Managermenue\" method=\"POST\">");
            out.println("<h2> Aktivit&auml;t ausw&auml;hlen</h2>");

            out.println("Mannschften hinzuf&uuml;gen, bearbeiten und l&ouml;schen:  <BR>");
            out.println("<INPUT TYPE=\"submit\" name=\"mannschaftsmenue\" value=\"Mannschaftsmen&uuml;\"><BR><BR>");
            
            out.println("Turnier hinzuf&uuml;gen, bearbeiten und l&ouml;schen:  <BR>");
            out.println("<input type=\"submit\" name=\"turniermenue\" value=\"Turniermen&uuml;\"><BR><BR>");
 
            out.println("Spiele hinzuf&uuml;gen, bearbeiten, l&ouml;schen, sperren und Ergebnisse eintragen:  <BR>");
            out.println("<input type=\"submit\" name = \"spielemenue\" value = \"Spielmen&uuml;\"><BR><BR>");

            out.println("Punktest&auml;nde der Benutzer abfragen: <BR>");
            out.println("<input type=\"submit\" name=\"punkte\" value=\"Punktest&auml;nde abfragen\"><BR><BR>");

            out.println("<input type=\"submit\" name=\"abmelden\" value=\"abmelden\"><BR>");
            
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    
    
    /**
     * Methode zur Ausgabe einer Liste der Punktstaende der Benutzer, sortiert von
     * bestem Tipper, zum schlechtesten.
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws SQLException 
     */
    protected void punkteliste (HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException{
        ArrayList<ArrayList<String>> list;
        list = new ArrayList<>();
        
        response.setContentType("text/html;charset=UTF-8");
        try(PrintWriter out = response.getWriter()){
            out.println("<DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Rangliste</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<form action=\"http://localhost:8080/Projektarbeit_V2/Managermenue\" method = \"POST\">");
            out.println("<h2>Rangliste der Tipper: </h2>");
            
            list = managerDB.rangliste(verbindungDB);
            out.println("<ol>");
            for(int i=0; i<list.size(); i++){
                if(list.get(i).get(1)==null){
                    out.println("<li>"+list.get(i).get(0)+": 0</li>");
                }
                else{
                    out.println("<li>"+list.get(i).get(0)+": "+list.get(i).get(1)+"</li>");
                }
            }
            out.println("</ol>");
            out.println("<input type=\"submit\" name=\"zurueck\" value=\"zur&uuml;ck\"><BR><BR>");
            
            out.println("</form>");
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
    } catch (SQLException | ClassNotFoundException | ParseException ex) {
        Logger.getLogger(Managermenue.class.getName()).log(Level.SEVERE, null, ex);
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
    } catch (SQLException | ClassNotFoundException | ParseException ex) {
        Logger.getLogger(Managermenue.class.getName()).log(Level.SEVERE, null, ex);
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
