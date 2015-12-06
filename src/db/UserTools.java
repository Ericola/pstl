package db;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.MongoException;

import utils.Database;

public class UserTools {

	/** Attributs table User **/
	private static final String	USERNAME	= "username";
	private static final String	LAST_NAME	= "last_name";
	private static final String	FIRST_NAME	= "first_name";
	private static final String	EMAIL		= "email";
	private static final String	ID			= "id";
	private static final String	TYPE		= "type";

	/**
	 * Enregistre un utilisateur dans la base de données
	 * @param username
	 * @param email
	 * @param password
	 * @throws DbException
	 * @throws NamingException
	 */
	public static void createUser( String username, String email, String lastName, String firstName, String password,
			int type ) throws DbException, NamingException {
		String request = "INSERT INTO user VALUES (null, '" + username + "', '" + password + "', '" + lastName + "', '"
				+ firstName + "', '" + email + "', " + type + ")";

		try {
			Connection c = Database.getConnection();
			Statement s = c.createStatement();
			s.executeUpdate( request );
			s.close();
			c.close();
		} catch ( SQLException e ) {
			e.printStackTrace();
			throw new DbException( "Erreur : creation d'un utilisateur : " + e.getMessage() );
		}
	}

	public static String getUsername( int id ) throws DbException, NamingException {
		String sql = "SELECT username FROM user WHERE id = '" + id + "'";

		try {
			Connection c = Database.getConnection();
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery( sql );
			rs.next();
			String username = rs.getString( "username" );
			s.close();
			c.close();
			return username;

		} catch ( SQLException e ) {
			throw new DbException( e.getMessage() );
		}
	}

	public static JSONObject getUserInfos( int id ) throws JSONException, DbException, NamingException {
		String sql = "SELECT * FROM user u WHERE u.id = '" + id + "'";
		JSONObject o = new JSONObject();

		try {
			Connection c = Database.getConnection();
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery( sql );
			rs.next();
			o.put( USERNAME, rs.getString( USERNAME ) );
			o.put( LAST_NAME, rs.getString( LAST_NAME ) );
			o.put( FIRST_NAME, rs.getString( FIRST_NAME ) );
			o.put( EMAIL, rs.getString( EMAIL ) );
			o.put( TYPE, rs.getString( TYPE ) );
			s.close();
			c.close();
			return o;

		} catch ( SQLException e ) {
			throw new DbException( e.getMessage() );
		}

	}

	public static List<JSONObject> searchUser( String text ) throws JSONException, UnknownHostException,
			MongoException, DbException, NamingException {

		String[] wordsTab = text.split( "[^\\d\\w]+" );

		// Construction regex
		String regex = "'" + wordsTab[0];

		for ( int i = 1; i < wordsTab.length; i++ ) {

			regex += "|" + wordsTab[i];

		}

		regex += "'";

		String sql = "SELECT * FROM user  WHERE username REGEXP " + regex + "OR Nom REGEXP " + regex
				+ "OR Prenom REGEXP " + regex;

		List<JSONObject> l = new ArrayList<JSONObject>();

		try {
			Connection c = Database.getConnection();
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery( sql );

			JSONObject o;

			while ( rs.next() ) {

				o = new JSONObject();
				o.put( USERNAME, rs.getString( USERNAME ) );
				o.put( LAST_NAME, rs.getString( LAST_NAME ) );
				o.put( FIRST_NAME, rs.getString( FIRST_NAME ) );
				o.put( EMAIL, rs.getString( EMAIL ) );
				o.put( ID, rs.getString( ID ) );
				l.add( o );
			}

			s.close();
			c.close();
			return l;

		} catch ( SQLException e ) {
			throw new DbException( e.getMessage() );
		}

	}

	public static void updateUser( Integer id, String column, String value ) throws DbException, NamingException {
		String request = "UPDATE user SET " + column + " = '" + value + "'  WHERE id = " + id + "";

		try {
			Connection c = Database.getConnection();
			Statement s = c.createStatement();
			s.executeUpdate( request );
			s.close();
			c.close();
		} catch ( SQLException e ) {
			e.printStackTrace();
			throw new DbException( "Erreur : creation d'un utilisateur : " + e.getMessage() );
		}
	}
}
