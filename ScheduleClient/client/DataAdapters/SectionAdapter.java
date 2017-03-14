package ScheduleClient.client.DataAdapters;

import com.google.gwt.user.client.rpc.IsSerializable;
import java.util.ArrayList;

/**
 *
 * @author Chris
 */
public class SectionAdapter implements IsSerializable{

    private ArrayList<ClassTimeAdapter> classTimes;
	
    private String sectionNum;
    private String professor;
    private String callNum;

    private boolean online;
    private int currentStudents;
    private int maxStudents;

    private int credits;
	
    private String comments;
    private String courseName;

    public SectionAdapter() { } // Empty constructor for serialization

    public SectionAdapter( String sectionNum,
                            String professor,
                            String courseName,
                            String callNum,
                            boolean online,
                            int currentStudents,
                            int maxStudents,
                            int credits,
                            String comments) {
							
        this.sectionNum = sectionNum;
        this.classTimes = new ArrayList<ClassTimeAdapter>();
        this.professor = professor;
        this.courseName = courseName;
        this.callNum = callNum;
        this.online = online;
        this.currentStudents = currentStudents;
        this.maxStudents = maxStudents;
        this.credits = credits;
        this.comments = comments;
    }

    public boolean equals(Object object) {
        if(object instanceof SectionAdapter) {
            SectionAdapter sa = (SectionAdapter)object;
            return sa.callNum.equals(callNum);
        }
        return false;
    }

    public void addClassTime(String building, int day, int start, int end) {
        classTimes.add(new ClassTimeAdapter(building, day, start, end));
    }

    public ArrayList<ClassTimeAdapter> getClassTimes() { return classTimes; }
    public String getSectionNum() { return sectionNum; }
    public String getCallNum() { return callNum; }
    public String getProfessor() { return professor; }
    public String getCourseName() { return courseName; }
    public String getCourse() { return sectionNum.substring(0, sectionNum.lastIndexOf("-")); }
    public boolean getOnline() { return online; }
    public int getCurrentStudents() { return currentStudents; }
    public int getMaxStudents() { return maxStudents; }
    public int getCredits() { return credits; }
    public String getComment() { return comments; }
}
