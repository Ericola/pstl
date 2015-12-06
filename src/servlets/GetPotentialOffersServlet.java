package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import services.Offer;

/**
 * Servlet implementation class GetPotentialOffers
 */
@WebServlet( name = "/GetPotentialOffers", urlPatterns = { "/get_potential_offers.json" } )
public class GetPotentialOffersServlet extends HttpServlet {
	private static final long	serialVersionUID	= 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetPotentialOffersServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException,
			IOException {
		String token = request.getParameter( "token" );

		response.setContentType( "text/plain" );
		PrintWriter out = response.getWriter();

		out.println( Offer.getPotentialOffers( token ) );
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException,
			IOException {
		// TODO Auto-generated method stub
	}

}
