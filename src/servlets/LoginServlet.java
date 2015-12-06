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
 * Servlet implementation class LoginServlet
 */
@WebServlet( name = "Login", urlPatterns = { "/login.json" } )
public class LoginServlet extends HttpServlet {
	private static final long	serialVersionUID	= 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException,
			IOException {
		//Get parameters
		String username = request.getParameter( "username" );
		String password = request.getParameter( "password" );

		//Response
		response.setContentType( "text/plain" );
		PrintWriter out = response.getWriter();
		out.println( User.login( username, password ) );
	}

}
