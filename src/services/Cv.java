package services;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.naming.NamingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.MongoException;

import db.AuthentificationTools;
import db.CvTools;
import db.DbException;
import db.OfferTools;
import utils.Database;
import utils.ErrorCodes;

public class Cv {

	public static JSONObject addExperience( String token, String title, String company, String startDate,
			String endDate, String description ) {

		try {
			// Verification de la validite de la cle
			if ( !AuthentificationTools.checkSession( token ) ) {
				return ServicesTools.error( "La session a expire", ErrorCodes.SESSION_EXPIRED );
			}
			// Insertion de l'experience dans la bd
			int user_id = AuthentificationTools.getUserId( token );
			CvTools.addExperience( user_id, title, company, startDate, endDate, description );
			;
			return ServicesTools.ok();

		} catch ( DbException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( NamingException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JAVA ) );
		} catch ( SQLException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );

		}
	}

	public static JSONObject addCompetence( String token, String[] competences ) throws UnknownHostException,
			MongoException {
		try {
			// Verification de la validite de la cle
			if ( AuthentificationTools.checkSession( token ) ) {
				int user_id = AuthentificationTools.getUserId( token );
				CvTools.deleteAllUserCompetence( user_id );
				if ( competences == null )
					return ServicesTools.ok();
				for ( int i = 0; i < competences.length; i++ ) {
					CvTools.addCompetence( user_id, competences[i] );
				}

				return ServicesTools.ok();
			} else {
				return ServicesTools.error( "La session a expire", ErrorCodes.SESSION_EXPIRED );
			}
		} catch ( DbException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( NamingException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JAVA ) );
		} catch ( SQLException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );

		}
	}

	public static JSONObject addFavoredCompetence( String token, String[] data ) {
		try {
			// Verification de la validite de la cle
			if ( AuthentificationTools.checkSession( token ) ) {
				int user_id = AuthentificationTools.getUserId( token );
				CvTools.deleteAllUserFavoredCompetence( user_id );
				if ( data == null )
					return ServicesTools.ok();
				for ( int i = 0; i < data.length; i++ ) {
					CvTools.addFavoredCompetence( user_id, data[i] );
				}
				return ServicesTools.ok();
			} else {
				return ServicesTools.error( "La session a expire", ErrorCodes.SESSION_EXPIRED );
			}
		} catch ( DbException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( NamingException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JAVA ) );
		} catch ( SQLException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );

		}
	}

	public static JSONObject addLanguage( String token, String language, String l ) {
		try {
			// Verification de la validite de la cle
			if ( AuthentificationTools.checkSession( token ) ) {
				int user_id = AuthentificationTools.getUserId( token );
				int level = Integer.parseInt( l );
				CvTools.addLanguage( user_id, language, level );
				return ServicesTools.ok();
			} else {
				return ServicesTools.error( "La session a expire", ErrorCodes.SESSION_EXPIRED );
			}
		} catch ( DbException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( NamingException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JAVA ) );

		}
	}

	public static JSONObject addFormation( String token, String diploma, String school, String startDate, String endDate ) {
		try {
			// Verification de la validite de la cle
			if ( AuthentificationTools.checkSession( token ) ) {
				int user_id = AuthentificationTools.getUserId( token );
				CvTools.addFormation( user_id, diploma, school, startDate, endDate );
				return ServicesTools.ok();
			} else {
				return ServicesTools.error( "La session a expire", ErrorCodes.SESSION_EXPIRED );
			}
		} catch ( DbException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( NamingException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JAVA ) );

		} catch (SQLException e) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		}
	}

	public static JSONObject getCv( String token ) {
		try {
			// Verification de la validite de la cle
			if ( AuthentificationTools.checkSession( token ) ) {
				int user_id = AuthentificationTools.getUserId( token );
				return CvTools.getCv( user_id );
			} else {
				return ServicesTools.error( "La session a expire", ErrorCodes.SESSION_EXPIRED );
			}
		} catch ( DbException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( NamingException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JAVA ) );

		}
	}

	public static JSONObject getAllCompetences( String token, String mot ) {
		try {
			// Verification de la validite de la cle
			if ( AuthentificationTools.checkSession( token ) ) {
				int user_id = AuthentificationTools.getUserId( token );
				String a;
				return CvTools.getAllCompetences( user_id, mot );
			} else {
				return ServicesTools.error( "La session a expire", ErrorCodes.SESSION_EXPIRED );
			}
		} catch ( DbException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( NamingException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JAVA ) );

		}
	}

	public static Object getPotentialCandidats( String token, String offerId ) {
		try {
			// Verification de la validite de la cle
			if ( AuthentificationTools.checkSession( token ) ) {
				//				int user_id = AuthentificationTools.getUserId( token );
				return new JSONArray( CvTools.getPotentials( offerId ) );
			} else {
				return ServicesTools.error( "La session a expire", ErrorCodes.SESSION_EXPIRED );
			}
		} catch ( DbException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( MongoException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( NamingException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( UnknownHostException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( JSONException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JSON ) );
		} catch ( SQLException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );

		}
	}

	public static String[] getCVasArrayOfWords( int user_id ) throws SQLException, NamingException {
		ArrayList<String> result = new ArrayList<>();
		Connection c = Database.getConnection();
		Statement s = c.createStatement();

		// Experience
		String sql = "SELECT title, description " + "FROM experience " + "WHERE user_id =" + user_id;
		ResultSet res = s.executeQuery( sql );

		while ( res.next() ) {
			String[] t = res.getString( "title" ).split( "[\\s,.?!]+" );
			for ( String str : t )
				result.add( str );
		}

		// Competence
		sql = "SELECT  name " + "FROM competence, user_competence " + "WHERE user_competence.user_id = " + user_id
				+ " AND competence.id = user_competence.competence_id";

		res = s.executeQuery( sql );
		while ( res.next() ) {
			result.add( res.getString( "name" ) );
		}

		// Favored competences
		sql = "SELECT  name " + "FROM competence, user_favored_competence "
				+ "WHERE user_favored_competence.user_id = " + user_id
				+ " AND competence.id = user_favored_competence.competence_id";
		res = s.executeQuery( sql );
		while ( res.next() ) {
			result.add( res.getString( "name" ) );
		}

		// Formation
		sql = "SELECT  formation.diploma, formation.school FROM formation, user_formation "
				+ "WHERE user_formation.user_id = " + user_id + " AND formation.id = user_formation.formation_id;";
		res = s.executeQuery( sql );
		while ( res.next() ) {
			String[] t = res.getString( "diploma" ).split( "[\\s,.?!]+" );
			for ( String str : t )
				result.add( str );
			t = res.getString( "school" ).split( "[\\s,.?!]+" );
			for ( String str : t )
				result.add( str );
		}

		// sql = "SELECT  language.name, can_speak.level " +
		// "FROM can_speak, language "
		// + "WHERE can_speak.user_id = " + user_id +
		// " AND language.id = can_speak.language_id";
		// res = s.executeQuery( sql );
		// while ( res.next() ) {
		// result.add( res.getString( "name" ) );
		// }

		return result.toArray( new String[0] );

	}
}