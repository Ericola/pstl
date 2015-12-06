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
 * Servlet implementation class AddExperiencesServlet
 */
@WebServlet(name = "/AddExperience", urlPatterns = { "/add_experience.json" })
public class AddExperienceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddExperienceServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String token = request.getParameter("token");
		String title = request.getParameter("title");
		String company = request.getParameter("company");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String description = request.getParameter("description");
		response.setContentType( "text/plain" );
		PrintWriter out = response.getWriter();
		out.println( Cv.addExperience(token, title, company, startDate, endDate, description));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
