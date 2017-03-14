/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ScheduleDataAccessor;
import ScheduleShare.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
/**
 *
 * @author Owner
 */
public class DumbDataMaker {
    static Random rand = new Random();
    static String[] subNames = {"MATH", "CHEM", "CE", "PORK", "BIO", "MGMT",
    "IT", "IS", "ARCH", "CS", "CIS", "ECO", "ECON", "FIRE", "MILK", "DOGS",
    "LIT", "ENG", "PE", "ME", "EE", "IE", "LANG" };

    static String [] prefixes = {"Fundamentals", "Studies", "Selected topics",
    "Workshop", "Capstone", "Exercises", "Components" };

    static String [] articles = {"of", "in", "for", "Related to", "About", "Concerning" };
    static String [] adjs = {"Advanced", "Rudimentary", "Abstract", "Concrete", "Modern",
    "Classical", "Gothic", "Hyperspatial", "Ethical", "Algebraic", "Linguistic",
    "Tasty", "Intoxicated", "Quick", "Efficient", "Theological", "Unproductive", "Electrical",
    "Computational", "Mechanical", "Architectural", "Basic", "Complex", "Underwater",
    "Subterranean", "Electrical", "Creative", "Simple"};
    static String [] topics = {"Mathematics", "Engineering", "Programming",
    "Painting", "Music Theory", "Carpentry", "Construction", "Architecture",
    "Writing", "Literature", "Design", "Dancing", "Screaming", "Exploding",
    "Hacking", "Cartooning", "Algebra", "Calculus", "Statistics", "Religion",
    "History", "Management", "Business", "Finance", "Biology", "Chemistry",
    "Demolition", "Hair-cutting", "Shoe Repair", "Basketball", "Philosophy",
    "Tennis", "Basket-weaving"};

    static String [] suffi = {"Honors", "I", "II","III", "IV", "for Veterans",
    "for Women", "for Engineers", "For Architects", "for Zookeepers", "Workshop",
    "Lab", "for Penguins" };

    static String [] buildings = {"KUPF", "ECEC", "CULL", "GITC", "ARCH",
    "CE", "TIER", "FMH" };

    static String [] profNames = {"Face", "Smith", "Herry", "Bojakaluso", "Firiojikab",
    "Miblo", "Xandis", "Coopopoulis", "Yarl", "Dingo", "Frimp", "Chaz", "Dingus",
    "Webster", "Soup", "Window", "Pepper", "Krastox", "Jujujujoookaloo", "Relkin" };


    static String [] profPre = {"Mr.", "Dr.", "Sir", "The Honorable Dr.", "Duke",
    "Ms.", "Mrs." };

    public DumbDataMaker() {
        
    }

    public static Semester [] semesters() {
        ArrayList<Semester> sems = new ArrayList<Semester>();
        int baseYear = 2009 + rand.nextInt( 4 ) - 2;
        int numSem = rand.nextInt(4) + 1;
        int baseSeas = rand.nextInt(4);
        int j = baseSeas;
        for( int i = 0; i < numSem; i++ ) {
            while( j < 4 ) {
                sems.add( new Semester( baseYear + i, j ));
                j++;
            }
            j = 0;
        }
        return sems.toArray( new Semester[0]);
    }

    public static String [] subjects() {
        ArrayList<String> subNamesUse = new ArrayList<String>();
        subNamesUse.addAll( Arrays.asList( subNames ) );
        
        int numSub = rand.nextInt(20);
        ArrayList<String> subjs = new ArrayList<String>();
        
        for( int i = 0; i < numSub; i++ ) {
            int in = rand.nextInt( subNamesUse.size() );
            subjs.add( subNamesUse.get( in ) );
            subNamesUse.remove( in );
        }
        
        return subjs.toArray( new String[0]);
    }

    public static Object [] randomize( Object[] from, int min, int var, boolean recount ) {
        ArrayList fromUse = new ArrayList();
        fromUse.addAll( Arrays.asList( from ) );

        int num = min + rand.nextInt(var);
        ArrayList toRet = new ArrayList();

        for( int i = 0; i < num; i++ ) {
            int in = rand.nextInt( fromUse.size() );
            toRet.add( fromUse.get( in ) );
            if( !recount ) {
                fromUse.remove( in );
            }
        }

        return toRet.toArray( new Object[0]);
    }

    public static Course [] courses( String subj ) {
        int numcourses = rand.nextInt(20);
        ArrayList<Course> cour = new ArrayList<Course>();
        for( int i = 0; i < numcourses; i++ ) {
            int num = rand.nextInt(500) + 100;
            String pref = prefixes[ rand.nextInt(prefixes.length)];
            String article = articles[ rand.nextInt(articles.length)];
            String adj = adjs[ rand.nextInt(adjs.length)];
            String topic = topics[ rand.nextInt(topics.length)];
            String suffix = suffi[ rand.nextInt(suffi.length)];

            boolean havePrefix = rand.nextBoolean();
            boolean haveAdj = rand.nextBoolean();
            boolean haveSuffix = rand.nextBoolean();

            StringBuilder s = new StringBuilder();
            if( havePrefix ) {
                s.append( pref + " " + article + " ");
            }
            if( haveAdj ) {
                s.append( adj + " " );
            }
            s.append( topic + " ");
            if( haveSuffix ) {
                s.append( suffix );
            }
            String st = String.format("%d", num);
            Course c = new Course( subj, st, s.toString(), subj );
            cour.add( c );
        }
        return cour.toArray( new Course[0]);
    }

    public static Section [] sections( String subj, String num ) {
        
        int numSec = rand.nextInt(10) + 2;
        
        ArrayList<Section> secs = new ArrayList<Section>();
        for( int i = 0 ; i < numSec; i++ ) {
            int maxtimes = 3 - rand.nextInt(3);
            Course c = new Course( subj, num, "", "");
            int secNum = 1 + i * 2;
            String secStr = String.format( "%03d", secNum );
            Professor p = new Professor( profPre[ rand.nextInt( profPre.length ) ] + " " +
                    profNames[ rand.nextInt( profNames.length )] );

            Section toAdd = new Section( c, secStr, p );

            for( int t = 0; t < maxtimes; t++ ) {
                // get between 60*8 and 60*21
                // = 60*13 + 60*8
                // five minute increments
                // = (60*8 + 30) + (5 * rand(60*13/5-1))
                Time t1 = new Time( 60*8+30+15*rand.nextInt((60*10)/15-1));
                int length = 0;
                switch( rand.nextInt(2) ) {
                    case 0:
                        length = 90; break;
                    case 1:
                        length = 180; break;
                }
                Time t2 = new Time( t1.timeVal + length );
                if( t1.compareTo(t2) >= 0 ) {
                    Time tmp = t1;
                    t1 = t2;
                    t2 = tmp;
                }

                ClassTime.Day[] vals = ClassTime.Day.values();
                int day = rand.nextInt( vals.length );
                ClassTime.Day d = vals[day];
                String building = buildings[ rand.nextInt( buildings.length )];
                String room = "" + ( rand.nextInt(600) + 100 );
                ClassTime ct = new ClassTime( building + " " + room, d, t1, t2 );
                toAdd.addTime( ct );
                toAdd.sectionCallNum = String.valueOf(rand.nextInt(100000));
            }
            secs.add( toAdd );
        }
        return secs.toArray( new Section[0] );
    }
}
