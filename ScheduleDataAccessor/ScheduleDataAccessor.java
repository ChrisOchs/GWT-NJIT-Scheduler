
package ScheduleDataAccessor;

import java.sql.*;
import java.util.ArrayList;
import ScheduleShare.*;

/**
*
* @author Chris Hukushi
* @author Julian Raymar
*/

// Stub for class used to make MySQL calls
public class ScheduleDataAccessor
{
    java.sql.Connection conn;
	
    // TODO: Add PreparedStatements
    public ScheduleDataAccessor()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception E) {
            System.err.println("Unable to load driver.");
            E.printStackTrace();
		}
		
        try
        {
            conn = DriverManager.getConnection("[REDACTED]");
        }
        catch (SQLException E)
        {
            System.out.println("SQLException: " + E.getMessage());
            System.out.println("SQLState:     " + E.getSQLState());
            System.out.println("VendorError:  " + E.getErrorCode());
        }
    }

    // TODO: add functions that make SQL calls
    public ResultSet getDBCourses()
    {
        try
        {
            Statement stmt = conn.createStatement();
            return stmt.executeQuery("SELECT * from Course");

        }
        catch (SQLException E)
        {
            System.out.println("SQLException: " + E.getMessage());
            System.out.println("SQLState:     " + E.getSQLState());
            System.out.println("VendorError:  " + E.getErrorCode());
        }
        return null;
    }

    public ResultSet getDBCourses( String subjectName, int year, int season )
    {
        try
        {
            Statement stmt = conn.createStatement();
            return stmt.executeQuery("SELECT * FROM Course, Semester WHERE course_subject = \"" + subjectName + "\" && " +
                    "course_semester_id = semester_id && " +
                    "semester_year = " + year + " && semester_season = " + season );
        }
        catch (SQLException E)
        {
            System.out.println("SQLException: " + E.getMessage());
            System.out.println("SQLState:     " + E.getSQLState());
            System.out.println("VendorError:  " + E.getErrorCode());
        }
        return null;
    }

    public ResultSet getDBCourseSubjects( int year, int season )
    {
        try
        {
            Statement stmt = conn.createStatement();
            return stmt.executeQuery( "SELECT DISTINCT course_subject FROM Course, Semester " +
                    "WHERE course_semester_id = semester_id && " +
                    "semester_year = " + year + " && semester_season = " + season );
        }
        catch (SQLException E)
        {
            System.out.println("SQLException: " + E.getMessage());
            System.out.println("SQLState:     " + E.getSQLState());
            System.out.println("VendorError:  " + E.getErrorCode());
        }
        return null;
    }
public ResultSet getDBCourseNames( int year, int season)
{
    try
    {
     Statement stmt = conn.createStatement();
	 
     return stmt.executeQuery("SELECT DISTINCT course_subject, course_name, course_number FROM Course, Semester " + "WHERE course_semester_id = semester_id && " +
                    "semester_year = " + year + " && semester_season = " + season );
    }
    catch (SQLException E)
    {
        System.out.println("SQLException: " + E.getMessage());
        System.out.println("SQLState:     " + E.getSQLState());
        System.out.println("VendorError:  " + E.getErrorCode());
    }
    return null;
}
    public ResultSet getDBSemesters()
    {
        try
        {
            Statement stmt = conn.createStatement();
            return stmt.executeQuery( "SELECT * FROM Semester" );
        }
        catch (SQLException E)
        {
            System.out.println("SQLException: " + E.getMessage());
            System.out.println("SQLState:     " + E.getSQLState());
            System.out.println("VendorError:  " + E.getErrorCode());
        }
        return null;
    }
	
public ResultSet getDBSectionsByProfessor(Semester currentSemester, String professorName)
{
    try
    {
         Statement stmt = conn.createStatement();
        return stmt.executeQuery( "SELECT * FROM Section JOIN Instructor ON section_instructor_id = instructor_id " +
                "JOIN Course ON section_course_id = course_id JOIN Semester ON course_semester_id = semester_id " +
                "WHERE instructor_lname = \"" + professorName + "\" && " +
                "semester_year = \"" + currentSemester.getYear() + "\" && semester_season =" + currentSemester.getSeason() );
    }
       catch (SQLException E)
        {
            System.out.println("SQLException: " + E.getMessage());
            System.out.println("SQLState:     " + E.getSQLState());
            System.out.println("VendorError:  " + E.getErrorCode());
        }
        return null;
}

    public ResultSet getDBSections( String courseSubject, String courseNum, int year, int season )
    {
        try
        {
            Statement stmt = conn.createStatement();
            return stmt.executeQuery( "SELECT * FROM Course, Section, Instructor, Semester " +
                    "WHERE section_course_id = course_id && instructor_id = section_instructor_id" +
                    "&& course_subject = \"" + courseSubject + "\" " +
                    "&& course_number = \"" + courseNum + "\" " +
                    "&& course_semester_id = semester_id && " +
                    "semester_year = " + year + " && semester_season = " + season );
        }
        catch (SQLException E)
        {
            System.out.println("SQLException: " + E.getMessage());
            System.out.println("SQLState:     " + E.getSQLState());
            System.out.println("VendorError:  " + E.getErrorCode());
        }
        return null;
    }

    public ResultSet getDBClassTimes( int sectionId )
    {
        try
        {
            Statement stmt = conn.createStatement();

            return stmt.executeQuery( "SELECT * FROM Classtime " +
                    "WHERE classtime_section_id = " + sectionId );
        }
        catch (SQLException E)
        {
            System.out.println("SQLException: " + E.getMessage());
            System.out.println("SQLState:     " + E.getSQLState());
            System.out.println("VendorError:  " + E.getErrorCode());
        }
        return null;
    }
	
 public ResultSet getDBCourses(int year, int season)
{
    try
    {
        Statement stmt = conn.createStatement();
       //TO DO
        return stmt.executeQuery("Select DISTINCT Course_subject, course_name, course-number FROM Course WHERE courseSemester semester_year = " + year + " && semester_season = " + season );

    }
    catch (SQLException E)
    {
         System.out.println("SQLException: " + E.getMessage());
         System.out.println("SQLState:     " + E.getSQLState());
         System.out.println("VendorError:  " + E.getErrorCode());

    } return null;
}

    public ResultSet getDBProfessors()
    {
        try
        {
            Statement stmt = conn.createStatement();
            return stmt.executeQuery("SELECT instructor_lname FROM Instructor  " +
                    "ORDER BY instructor_lname DESC");
        }
        catch (SQLException E)
        {
            System.out.println("SQLException: " + E.getMessage());
            System.out.println("SQLState:     " + E.getSQLState());
            System.out.println("VendorError:  " + E.getErrorCode());
        }
        return null;
    }



    /***************************************/

    public Course[] getCourses( )
    {
        ResultSet rs = getDBCourses();
        ArrayList<Course> courses = new ArrayList<Course>();

        try
        {
            while (rs.next())
            {
                String subject = rs.getString("course_subject");
                String number = rs.getString("course_number");
                String name = rs.getString("course_name");
                String department = rs.getString("course_department");
                int credits = rs.getInt("course_credits");
                String desc = rs.getString("course_description");
                courses.add(new Course(subject, number, name, department, credits, desc));
            }
			
            return courses.toArray(new Course[0]);
        }
        catch (SQLException E)
        {
            System.out.println("SQLException: " + E.getMessage());
            System.out.println("SQLState:     " + E.getSQLState());
            System.out.println("VendorError:  " + E.getErrorCode());
        }
        return null;
    }

    public Course[] getCourses( String subjectName, int year, int season )
    {
        ResultSet rs = getDBCourses( subjectName, year, season );
        ArrayList<Course> courses = new ArrayList<Course>();

        try
        {
            while (rs.next())
            {
                String subject = rs.getString("course_subject");
                String number = rs.getString("course_number");
                String name = rs.getString("course_name");
                String department = rs.getString("course_department");
                int credits = rs.getInt("course_credits");
                String desc = rs.getString("course_description");
                courses.add(new Course(subject, number, name, department, credits, desc));
            }
            return courses.toArray(new Course[0]);
        }
        catch (SQLException E)
        {
            System.out.println("SQLException: " + E.getMessage());
            System.out.println("SQLState:     " + E.getSQLState());
            System.out.println("VendorError:  " + E.getErrorCode());
        }
        return null;
    }

    public String[] getSubjects( int year, int season )
    {
        ResultSet rs = getDBCourseSubjects( year, season );
        ArrayList<String> subjects = new ArrayList<String>();

        try
        {
            while (rs.next())
            {
                subjects.add(rs.getString("course_subject"));
            }
            return subjects.toArray(new String[0]);
        }
        catch (SQLException E)
        {
            System.out.println("SQLException: " + E.getMessage());
            System.out.println("SQLState:     " + E.getSQLState());
            System.out.println("VendorError:  " + E.getErrorCode());
        }
        return null;
    }

    public Semester[] getSemesters()
    {
        ResultSet rs = getDBSemesters();
        ArrayList<Semester> semesters = new ArrayList<Semester>();

        try
        {
            while (rs.next())
            {
                int year = rs.getInt("semester_year");
                int season = rs.getInt("semester_season");
                semesters.add(new Semester(year, season));
            }
            return semesters.toArray(new Semester[0]);
        }
        catch (SQLException E)
        {
            System.out.println("SQLException: " + E.getMessage());
            System.out.println("SQLState:     " + E.getSQLState());
            System.out.println("VendorError:  " + E.getErrorCode());
        }
        return null;
    }

    public ArrayList<ClassTime> getClassTimes( int sectionId )
    {
        ResultSet rs = getDBClassTimes( sectionId );
        ArrayList<ClassTime> classTimes = new ArrayList<ClassTime>();

        try
        {
            while (rs.next())
            {
                int dayIndex = rs.getInt("classtime_day");
                ClassTime.Day[] vals = ClassTime.Day.values();
                ClassTime.Day day = vals[dayIndex];

                String room = rs.getString("classtime_room");

                int startHours = rs.getInt("classtime_start_hours");
                int startMinutes = rs.getInt("classtime_start_minutes");
                int endHours = rs.getInt("classtime_end_hours");
                int endMinutes = rs.getInt("classtime_end_minutes");
				
                ScheduleShare.Time startTime = new ScheduleShare.Time(startHours, startMinutes);
                ScheduleShare.Time endTime = new ScheduleShare.Time(endHours, endMinutes);

                classTimes.add(new ClassTime(room, day, startTime, endTime ));
            }
            return classTimes;
        }
        catch (SQLException E)
        {
            System.out.println("SQLException: " + E.getMessage());
            System.out.println("SQLState:     " + E.getSQLState());
            System.out.println("VendorError:  " + E.getErrorCode());
        }
        return null;
    }

    public Section[] getSections( String courseSubject, String courseNum, int year, int season  )
    {
        ResultSet rs = getDBSections( courseSubject, courseNum, year, season );
        ArrayList<Section> sections = new ArrayList<Section>();

        try
        {
            while (rs.next())
            {
                String sectionNumber = rs.getString("section_number");
                String sectionCallNumber = rs.getString("section_call_number");
                boolean online = rs.getBoolean("section_online");
                boolean closed = rs.getBoolean("section_closed");
                int maxStudents = rs.getInt("section_max_students");
                int curStudents = rs.getInt("section_current_students");
                int credits = rs.getInt("section_credits");
                String comments = rs.getString("section_comments");

                String fName = rs.getString("instructor_fname");
                String lName = rs.getString("instructor_lname");
                Professor professor = new Professor(fName, lName);

                String subject = rs.getString("course_subject");
                String number = rs.getString("course_number");
                String name = rs.getString("course_name");
                String department = rs.getString("course_department");
                if( department == null )
                    department = subject;
                Course course = new Course( subject, number, name, department, credits );

                int sectionId = rs.getInt("section_id");
                ArrayList<ClassTime> classTimes = getClassTimes( sectionId );

                Section section = new Section( course, sectionNumber, sectionCallNumber, professor, classTimes, online, closed, maxStudents, curStudents, credits, comments );
                sections.add(section);
            }
            return sections.toArray(new Section[0]);
        }
        catch (SQLException E)
        {
            System.out.println("SQLException: " + E.getMessage());
            System.out.println("SQLState:     " + E.getSQLState());
            System.out.println("VendorError:  " + E.getErrorCode());
        }
        return null;
    }

    public String [] getProfessors() {
        ResultSet rs = getDBProfessors();
        ArrayList<String> professors = new ArrayList<String>();

        try {
            while (rs.next()) {
                String professorName = rs.getString("instructor_lname");
                professors.add(professorName);
            }
            return professors.toArray(new String[0]);
        } catch (SQLException E) {
            System.out.println("SQLException: " + E.getMessage());
            System.out.println("SQLState:     " + E.getSQLState());
            System.out.println("VendorError:  " + E.getErrorCode());
        }
        return null;
    }
    public Section [] getSectionsByProfessors(Semester currentSemester,String ProfessorName  )
    {
       ResultSet rs = getDBSectionsByProfessor( currentSemester,ProfessorName);
        ArrayList<Section> sections = new ArrayList<Section>();

        try
        {
            while (rs.next())
            {
                String sectionNumber = rs.getString("section_number");
                String sectionCallNumber = rs.getString("section_call_number");
				
                boolean online = rs.getBoolean("section_online");
                boolean closed = rs.getBoolean("section_closed");
                int maxStudents = rs.getInt("section_max_students");
                int curStudents = rs.getInt("section_current_students");
                int credits = rs.getInt("section_credits");
                String comments = rs.getString("section_comments");

                String fName = rs.getString("instructor_fname");
                String lName = rs.getString("instructor_lname");
                Professor professor = new Professor(fName, lName);

                String subject = rs.getString("course_subject");
                String number = rs.getString("course_number");
                String name = rs.getString("course_name");
                String department = rs.getString("course_department");
				
                if( department == null )
                    department = subject;
                Course course = new Course( subject, number, name, department, credits );

                int sectionId = rs.getInt("section_id");
                ArrayList<ClassTime> classTimes = getClassTimes( sectionId );

                Section section = new Section( course, sectionNumber, sectionCallNumber, professor, classTimes, online, closed, maxStudents, curStudents, credits, comments );
                sections.add(section);
            }
			
            return sections.toArray(new Section[0]);
			
        } catch (SQLException E) {
            System.out.println("SQLException: " + E.getMessage());
            System.out.println("SQLState:     " + E.getSQLState());
            System.out.println("VendorError:  " + E.getErrorCode());
        }
        return null;
    }
        public String [] getCourseNames(int year, int season) {
        ResultSet rs = getDBCourseNames(year, season);
        ArrayList<String> courses = new ArrayList<String>();

        try {
            while (rs.next()) {
                String CourseNames = rs.getString("course_subject") + "-" + 
                        rs.getString("course_number") + " " + rs.getString("course_name");
						
                courses.add(CourseNames);
            }
			
            return courses.toArray(new String[0]);
        } catch (SQLException E) {
            System.out.println("SQLException: " + E.getMessage());
            System.out.println("SQLState:     " + E.getSQLState());
            System.out.println("VendorError:  " + E.getErrorCode());
        }
		
        return null;
    }


}
