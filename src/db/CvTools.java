package db;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.NamingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import services.ServicesTools;
import utils.Database;
import utils.ErrorCodes;
import utils.WordVO;

import com.mongodb.MongoException;

public class CvTools {

	/**
	 * Ajoute un objet "Experience" 
	 * 
	 * @param id
	 * @param column
	 * @param value
	 * @throws DbException 
	 * @throws NamingException
	 * @throws SQLException 
	 */
	public static void addExperience( int user_id, String title, String company, String startDate, String endDate,
			String description ) throws DbException, NamingException, SQLException {

		String sql = "INSERT INTO experience VALUES(null, ?, ?, STR_TO_DATE( ? ,'%d/%m/%Y'), STR_TO_DATE(?,'%d/%m/%Y'), ?, "
				+ user_id + ")";

		try ( Connection c = Database.getConnection() ) {
			try ( PreparedStatement s = c.prepareStatement( sql ) ) {
				s.setString( 1, title );
				s.setString( 2, company );
				s.setString( 3, startDate );
				s.setString( 4, endDate );
				s.setString( 5, description );
				s.executeUpdate();
			}

			// index
			if ( title != null )
				IndexTools.CV.index( title.split( "[\\s,.?!/]+" ), user_id );
			if ( description != null )
				IndexTools.CV.index( description.split( "[\\s,.?!/]+" ), user_id );
		}

	}

	public static void addFavoredCompetence( int user_id, String name ) throws NamingException, DbException,
			SQLException {
		String sql = "SELECT id FROM competence WHERE name = " + "'" + name + "'";
		int competence_id;
		try ( Connection c = Database.getConnection() ) {

			try ( PreparedStatement s = c.prepareStatement( sql ) ) {
				ResultSet res = s.executeQuery();
				if ( !res.next() ) { // Si La competence n'existe pas encore dans la base On la cree
					addCompetence( user_id, name );
					try ( PreparedStatement ssss = c.prepareStatement( sql ) ) {
						ResultSet resss = ssss.executeQuery();
						resss.next();
						competence_id = resss.getInt( "id" );
					}
				} else
					competence_id = res.getInt( "id" ); // On recupere l'id de la
			}
			sql = "INSERT INTO user_favored_competence VALUES(" + user_id + "," + competence_id + ")";
			try ( PreparedStatement s = c.prepareStatement( sql ) ) {
				// competence
				s.executeUpdate(); // On insert dans la table
				// user_favored_competence
			}

		}
		String[] s = { name };
		IndexTools.CV.index( s, user_id );
	}

	public static void deleteAllUserFavoredCompetence( int user_id ) throws NamingException, DbException, SQLException {
		String sql;
		List<String> comp = new ArrayList<>();
		try ( Connection c = Database.getConnection() ) {

			//  unindex
			sql = "SELECT name FROM user_favored_competence, competence WHERE user_id = " + user_id
					+ " AND id = competence_id";
			try ( PreparedStatement s = c.prepareStatement( sql ) ) {
				ResultSet rs = s.executeQuery();
				while ( rs.next() )
					comp.add( rs.getString( "name" ) );
			}

			if ( comp.size() != 0 )
				IndexTools.CV.unindex( comp.toArray( new String[0] ), user_id );

			sql = "DELETE FROM user_favored_competence WHERE user_id = " + user_id + ";";

			try ( PreparedStatement s = c.prepareStatement( sql ) ) {
				s.executeUpdate();
			}
		}
	}

	public static void addCompetence( int user_id, String name ) throws NamingException, DbException, SQLException {
		int id;
		try ( Connection c = Database.getConnection() ) {

			String sql = "SELECT id FROM competence WHERE name = ?";
			try ( PreparedStatement s = c.prepareStatement( sql ) ) {
				s.setString( 1, name );
				ResultSet res = s.executeQuery();
				if ( !res.next() ) { // Si La competence n'existe pas encore dans la base On le cree

					String sql2 = "INSERT INTO competence VALUES(null, ? )";
					try ( PreparedStatement sss = c.prepareStatement( sql2 ) ) {
						sss.setString( 1, name );
						sss.executeUpdate();
						ResultSet ress = s.executeQuery();
						ress.next();
						id = ress.getInt( "id" );
					}
				} else
					id = res.getInt( "id" );
			}
			String sql3 = "INSERT INTO user_competence VALUES(" + user_id + ", " + id + ")";
			try ( PreparedStatement s = c.prepareStatement( sql3 ) ) {
				s.executeUpdate(); // On relie la competence a l'utilisateur
			}
		}

		String[] s = { name };
		IndexTools.CV.index( s, user_id );
	}

	public static void deleteAllUserCompetence( int user_id ) throws NamingException, DbException, SQLException {
		String sql;
		List<String> comp = new ArrayList<>();
		try ( Connection c = Database.getConnection() ) {

			//  unindex
			sql = "SELECT name FROM user_competence, competence WHERE user_id = " + user_id + " AND id = competence_id";
			try ( PreparedStatement s = c.prepareStatement( sql ) ) {
				ResultSet rs = s.executeQuery();
				while ( rs.next() )
					comp.add( rs.getString( "name" ) );
			}

			if ( comp.size() != 0 )
				IndexTools.CV.unindex( comp.toArray( new String[0] ), user_id );

			// delete
			sql = "DELETE FROM user_competence WHERE user_id = " + user_id + ";";
			try ( PreparedStatement s = c.prepareStatement( sql ) ) {
				s.executeUpdate();
			}
		}
	}

	public static void addFormation( int user_id, String diploma, String school, String startDate, String endDate )
			throws NamingException, DbException, SQLException {
		String sql1 = "SELECT id FROM formation WHERE diploma = ? AND school = ?;";
		String sql2 = "INSERT INTO formation VALUES(null, ?, ?)";
		String sql3;
		int formation_id;
		try ( Connection c = Database.getConnection() ) {
			try ( PreparedStatement s = c.prepareStatement( sql1 ) ) {
				s.setString( 1, diploma );
				s.setString( 2, school );
				ResultSet res = s.executeQuery();
				if ( !res.next() ) { // Si La formation n'existe pas encore dans la base on la recree
					try ( PreparedStatement s2 = c.prepareStatement( sql2 ) ) {
						s2.setString( 1, diploma );
						s2.setString( 2, school );
						s2.executeUpdate();
						res = s.executeQuery();
						res.next();
					}
				}
				formation_id = res.getInt( "id" ); // On recupere l'id de la formation
			}
			sql3 = "INSERT INTO user_formation VALUES(" + user_id + ", " + formation_id + ", STR_TO_DATE('" + startDate
					+ "' ,'%d/%m/%Y'), STR_TO_DATE(' " + endDate + "' ,'%d/%m/%Y'));";
			try ( PreparedStatement s = c.prepareStatement( sql3 ) ) {
				s.executeUpdate(); // On relie la formation a l'utilisateur
			}
			if ( diploma != null )
				IndexTools.CV.index( diploma.split( "[\\s,.?!/]+" ), user_id );
		}
	}

	public static void addLanguage( int user_id, String name, int level ) throws NamingException, DbException {
		String sql1 = "SELECT id FROM language WHERE name = '" + name + "';";
		String sql2 = "INSERT INTO language VALUES(null, '" + name + "');";
		String sql3;
		try {
			Connection c = Database.getConnection();
			Statement s = c.createStatement();
			ResultSet res = s.executeQuery( sql1 );
			if ( !res.next() ) { // Si la langue n'existe pas encore dans la
				// base
				// On le cree et on relance la requete sql1
				s.executeUpdate( sql2 );
				res = s.executeQuery( sql1 );
				res.next();
			}
			int language_id = res.getInt( "id" ); // On recupere l'id de la
			// langue
			sql3 = "INSERT INTO can_speak VALUES(" + user_id + ", " + language_id + ", " + level + ");";
			s.executeUpdate( sql3 ); // On relie la langue a l'utilisateur
			s.close();
			c.close();
		} catch ( SQLException e ) {
			throw new DbException( e.getMessage() );
		}
	}

	public static JSONObject getCv( int user_id ) throws DbException, NamingException {
		JSONObject j;
		JSONArray experiences, competences, favored_competences, formation, language;
		String sql1 = "SELECT id, title, company, start_date, end_date, description " + "FROM experience "
				+ "WHERE user_id =" + user_id;

		String sql2 = "SELECT competence.id, competence.name " + "FROM competence, user_competence "
				+ "WHERE user_competence.user_id = " + user_id + " AND competence.id = user_competence.competence_id;";

		String sql3 = "SELECT competence.id, competence.name " + "FROM competence, user_favored_competence "
				+ "WHERE user_favored_competence.user_id = " + user_id
				+ " AND competence.id = user_favored_competence.competence_id;";

		String sql4 = "SELECT formation.id, formation.diploma, formation.school, user_formation.start_date, user_formation.end_date "
				+ "FROM formation, user_formation "
				+ "WHERE user_formation.user_id = "
				+ user_id
				+ " AND formation.id = user_formation.formation_id;";

		String sql5 = "SELECT language.id, language.name, can_speak.level " + "FROM can_speak, language "
				+ "WHERE can_speak.user_id = " + user_id + " AND language.id = can_speak.language_id;";

		try {
			Connection c = Database.getConnection();
			Statement s = c.createStatement();
			ResultSet res = s.executeQuery( sql1 );
			experiences = new JSONArray();
			//On ajoute les experiences
			while ( res.next() ) {
				JSONObject exp = new JSONObject();
				exp.put( "id", res.getInt( "id" ) );
				exp.put( "title", res.getString( "title" ) );
				exp.put( "company", res.getString( "company" ) );
				exp.put( "startDate", res.getString( "start_date" ) );
				exp.put( "endDate", res.getString( "end_date" ) );
				exp.put( "description", res.getString( "description" ) );
				experiences.put( exp );
			}

			res = s.executeQuery( sql2 );
			competences = new JSONArray();
			//On ajoute les competences
			while ( res.next() ) {
				//				JSONObject comp = new JSONObject();
				//				comp.put( "id", res.getInt( "competence.id" ) );
				//				comp.put( "name", res.getString( "competence.name" ) );
				competences.put( res.getString( "competence.name" ) );
			}

			res = s.executeQuery( sql3 );
			favored_competences = new JSONArray();
			//On ajoute les favored_competences
			while ( res.next() ) {
				//				JSONObject fav_comp = new JSONObject();
				//				fav_comp.put( "id", res.getInt( "competence.id" ) );
				//				fav_comp.put( "name", res.getString( "competence.name" ) );
				favored_competences.put( res.getString( "competence.name" ) );
			}

			res = s.executeQuery( sql4 );
			formation = new JSONArray();
			//On ajoute les formations
			while ( res.next() ) {
				JSONObject form = new JSONObject();
				form.put( "id", res.getInt( "formation.id" ) );
				form.put( "diploma", res.getString( "formation.diploma" ) );
				form.put( "school", res.getString( "formation.school" ) );
				form.put( "startDate", res.getString( "user_formation.start_date" ) );
				form.put( "endDate", res.getString( "user_formation.end_date" ) );
				formation.put( form );
			}

			res = s.executeQuery( sql5 );
			language = new JSONArray();
			//On ajoute les langues
			while ( res.next() ) {
				JSONObject lang = new JSONObject();
				lang.put( "id", res.getInt( "language.id" ) );
				lang.put( "name", res.getString( "language.name" ) );
				lang.put( "level", res.getInt( "can_speak.level" ) );
				language.put( lang );
			}

			j = new JSONObject();
			j.put( "experience", experiences );
			j.put( "competences", competences );
			j.put( "favored_competences", favored_competences );
			j.put( "formation", formation );
			j.put( "language", language );

			s.close();
			c.close();

			return j;
		} catch ( SQLException e ) {
			throw new DbException( e.getMessage() );
		} catch ( JSONException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JSON ) );
		}

	}

	public static JSONObject getAllCompetences( int user_id, String mot ) throws DbException, NamingException {
		String sql = "SELECT name FROM competence where name like '" + mot + "%'";
		JSONArray competences = new JSONArray();
		JSONObject j = new JSONObject();
		try {
			Connection c = Database.getConnection();
			Statement s = c.createStatement();
			ResultSet res = s.executeQuery( sql );
			while ( res.next() ) {
				competences.put( res.getString( "name" ) );
			}
			j.put( "competences", competences );
			s.close();
			c.close();
			return j;
		} catch ( SQLException e ) {
			throw new DbException( e.getMessage() );
		} catch ( JSONException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JSON ) );
		}
	}

	public static List<JSONObject> getPotentials( String offerId ) throws UnknownHostException, MongoException,
			SQLException, JSONException, NamingException {

		WordVO[] wordsTab = OfferTools.getOfferasArrayOfWords( offerId ).toArray( new WordVO[0] );
		List<JSONObject> l = new ArrayList<>();
		List<CvRSV> list = new ArrayList<>();

		//		double ref = getScore( offerId, wordsTab );
		try ( Connection cnx = Database.getConnection() ) {

			String sql = "SELECT id FROM user";
			try ( PreparedStatement s = cnx.prepareStatement( sql ) ) {
				ResultSet rs = s.executeQuery();

				while ( rs.next() ) {

					int id = rs.getInt( "id" );

					// Calcul du RSV de cette offre
					Double RSV = 0.0;
					for ( WordVO word : wordsTab ) {

						// Recuperation du tf
						sql = "SELECT tf FROM ctf WHERE word = ? AND cv= " + id;
						double tf;
						try ( PreparedStatement s2 = cnx.prepareStatement( sql ) ) {
							s2.setString( 1, word.getWord() );
							ResultSet rs2 = s2.executeQuery();

							// Si le mot n'appartient pas au cv au adnalyse le suivant
							if ( !rs2.next() )
								continue;
							tf = rs2.getDouble( "tf" );
						}

						// Recuperation du df
						sql = "SELECT df FROM cdf WHERE word = ?";
						double df;
						try ( PreparedStatement s3 = cnx.prepareStatement( sql ) ) {
							s3.setString( 1, word.getWord() );
							ResultSet rs3 = s3.executeQuery();
							rs3.next();
							df = rs3.getDouble( "df" );
						}

						// Calcul du RSV:
						RSV = RSV + ( tf * ( 1 / df ) * word.getWeight() );
						String str = RSV.toString();
					

					}
					sql = "SELECT first_name, last_name FROM user WHERE id = ?";
					try ( PreparedStatement s4 = cnx.prepareStatement( sql ) ) {
						s4.setInt( 1, id );

						ResultSet rs4 = s4.executeQuery();
						rs4.next();
						list.add( new CvRSV( id, rs4.getString( "first_name" ), rs4.getString( "last_name" ), RSV ) );
					}

				}
				// Trie
				Collections.sort( list, Collections.reverseOrder() );

				// Creation lsite finale
				int i = 0;
				CvRSV crsv;
				while ( i < list.size() && ( crsv = list.get( i ) ).getRsv() > 0 ) {
					JSONObject jo = new JSONObject();
					jo.put( "id", crsv.getId() );
					jo.put( "last_name", crsv.getLastName() );
					jo.put( "first_name", crsv.getFirstName() );
					double ref = list.get( 0 ).getRsv();
					Double score = crsv.getRsv() / ref * 100;
					DecimalFormat f = new DecimalFormat();
					f.setMaximumFractionDigits( 2 );

					jo.put( "score", f.format( score ) );
					l.add( jo );
					i++;
				}
			}
		}// close db
		Database.closeMongo();

		return l;
	}

	public static double getScore( String offer, WordVO[] wordsTab ) throws SQLException, NamingException {
		Double RSV = 0.0;
		try ( Connection cnx = Database.getConnection() ) {

			for ( WordVO word : wordsTab ) {
				System.out.println( RSV );

				// Recuperation du tf
				String sql = "SELECT tf FROM otf WHERE word = \"" + word.getWord() + "\" AND offer=\"" + offer + "\"";
				Statement s = cnx.createStatement();
				ResultSet rs = s.executeQuery( sql );
				// Si le mot n'appartient pas a l'offre au adnalyse le suivant
				if ( !rs.next() )
					continue;
				double tf = rs.getDouble( "tf" );
				s.close();

				// Recuperation du df
				sql = "SELECT df FROM odf WHERE word = \"" + word.getWord() + "\"";
				s = cnx.createStatement();
				rs = s.executeQuery( sql );

				rs.next();
				double df = rs.getDouble( "df" );
				s.close();

				// Calcul du RSV:
				RSV = RSV + ( tf * ( 1 / df ) * word.getWeight() );
				String str = RSV.toString();

			}
		}
		return RSV;

	}

	public static WordVO[] getCVasArrayOfWords( int user_id ) throws SQLException, NamingException {
		ArrayList<WordVO> result = new ArrayList<>();
		Connection c = Database.getConnection();
		Statement s = c.createStatement();

		// Experience
		String sql = "SELECT title, description " + "FROM experience " + "WHERE user_id =" + user_id;
		ResultSet res = s.executeQuery( sql );

		while ( res.next() ) {
			String[] t = res.getString( "title" ).split( "[\\s,.?!/]+" );
			for ( String str : t )
				result.add( new WordVO( str, 1 ) );
		}

		// Competence
		sql = "SELECT  name " + "FROM competence, user_competence " + "WHERE user_competence.user_id = " + user_id
				+ " AND competence.id = user_competence.competence_id";

		res = s.executeQuery( sql );
		while ( res.next() ) {
			result.add( new WordVO( res.getString( "name" ), 5 ) );
		}

		// Favored competences
		sql = "SELECT  name " + "FROM competence, user_favored_competence "
				+ "WHERE user_favored_competence.user_id = " + user_id
				+ " AND competence.id = user_favored_competence.competence_id";
		res = s.executeQuery( sql );
		while ( res.next() ) {
			result.add( new WordVO( res.getString( "name" ), 10 ) );
		}

		// Formation
		sql = "SELECT  formation.diploma FROM formation, user_formation " + "WHERE user_formation.user_id = " + user_id
				+ " AND formation.id = user_formation.formation_id;";
		res = s.executeQuery( sql );
		while ( res.next() ) {
			String[] t = res.getString( "diploma" ).split( "[\\s,.?!/]+" );
			for ( String str : t )
				result.add( new WordVO( str, 1 ) );

		}

		// sql = "SELECT  language.name, can_speak.level " +
		// "FROM can_speak, language "
		// + "WHERE can_speak.user_id = " + user_id +
		// " AND language.id = can_speak.language_id";
		// res = s.executeQuery( sql );
		// while ( res.next() ) {
		// result.add( res.getString( "name" ) );
		// }

		return result.toArray( new WordVO[0] );

	}

}
