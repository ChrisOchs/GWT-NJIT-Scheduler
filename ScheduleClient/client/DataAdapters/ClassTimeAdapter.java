package ScheduleClient.client.DataAdapters;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 *
 * @author Chris
 */
public class ClassTimeAdapter implements IsSerializable, Comparable {

    private int day;
    private int startTime;
    private int endTime;
    
    private String room;

    public ClassTimeAdapter() { } // Empty default constructor for serialization

    public ClassTimeAdapter(String room, int day, int start, int end){
        this.day = day;
        this.startTime = start;
        this.endTime = end;
        this.room = room;
    }

    public int getDay()     { return day; }
    public int getStart()   { return startTime; }
    public int getEnd()     { return endTime; }
    public String getRoom() { return room; }

    public boolean equals(ClassTimeAdapter other) {
        return this.day == other.day && this.startTime == other.startTime &&
                this.endTime == other.endTime;
    }

    public int compareTo(Object object) {
        if(object instanceof ClassTimeAdapter) {
            ClassTimeAdapter other = (ClassTimeAdapter)object;

            if(this.day == other.day) {
                if(this.startTime < other.startTime) {
                    return -1;
                }
                else if( this.startTime > other.startTime) {
                    return 1;
                }
                else {
                    return 0;
                }
            }
            else {
                if(this.day < other.day) {
                    return -1;
                }
                else {
                    return 1;
                }
            }
        }

        return 0;
    }
    
    public String toString() { return convertTime(startTime) + " - " + convertTime(endTime); }

    public String convertDay(int day) {
        String [] days = {"Sun", "M", "T", "W", "R", "F", "Sat"};
        
        return days[day];
    }

    public String getDayString() { return convertDay(day); }

    private String convertTime(int time) {
        String minute = "00";

        int hour = time / 60;
        int min = time % 60;

        if(min > 0 && min < 10) {
            minute = "0" + min;
        } else if( min > 10) {
            minute = Integer.toString(min);
        }

        if(hour > 11) {
            if(hour == 12) {
                return "12:" + minute + "PM";
            } else {
                return (hour % 12) + ":" + minute + "PM";
            }
        }
        else {
            return hour + ":" + minute + "AM";
        }
    }
}
