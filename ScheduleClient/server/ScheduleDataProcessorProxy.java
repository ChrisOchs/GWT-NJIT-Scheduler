package ScheduleClient.server;

import ScheduleClient.client.DataAdapters.SectionAdapter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;

import ScheduleShare.*;
import java.io.EOFException;
import java.util.ArrayList;

// Singleton proxy class for sending messages and receiving responses
// from the Data Processor servlet.
public class ScheduleDataProcessorProxy {

    private final String processorServletRootAddress = "[REDACTED]";

    // Returns the address of the ScheduleDataProcessor servlet
    private String getDataProcessorAddress() {
        String response = processorServletRootAddress + "ScheduleDataProcessorServlet/ScheduleDataProcessorServlet";

        return response;
    }

    public ScheduleDataProcessorProxy() {
    }

    public ScheduleDataProcessorProxy() {
        
    }

    // Sends an array of serializable objects to the back end servlet. The first
    // object in the array HAS TO BE the name of the command you want to send.
    // It must correspond directly to a command name on the remove data processor
    // servlet.
    private Object sendRemoteCommand(Serializable objects[]) {
        
        try { // Try to send the object to the remote servlet
            ObjectInputStream ois = sendObjectsToServlet(objects);
            Object object = null;

            try {
                object = ois.readObject(); // Read the returned object (if any)
            }
            catch(EOFException eof) {
                // Ignore the EOF exception since it means no data
                // was returned and thats okay for functions
                // that don't return data
            }

            ois.close();

            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Opens a URLConnection to the remote servlet and then sends all of the given
    // Serializable objects to the servlet.
    private ObjectInputStream sendObjectsToServlet(Serializable objects[]) throws Exception {
        URLConnection connection = new URL(getDataProcessorAddress()).openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setDefaultUseCaches(false);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());

        // Send all of the objects to the servlet
        for (Serializable s : objects) {
            out.writeObject(s);
        }

        out.flush();
        out.close();

        ObjectInputStream o = null;

        try { // Try to read in the response from the servlet
            o = new ObjectInputStream(connection.getInputStream());
        } catch (Exception e) {
            System.out.println(e);
        }

        return o;
    }

    // Asks the SceduleDataProcessor to send a Holla
    public String sendHolla() {
        Serializable[] objects = {"hollaBack"};
        return (String) sendRemoteCommand(objects) + "girl!";
    }

    // Echoes a string
    public String echo(String string) {
        Serializable[] objects = {"echo", string};

        return (String) sendRemoteCommand(objects);
    }

    // Returns the available semesters
    public String[] getAvailableSemesters() {
        Serializable[] objects = {"getAvailableSemesters"};

        // Convert returned semesters into a string array
        Semester[] sems = (Semester[]) sendRemoteCommand(objects);
        String[] semNames = new String[sems.length];
        for (int i = 0; i < sems.length; i++) {
            semNames[i] = sems[i].toString();
        }
        return semNames;
    }

    // Sets the selected semester
    public void setSelectedSemester(String semester) {
        Serializable[] objects = {"setSelectedSemester", semester};

        sendRemoteCommand(objects);
    }

    public String[] getAvailableSubjects() {
        Serializable[] objects = {"getAvailableSubjects"};

        return (String[]) sendRemoteCommand(objects);
    }

    public String[] getAvailableCourses(String subject) {
        Serializable[] objects = {"getAvailableCourses", subject};

        Course[] courses = (Course[]) sendRemoteCommand(objects);
        String[] courseNames = new String[courses.length];
        for (int i = 0; i < courses.length; i++) {
            courseNames[i] = courses[i].longDesc();
        }
        return courseNames;
    }

    public SectionAdapter[] getAvailableSections(String course) {
        Serializable[] objects = {"getAvailableSections", course};
        Section[] sections = (Section[]) sendRemoteCommand(objects);

        return convertSectionsToAdapters(sections);
    }

    public Boolean addSection(String section) {
        Serializable [] objects = {"addSection", section};
        return (Boolean)sendRemoteCommand(objects);
    }

    public Boolean removeSection(String section) {
        Serializable [] objects = {"removeSection", section};
        return (Boolean)sendRemoteCommand(objects);
    }

    public SectionAdapter[] autoScheduleClasses(ArrayList<String> courses) {
        Serializable [] objects = {"autoSchedule", courses};
        Section [] sections = (Section[])sendRemoteCommand(objects);

        return convertSectionsToAdapters(sections);
    }

    public String [] getProfessors() {
        Serializable [] objects = {"getProfessors"};
        return (String[])sendRemoteCommand(objects);
    }

    public String [] getCourseNames() {
        Serializable [] objects = {"getCourseNames"};
        return (String[])sendRemoteCommand(objects);
    }

    public SectionAdapter [] getSectionsByProfessor(String professor) {
        Serializable [] objects = {"getSectionsByProfessor", professor};
        Section [] sections = (Section[])sendRemoteCommand(objects);
        return convertSectionsToAdapters(sections);
    }

    private SectionAdapter[] convertSectionsToAdapters(Section[] sections) {
	
        SectionAdapter[] adaptedSections = new SectionAdapter[sections.length];

        for (int i = 0; i < sections.length; i++) {
            adaptedSections[i] = new SectionAdapter(sections[i].toString(),
                    sections[i].prof.toString(),
                    sections[i].getCourseName(),
                    sections[i].sectionCallNum,
                    sections[i].isOnline(),
                    sections[i].currentStudents,
                    sections[i].maxStudents,
                    sections[i].credits,
                    sections[i].comments);

            for (ClassTime classTime : sections[i].times) {
                adaptedSections[i].addClassTime(classTime.getRoomString(), classTime.day.ordinal(),
                        classTime.timeStart.timeVal, classTime.timeEnd.timeVal);
            }
        }

        return adaptedSections;
    }
}
