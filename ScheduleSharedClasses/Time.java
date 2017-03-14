package ScheduleShare;

import java.io.Serializable;
/**
 *
 * @author 
 */
 public class Time implements Comparable, Serializable {
        public int timeVal;

        public Time () {
            timeVal = 0;
        }
        public Time( int hr, int min ) {
            if( hr > 23 || min > 59 || hr < 0 || min < 0 ) {
                timeVal = 0; // bad time entered
            }
            timeVal = hr * 60 + min;
        }
        public Time( int timeVal ) {
            this.timeVal = timeVal % (60 * 24);
        }

        public int diff( Time other ) {
            return Math.abs(timeVal - other.timeVal);
        }
        public int compareTo( Object other ) {
            Time o = (Time) other;
            if( timeVal < o.timeVal ) {
                return -1;
            } else if ( timeVal > o.timeVal ) {
                return 1;
            } else {
                return 0;
            }
        }
        public String toString() {
            return String.format("%02d:%02d ", timeVal / 60, timeVal % 60);
        }
 }