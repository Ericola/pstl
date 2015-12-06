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
 * Servlet implementation class GetSpecOffer
 */
@WebServlet(name = "GetSpecOffer", urlPatterns = { "/get_spec_offer.json" })
public class GetSpecOfferServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetSpecOfferServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("id");

		// Response
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.println(Offer.getSpecOffer(id));
	}


}
