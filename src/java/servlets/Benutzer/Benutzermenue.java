/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.Benutzer;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Klasse zur Darstellung des Benutzermenues mit Weiterleitung zu Punkte- und
 * Tippmenue
 *
 * @author Annika Petry
 * @author Timo Flake
 */
public class Benutzermenue extends HttpServlet {

    
    @Override
    public void init(ServletConfig config) throws ServletException{
        super.init(config);
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
     * @throws java.text.ParseException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException, ParseException {
        //prueft ob die Session noch aktiv ist
        HttpSession session = request.getSession(false);
        if( session == null ) fehlermeldungSession(request, response);
        
        if(request.getParameter("tippmenue") != null){
            Tippmenue tipp = new Tippmenue();
            tipp.init();
            tipp.tippmenue(request, response);
        }
        else if(request.getParameter("punktemenue") != null){
            Punktemenue punkte = new Punktemenue();
            punkte.init();
            punkte.punktemenue(request, response);
        }
        else if(request.getParameter("zurueck") != null){
            benutzermenue(request, response);
        }

        else if(request.getParameter("abmelden") != null){
            //beenden der Session
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect("index.html");
        }
    }
    
    
    
    /**
     * Methode zur Darstellung der Menuestruktur des Benutzermenues
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    public void benutzermenue(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Benutzermenue</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<form action=\"http://localhost:8080/Projektarbeit_V2/Benutzermenue\" method=\"POST\">");
            
            out.println("<h2> Aktivit&auml;t ausw&auml;hlen</h2>");
            out.println("Tipps verwalten und hinzuf&uuml;gen: <BR>");
            
            out.println("<input type=\"submit\" name=\"tippmenue\" value=\"Tippmen&uuml;\"><BR><BR>");
            out.println("Ergebnisse und Punktest&auml;nde abfragen: <BR>");
            
            out.println("<input type=\"submit\" name=\"punktemenue\" value=\"Ergebnisse\"><BR><BR>");
            out.println("<input type=\"submit\" name=\"abmelden\" value=\"abmelden\"><BR><BR>");
            
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    
    
    /**
     * Darstellung der HTML-Fehlerseite fuer Fehler im Benutzermenue
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    protected void fehlermeldung(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Fehlermeldung Benutzermenue</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<form action=\"http://localhost:8080/Projektarbeit_V2/Benutzermenue\" method=\"POST\">");
            
            out.println("<h2>Fehler</h2><BR>");
            out.println("Leider gab es ein Problem, bitte versuchen Sie es erneut!<BR>");
            
            out.println("<input type=\"submit\" name=\"zurueck\" value=\"zur&uuml;ck\"><BR>");
            
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
}
    
    
    
    /**
     * Methode zur Darstellung der HTML-Fehlerseite, wenn die Session abgelaufen ist
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
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(Benutzermenue.class.getName()).log(Level.SEVERE, null, ex);
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
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(Benutzermenue.class.getName()).log(Level.SEVERE, null, ex);
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
