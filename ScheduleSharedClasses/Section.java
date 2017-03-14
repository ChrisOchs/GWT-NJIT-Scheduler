package ScheduleShare;

import java.util.ArrayList;
import java.io.Serializable;

/**
 *
 * @author 
 */
public class Section implements Serializable {
    public Course member;
    public ArrayList<ClassTime> times;
    public String sectionNum;
    public String sectionCallNum;
    public String comments;
    public Professor prof;
    public int currentStudents;
    public int maxStudents;
    public int credits;
    public boolean online;
    public boolean closed;

    public Section() {
        // do not use - required for serializablility
    }

    public Section( Course member, String sectionNum, Professor prof )
    {
            this.member = member;
            this.sectionNum = sectionNum;
            this.prof = prof;
            times = new ArrayList();
    }

    public Section( Course member, String sectionNum, String sectionCallNum, Professor prof )
    {
            this.member = member;
            this.sectionNum = sectionNum;
            this.sectionCallNum = sectionCallNum;
            this.prof = prof;
            times = new ArrayList();
    }

    public Section( Course member, String sectionNum, String sectionCallNum, Professor prof, ArrayList<ClassTime> times )
    {
            this.member = member;
            this.sectionNum = sectionNum;
            this.sectionCallNum = sectionCallNum;
            this.prof = prof;
            this.times = times;
    }

    public Section( Course member, String sectionNum, String sectionCallNum, Professor prof, ArrayList<ClassTime> times, boolean online,
                    boolean closed, int maxStudents, int currentStudents, int credits, String comments )
    {
            this.member = member;
            this.sectionNum = sectionNum;
            this.sectionCallNum = sectionCallNum;
            this.prof = prof;
            this.times = times;
            this.online = online;
            this.closed = closed;
            this.currentStudents = currentStudents;
            this.maxStudents = maxStudents;
            this.credits = credits;
            this.comments = comments;
    }

    public void addTime( ClassTime toAdd ) {
        times.add( toAdd );
    }

    public boolean isOnline()
    {
        return online;
    }

    public boolean conflictsWith( Section s )
    {
        if( online )
            return false;
        for( ClassTime time1: times )
        {
            for( ClassTime time2:s.times)
            {
                if( time1.conflictsWith(time2))
                    return true;
            }
        }
        return false;
    }

    public ArrayList<ClassTime> getClassTimes()
    {
        return times;
    }

    public String getCourseName() { return member.name; }

    @Override
    public String toString() {
        return member.subject + "-" + member.num + "-" + sectionNum;
    }
    public String longDesc() {
        StringBuilder s = new StringBuilder();
        s.append( member.toString() + "-" + sectionNum + ", " + prof + " " );
        for( ClassTime time : times ) {
            s.append( "{ " + time + " }" );
        }

        return s.toString();
    }
    public String hugeDesc() {
        StringBuilder s = new StringBuilder();
        s.append( member.toString() + "-" + sectionNum + ", " + prof + " \n" );
        s.append("Credits: " + credits + "MaxStudents: " + maxStudents + "CurrentStudents:" + currentStudents + "\n");
        String status = closed ? "Closed" : "Open";
        String sOnline = online ? "Online" : "Offline";
        s.append(status + " " + sOnline);
        for( ClassTime time : times ) {
            s.append( "{ " + time + " }" );
        }

        return s.toString();
    }
}
