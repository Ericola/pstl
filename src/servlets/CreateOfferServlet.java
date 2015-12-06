package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import services.Offer;
import services.User;

/**
 * Servlet implementation class CreateOfferServlet
 */
@WebServlet(name = "CreateOffer", urlPatterns = { "/create_offer.json" })
public class CreateOfferServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateOfferServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// Get parameters
		request.setCharacterEncoding("UTF-8");
		String token = request.getParameter("token");
		String offer_id = request.getParameter("offer_id"); // id de l'offre
		String column_id = request.getParameter("column_id"); // numero de la
		// rubrique
//		0 : 1ere rubrique
//		1 : rubrique description
//		2 : rubrique competences requises
//		3 : rubrique info contact
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		switch (Integer.parseInt(column_id)) {
		case 0:

			String title = request.getParameter("title");
			String location = request.getParameter("location");
			String start_date = request.getParameter("start_date");
			String mission_duration = request.getParameter("mission_duration");
			String business = request.getParameter("business");
			out.println(Offer.createOfferInfo(token, title, location, start_date, mission_duration, business, offer_id));
			break;

		case 1:
			String desc = request.getParameter("description");
			out.println(Offer.createOfferDesc(token, desc, offer_id));
			break;

		case 2:
			String[] required_competence = request
					.getParameterValues("required_competence[]");
			out.println(Offer.createOfferComp(token, required_competence, offer_id));
			break;

		case 3:
			String contact = request.getParameter("contact");
			out.println(Offer.createOfferContact(token, contact, offer_id));
			break;

		default: // L'utilisateur n'a rien �crit dans l'offre. On ne renvoie rien
			break;
		}
	}

}
