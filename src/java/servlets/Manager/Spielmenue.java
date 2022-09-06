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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import services.Pruefmethoden;


/**
 * Klasse mit der Menuestruktur des Spielmenues (Zugriff nur fuer den Manager)
 *
 * @author Annika Petry
 * @author Timo Flake
 */
public class Spielmenue extends HttpServlet {
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
     * @throws java.text.ParseException
     */ 
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException, ParseException {
        //prueft ob Session noch aktiv
        HttpSession session = request.getSession(false);
        if( session == null ) fehlermeldungSession(request, response);
        
        if(request.getParameter("neues_Spiel")!=null){
            spielHinzufuegen(request, response);
        }
        else if(request.getParameter("speichern_Spiel")!=null){
            String team1 = request.getParameter("Mannschaften_DropDown1");
            String team2 = request.getParameter("Mannschaften_DropDown2");
            String turnier = request.getParameter("Turnier_DropDown");
            String anstossDatum = request.getParameter("anstoss");
            String anstossZeit = request.getParameter("anstoss_uhrzeit");
            Pruefmethoden pruefmethoden = new Pruefmethoden();
            
            //prueft ob das Spiel in der Zukunft liegt und ob zwei unterschiedliche Teams gegeneinader spielen
            if(pruefmethoden.spielSpeichernPruefen(team1, team2, anstossDatum, anstossZeit)){
                managerDB.spielHinzufuegen(verbindungDB, team1, team2, turnier, anstossDatum, anstossZeit);
                spielemenue(request, response);
            }
            else{
                fehlermeldung(request, response);
            }
        }
        else if(request.getParameter("spiel_sperren")!=null){
            String[] temp = request.getParameterValues("sperren");
            //prueft ob Spiele ausgewaehlt wurden
            if(temp==null) fehlermeldung(request, response);
            for(int i=0; i<temp.length;i++){
                managerDB.spielSperren(verbindungDB, temp[i]);
            }
            spielemenue(request, response);
        }
        else if(request.getParameter("spiel_entsperren") != null){
            String[] id = request.getParameterValues("ergebnis");
            //prueft ob Spiele ausgewaehlt wurden
            if(id==null) fehlermeldung(request, response);
            for(int i=0; i<id.length; i++){
                managerDB.spielEntsperren(verbindungDB, id[i]);
            }
            spielemenue(request, response);
        }
        else if(request.getParameter("spiel_bearbeiten")!= null){
            String id[]=request.getParameterValues("sperren");
             //prueft ob ein Spiel ausgewaehlt wurde
            if(id==null || id.length!=1)fehlermeldung(request, response);
            spielBearbeiten(request, response, id[0]);
        }
        else if(request.getParameter("aendern_Spiel")!= null){
            String id=request.getParameter("spiel_geaendert");
            String team1 = request.getParameter("Mannschaften_DropDown1");
            String team2 = request.getParameter("Mannschaften_DropDown2");
            String turnier = request.getParameter("Turnier_DropDown");
            String anstossDatum = request.getParameter("anstoss");
            String anstossZeit = request.getParameter("anstoss_uhrzeit");
            Pruefmethoden pruefmethoden = new Pruefmethoden();
            
            if(pruefmethoden.spielSpeichernPruefen(team1, team2, anstossDatum, anstossZeit)){
                managerDB.spielBearbeiten(verbindungDB, team1, team2, turnier, anstossDatum, anstossZeit, id);
                spielemenue(request, response);
            }
            else{
                fehlermeldung(request, response);
            }
        }
        else if(request.getParameter("ergebnis_speichern")!=null){
            String erg1;
            String erg2;
            String[] temp = request.getParameterValues("ergebnis");
             //prueft ob Spiele ausgewaehlt wurden
            if(temp==null) fehlermeldung(request, response);
            for(int i=0; i<temp.length;i++){
                erg1 = request.getParameter("erg1_"+temp[i]);
                erg2 = request.getParameter("erg2_"+temp[i]);
                //prueft ob keine leeren angaben fuer Ergebnisse gemacht wurden
                if(erg1.isEmpty() || erg2.isEmpty()) fehlermeldung(request, response);
                managerDB.ergebnisSpeichern(verbindungDB, temp[i], erg1, erg2);
            }
            spielemenue(request, response);
        }
        else if(request.getParameter("abgeschlossene_Spiele")!=null){
            abgeschlosseneSpiele(request, response);
        }
        else if(request.getParameter("spiel_loeschen") != null){
            String id[]=request.getParameterValues("sperren");
            //prueft ob Spiele ausgewaehlt wurden
            if(id==null) fehlermeldung(request, response);
            for(int i=0; i<id.length;i++){
                managerDB.spielLoeschen(verbindungDB, id[i]);
            }
            spielemenue(request, response);
        }
        else if(request.getParameter("zurueck") != null){
            spielemenue(request, response);
        }
        else if(request.getParameter("zurueck_managermenue") != null){
            Managermenue manager = new Managermenue();
            manager.managermenue(request, response);
        }
    }
    
    
    
    /**
     * Methode zur Darstellung der HTML-Seite mit der Menuestruktur der Spiele
     * Darstellung der offenen und geschlossenen Spiele, mit der Moeglichkeit, 
     * Spiele zu erstellen, bearbeiten, sperren, entsperren, Ergebnisse einzutragen
     * und Spiele zu loeschen.
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws SQLException 
     * @throws java.text.ParseException 
     */
    protected void spielemenue(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException, ParseException {
        
        ArrayList<ArrayList<String>> list;
        list = new ArrayList<>();
        String ID;
        
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Spielmenue</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<form action=\"http://localhost:8080/Projektarbeit_V2/Spielmenue\" method=\"POST\">");
            
            out.println("<h2>offene Spiele:</h2>");
            //Liste mit nicht gesperrten Spielen
            //Reihenfolge: Mannschaft1, Mannschaft2, Anstossdatum, Anstosszeit, Turnier
            list = managerDB.listeSpieleOffen(verbindungDB);
            Pruefmethoden pruefmethoden = new Pruefmethoden();
            for(int i=0; i<list.size();i++){
                //prueft ob das Anstossdatum und die Anstosszeit eines Spieles bereits ueberschritten wurde und sperrt diese 
                if(pruefmethoden.anstosszeitPruefen(list.get(i).get(2), list.get(i).get(3))){
                ID = list.get(i).get(5);
                out.println("<input type=\"checkbox\" name=\"sperren\" value=\""+ID+"\">");
                out.println(list.get(i).get(0));
                out.println(" : " + list.get(i).get(1));
                out.println("    " + list.get(i).get(2));
                out.println("    "+ list.get(i).get(3));
                out.println("    Turnier: "+ list.get(i).get(4) + "<BR>");
                }
                else{
                    managerDB.spielSperren(verbindungDB, list.get(i).get(5));
                }
            }
            out.println("<input type=\"submit\" name=\"spiel_sperren\" value=\"Spiele sperren\">");
            out.println("<input type=\"submit\" name=\"spiel_bearbeiten\" value=\"Spiel bearbeiten\">");
            out.println("<input type=\"submit\" name=\"spiel_loeschen\" value=\"Spiele l&ouml;schen\"><BR><BR>");
            
            out.println("<h2>gesperrte Spiele:</h2>");
            //Liste mit gesperrten Spielen
            //Reihenfolge: Mannschaft1, Mannschaft2, Anstossdatum, Anstosszeit, Turnier
            list = managerDB.listeSpieleGesperrt(verbindungDB);
            for(int i=0; i<list.size();i++){
                ID = list.get(i).get(5);
                out.println("<input type=\"checkbox\" name=\"ergebnis\" value=\""+ID+"\">");
                out.println(list.get(i).get(0));
                out.println(" : " + list.get(i).get(1));
                out.println("    " + list.get(i).get(2));
                out.println("    "+ list.get(i).get(3));
                out.println("    Turnier: "+ list.get(i).get(4));
                out.println("<input type=\"number\" id=\"erg1_"+ID+"\" name=\"erg1_"+ID+"\"  min=\"0\" step=\"1\" value=\"0\">");
                out.println(":");
                out.println("<input type=\"number\" id=\"erg2_"+ID+"\" name=\"erg2_"+ID+"\" min=\"0\" step=\"1\" value=\"0\" ><BR>");
            }
            out.println("<input type=\"submit\" name=\"ergebnis_speichern\" value=\"Ergebnisse speichern\">");
            out.println("<input type=\"submit\" name=\"spiel_entsperren\" value=\"Spiele entsperren\"><BR><BR>");
            out.println("<input type=\"submit\" name=\"neues_Spiel\" value=\"neues Spiel\">");
            out.println("<input type=\"submit\" name=\"abgeschlossene_Spiele\" value=\"abgeschlossene Spiele\">");
            out.println("<input type=\"submit\" name=\"zurueck_managermenue\" value=\"zur&uuml;ck\">");
            
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    
    
    /**
     * Methode zum bearbeiten von Spielen
     * 
     * @param request
     * @param response
     * @param id
     * @throws ServletException
     * @throws IOException
     * @throws SQLException 
     */
    protected void spielBearbeiten(HttpServletRequest request, HttpServletResponse response, String id)
            throws ServletException, IOException, SQLException {

        ArrayList<ArrayList<String>> list_Mannschaften = managerDB.mannschaftsListe(verbindungDB);
        ArrayList<ArrayList<String>> list_Turnier = managerDB.turnierListe(verbindungDB);
        
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
             
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Spiel bearbeiten</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<form action=\"http://localhost:8080/Projektarbeit_V2/Spielmenue\" method = \"POST\">");
            
            out.println("<input type=\"hidden\" name=\"spiel_geaendert\" value=\""+id+"\">");
            out.println("<h2>Spiel bearbeiten</h2>");
            
            out.println("<form action = \"select.html\">");
            out.println("Mannschaften:");
            out.println("<select name=\"Mannschaften_DropDown1\" size=\"1\">");
            for(int i=0; i<list_Mannschaften.size();i++){
                out.println("<option value="+list_Mannschaften.get(i).get(0)+">"+list_Mannschaften.get(i).get(1)+"</option>");
            } 
            out.println("</select>");
            
            out.println("<form action = \"select.html\">");
            out.println("<select name=\"Mannschaften_DropDown2\" size=\"1\">");
            for(int i=0; i<list_Mannschaften.size();i++){
                out.println("<option value="+list_Mannschaften.get(i).get(0)+">"+list_Mannschaften.get(i).get(1)+"</option>");
            } 
            out.println("</select><BR><BR>");
            
            out.println("Turnier");
            
            out.println("<form action = \"select.html\">");
            out.println("<select name=\"Turnier_DropDown\" size=\"1\">");
            for(int i=0; i<list_Turnier.size();i++){
                out.println("<option value="+list_Turnier.get(i).get(0)+">"+list_Turnier.get(i).get(1)+"</option>");
            } 
            out.println("</select><BR><BR>");
            
            out.println("Geben Sie die Anstoßzeit an:");
            
            out.println("<input type=\"date\" name=\"anstoss\">");
            out.println("<input type=\"time\" name=\"anstoss_uhrzeit\"><BR>");
            out.println("<input type=\"submit\" name=\"aendern_Spiel\" value=\"&auml;ndern\">");
            out.println("<input type=\"submit\" name=\"zurueck\" value=\"zur&uuml;ck\">");
            
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    
    
    /**
     * Methode, um neue Spiele hinzuzufuegen, mit der eingabe von zwei Mannschaften,
     * dem Turnier, Datum und Uhrzeit
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws SQLException 
     */
    protected void spielHinzufuegen(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        ArrayList<ArrayList<String>> list_Mannschaften = managerDB.mannschaftsListe(verbindungDB);
        ArrayList<ArrayList<String>> list_Turnier = managerDB.turnierListe(verbindungDB);
        
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Spiel hinzuf&uuml;gen</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<form action=\"http://localhost:8080/Projektarbeit_V2/Spielmenue\" method = \"POST\">");
            
            out.println("<h2>Spiel hinzuf&uuml;gen</h2>");
            
            out.println("<form action = \"select.html\">");
            out.println("Mannschaften:");
            out.println("<select name=\"Mannschaften_DropDown1\" size=\"1\">");
            for(int i=0; i<list_Mannschaften.size();i++){
                out.println("<option value="+list_Mannschaften.get(i).get(0)+">"+list_Mannschaften.get(i).get(1)+"</option>");
            } 
            out.println("</select>");
            
            out.println("<form action = \"select.html\">");
            out.println("<select name=\"Mannschaften_DropDown2\" size=\"1\">");
            for(int i=0; i<list_Mannschaften.size();i++){
                out.println("<option value="+list_Mannschaften.get(i).get(0)+">"+list_Mannschaften.get(i).get(1)+"</option>");
            } 
            out.println("</select><BR><BR>");
            
            out.println("Turnier");
            
            out.println("<form action = \"select.html\">");
            out.println("<select name=\"Turnier_DropDown\" size=\"1\">");
            for(int i=0; i<list_Turnier.size();i++){
                out.println("<option value="+list_Turnier.get(i).get(0)+">"+list_Turnier.get(i).get(1)+"</option>");
            } 
            out.println("</select><BR><BR>");
            
            out.println("Geben Sie die Anstoßzeit an:");
            
            out.println("<input type=\"date\" name=\"anstoss\">");
            out.println("<input type=\"time\" name=\"anstoss_uhrzeit\"><BR>");
            out.println("<input type=\"submit\" name=\"speichern_Spiel\" value=\"speichern\">");
            out.println("<input type=\"submit\" name=\"zurueck\" value=\"zur&uuml;ck\">");
            
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
    }
   
    
    
    /**
     * Methode mit der HTML-Darstellung der abgeschlossenen Spiele.
     * Spiele gelten als abgeschlossen, wenn sie geschlossen wurden und ein 
     * Ergebnis eingetragen wurde.
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws SQLException 
     */
    protected void abgeschlosseneSpiele(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        ArrayList<ArrayList<String>> list;
        list = new ArrayList<>();
        
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>abgeschlossene Spiele</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<form action=\"http://localhost:8080/Projektarbeit_V2/Spielmenue\" method=\"POST\">");
            out.println("<h1>abgeschlossene Spiele:</h1>");
            
            out.println("<table>");
            out.println("<tr>");
            out.println("<th>Team1</th>");
            out.println("<th>Team2</th>");
            out.println("<th>Tore1</th>");
            out.println("<th>Tore2</th>");
            out.println("<th>Datum</th>");
            out.println("<th>Uhrzeit</th>");
            out.println("</tr>");
            
            list = managerDB.listeSpieleAbgeschlossen(verbindungDB);
            for(int i=0; i<list.size();i++){
                out.println("<tr>");
                out.println("<td>"+list.get(i).get(0)+"</td>");
                out.println("<td>"+list.get(i).get(1)+"</td>");
                out.println("<td>"+list.get(i).get(2)+"</td>");
                out.println("<td>"+list.get(i).get(3)+"</td>");
                out.println("<td>"+list.get(i).get(4)+"</td>");
                out.println("<td>"+list.get(i).get(5)+"</td>");
                out.println("</tr>");
            }
            out.println("</table><BR>");
            
            out.println("<input type=\"submit\" name=\"zurueck\" value=\"zur&uuml;ck\">");
            
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    
    
    /**
     * Methode mit allgemeiner Fehlerseite, fuer Fehler bei Eingaben etc.
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
                out.println("<title>Fehler Spielmenue</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("<form action=\"http://localhost:8080/Projektarbeit_V2/Spielmenue\" method = \"POST\">");
                
                out.println("<h2>Fehler</h2><BR>");
                out.println("Leider gab es ein Problem, bitte versuchen Sie es erneut!<BR>");
                
                out.println("<input type=\"submit\" name=\"zurueck\" value=\"zur&uuml;ck\"><BR><BR>");
                
                out.println("</form>");
                out.println("</body>");
                out.println("</html>");
            }
        }
    
    
    
    /**
     * Methode, zur Darstellung der HTML-Fehlerseite wenn die Session ablaeuft
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
            Logger.getLogger(Spielmenue.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(Spielmenue.class.getName()).log(Level.SEVERE, null, ex);
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
