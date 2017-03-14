package ScheduleClient.server;

import ScheduleClient.client.DataAdapters.SectionAdapter;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import ScheduleClient.client.ScheduleClientService;
import java.util.ArrayList;

public class ScheduleClientServiceImpl extends RemoteServiceServlet
        implements ScheduleClientService {

    public String doHolla() {
        return new ScheduleDataProcessorProxy().sendHolla();
    }

    public String echo(String string) {
        return new ScheduleDataProcessorProxy().echo(string);
    }

    public String [] getAvailableSemesters() {
        return new ScheduleDataProcessorProxy().getAvailableSemesters();
    }

    public String [] getAvailableSubjects() {
        return new ScheduleDataProcessorProxy().getAvailableSubjects();
    }

    public String [] getAvailableCourses(String subject) {
        return new ScheduleDataProcessorProxy().getAvailableCourses(subject);
    }

    public SectionAdapter [] getAvailableSections(String course) {
        return new ScheduleDataProcessorProxy().getAvailableSections(course);
    }

    public SectionAdapter [] autoScheduleClasses(ArrayList<String> courses) {
        return new ScheduleDataProcessorProxy().autoScheduleClasses(courses);
    }

    public String [] getProfessors() {
        return new ScheduleDataProcessorProxy().getProfessors();
    }

    public String [] getCourseNames() {
        return new ScheduleDataProcessorProxy().getCourseNames();
    }

    public SectionAdapter [] getSectionsByProfessor(String professor) {
        return new ScheduleDataProcessorProxy().getSectionsByProfessor(professor);
    }
}
