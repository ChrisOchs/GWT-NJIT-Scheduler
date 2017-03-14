package ScheduleClient.client;

import ScheduleClient.client.DataAdapters.SectionAdapter;
import com.google.gwt.user.client.rpc.RemoteService;
import java.util.ArrayList;

public interface ScheduleClientService extends RemoteService {
    public String doHolla();
    public String echo(String string);

    public String getSessionKey();

    public String [] getAvailableSemesters();

    public String [] getAvailableSubjects(String semester);
    public String [] getAvailableCourses(String semester, String subject);
	
    public SectionAdapter [] getAvailableSections(String semester, String subject, String course);

    public Boolean addSection(SectionAdapter sa);
    public Boolean removeSection(SectionAdapter sa);
    
    public SectionAdapter[] autoScheduleClasses(ArrayList<String> courses);

    public String [] getProfessors(String semester);
    public String [] getCourseNames(String semester);

    public SectionAdapter[] getSectionsByProfessor(String semester, String professor);
}
