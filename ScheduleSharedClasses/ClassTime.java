package ScheduleShare;

import java.io.Serializable;

/**
 *
 * @author 
 */
public class ClassTime implements Serializable {
    public enum Day {
        SUNDAY( "Sunday" ),
        MONDAY( "Monday" ),
        TUESDAY( "Tuesday" ),
        WEDNESDAY( "Wednesday" ),
        THURSDAY( "Thursday" ),
        FRIDAY( "Friday"),
        SATURDAY( "Saturday" ),
        TBA( "To Be Announced" );
       
        String title;
        Day( String title ) { 
            this.title = title;
        }       
    };
   

    // time = minutes from 00:00, for now
    public String room;
    public Day day; // 0-6 mon to sun
    public Time timeStart;
    public Time timeEnd;

    public ClassTime() {

    }
    public ClassTime( String room, Day day, Time timeStart, Time timeEnd ) {
        this.room = room;
        this.day = day;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    @Override
    public String toString() {
        return room + " " + day + " " + timeStart + "- " + timeEnd
                + " " + timeStart.diff(timeEnd) + " min";
    }

    public boolean conflictsWith( ClassTime t )
    {
        if( t.day == day && t.day != Day.TBA )
        {
            if( (t.timeStart.timeVal >= timeStart.timeVal && t.timeStart.timeVal <= timeEnd.timeVal) || (t.timeEnd.timeVal <= timeEnd.timeVal && t.timeEnd.timeVal >= timeStart.timeVal) )
                return true;
        }
        return false;
    }

    public String getRoomString() { return room; }
}
