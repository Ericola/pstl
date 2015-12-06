package db;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.NamingException;

import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import utils.Database;
import utils.WordVO;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

public class OfferTools {
	private static final String	COLLECTION			= "offer";

	/** Attributs Offer **/
	private static final String	_ID					= "_id";
	private static final String	USER_ID				= "user_id";
	private static final String	TITLE				= "title";
	private static final String	LOCATION			= "location";
	private static final String	PUBLISHED_DATE		= "published_date";
	private static final String	START_DATE			= "start_date";
	private static final String	MISSION_DURATION	= "mission_duration";
	private static final String	BUSINESS			= "business";
	private static final String	DESCRIPTION			= "description";
	private static final String	REQUIRED_COMPETENCE	= "required_competence";
	private static final String	CONTACT				= "contact";

	/**
	 * Ajoute la rubrique contact d'une offre
	 * 
	 * @param user_id
	 * @param phone
	 * @param email
	 * @param OfferId
	 * @return
	 * @throws UnknownHostException
	 * @throws MongoException
	 * @throws JSONException
	 */
	public static JSONObject createOfferContact( int user_id, String contact, String OfferId )
			throws UnknownHostException, MongoException, JSONException {
		DBCollection col = Database.getMongoDb().getCollection( COLLECTION );
		DBCursor c;
		if ( OfferId == null ) { // On cree l'offre, on ajoute les infos et on
			// renvoie l'id de l'offre
			BasicDBObject o = new BasicDBObject();
			o.put( USER_ID, user_id );
			o.put( CONTACT, contact );
			o.put( PUBLISHED_DATE, new Date() );
			col.insert( o );
			c = col.find( o );
			c.sort( new BasicDBObject( _ID, -1 ) );
			if ( c.hasNext() ) {
				DBObject o1 = c.next();
				Database.closeMongo();
				JSONObject j = new JSONObject();
				j.put( "id", o1.get( _ID ) );
				return j;
			}
		} else {
			// BasicDBObject o = new BasicDBObject().append("$set",
			// new BasicDBObject().append(CONTACT, contact));
			// col.update(new BasicDBObject().append(_ID, OfferId),
			// o);
			// c = col.find(new BasicDBObject(_ID, OfferId));
			BasicDBObject o = new BasicDBObject( "$set", new BasicDBObject( CONTACT, contact ) );
			col.update( new BasicDBObject( _ID, new ObjectId( OfferId ) ), o );
			Database.closeMongo();
			return null;
		}
		Database.closeMongo();
		return null;
	}

	/**
	 * Ajoute la 1ere rubrique d'une offre
	 * 
	 * @param user_id
	 * @param title
	 * @param location
	 * @param start_date
	 * @param mission_duration
	 * @param business
	 * @param offerId
	 * @return
	 * @throws UnknownHostException
	 * @throws MongoException
	 * @throws JSONException
	 * @throws NamingException
	 * @throws SQLException
	 */
	public static JSONObject createOfferInfo( int user_id, String title, String location, String start_date,
			String mission_duration, String business, String offerId ) throws UnknownHostException, MongoException,
			JSONException, SQLException, NamingException {
		DBCollection col = Database.getMongoDb().getCollection( COLLECTION );
		DBCursor c;
		if ( offerId == null ) { // On cree l'offre, on ajoute les infos et on
			// renvoie l'id de l'offre
			BasicDBObject o = new BasicDBObject();
			o.put( USER_ID, user_id );
			o.put( TITLE, title );
			o.put( LOCATION, location );
			o.put( START_DATE, start_date );
			o.put( PUBLISHED_DATE, new Date() );
			o.put( MISSION_DURATION, mission_duration );
			o.put( BUSINESS, business );
			col.insert( o );
			c = col.find( o );
			c.sort( new BasicDBObject( _ID, -1 ) );
			if ( c.hasNext() ) {
				DBObject o1 = c.next();
				Database.closeMongo();
				JSONObject j = new JSONObject();
				j.put( "id", o1.get( _ID ) );
				offerId = j.getString( "id" );

				IndexTools.Offer.index( title, offerId );
				return j;
			}
		} else {
			BasicDBObject oid = new BasicDBObject( _ID, new ObjectId( offerId ) );
			c = col.find( oid );
			String oldTitle = "";
			if ( c.hasNext() ) {
				BasicDBObject oeric = ( BasicDBObject ) c.next();
				oldTitle = oeric.getString( TITLE );
			}

			BasicDBObject o = new BasicDBObject( "$set", new BasicDBObject( TITLE, title ) );
			BasicDBObject o1 = new BasicDBObject( "$set", new BasicDBObject( LOCATION, location ) );
			BasicDBObject o2 = new BasicDBObject( "$set", new BasicDBObject( START_DATE, start_date ) );
			BasicDBObject o3 = new BasicDBObject( "$set", new BasicDBObject( MISSION_DURATION, mission_duration ) );
			BasicDBObject o4 = new BasicDBObject( "$set", new BasicDBObject( BUSINESS, business ) );

			col.update( new BasicDBObject( _ID, new ObjectId( offerId ) ), o );
			col.update( new BasicDBObject( _ID, new ObjectId( offerId ) ), o1 );
			col.update( new BasicDBObject( _ID, new ObjectId( offerId ) ), o2 );
			col.update( new BasicDBObject( _ID, new ObjectId( offerId ) ), o3 );
			col.update( new BasicDBObject( _ID, new ObjectId( offerId ) ), o4 );
			Database.closeMongo();

			// Indexation
			IndexTools.Offer.unindex( oldTitle, offerId );
			IndexTools.Offer.index( title, offerId );

		}

		return null;
	}

	/**
	 * Ajoute la rubrique description
	 * 
	 * @param user_id
	 * @param description
	 * @param offerId
	 * @return
	 * @throws UnknownHostException
	 * @throws MongoException
	 * @throws JSONException
	 * @throws NamingException
	 * @throws SQLException
	 */
	public static JSONObject createOfferDesc( int user_id, String description, String offerId )
			throws UnknownHostException, MongoException, JSONException, SQLException, NamingException {
		DBCollection col = Database.getMongoDb().getCollection( COLLECTION );
		DBCursor c;
		if ( offerId == null ) { // On cree l'offre, on ajoute les infos et on
			// renvoie l'id de l'offre
			BasicDBObject o = new BasicDBObject();
			o.put( USER_ID, user_id );
			o.put( DESCRIPTION, description );
			o.put( PUBLISHED_DATE, new Date() );
			col.insert( o );
			c = col.find( o );
			c.sort( new BasicDBObject( _ID, -1 ) );
			if ( c.hasNext() ) {
				DBObject o1 = c.next();
				Database.closeMongo();
				JSONObject j = new JSONObject();
				j.put( "id", o1.get( _ID ) );

				offerId = j.getString( "id" );

				IndexTools.Offer.index( description, offerId );
				return j;
			}
		} else {

			BasicDBObject oid = new BasicDBObject( _ID, new ObjectId( offerId ) );
			c = col.find( oid );
			String oldDescription = "";
			if ( c.hasNext() ) {
				BasicDBObject oeric = ( BasicDBObject ) c.next();
				oldDescription = oeric.getString( DESCRIPTION );

			}

			BasicDBObject o = new BasicDBObject( "$set", new BasicDBObject( DESCRIPTION, description ) );
			col.update( new BasicDBObject( _ID, new ObjectId( offerId ) ), o );
			Database.closeMongo();

			if ( oldDescription != null )
				IndexTools.Offer.unindex( oldDescription, offerId );
			IndexTools.Offer.index( description, offerId );
			return null;
		}
		Database.closeMongo();
		return null;
	}

	/**
	 * Ajoute les competences requis de l'offre
	 * 
	 * @param user_id
	 * @param comp
	 * @param offerId
	 * @return
	 * @throws UnknownHostException
	 * @throws MongoException
	 * @throws JSONException
	 * @throws NamingException
	 * @throws SQLException
	 */
	public static JSONObject createOfferComp( int user_id, String[] comp, String offerId ) throws UnknownHostException,
			MongoException, JSONException, SQLException, NamingException {
		DBCollection col = Database.getMongoDb().getCollection( COLLECTION );
		DBCursor c;
		if ( offerId == null ) { // On cree l'offre, on ajoute les infos et on
			// renvoie l'id de l'offre
			BasicDBObject o = new BasicDBObject();
			o.put( USER_ID, user_id );
			o.put( REQUIRED_COMPETENCE, comp );
			o.put( PUBLISHED_DATE, new Date() );
			col.insert( o );
			c = col.find( o );
			c.sort( new BasicDBObject( _ID, -1 ) );
			if ( c.hasNext() ) {
				DBObject o1 = c.next();
				Database.closeMongo();
				JSONObject j = new JSONObject();
				j.put( "id", o1.get( _ID ) );

				// INDEXATION
				offerId = j.getString( "id" );
				IndexTools.Offer.index( comp, offerId );

				return j;
			}
		} else {

			// save old value
			BasicDBObject oid = new BasicDBObject( _ID, new ObjectId( offerId ) );
			c = col.find( oid );
			String[] oldComp = null;
			if ( c.hasNext() ) {
				BasicDBList oeric = ( BasicDBList ) c.next().get( REQUIRED_COMPETENCE );
				if ( oeric != null )
					oldComp = oeric.toArray( new String[0] );

			}

			// UPDATE mongo
			BasicDBObject o = new BasicDBObject( "$set", new BasicDBObject( REQUIRED_COMPETENCE, comp ) );
			col.update( new BasicDBObject( _ID, new ObjectId( offerId ) ), o );
			Database.closeMongo();

			// INDEXATION
			if ( oldComp != null )
				IndexTools.Offer.unindex( oldComp, offerId );
			IndexTools.Offer.index( comp, offerId );
			return null;
		}
		Database.closeMongo();
		return null;
	}

	/**
	 * Creation d'une offre vide
	 * 
	 * @param user_id
	 * @return L'id de l'offre
	 * @throws UnknownHostException
	 * @throws MongoException
	 */
	public static Object createEmptyOffer( int user_id ) throws UnknownHostException, MongoException {
		DBCollection col = Database.getMongoDb().getCollection( COLLECTION );
		DBCursor c;
		DBObject dbo;
		BasicDBObject o = new BasicDBObject();
		o.put( USER_ID, user_id );
		o.put( PUBLISHED_DATE, new Date() );
		col.insert( o );
		c = col.find( o );
		if ( c.hasNext() ) {
			dbo = c.next();
			return dbo.get( _ID );
		}
		return null;
	}

	public static int[] addCompetences( int user_id, String[] required_competence ) throws DbException, NamingException {
		String sql, sql2;
		int res[] = new int[required_competence.length];

		try ( Connection c = Database.getConnection() ) {
			for ( int i = 0; i < required_competence.length; i++ ) {

				sql = "SELECT id FROM competence WHERE name = ? ";
				try ( PreparedStatement s = c.prepareStatement( sql ) ) {
					s.setString( 1, required_competence[i] );
					ResultSet resComp = s.executeQuery();

					if ( !resComp.next() ) { // Si La competence n'existe pas encore dans la base On le cree
						sql2 = "INSERT INTO competence VALUES(null, ? )";
						try ( PreparedStatement s2 = c.prepareStatement( sql2 ) ) {
							s2.setString( 1, required_competence[i] );
							s2.executeUpdate();
							resComp = s.executeQuery();
							resComp.next();
						}
					}
					res[i] = resComp.getInt( "id" );
				}

			}
			return res;
		} catch ( SQLException e ) {
			throw new DbException( e.getMessage() );
		}
	}

	public static JSONObject getSpecOffer( String offer_id ) throws JSONException, UnknownHostException, MongoException {
		DBCollection col = Database.getMongoDb().getCollection( COLLECTION );
		JSONObject jo = new JSONObject();
		// Conditions de recherche
		BasicDBObject o = new BasicDBObject( _ID, new ObjectId( offer_id ) );
		DBCursor c = col.find( o );

		if ( c.hasNext() ) {
			DBObject dbo = c.next();
			jo.put( USER_ID, dbo.get( USER_ID ) );
			jo.put( TITLE, dbo.get( TITLE ) );
			jo.put( LOCATION, dbo.get( LOCATION ) );
			jo.put( PUBLISHED_DATE, dbo.get( PUBLISHED_DATE ) );
			jo.put( START_DATE, dbo.get( START_DATE ) );
			jo.put( MISSION_DURATION, dbo.get( MISSION_DURATION ) );
			jo.put( BUSINESS, dbo.get( BUSINESS ) );
			jo.put( DESCRIPTION, dbo.get( DESCRIPTION ) );
			jo.put( REQUIRED_COMPETENCE, dbo.get( REQUIRED_COMPETENCE ) );
			jo.put( CONTACT, dbo.get( CONTACT ) );
		}

		c.close();
		Database.closeMongo();
		return jo;
	}

	public static List<JSONObject> getAllUserOffer( int user_id ) throws JSONException, UnknownHostException,
			MongoException, SQLException, NamingException {
		DBCollection col = Database.getMongoDb().getCollection( COLLECTION );

		// Conditions de recherche
		BasicDBObject o = new BasicDBObject( USER_ID, user_id );
		DBCursor c = col.find( o );
		// c.sort( new BasicDBObject( _ID, -1 ) ); // newer posts first
		ArrayList<JSONObject> l = new ArrayList<>();

		while ( c.hasNext() ) {
			DBObject dbo = c.next();
			JSONObject jo = new JSONObject();
			jo.put( "id", dbo.get( _ID ) );

			jo.put( TITLE, dbo.get( TITLE ) );
			jo.put( LOCATION, dbo.get( LOCATION ) );
			jo.put( PUBLISHED_DATE, dbo.get( PUBLISHED_DATE ) );
			String desc = "";
			if ( dbo.get( DESCRIPTION ) != null )
				if ( dbo.get( DESCRIPTION ).toString().length() > 250 )
					desc = dbo.get( DESCRIPTION ).toString().substring( 0, 250 ) + ". . .";
				else
					desc = dbo.get( DESCRIPTION ).toString();
			jo.put( DESCRIPTION, desc );

			BasicDBObject bdo = ( BasicDBObject ) dbo;
			jo.put( "nb_potential_users", CvTools.getPotentials( bdo.getString( _ID ) ).size() );

			l.add( jo );

		}
		c.close();
		Database.closeMongo();
		return l;
	}

	public static List<JSONObject> getAllOffer() throws JSONException, UnknownHostException, MongoException {
		DBCollection col = Database.getMongoDb().getCollection( COLLECTION );

		DBCursor c = col.find();
		c.sort( new BasicDBObject( _ID, -1 ) ); // newer posts first
		List<JSONObject> l = new ArrayList<>();
		while ( c.hasNext() ) {
			DBObject dbo = c.next();
			JSONObject jo = new JSONObject();
			jo.put( "id", dbo.get( _ID ) );
			jo.put( USER_ID, USER_ID );
			jo.put( TITLE, dbo.get( TITLE ) );
			jo.put( LOCATION, dbo.get( LOCATION ) );
			jo.put( PUBLISHED_DATE, dbo.get( PUBLISHED_DATE ) );
			String desc = "";

			if ( dbo.get( DESCRIPTION ) != null ) {
				if ( dbo.get( DESCRIPTION ).toString().length() > 250 )
					desc = dbo.get( DESCRIPTION ).toString().substring( 0, 250 ) + ". . .";
				else
					desc = dbo.get( DESCRIPTION ).toString();
			}
			jo.put( DESCRIPTION, desc );
			l.add( jo );
		}
		c.close();
		Database.closeMongo();
		return l;
	}

	public static void deleteOffer( String offerId ) throws UnknownHostException, MongoException, SQLException,
			NamingException {
		DBCollection col = Database.getMongoDb().getCollection( COLLECTION );
		BasicDBObject o = new BasicDBObject( _ID, new ObjectId( offerId ) );
		IndexTools.Offer.unindex( getOfferasArrayOfWords( offerId ).toArray( new String[0] ), offerId );
		col.remove( o );
		Database.closeMongo();
	}

	public static List<JSONObject> getPotentials( int user_id ) throws UnknownHostException, MongoException,
			SQLException, JSONException, NamingException {

		WordVO[] wordsTab = CvTools.getCVasArrayOfWords( user_id );
		//		double ref = getScore( user_id, wordsTab );

		List<ObjetRSV> list = new ArrayList<>();

		// CV Ids Retrieval:
		DBCollection col = Database.getMongoDb().getCollection( COLLECTION );
		DBCursor c = col.find();

		Connection cnx = Database.getConnection();

		while ( c.hasNext() ) {

			DBObject dbo = c.next();
			String offer = ( ( ObjectId ) dbo.get( "_id" ) ).toString();

			// Calcul du RSV de cette offre
			Double RSV = 0.0;
			for ( WordVO word : wordsTab ) {

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

			list.add( new ObjetRSV( dbo, RSV ) );

		}
		// Trie
		Collections.sort( list, Collections.reverseOrder() );

		// Creation lsite finale
		List<JSONObject> l = new ArrayList<>();
		int i = 0;
		ObjetRSV orsv;
		while ( i < list.size() && ( orsv = list.get( i ) ).getRsv() > 0 ) {
			JSONObject jo = new JSONObject();
			jo.put( "id", orsv.getDbo().get( _ID ) );

			jo.put( USER_ID, USER_ID );
			jo.put( TITLE, orsv.getDbo().get( TITLE ) );
			jo.put( LOCATION, orsv.getDbo().get( LOCATION ) );
			jo.put( PUBLISHED_DATE, orsv.getDbo().get( PUBLISHED_DATE ) );
			String desc = "";
			if ( orsv.getDbo().get( DESCRIPTION ) != null )
				if ( orsv.getDbo().get( DESCRIPTION ).toString().length() > 250 )
					desc = orsv.getDbo().get( DESCRIPTION ).toString().substring( 0, 250 ) + ". . .";
				else
					desc = orsv.getDbo().get( DESCRIPTION ).toString();

			jo.put( DESCRIPTION, desc );
			double ref = list.get( 0 ).getRsv();
			Double score = orsv.getRsv() / ref * 100;
			DecimalFormat f = new DecimalFormat();
			f.setMaximumFractionDigits( 2 );

			jo.put( "score", f.format( score ) );
			l.add( jo );
			i++;
		}

		c.close();
		Database.closeMongo();

		return l;
	}

	//	public static double getScore( int id, WordVO[] wordsTab ) throws SQLException, NamingException {
	//		Double RSV = 0.0;
	//		String sql;
	//		try ( Connection cnx = Database.getConnection() ) {
	//
	//			for ( WordVO word : wordsTab ) {
	//
	//				// Recuperation du tf
	//				sql = "SELECT tf FROM ctf WHERE word = ? AND cv= " + id;
	//				double tf;
	//				try ( PreparedStatement s2 = cnx.prepareStatement( sql ) ) {
	//					s2.setString( 1, word.getWord() );
	//					ResultSet rs2 = s2.executeQuery();
	//
	//					// Si le mot n'appartient pas au cv au adnalyse le suivant
	//					if ( !rs2.next() )
	//						continue;
	//					tf = rs2.getDouble( "tf" );
	//				}
	//
	//				// Recuperation du df
	//				sql = "SELECT df FROM cdf WHERE word = ?";
	//				double df;
	//				try ( PreparedStatement s3 = cnx.prepareStatement( sql ) ) {
	//					s3.setString( 1, word.getWord() );
	//					ResultSet rs3 = s3.executeQuery();
	//					rs3.next();
	//					df = rs3.getDouble( "df" );
	//				}
	//
	//				// Calcul du RSV:
	//				RSV = RSV + ( tf * ( 1 / df ) * word.getWeight() );
	//				String str = RSV.toString();
	//
	//			}
	//		}
	//		return RSV;
	//
	//	}

	/**
	 * Retourne le tableau de mots d'indexation de l'offre
	 * 
	 * @param offerId
	 * @return
	 * @throws UnknownHostException
	 * @throws MongoException
	 */
	public static ArrayList<WordVO> getOfferasArrayOfWords( String offerId ) throws UnknownHostException,
			MongoException {
		DBCollection col = Database.getMongoDb().getCollection( COLLECTION );
		BasicDBObject o = new BasicDBObject( _ID, new ObjectId( offerId ) );
		ArrayList<WordVO> tmp = new ArrayList<>();
		DBCursor c = col.find( o );
		if ( c.hasNext() ) {
			BasicDBObject o1 = ( BasicDBObject ) c.next();
			BasicDBList oeric = ( BasicDBList ) o1.get( REQUIRED_COMPETENCE );
			String temp = o1.getString( TITLE );

			if ( temp != null ) {
				String[] str = temp.split( "[\\s,.?!/]+" );
				for ( String s : str )
					tmp.add( new WordVO( s, 1 ) );
			}

			temp = o1.getString( DESCRIPTION );
			if ( temp != null ) {
				String[] str = temp.split( "[\\s,.?!/]+" );
				for ( String s : str )
					tmp.add( new WordVO( s, 1 ) );
			}

			if ( oeric != null ) {
				String[] str = oeric.toArray( new String[0] );
				for ( String s : str )
					tmp.add( new WordVO( s, 5 ) );
			}

		}
		c.close();
		Database.closeMongo();
		return tmp;
	}

	public static List<JSONObject> search(String[] param, String business, String location) throws SQLException, NamingException, UnknownHostException, MongoException, JSONException {
		
		// On initialise la liste de mots de la recherche
		ArrayList<String> wordsTab = new ArrayList<>();
		for(int i = 0; i < param.length; i++){
			wordsTab.add(param[i]);
		}
		wordsTab.add(business);
		wordsTab.add(location);

		List<ObjetRSV> list = new ArrayList<>();

		// CV Ids Retrieval:
		DBCollection col = Database.getMongoDb().getCollection( COLLECTION );
		DBCursor c = col.find();

		Connection cnx = Database.getConnection();

		while ( c.hasNext() ) {

			DBObject dbo = c.next();
			String offer = ( ( ObjectId ) dbo.get( "_id" ) ).toString();

			// Calcul du RSV de cette offre
			Double RSV = 0.0;
			for ( String word : wordsTab ) {

				// Recuperation du tf
				String sql = "SELECT tf FROM otf WHERE word = \"" + word + "\" AND offer=\"" + offer + "\"";
				Statement s = cnx.createStatement();
				ResultSet rs = s.executeQuery( sql );
				// Si le mot n'appartient pas a l'offre au adnalyse le suivant
				if ( !rs.next() )
					continue;
				double tf = rs.getDouble( "tf" );
				s.close();

				// Recuperation du df
				sql = "SELECT df FROM odf WHERE word = \"" + word + "\"";
				s = cnx.createStatement();
				rs = s.executeQuery( sql );

				rs.next();
				double df = rs.getDouble( "df" );
				s.close();

				// Calcul du RSV:
				RSV = RSV + tf * ( 1 / df );
				String str = RSV.toString();

			}

			list.add( new ObjetRSV( dbo, RSV ) );

		}
		// Trie
		Collections.sort( list, Collections.reverseOrder() );

		// Creation lsite finale
		List<JSONObject> l = new ArrayList<>();
		int i = 0;
		ObjetRSV orsv;
		while ( i < list.size() && ( orsv = list.get( i ) ).getRsv() > 0 ) {
			JSONObject jo = new JSONObject();
			jo.put( "id", orsv.getDbo().get( _ID ) );

			jo.put( USER_ID, USER_ID );
			jo.put( TITLE, orsv.getDbo().get( TITLE ) );
			jo.put( LOCATION, orsv.getDbo().get( LOCATION ) );
			jo.put( PUBLISHED_DATE, orsv.getDbo().get( PUBLISHED_DATE ) );
			String desc = "";
			if ( orsv.getDbo().get( DESCRIPTION ) != null )
				if ( orsv.getDbo().get( DESCRIPTION ).toString().length() > 250 )
					desc = orsv.getDbo().get( DESCRIPTION ).toString().substring( 0, 250 ) + ". . .";
				else
					desc = orsv.getDbo().get( DESCRIPTION ).toString();

			jo.put( DESCRIPTION, desc );
			double ref = list.get( 0 ).getRsv();
			Double score = orsv.getRsv() / ref * 100;
			DecimalFormat f = new DecimalFormat();
			f.setMaximumFractionDigits( 2 );

			jo.put( "score", f.format( score ) );
			l.add( jo );
			i++;
		}

		c.close();
		Database.closeMongo();

		return l;
	}

}
