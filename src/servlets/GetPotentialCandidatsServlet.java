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
 * Servlet implementation class GetPotentialOffers
 */
@WebServlet( name = "/GetPotentialCandidats", urlPatterns = { "/get_potential_candidats.json" } )
public class GetPotentialCandidatsServlet extends HttpServlet {
	private static final long	serialVersionUID	= 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetPotentialCandidatsServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException,
			IOException {
		String token = request.getParameter( "token" );
		String offerId = request.getParameter( "id" );

		response.setContentType( "text/plain" );
		PrintWriter out = response.getWriter();

		out.println( Cv.getPotentialCandidats( token, offerId ) );
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException,
			IOException {
		// TODO Auto-generated method stub
	}

}
