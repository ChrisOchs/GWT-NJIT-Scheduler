package ScheduleShare;

import java.io.Serializable;

/**
 *
 * @author 
 */
public class Professor implements Serializable {
    public String fname;
    public String lname;

    public Professor() {

    }
    public Professor( String name ) {
        this.lname = name;
    }
    public Professor( String fname, String lname ) {
        this.lname = lname;
        this.fname = fname;
    }

    @Override
    public String toString() {
        return lname;
    }
}
