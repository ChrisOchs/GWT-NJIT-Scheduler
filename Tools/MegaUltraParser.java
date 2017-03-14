/* NJIT Website class-getting parsing application
 *
 * USAGE:
 *
 * % java MegaUltraParser [semester] [year] [?todb]
 *
 * where
 * semester = summer, fall, winter, spring
 * year = dddd (eg 2009, 2010)
 * todb means to input into our NJIT SQL database, otherwise, just print
 * to a file
 *
 * @author Chris Hukushi
 */

package Tools;

import java.io.*;
import java.net.*;
import java.util.regex.*;
import java.util.*;
import java.sql.*;

import ScheduleShare.*;

/**
 *
 * @author Chris
 */
public class MegaUltraParser {
    static java.sql.Connection conn;

    static String[] subjectsRaw = {
        "ACCT", "AD", "ARC", "ARCH", "ART", "AS", "BINF", "BIOL", "BME", "BNFO", "BUS",
        "CE", "CET", "CHE", "CHEM", "CIMT", "CMT", "COM", "COOP", "CPT", "CS", "DD", "ECE", "ECET", "ECON",
        "EM", "ENE", "ENG", "ENR", "ENTR", "EPS", "ESC", "ET", "EVSC", "FED", "FIN",
        "FRSH", "HIST", "HRM", "HSS", "HST", "HUM", "ID", "IE", "IM", "INT", "IS", "IT", "LIT", "MARC", "MATH",
        "ME", "MECH", "MET", "MGMT", "MIP", "MIS", "MNE", "MNET", "MR", "MRKT", "MTH", "MTSE", "OM", "OPSE",
        "OSIH", "PE", "PHEN", "PHIL", "PHYS", "PTC", "R014", "R070", "R074", "R080", "R082", "R120", "R160",
        "R165", "R198", "R202", "R215", "R220", "R300", "R350", "R352", "R375", "R390",
        "R420", "R460", "R478", "R510", "R512", "R546", "R560", "R565", "R580", "R620", "R630", "R640", "R645", "R685", "R700", "R701", "R730", "R750", "R790", "R799",
        "R810", "R830", "R834", "R860", "R910", "R920", "R940", "R965", "R970", "R977", "R988", "RUTG",
        "SET", "STS", "THTR", "TRAN", "TUTR", "UMD", "URB"};

    public static void main(String[] args) throws Exception {


        boolean toDB = false;
		
        if( args.length == 3 && args[2].equals("todb") ) {
            toDB = true;
            System.out.println("Performing database output to sql.njit.edu");
        } else {
            System.out.println("NOT performing database output");
        }
		
        // setup database
        connectDB();
		
        int totalClasses = 0;

        String fname = "output.txt";
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fname, false)));
        out.print("Classes from baseURL ");


        // input into semester if nonexsistant, ignore for now;
        String line;
        // I should not set semester id this way. will fix later.
        Matcher m, t;

        // args[0] = season
        // args[1] = year
        // args[2] = todb

        args[0] = args[0].toLowerCase();
        char seachar;
        if(args[0].equals("summer")) {
            seachar = 'U';
        } else {
            seachar = args[0].toUpperCase().charAt(0);
        }


        String baseUrl = String.format("http://www.njit.edu/registrar/schedules/courses/"+args[0]+"/"+args[1]+seachar+".");
        System.out.println(baseUrl);
        out.println(baseUrl);

        ArrayList<String> subjectsArrayList = new ArrayList();
        // Get the available subjects for this semester from the index_list page

        URL listAddress = new URL("http://www.njit.edu/registrar/schedules/courses/"+args[0]+"/index_list.html");
        BufferedReader listIn = new BufferedReader(new InputStreamReader(listAddress.openStream()));

        Pattern linkPat = Pattern.compile("^.*\">(.*)</A>.*$");


        while( (line = listIn.readLine()) != null ) {
              Matcher mat = linkPat.matcher(line);
              if( mat.matches() ) {
                  subjectsArrayList.add(mat.group(1));
              }
        }

        String [] subjects = subjectsArrayList.toArray( new String[0]);
        listIn.close();

        // debugging: get raw descriptions for all subjects:
        int numSubPagesLoaded = 0;
        Pattern descPattern = Pattern.compile("^<a name=\"\\w+\"><b>(.*)$");
	Pattern nameDescSplitterPattern = Pattern.compile("^(\\w+) (\\w+) -(.*)<br/>(.*)$");
        // assemble a hashmap of <name, num> to <description>

        HashMap<String, String> nameToDesc = new HashMap();
        
        for( String subj : subjects ) {
            try {
            URL u = new URL("http://catalog.njit.edu/courses/"+subj+".php");
            listIn = new BufferedReader(new InputStreamReader(u.openStream()));
            } catch ( Exception e ) {
                System.err.println("Not found: " + e );
                continue;
            }
            while( (line = listIn.readLine() ) != null ) {
                Matcher descMatch = descPattern.matcher( line );
                if( descMatch.matches() ) {
			Matcher splitter = nameDescSplitterPattern.matcher( descMatch.group(1) );
			if( splitter.matches() ) {
			    String courseName = splitter.group(1).toUpperCase();
                            String courseNumber = splitter.group(2);
                            if( courseNumber.contains("H")) {
                                continue;
                            }

                            String desc = splitter.group(4).replaceAll("<.*>", "");
                            System.out.println( courseName + " " + courseNumber + "\n" + desc );
                            nameToDesc.put( courseName + " " + courseNumber, desc );
			}
                }
            }
            
            numSubPagesLoaded++;
            System.out.println(numSubPagesLoaded);
            listIn.close();
        }
        out.flush();


        // generate semester in database
        int seasonInt = 1;
        for( int p = 1; p <= 4; p++ ) { 
            if( args[0].equalsIgnoreCase(Semester.seasonNames[p-1])) {
                seasonInt = p;
                break;
            }
        }

        String statement = "SELECT * FROM Semester WHERE semester_year " +
                "=" + args[1] + " && " + "semester_season = " + seasonInt;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(statement);
        ResultSet keys = stmt.getGeneratedKeys();

        int semester_id = 0;
        if (!rs.next()) { // if no result came back
            statement = "INSERT INTO Semester " +
                    "(semester_year, semester_season) VALUES (" + args[1] + "," +
                    seasonInt + ")";
            stmt.executeUpdate(statement, Statement.RETURN_GENERATED_KEYS);
            keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                semester_id = keys.getInt(1);
            }
        } else {
            semester_id = rs.getInt(1);
            // clear the preexisting semester

        }


        for (String subject : subjects) {

            System.out.println("Getting " + subject + " courses...");
            System.out.println("Read " + totalClasses + " classes.");
            
            String url = String.format(baseUrl + "%s.html", subject);

            URL address = new URL(url);
            URLConnection newconn = address.openConnection();
            newconn.connect();
			
            BufferedReader in = new BufferedReader(new InputStreamReader(address.openStream()));
            
            //Regexp that finds stuff
            Pattern title = Pattern.compile(".*\">\\s*(\\w+)\\s*</A>\\s*<.*>(.*)</U.*");
            Pattern field = Pattern.compile("\\s*<(td|TD).*?>(.*)</.*$");

            String number = "", secTitle = "";
            int i = 0;


            String section = "", call = "", state = "";
            int max = 0, now = 0;
            String instructor = "";
            String credits = "";
			String comments = "";
            boolean noTimes = false;
            ArrayList<ClassTime> ctlist = new ArrayList();
            
            // Correctly finding classtimes needs certain outerscope vars:
            // Assume a maximum of 20 classtime entries;
            int days[] = new int[20];
            int starthour[] = new int[20];
            int endhour[] = new int[20];
            int startmin[] = new int[20];
            int endmin[] = new int[20];
            boolean copyprev[] = new boolean[20];

            String room[] = new String[20];
            int numtimes = 0;

            while( (line = in.readLine()) != null ) {
                line = line.replaceAll("&nbsp;", "");
                m = field.matcher(line);
                t = title.matcher(line);

                if( t.matches() ) {
                    number = t.group(1).trim();
                    secTitle = t.group(2).trim();

                    // System.out.println("Found::" + number + " " + secTitle );

                    continue;
                }
                // Account for special cases here

                if (line.contains("eLearning") || 
                    line.contains("announced")) {
                    noTimes = true;
                    numtimes = 0;
                    i = 5; // skip missing table data
                    continue;
                }

                if( !m.matches() ) { continue; }
                String found = m.group(2).trim();
                // System.out.println( "Found::" + found + " at " + i  );


                // There are 7 more fields which arrive in order
                if( i == 0 ) { // section
                    section = found;
                } else if( i == 1 ) { // call #
                    call = found;
                } else if( i == 2 ) { // days
                    
                    String tmpdays = found.replaceAll(" ","").replaceAll("<(BR|br)>"," ").trim();
                    StringTokenizer st = new StringTokenizer(tmpdays);
                    // System.out.println( tmpdays );
                    if( !st.hasMoreTokens() ) {
                        noTimes = true;
                        i++;
                        continue;
                    } else { 
                        noTimes = false;
                        int index = 0;
                        while( st.hasMoreTokens() ) {
                            String day = st.nextToken();
                            int nday = 0;

                            if( day.equals("TBA")) {
                                nday = 7;
                                numtimes = 0;
                                noTimes = true;
                            }


                            boolean duplicate = false;
                            for( int ind = 0; ind < day.length(); ind++ ) {
                                copyprev[index] = duplicate;
                                char singleDay = day.charAt(ind);
                                if( singleDay == 'M')  nday = 1;
                                if( singleDay == 'T')  nday = 2;
                                if( singleDay == 'W')  nday = 3;
                                if( singleDay == 'R')  nday = 4;
                                if( singleDay == 'F')  nday = 5;
                                if( singleDay == 'S')  nday = 6;

                                days[index] = nday;
                                index++;
                                numtimes++;
                                duplicate = true; // Same classtime as prev day
                            }
                        }
                    }
                } else if( i == 3 ) { // times
                    if( noTimes == true ) {
                        i++;
                        continue;
                    }
                    String tmptimes = found.replaceAll(" ","").replaceAll("<BR>"," ").trim();
                    StringTokenizer st = new StringTokenizer(tmptimes);
                    Pattern time = Pattern.compile("(\\d+)(am|pm)-(\\d+)(am|pm)");
                    int index = 0;
                    int foundtimes = 0;
                    while( st.hasMoreTokens() ) {
                        String tok = st.nextToken();
                        
                       
                        Matcher timeMatch = time.matcher(tok);
                        if( !timeMatch.find() ) { continue; }

                        int t1 = Integer.parseInt( timeMatch.group(1) );
                        if( timeMatch.group(2).equals("pm") && 
                                t1 <= 1159 ) { t1 += 1200; }
                        int t2 = Integer.parseInt( timeMatch.group(3) );
                        if( timeMatch.group(4).equals("pm") &&
                                t2 <= 1159 ) { t2 += 1200; }

                        if( !copyprev[index] ) {
                            starthour[index] = t1 / 100;
                            startmin[index] = t1 % 100;
                            endhour[index] = t2 / 100;
                            endmin[index] = t2 % 100;
                            index++;
                        }

                        while( copyprev[index] && index < 20) {
                            starthour[index] = starthour[index-1];
                            startmin[index] = startmin[index-1];
                            endhour[index] = endhour[index-1];
                            endmin[index] = endmin[index-1];
                            index++;
                        }


                        // System.out.println("" + t1 + " " + t2 );
                    }
                } else if( i == 4 ) { // Rooms
                    if( noTimes == true ) {
                        i++;
                        continue;
                    }
                    String tmprooms = found.replaceAll("<BR>","=").trim();
                    StringTokenizer st = new StringTokenizer(tmprooms, "=");
                    int index = 0;
                    while( st.hasMoreTokens() ) {
                        if( !copyprev[index]) {
                            room[index] = st.nextToken().trim();
                            index++;
                        }
                        
                        while( copyprev[index] && index < 20 ) {
                            room[index] = room[index - 1];
                            index++;
                            // System.out.println(room[index]);
                        } 
                    }
                    int x = 0;
                    for( String s : room ) {
                        if ( s == null || s.equals("")) {
                            room[x] = "";
                        }
                        x++;
                    }

                    // We now have enough information to compile classtimes;
                    for( int z = 0; z < numtimes; z++ ) {
                          ClassTime.Day[] vals = ClassTime.Day.values();
                          ClassTime.Day day = vals[days[z]];

                          ScheduleShare.Time t1 = new ScheduleShare.Time( starthour[z], startmin[z] );
                          ScheduleShare.Time t2 = new ScheduleShare.Time( endhour[z], endmin[z] );
                          ctlist.add( new ClassTime( room[z], day, t1, t2));
                    }

                } else if( i == 5 ) { // Status
                    state = found;
                } else if( i == 6 ) { // max students
                    max = Integer.parseInt(found);
                } else if( i == 7 ) { // current stu/dents
                    now = Integer.parseInt(found);
                } else if( i == 8 ) { // Instructor
                    instructor = found;
                } else if( i == 9 ) { // Comments. Ignore this for now.
			comments = found.replaceAll("<br>", "").trim();

                } else if( i == 10 ) { // Credits
                    credits = found;
                
                    // Total class has been found
                    out.println( secTitle + ": " +subject + " " + number + " sem: " + semester_id );
                    out.println( "Section " + section + " call #" + call );
                    out.println("TIMES:"); 
                    if( noTimes ) { 
                        out.println("Course has no set times");
                    } else { 
                        for( ClassTime c : ctlist ) { 
                            out.println( c );
                        }
                        numtimes = 0;
                    }

                    out.println("" + now + "/" + max + " enrolled. Status: " + state );
                    out.println("Instructor: " +  instructor + "     Credits: " + credits );
                    out.println("Comments: " + comments);
                    out.println();

                    int creds = 0;
                    try {
                        creds = Integer.parseInt(credits.substring(0,1));
                    } catch (Exception e) {
                        System.err.println("Bad format or no credits for call #: " + call);
                        creds = 0;
                    }

                    // But write the description to the file no matter what
                    String description = nameToDesc.get( subject + " " + number );
                    if( description == null ) {
                        description = "";
                    }
                    description = description.replaceAll("[;\"]", "");
                    Object [] vals = {creds, secTitle, number, subject, subject, semester_id, description };
                    out.println("Description: " + description + "\n----------------------");

                    totalClasses++;

                    // Input into database

                    if( toDB ) {
                        // Do the courses

                        

                        String params = makeEntries( vals );

                        statement = "SELECT * FROM Course WHERE course_subject ='" +
                                subject +"' AND course_number = '" + number + "'" + " AND course_semester_id = " +
                                semester_id;
                        rs = stmt.executeQuery(statement);
                        String courseKey = "";
                        keys = stmt.getGeneratedKeys();
                        if( !rs.next() ) { // if no response, not already in DB


                            statement = "INSERT INTO Course " +
                                    "(course_credits, course_name, course_number, course_subject," +
                                    "course_department, course_semester_id, course_description) VALUES " +
                                    "( " + params + " )";
                            int gk = stmt.executeUpdate(statement, Statement.RETURN_GENERATED_KEYS);
                            keys = stmt.getGeneratedKeys();

                            if( keys.next() ) {
                                courseKey = keys.getString(1);
                            }
                        } else {
                            courseKey = rs.getString(1);
                        }

                        String profKey = "";
                        // Some professors are dumb and have ''''s in their names.
                        // See, you can't even read what I just wrote!

                        if( instructor.contains("'") ) {
                            instructor = instructor.replaceAll("'", "-");
                        }
                        // Do the professor
                        statement = "SELECT * FROM Instructor WHERE instructor_lname " +
                                "= '" + instructor + "'";
                        try{
                            rs = stmt.executeQuery( statement );
                        } catch (Exception e ) {
                            System.err.println("Bad statment: " + statement);
                        }
                        if( !rs.next() ) { // if no result came back
                            statement = "INSERT INTO Instructor " +
                                "(instructor_lname) VALUES ( '" + instructor + "')";
                            stmt.executeUpdate( statement, Statement.RETURN_GENERATED_KEYS );
                            keys = stmt.getGeneratedKeys();
                            if( keys.next() ) {
                                profKey = keys.getString(1);
                            }
                        } else {
                            profKey = rs.getString(1);
                        }



                        // Do the section
                        Boolean tmp = new Boolean( noTimes );
                        Boolean sTmp = (state.equals("Closed")) ? true : false;
                        comments = comments.replaceAll("[\";]", "");
                        Object [] vals2 = { courseKey, profKey , call, section, tmp, sTmp, max, now, credits, comments };
                        params = makeEntries( vals2 );
                        statement = "INSERT INTO Section VALUES ( DEFAULT, " + params + ")";
                        statement = statement.replaceAll("<.*>", "");
                        // System.out.println(statement);
                        stmt.setEscapeProcessing( true );
                        stmt.executeUpdate( statement, Statement.RETURN_GENERATED_KEYS );
                        keys = stmt.getGeneratedKeys();
                        String secKey = "";
                        if( keys.next() ) {
                            secKey = keys.getString(1);
                        }


                        // Finally, do the classtimes
                        // Classtimes are guaranteed to be unique, somehow
                        if( !noTimes ) {
                            for( ClassTime c : ctlist ) {
                                Object [] vals3 = { secKey, c.room, "", c.timeStart.timeVal / 60,
                                c.timeEnd.timeVal / 60, c.timeStart.timeVal % 60, c.timeEnd.timeVal % 60,
                                c.day.ordinal()
                                };
                                params = makeEntries( vals3 );
                                statement = "INSERT INTO Classtime VALUES ( DEFAULT, " + params + ")";
                                stmt.executeUpdate( statement );
                            }
                        }
                    }
                    
                    // clear data for next pass
                    ctlist.clear();
                    Arrays.fill( copyprev, false );
                    Arrays.fill( room, "");
                  
                        

                    // Reset position
                    i = -1;
                }

                // Increment to next piece of information
                i++;

            }

        }
        System.out.println("Done. Read " + totalClasses + " classes ");
        out.flush();
        out.close();

    }

    static public void connectDB() {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
	}
	catch (Exception E)
        {
            System.err.println("Unable to load driver.");
            E.printStackTrace();
	}
        try
        {
            conn = DriverManager.getConnection("[REDACTED]");
        }
        catch (SQLException E)
        {
            System.out.println("SQLException: " + E.getMessage());
            System.out.println("SQLState:     " + E.getSQLState());
            System.out.println("VendorError:  " + E.getErrorCode());
        }
    }

    // This is totally insecure, and also redundant since prepared statements do the same thing
    // this should be changed
    static String makeEntries( Object [] vals ) {
        String params = "";
        int ind = 0;
        for (Object o : vals) {
            if( o instanceof Boolean ) {
                if (ind == vals.length - 1) {
                    params = params.concat("" + o + "");
                } else {
                    params = params.concat("" + o + ", ");
                }
            } else {
                if (ind == vals.length - 1) {
                    params = params.concat("\"" + o + "\"");
                } else {
                    params = params.concat("\"" + o + "\", ");
                }
            }
            ind++;
        }
        return params;
    }
}
