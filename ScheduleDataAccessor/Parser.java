/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ScheduleDataAccessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

/**
 *
 * @author Georgios
 */
public class Parser {

    static String Course;
    static String[] subjects = {
        "ACCT", "AD", "ARC", "ARCH", "ART", "AS", "BINF", "BIOL", "BME", "BNFO", "BUS",
        "CE", "CET", "CHE", "CHEM", "CIMT", "CMT", "COM", "COOP", "CPT", "CS", "DD", "ECE", "ECET", "ECON",
        "EM", "ENE", "ENG", "ENR", "ENTR", "EPS", "ESC", "ET", "EVSC", "FED", "FIN",
        "FRSH", "HIST", "HRM", "HSS", "HST", "HUM", "ID", "IE", "IM", "INT", "IS", "IT", "LIT", "MARC", "MATH",
        "ME", "MECH", "MET", "MGMT", "MIP", "MIS", "MNE", "MNET", "MR", "MRKT", "MTH", "MTSE", "OM", "OPSE",
        "OSIH", "PE", "PHEN", "PHIL", "PHYS", "PTC", "R014", "R070", "R074", "R080", "R082", "R120", "R160",
        "R165", "R198", "R202", "R215", "R220", "R300", "R350", "R352", "R375", "R390",
        "R420", "R460", "R478", "R510", "R512", "R546", "R560", "R565", "R580", "R620", "R630", "R640", "R645", "R685", "R700", "R701", "R730", "R750", "R790", "R799",
        "R810", "R830", "R834", "R860", "R910", "R920", "R940", "R965", "R970", "R977", "R988", "RUTG",
        "SET", "STS", "THTR", "TRAN","TUTR","UMD","URB",};

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        parce();
    }

    public static void parce() {
        try {
            HTMLEditorKit.ParserCallback callback =
                    new HTMLEditorKit.ParserCallback() {

                        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("courses.txt", true)));
                        
                        HTML.Tag lastTag  = null;

                        public void handleStartTag(HTML.Tag t,
                                MutableAttributeSet a, int pos) {
                            lastTag = t;
                        }

                        public void handleText(char[] data, int pos) {
                            if(lastTag == HTML.Tag.TH) {
                                return;
                            }
                            
                            out.println(new String(data));
                        }

                        public void handleEndTag(HTML.Tag t, int pos) {
                            if(t == HTML.Tag.I) {
                                out.println("</semester>");
                            } else if( t == HTML.Tag.H1) {
                                out.println("</subject>");
                            } else if( t == HTML.Tag.A ) {
                                out.println("</number>");
                            } else if( t == HTML.Tag.U ) {
                                out.println("</title>");
                            } else if ( t == HTML.Tag.TR ) {
                                if( lastTag != HTML.Tag.TH ) {
                                    out.println("</section>");
                                }
                            }
                            else if( t == HTML.Tag.TD ) {
                                out.println("</data>");
                            }
                            else if( t == HTML.Tag.TABLE ) {
                                out.println("</course>");
                            }
                        }
                    };

            try {
                for (String subject : subjects) {
                    System.out.println("Getting " + subject + " courses...");
                    String url = String.format("http://www.njit.edu/registrar/schedules/courses/fall/2009F.%s.html", subject);
                    URL address = new URL(url);
                    URLConnection newconn = address.openConnection();
                    newconn.connect();
                    BufferedReader in = new BufferedReader(new InputStreamReader(address.openStream()));
                    new ParserDelegator().parse(in, callback, false);
                    in.close();
                }
            } catch (Exception e) {
            }

        } catch (Exception f) {
        }
    }
}
