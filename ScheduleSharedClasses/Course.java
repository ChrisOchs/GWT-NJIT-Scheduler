package ScheduleShare;

import java.util.ArrayList;
import java.io.Serializable;
/**
 *
 * @author 
 */
public class Course implements Serializable {
    // There is ambiguity in the course list - crsID may be MECH while dept may
    // be CE - they are the same class but CE is more specific. This odd
    // naming system comes from the XLS file.
    public String subject;
    public String dept;
    public String num;
    public String name;
    public String description;
    public int credits;
    public boolean grad;

    public Course() {
        // don't ever use this.

    }

    public Course( String subject, String num, String name, String dept, int credits, String desc ) {
        this.subject = subject;
        this.num = num;
        this.name = name;
        this.dept = dept;
        this.credits = credits;
        this.description = desc;
    }

    public Course( String subject, String num, String name, String dept, int credits ) {
        this.subject = subject;
        this.num = num;
        this.name = name;
        this.dept = dept;
        this.credits = credits;
    }

    @Override
    public String toString() {
        return subject + "-" + num;
    }

    public String longDesc() {
        return subject + "-" + num + " " + name + "|" + description;
    }
}