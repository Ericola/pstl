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
 * Servlet implementation class CreateUserServlet
 */
@WebServlet( name = "CreateUser", urlPatterns = { "/create_user.json" } )
public class CreateUserServlet extends HttpServlet {
	private static final long	serialVersionUID	= 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException,
			IOException {
		String username = request.getParameter( "username" );
		String email = request.getParameter( "email" );
		String lastName = request.getParameter( "lastName" );
		String firstName = request.getParameter( "firstName" );
		String password = request.getParameter( "password" );
		int type = Integer.parseInt( request.getParameter( "type" ) );

		//Response
		response.setContentType( "text/plain" );
		PrintWriter out = response.getWriter();
		out.println( User.createUser( username, email, lastName, firstName, password, type ) );
	}

}
