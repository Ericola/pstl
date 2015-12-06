package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import services.Cv;
import services.Offer;

/**
 * Servlet implementation class SearchServlet
 */
@WebServlet( name = "/Search", urlPatterns = { "/search.json" } )
public class SearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String parameterSearch = request.getParameter("keywords");
		String[] parameters = parameterSearch.split("[\\s,.?!/]+");
		String business = request.getParameter("business");
		String location = request.getParameter("location");
		response.setContentType( "text/plain" );
		PrintWriter out = response.getWriter();

		out.println( Offer.search(parameters, business, location));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
