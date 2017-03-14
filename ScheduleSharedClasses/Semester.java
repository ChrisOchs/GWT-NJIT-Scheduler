package ScheduleShare;

import java.io.Serializable;

/**
 *
 * @author 
 */

public class Semester implements Serializable {
    public static String [] seasonNames = {"Spring", "Summer", "Fall", "Winter"};
    int year;
    int season;

    public Semester() {
       
    }
    public Semester( int year, int season ){
        this.year = year;
        this.season = season;
    }

    public int getYear()
    {
        return year;
    }
    public int getSeason()
    {
        return season;
    }

    @Override
    public String toString() {
        return seasonNames[season-1] + " " + year;
    }

    public static Semester fromString( String s ) {
        String[] terms = s.split(" ");
        int se = 0;
        for( int i = 0; i < 4; i++ ) {
            if( seasonNames[i].equals(terms[0])) {
                se = i+1;
            }
        }
        int yr = Integer.parseInt(terms[1]);
        return new Semester( yr, se);
    }


}
