/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.Benutzer;


import services.DBBenutzer;
import services.DBVerbindung;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import services.Pruefmethoden;

/**
 * Klasse mit der Menuestruktur fuer das Tippmenue, auf dem die eigenen Tipps angegeben
 * werden, sowie die Moeglichkeit neue Tipps anzugeben, beziehungsweise eigene Tipps
 * zu aendern
 *
 * @author Annika Petry
 * @author Timo Flake
 */
public class Tippmenue extends HttpServlet {
    private DBBenutzer benutzerDB;
    protected DBVerbindung verbindungDB;
    
    @Override
    public void init(){
        verbindungDB= DBVerbindung.dbVerbindung;
        benutzerDB=new DBBenutzer();
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
        if( session == null )fehlermeldungSession(request, response);
            
        if(request.getParameter("tipp_speichern") != null){
            String tipp1;
            String tipp2;
            String[] temp = request.getParameterValues("nicht_getippt");
            String benutzername= (String) session.getAttribute("user");
            Pruefmethoden pruefmethoden = new Pruefmethoden();
            //prueft ob Spiele ausgewaehlt wurden
            if(temp==null) fehlermeldung(request, response);
            //speichert fuer jedes ausgewaehlte Spiel die eingegebenen tipps
            for(int i=0; i<temp.length; i++){
                ArrayList<String> zeit=benutzerDB.getZeitID(verbindungDB, temp[i]);
                //prueft ob auf ein Spiel inzwischen kein Tipp mehr abgegeben werden darf
                if(pruefmethoden.anstosszeitPruefen(zeit.get(0), zeit.get(1))){
                    tipp1 = request.getParameter("tipp1_"+temp[i]);
                    tipp2 = request.getParameter("tipp2_"+temp[i]);
                    //prueft ob in Tipps keine leere Angabe gemacht wurde
                    if(tipp1.isEmpty()||tipp2.isEmpty()) fehlermeldung(request, response);
                    benutzerDB.tippSpeichern(verbindungDB, temp[i], tipp1, tipp2, benutzername);
                }
            }
            tippmenue(request, response);
        }
        else if(request.getParameter("Tipp_aendern") != null){
            String tipp1;
            String tipp2;
            String[] temp = request.getParameterValues("neu_getippt");
            String benutzername= (String) session.getAttribute("user");
            Pruefmethoden pruefmethoden = new Pruefmethoden();
            //preuft ob spiele ausgewaehlt wurden
            if(temp==null) fehlermeldung(request, response);
            //aendert fuer jedes ausgewaehlte Spiel die Tipps
            for(int i=0; i<temp.length;i++){
                ArrayList<String> zeit=benutzerDB.getZeitID(verbindungDB, temp[i]);
                //prueft ob auf ein Spiel inzwischen kein Tipp mehr abgegeben werden darf
                if(pruefmethoden.anstosszeitPruefen(zeit.get(0), zeit.get(1))){
                    tipp1 = request.getParameter("tipp1_"+temp[i]);
                    tipp2 = request.getParameter("tipp2_"+temp[i]);
                    //prueft ob fuer tipps keine leere Angabe getaetigt wurde
                    if(tipp1.isEmpty()||tipp2.isEmpty()) fehlermeldung(request, response);
                    benutzerDB.tippAendern(verbindungDB, temp[i], tipp1, tipp2, benutzername);
                }
            }
            tippmenue(request, response);
        }
        else if(request.getParameter("zurueck") != null){
            tippmenue(request, response);
        }
        else if(request.getParameter("zurueck_Benutzermenue") != null){
            Benutzermenue benutzer = new Benutzermenue ();
            benutzer.benutzermenue(request, response);
        }
    }
        
    
    
    /**
     * Methode zur Darstellung der HTML-Seite der Tippmenuestrukturm mit einer Liste
     * der noch offenen Spiele, auf die noch nicht getippt wurde, einer Liste der 
     * Spiele auf die der Benutzer bereits getippt hat und der Moeglichkeit entweder
     * neue Tipps abzugeben oder Tipps zu aendern
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws SQLException 
     * @throws java.text.ParseException 
     */
    protected void tippmenue(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException, ParseException {
        HttpSession session = request.getSession(false);
        if(session!=null){
            
            Pruefmethoden pruefmethoden = new Pruefmethoden();
            ArrayList<ArrayList<String>> list;
            list = new ArrayList<>();
            String id;
            String benutzername;


            benutzername= (String) session.getAttribute("user");

            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Benutzermenue</title>");            
                out.println("</head>");
                out.println("<body>");
                out.println("<form action=\"http://localhost:8080/Projektarbeit_V2/Tippmenue\" method=\"POST\">");

                out.println("<h2>Spiele, auf die Sie noch nicht getippt haben: </h2>");
                //Liste der nicht getippten Spiele Reihenfolge der Elemente:
                //Mannschaft1, Mannschaft2, Anstossdatum, Anstosszeit
                list = benutzerDB.spieleNichtGetippt(verbindungDB, benutzername);
                for(int i=0; i<list.size(); i++){
                    if(pruefmethoden.anstosszeitPruefen(list.get(i).get(2), list.get(i).get(3))){
                        id=list.get(i).get(4);
                        out.println("<input type=\"checkbox\" name=\"nicht_getippt\" value=\""+id+"\">");
                        out.println(list.get(i).get(0));
                        out.println(" : " +list.get(i).get(1));
                        out.println("    " +list.get(i).get(2));
                        out.println("    "+ list.get(i).get(3));
                        out.println("<input type=\"number\" id=\"tipp1_"+id+"\" name=\"tipp1_"+id+"\" min=\"0\" step=\"1\" value=\"0\">");
                        out.println(":");
                        out.println("<input type=\"number\" id=\"tipp2_"+id+"\" name=\"tipp2_"+id+"\" min=\"0\" step=\"1\" value=\"0\"><BR>");
                    }
                }

                out.println("<input type=\"submit\" name=\"tipp_speichern\" value=\"Tipps speichern\">");

                out.println("<h2>Ihre noch &auml;nderbaren Tipps: </h2>");
                // Liste der bereits getippten Spiele Reihenfolge der Elemente:
                //Mannschaft1, Mannschaft2, Anstossdatum, Anstosszeit
                list = benutzerDB.spieleGetippt(verbindungDB, benutzername);
                for(int i=0; i<list.size(); i++){
                    if(pruefmethoden.anstosszeitPruefen(list.get(i).get(2), list.get(i).get(3))){
                        String tipptore1;
                        String tipptore2;
                        id=list.get(i).get(6);
                        out.println("<input type=\"checkbox\" name=\"neu_getippt\" value=\""+id+"\">");
                        out.println(list.get(i).get(0));
                        out.println(" : " +list.get(i).get(1));
                        out.println("    "+ list.get(i).get(2));
                        out.println("    "+ list.get(i).get(3));
                        tipptore1 = list.get(i).get(4);
                        tipptore2 = list.get(i).get(5);
                        out.println("<input type=\"number\" id=\"tipp1_"+id+"\" name=\"tipp1_"+id+"\" min=\"0\" step=\"1\" value=\""+tipptore1+"\">");
                        out.println(":");
                        out.println("<input type=\"number\" id=\"tipp2_"+id+"\" name=\"tipp2_"+id+"\" min=\"0\" step=\"1\" value=\""+tipptore2+"\"><BR>");
                    }
                }

                out.println("<input type=\"submit\" name=\"Tipp_aendern\" value=\"Tipps &auml;ndern\"><BR><BR>");
                out.println("<input type=\"submit\" name=\"zurueck_Benutzermenue\" value=\"zur&uuml;ck\"><BR>");

                out.println("</form>");
                out.println("</body>");
                out.println("</html>");
            }
        }
    }
    
    
    /**
     * Methode zur Darstellung der allgemeinen Fehlerseite des Tippmenues
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
            out.println("<title>Fehlermeldung Tippmenue</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<form action=\"http://localhost:8080/Projektarbeit_V2/Tippmenue\" method=\"POST\">");
            
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
            Logger.getLogger(Tippmenue.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(Tippmenue.class.getName()).log(Level.SEVERE, null, ex);
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
