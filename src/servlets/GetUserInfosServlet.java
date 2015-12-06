package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import services.User;

/**
 * Servlet implementation class GetUserInfosServlet
 */
@WebServlet( "/get_user_infos" )
public class GetUserInfosServlet extends HttpServlet {
	private static final long	serialVersionUID	= 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException,
			IOException {
		// Get parameters
		String token = request.getParameter( "token" );

		// Response
		response.setContentType( "text/plain" );
		PrintWriter out = response.getWriter();
		out.println( User.getUserInfos( token ) );
	}

}
