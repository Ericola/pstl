package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

import javax.naming.NamingException;

import utils.Database;

/**
 * NE PAS OUBLIER DE CLOSE LES CONNECTIONS !
 */
public class AuthentificationTools {

	private static final int	DUREE_VALIDITE_SESSION	= 432000000;	// 5minutes

	public static boolean userExists( String username ) throws DbException, NamingException {
		String sql = "SELECT * FROM user WHERE username ='" + username + "'";

		try {
			Connection c = Database.getConnection();
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery( sql );

			if ( rs.next() ) {
				s.close();
				c.close();
				return true;
			} else {
				s.close();
				c.close();
				return false;
			}

		} catch ( SQLException e ) {
			throw new DbException( "Erreur : userExists : " + e.getMessage() );
		}
	}

	public static boolean checkPassword( String username, String password ) throws DbException, NamingException {

		String sql = "SELECT password FROM user WHERE username = '" + username + "'";

		try {
			Connection c = Database.getConnection();
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery( sql );
			rs.next();
			String p = rs.getString( "password" );

			s.close();
			c.close();
			if ( p.equals( password ) )
				return true;
			else
				return false;
		} catch ( SQLException e ) {
			throw new DbException( e.getMessage() );
		}
	}

	public static int getIdFromUsername( String username ) throws DbException, NamingException {
		String sql = "SELECT id FROM user WHERE username = '" + username + "'";

		try {
			Connection c = Database.getConnection();
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery( sql );
			rs.next();
			int id = rs.getInt( "id" );
			s.close();
			c.close();
			return id;

		} catch ( SQLException e ) {
			throw new DbException( e.getMessage() );
		}
	}

	public static String insertSession( int id_user ) throws DbException, NamingException {
		String token;
		try {
			Connection c = Database.getConnection();
			Statement s;
			ResultSet rs;

			// Recherche d'une clé valide ( pas déja utilisée)
			do {
				token = generateToken();
				String sql = "SELECT * FROM session WHERE token = '" + token + "'";
				s = c.createStatement();
				rs = s.executeQuery( sql );
			} while ( rs.next() );
			s.close();

			// Insertion de la clé dans la base
			long timestamp = System.currentTimeMillis();
			String sql = "INSERT INTO `session`( `token`, `user_id`, `start`) VALUES ( '" + token + "', " + id_user
					+ ", " + timestamp + ")";
			s = c.createStatement();
			s.executeUpdate( sql );
			s.close();
			c.close();
		} catch ( SQLException e ) {
			throw new DbException( e.getMessage() );
		}
		return token;
	}

	/**
	 * Supprime une clé session associé a un utilisateur.
	 * @param user_id
	 * @param token
	 * @return
	 * @throws DbException
	 * @throws NamingException
	 */
	public static String deleteSession( String token ) throws DbException, NamingException {
		String sql = "DELETE FROM session WHERE token = '" + token + "'";

		try {
			Connection c = Database.getConnection();
			Statement s = c.createStatement();
			s.executeUpdate( sql );
			s.close();
			c.close();
		} catch ( SQLException e ) {
			throw new DbException( e.getMessage() );
		}
		return token;
	}

	/**
	 * 
	 * @param id_user
	 * @return vrai si la session est valide, faux sinon.
	 * @throws DbException
	 * @throws NamingException
	 */
	public static boolean checkSession( String token ) throws DbException, NamingException {
		String sql = "SELECT s.start, s.remember_me FROM session s WHERE  s.token = '" + token + "'";
		long timestamp = System.currentTimeMillis();
		boolean isValid = false;
		try {
			Connection c = Database.getConnection();
			Statement s = c.createStatement();
			ResultSet res = s.executeQuery( sql );
			res.next();

			if ( !res.getBoolean( "remember_me" ) ) {

				// La clé est toujours valide, on prolonge la session
				if ( timestamp < ( res.getLong( "start" ) + DUREE_VALIDITE_SESSION ) ) {
					sql = "UPDATE session SET start = " + timestamp + " WHERE token = '" + token + "'";
					isValid = true;
				} else {
					// La clé n'est plus valide, on supprime la clé
					sql = "DELETE FROM session WHERE token = '" + token + "'";
					isValid = false;
				}

				s.executeUpdate( sql );
				s.close();
				c.close();
			}
		} catch ( SQLException e ) {
			throw new DbException( "La session a expiré : " + e.getMessage() );

		}
		return isValid;
	}

	public static int getUserId( String token ) throws DbException, NamingException {

		String sql = "SELECT user_id FROM session WHERE token = '" + token + "'";

		try {
			Connection c = Database.getConnection();
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery( sql );
			rs.next();
			int id = rs.getInt( "user_id" );
			s.close();
			c.close();
			return id;

		} catch ( SQLException e ) {
			throw new DbException( e.getMessage() );
		}

	}

	/**
	 * @return une chaine de caractères aléatoires(entre A-Z et 0-9)
	 */
	private static String generateToken() {
		char[] tab = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
				't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

		StringBuilder sb = new StringBuilder();
		Random r = new Random();
		for ( int i = 0; i < 32; i++ ) {
			sb.append( tab[r.nextInt( tab.length )] );
		}

		return sb.toString();
	}

	public static int getTypeFromId( int id_user ) throws SQLException, NamingException {
		String sql = "SELECT type FROM user WHERE id = '" + id_user + "'";
		Connection c = Database.getConnection();
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery( sql );
		rs.next();
		int id = rs.getInt( "type" );
		s.close();
		c.close();
		return id;

	}

}
