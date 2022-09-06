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
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Klasse zur Darstellung des Punktemenues, mit Angabe von abgeschlossenen Spielen,
 * mit Angabe des eigenen Puntestandes und der Angabe des Rangs, auf dem man sich
 * unter den Tippern befindet
 *
 * @author Annika Petry
 * @author Timo Flake
 */
public class Punktemenue extends HttpServlet {
    private DBBenutzer benutzerDB;
    protected DBVerbindung verbindungDB;
    
    @Override
    public void init(){
        verbindungDB=DBVerbindung.dbVerbindung;
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
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            //prueft ob die Session noch aktiv ist
            HttpSession session = request.getSession(false);
            if( session == null ) fehlermeldungSession(request, response);
            
            if(request.getParameter("zurueck") != null){
                Benutzermenue benutzer = new Benutzermenue();
                benutzer.benutzermenue(request, response);
            }
    }

    
    
    /**
     * Methode zur Darstellung der HTML-Seite, auf der die abgeschlossenen Spiele
     * angegeben werden.Außerdem werden hier, falls vorhanden, die eigenen Tipps,
     * der eigenen Punktestand und der Rang unter den Tippenden angezeigt
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws SQLException 
     */
    protected void punktemenue(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        
        ArrayList<ArrayList<String>> list;
        list = new ArrayList<>();
        
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Ergebnisse</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<form action=\"http://localhost:8080/Projektarbeit_V2/Punktemenue\" method=\"POST\">");
            
            HttpSession session = request.getSession(false);
            String benutzername= (String) session.getAttribute("user");
            
            out.println("<table>");
            out.println("<tr>");
            out.println("<th>Team1</th>");
            out.println("<th>Team2</th>");
            out.println("<th>Datum</th>");
            out.println("<th>Uhrzeit</th>");
            out.println("<th>Tore1</th>");
            out.println("<th>Tore2</th>");
            out.println("<th>Ihr Tipp1</th>");
            out.println("<th>Ihr Tipp2</th>");
            out.println("</tr>");
            
            out.println("<h1> gesperrte Spiele, ohne Ergebnis: </h1>");
            
            list = benutzerDB.spieleOhneErgebnis(verbindungDB, benutzername);
            for(int i=0; i<list.size(); i++){
                out.println("<tr>");
                out.println("<td>"+list.get(i).get(0)+"</td>");
                out.println("<td>"+list.get(i).get(1)+"</td>");
                out.println("<td>"+list.get(i).get(2)+"</td>");
                out.println("<td>"+list.get(i).get(3)+"</td>");
                out.println("<td>-</td>");
                out.println("<td>-</td>");
                if(list.get(i).get(4).equals("-1") && list.get(i).get(5).equals("-1")){
                    out.println("<td>-</td>");
                    out.println("<td>-</td>");
                }
                else{
                    out.println("<td>"+list.get(i).get(4)+"</td>");
                    out.println("<td>"+list.get(i).get(5)+"</td>");
                }
                out.println("</tr>");
            }
            out.println("</table><BR>");
            
            out.println("<h1> abgeschlossene Spiele: </h1>");
            
            out.println("<table>");
            out.println("<tr>");
            out.println("<th>Team1</th>");
            out.println("<th>Team2</th>");
            out.println("<th>Datum</th>");
            out.println("<th>Uhrzeit</th>");
            out.println("<th>Tore1</th>");
            out.println("<th>Tore2</th>");
            out.println("<th>Ihr Tipp1</th>");
            out.println("<th>Ihr Tipp2</th>");
            out.println("</tr>");
            
            list = benutzerDB.spieleAbgeschlossen(verbindungDB, benutzername);
            for(int i=0; i<list.size(); i++){
                out.println("<tr>");
                out.println("<td>"+list.get(i).get(0)+"</td>");
                out.println("<td>"+list.get(i).get(1)+"</td>");
                out.println("<td>"+list.get(i).get(2)+"</td>");
                out.println("<td>"+list.get(i).get(3)+"</td>");
                out.println("<td>"+list.get(i).get(4)+"</td>");
                out.println("<td>"+list.get(i).get(5)+"</td>");
                if(list.get(i).get(6).equals("-1") && list.get(i).get(7).equals("-1")){
                    out.println("<td>-</td>");
                    out.println("<td>-</td>");
                }
                else{
                    out.println("<td>"+list.get(i).get(6)+"</td>");
                    out.println("<td>"+list.get(i).get(7)+"</td>");
                }
                out.println("</tr>");
            }
            out.println("</table><BR>");
            
            list = benutzerDB.rangliste(verbindungDB);
            for(int i=0; i<list.size(); i++){
                if(list.get(i).get(0).equals(benutzername)){
                    if(list.get(i).get(1) == null){
                        out.println("Ihr Punktestand betr&auml;gt: 0<BR>");
                    }
                    else{
                        out.println("Ihr Punktestand betr&auml;gt: "+ list.get(i).get(1)+"<BR>");
                    }
                    out.println("Ihr Rang ist: "+(i+1)+" von "+(list.size())+" Tippern <BR>");
                }
            }
            
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
        processRequest(request, response);
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
        processRequest(request, response);
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
