/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.Anmelden;

import services.DBVerbindung;
import services.DBVerwaltung;
import services.BenutzerSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import servlets.Benutzer.Benutzermenue;
import servlets.Manager.Managermenue;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpSession;


/**
 * Allgemeines Startmenue und weiterleitung an die betreffenden
 * speziellen Menues (Benutzer und Manager)
 * 
 * @author Annika Petry
 * @author Timo Flake
 * 
 */
public class Anmelden extends HttpServlet {
    private DBVerbindung verbindung;
    private DBVerwaltung verwaltung;
     
    /**
     * Init zur Initialisierung der Datenbankenverbindung zum erstellen eines
     * Verwaltungsobjekts der DB-Verwaltung der Strukturierungslasse
     * 
     * @param config
     * @throws ServletException 
     */
    @Override
    public void init(ServletConfig config) throws ServletException{
        super.init(config);
        verbindung=DBVerbindung.dbVerbindung;
        verwaltung = new DBVerwaltung();
    }
    
    
    
    /**
     * Leitet die Anfragen weiter fuer beide HTTP <code>GET</code> und <code>POST</code>
     * Methoden.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ClassNotFoundException, SQLException {
        
        if(request.getParameter("registrieren")!=null){
            registrieren(request, response);
        }
        else if(request.getParameter("zurueck")!=null){
            response.sendRedirect("index.html");
        }
        else if(request.getParameter("anmelden")!=null){
            anmelden(request, response);
        }
    }
    

    
    /**
     * Ueberpruefung ob das angegebene Passwort zum Benutzernamen passt,
     * ggf. Weiterleitung an betreffende Menues oder Fehlerseite
     * 
     * @param request
     * @param response
     * @throws SQLException
     * @throws ServletException
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    protected void anmelden (HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException, ClassNotFoundException{

        String benutzername = request.getParameter("benutzername");
        String passwort = request.getParameter("passwort");
        
        //prueft ob bereits eine Session besteht,
        //wenn das der Fall ist wird diese beendet und eine neue Session gestartet
         HttpSession session = request.getSession(false);
         if(session!=null){
             session.invalidate();
         }
         session = request.getSession(true);
            
         //Stimmen die eingegebenen Anmeldedaten mit denen der Datenbank uberein
        if(verwaltung.anmeldungBenutzer(verbindung, benutzername, passwort)){
            session.setAttribute("user", benutzername);
            BenutzerSession benutzerDaten = (BenutzerSession) session.getAttribute("benutzerDaten");
            if (benutzerDaten == null) {
                benutzerDaten = new BenutzerSession();
                //loest die valueBound Methode des HttpSessionBindingListener aus
                request.getSession().setAttribute("benutzerDaten", benutzerDaten);
            }
            
            Benutzermenue benutzer = new Benutzermenue();
            benutzer.benutzermenue(request, response);
        }
        //Stimmen die eingegebenen Anmeldedaten mit denen der Datenbank uberein
        else if(verwaltung.anmeldungManager(verbindung, benutzername, passwort)){
            session.setAttribute("user", benutzername);
            session.setAttribute("manager", verwaltung.listeManager(verbindung));
            BenutzerSession benutzerDaten = (BenutzerSession) session.getAttribute("benutzerDaten");
            if (benutzerDaten == null) {
                benutzerDaten = new BenutzerSession();
                //loest die valueBound Methode des HttpSessionBindingListener aus
                request.getSession().setAttribute("benutzerDaten", benutzerDaten);
            }
            Managermenue manager = new Managermenue();
            manager.managermenue( request, response);
        } 
        else{
            fehlerAnmeldung(request, response);
        }
    }
    
    
    
    /**
     * Ueberpruefung, ob der gewaehlte Benutzername schon vergeben ist,
     * sonst speichern der Anmeldedaten.
     * Weiterleitung ggf. an betreffende Menues oder Fehlerseite
     * 
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws ServletException 
     */
    protected void registrieren (HttpServletRequest request, HttpServletResponse response) 
            throws IOException, SQLException, ClassNotFoundException, ServletException{    

        String benutzername = request.getParameter("benutzername");
        String passwort = request.getParameter("passwort");
        
        //prueft ob es bereit eine Session besteht,
        //wenn das der Fall ist wird diese beendet und eine neue Session gestartet
        HttpSession session = request.getSession(false);
        if(session!=null){
            session.invalidate();
        }
        session = request.getSession(true);
            
        //Stimmen die eingegebenen Anmeldedaten mit denen der Datenbank uberein
        if(verwaltung.benutzernamePruefen(verbindung, benutzername)){
            verwaltung.benutzerHinzufuegen(verbindung, benutzername, passwort);
            
            session.setAttribute("user", benutzername);
            BenutzerSession userData = (BenutzerSession) session.getAttribute("userData");
            if (userData == null) {
                userData = new BenutzerSession();
                //loest die valueBound Methode des HttpSessionBindingListener aus
                request.getSession().setAttribute("userData", userData);
            }
            
            Benutzermenue benutzer = new Benutzermenue();
            benutzer.benutzermenue(request, response);
        }
        else{
            fehlerRegistrierung(request, response);
        }
    }
    
    
    
    /**
     * Fehlerseite bei Anmeldung mit nicht passendem Benutzernamen oder Passwort
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    protected void fehlerAnmeldung(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Fehler Anmeldung</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<form action=\"http://localhost:8080/Projektarbeit_V2/Anmelden\" method = \"POST\">");
            
            out.println("<h2>Fehler</h2><BR>");
            out.println("Fehler beim Anmelden, Benutzername oder Passwort falsch<BR>");
            
            out.println("<input type=\"submit\" name=\"zurueck\" value=\"zur&uuml;ck\"><BR><BR>");
            
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    
    
    /**
     * Fehlerseite bei Registrierung mit bereits verwendetem Benutzernamen
     * 
     * @param request
     * @param response
     * @throws IOException 
     */
    protected void fehlerRegistrierung(HttpServletRequest request, HttpServletResponse response) 
            throws IOException{
        response.setContentType("text/html;charset=UTF-8");
        try(PrintWriter out = response.getWriter()){
            out.println("<DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Fehler Registrierung</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<form action=\"http://localhost:8080/Projektarbeit_V2/Anmelden\" method = \"POST\">");
            
            out.println("<h2>Fehler</h2><BR>");
            out.println("Fehler beim Registrieren.<BR> Benutzername wird bereits verwendet.<BR>");
            
            out.println("<input type=\"submit\" name=\"zurueck\" value=\"zur&uuml;ck\"><BR><BR>");
            
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
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Anmelden.class.getName()).log(Level.SEVERE, null, ex);
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
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Anmelden.class.getName()).log(Level.SEVERE, null, ex);
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
