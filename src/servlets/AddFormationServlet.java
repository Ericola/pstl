package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import services.Cv;

/**
 * Servlet implementation class AddFormationsServlet
 */
@WebServlet( name = "/addFormation", urlPatterns = { "/update_formation.json" } )
public class AddFormationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddFormationServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String token = request.getParameter("token");
		String diploma = request.getParameter("diploma");
		String school = request.getParameter("school");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		
		response.setContentType( "text/plain" );
		PrintWriter out = response.getWriter();
		out.println( Cv.addFormation(token, diploma, school, startDate, endDate));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
