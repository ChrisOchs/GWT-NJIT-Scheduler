package ScheduleDataAccessor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ScheduleShare.*;

// Servlet used to provide access to data in the MySQL server
public class ScheduleDataAccessorServlet extends HttpServlet {

    private ScheduleDataAccessor dataAccessor;

    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
		
        throw new ServletException("Improper call to doGet()");
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        dataAccessor = new ScheduleDataAccessor();

        ObjectInputStream in = new ObjectInputStream(request.getInputStream());
        ObjectOutputStream out = new ObjectOutputStream(response.getOutputStream());

        try {
            // Read in command name, which is always the first object
            String commandName = (String)in.readObject();

            // TODO: From Middleware: I need these functions implemented
            // for the model:
			
            // replace "new obj[0]" with a populated array from the database
            if ( commandName.equals("getAvailableCourses") )
            {
                // get all courses (not sections) that exist in this semester
                Semester currentSemester = (Semester)in.readObject();
                String subject = (String) in.readObject();
                out.writeObject( dataAccessor.getCourses( subject, currentSemester.getYear(), currentSemester.getSeason() )) ;
                //out.writeObject( DumbDataMaker.courses( subject) );

            } 
            else if ( commandName.equals("getAvailableSubjects") )
            {
                Semester currentSemester = (Semester)in.readObject();
                out.writeObject( dataAccessor.getSubjects( currentSemester.getYear(), currentSemester.getSeason() ) );
                System.out.println( "" + currentSemester.getYear() + " " + currentSemester.getSeason() );
                //out.writeObject( DumbDataMaker.subjects() );

            } 
            else if ( commandName.equals("getAvailableSections") )
            {
                Semester currentSemester = (Semester)in.readObject();
                // subj = MATH, num = 222 - for example
                String subj = (String)in.readObject();
                String num = (String)in.readObject();
                System.out.println( subj + " " + num);
                out.writeObject( dataAccessor.getSections(subj, num, currentSemester.getYear(), currentSemester.getSeason()));
                //out.writeObject( DumbDataMaker.sections(subj, num));

            } 
            else if ( commandName.equals("getAvailableSemesters") )
            {
                // test code
                //out.writeObject( DumbDataMaker.semesters() );
                out.writeObject( dataAccessor.getSemesters() );

            }
            else if( commandName.equals("getProfessors") ) {
                out.writeObject( dataAccessor.getProfessors() );
            }
            else if( commandName.equals("getCourseNames")) {

                Semester currentSemester = (Semester)in.readObject();
                out.writeObject( dataAccessor.getCourseNames(currentSemester.getYear(), currentSemester.getSeason() )) ;           
            }

            else if( commandName.equals("getSectionsByProfessor")) {
               String professorName = (String)in.readObject();
               Semester currentSemester = (Semester)in.readObject();
               out.writeObject(dataAccessor.getSectionsByProfessors(currentSemester, professorName));
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        // TODO: add command handling

        in.close();
        out.close();
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servlet used to access schedule data from the MySQL database";
    }



}
