package ScheduleClient.client;

import ScheduleClient.client.DataAdapters.SectionAdapter;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;

// Asyncronous interface for handling RPC calls to the client servlet
public interface ScheduleClientServiceAsync {

    public void doHolla(AsyncCallback<String> callback);
    public void echo(String string, AsyncCallback<String> callback);

    public void getAvailableSemesters(AsyncCallback<String[]> callback);

    public void getAvailableSubjects(AsyncCallback<String[]> callback);
    public void getAvailableCourses(String subject, AsyncCallback<String[]> callback);
    public void getAvailableSections(String course, AsyncCallback<SectionAdapter[]> callback);

    public void addSection(SectionAdapter sa, AsyncCallback<Boolean> callback);
    public void removeSection(SectionAdapter sa, AsyncCallback<Boolean> callback);

    public void autoScheduleClasses(ArrayList<String> courses, AsyncCallback<SectionAdapter[]> callback);

    public void getProfessors(AsyncCallback<String[]> callback);
    public void getCourseNames(AsyncCallback<String[]> callback);

    public void getSectionsByProfessor(String professor, String semester, AsyncCallback<SectionAdapter[]> callback);
	
}
