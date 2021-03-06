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
 * Servlet implementation class DeleteOfferServlet
 */
@WebServlet(name = "DeleteOffer", urlPatterns = { "/delete_offer.json" })
public class DeleteOfferServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteOfferServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String token = request.getParameter("token");
		String id = request.getParameter("id");

		// Response
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.println(Offer.deleteOffer(token, id));
	}


}
