package db;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.NamingException;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import utils.Database;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import com.mongodb.MongoException;

public class MapReduceTools {

	private static final String	COLLECTION			= "posts";
	private static final String	COLLECTION_COMMENTS	= "comments";

	/** Attributs **/
	private static final String	_ID					= "_id";
	private static final String	AUTHOR_ID			= "author_id";
	private static final String	LOGIN				= "login";
	private static final String	CONTENT				= "content";
	private static final String	DATE				= "date";

	
	private static List<JSONObject> getDF() throws UnknownHostException, MongoException, JSONException {
		DBCollection col = Database.getMongoDb().getCollection( COLLECTION );
		String map =	"function () {" +
							"var cont = this.content;" +
							"cont = cont.toLowerCase();"+
							"var myArray = cont.split(/[^\\d\\w]+/);"	+
							"uniqueArray = myArray.filter(function(elem, pos) {" + 
								"return myArray.indexOf(elem) == pos;" +
							"});"+ 
							"for(var i in uniqueArray){" +
								"if(uniqueArray[i] != \"\")"+
									"emit(uniqueArray[i], 1);"+
							"}" +
						"}";

		String reduce = "function (key, values) { " + 
							" total = 0; " + 
							" for (var i in values) { "+ 
								" total += values[i]; " +
							" } " + 
							" return total }";

		MapReduceCommand cmd = new MapReduceCommand( col, map, reduce, null, MapReduceCommand.OutputType.INLINE, null );

		MapReduceOutput out = col.mapReduce( cmd );

		List<JSONObject> l = new ArrayList<>();
		for ( DBObject o : out.results() ) {
			JSONObject jo = new JSONObject();
			jo.put( "df", o.get( "value" ) );
			jo.put( "word", o.get( "_id" ) );
			l.add( jo );
		}
		Database.closeMongo();
		return l;
	}

	public static void fillTableDF() throws UnknownHostException, MongoException, JSONException, DbException, NamingException {
		List<JSONObject> l = getDF();

		try {
			Connection c = Database.getConnection();
			for ( int i = 0; i < l.size(); i++ ) {

				int df = l.get( i ).getInt( "df" );

				String request = "INSERT INTO DF VALUES ('" + l.get( i ).getString( "word" ) + "'," + df + ")";

				Statement s = c.createStatement();
				s.executeUpdate( request );
				s.close();
			}
			c.close();
		} catch ( SQLException e ) {
			e.printStackTrace();
			throw new DbException( "Erreur : insertion dans DF : " + e.getMessage() );
		}
	}
	
	private static List<JSONObject> getTF() throws UnknownHostException, MongoException, JSONException {
		DBCollection col = Database.getMongoDb().getCollection( COLLECTION );
		String map =	"function () {" +
							"var cont = this.content; " +
							"cont = cont.toLowerCase();"+
							"var myArray = cont.split(/[^\\d\\w]+/);"+
							"for(var i in myArray){" +
								"if(myArray[i] != \"\")" +
									"emit({postid:this._id,word:myArray[i]}, 1);}" +
						"}";

		String reduce = "function (key, values) { " + 
							" total = 0; " + 
							" for (var i in values) { "+ 
								" total += values[i]; " +
							" } " + 
							" return total }";

		MapReduceCommand cmd = new MapReduceCommand( col, map, reduce, null, MapReduceCommand.OutputType.INLINE, null );

		MapReduceOutput out = col.mapReduce( cmd );

		List<JSONObject> l = new ArrayList<>();
		for ( DBObject o : out.results() ) {
			JSONObject jo = new JSONObject();
			jo.put( "tf", o.get( "value" ) );
			jo.put( "key", o.get( "_id" ) );
			l.add( jo );
		}
		Database.closeMongo();
		return l;
	}
	
	public static void fillTableTF() throws UnknownHostException, MongoException, JSONException, DbException, NamingException {
		List<JSONObject> l = getTF();

		try {
			
			Connection c = Database.getConnection();
			
			for ( int i = 0; i < l.size(); i++ ) {

				int tf = l.get( i ).getInt( "tf" );
				
				BasicDBObject key = (BasicDBObject)l.get( i ).get( "key" );
				String word = key.getString("word");
				
				ObjectId postid = (ObjectId)key.get("postid");
				String pid = postid.toString();
				
				String request = "INSERT INTO TF VALUES ('" + word +"','"+ pid + "'," + tf + ")";

				Statement s = c.createStatement();
				s.executeUpdate( request );
				s.close();
			}
			c.close();
		} catch ( SQLException e ) {
			e.printStackTrace();
			throw new DbException( "Erreur : insertion dans TF : " + e.getMessage() );
		}
	}

	

	public static List<JSONObject> search( String text ) throws UnknownHostException, MongoException, SQLException,
			JSONException, NamingException {

		String[] wordsTab = text.split("[\\s,.?!]+");
		List<ObjetRSV> list = new ArrayList<>();

		//Posts's Ids Retrieval:
		DBCollection col = Database.getMongoDb().getCollection( COLLECTION );
		DBCursor c =col.find();


		Connection cnx = Database.getConnection();

		while(c.hasNext()){

			DBObject dbo = c.next();
			String post = ( (ObjectId) dbo.get("_id") ).toString();


			//Calcul du RSV de ce post 
			Double RSV = 0.0;
			for( String word : wordsTab){

				//Recuperation du tf
				String sql = "SELECT tf FROM TF WHERE word = \"" + word + "\" AND post=\""+ post +"\"";
				Statement s = cnx.createStatement();
				ResultSet rs = s.executeQuery( sql );
				//Si le mot n'appartient pas au post au analyse le suivant
				if(!rs.next())
					continue;
				double tf =rs.getDouble("tf");
				s.close();
				
				//Recuperation du df
				sql = "SELECT df FROM DF WHERE word = \"" + word+"\"";
				s = cnx.createStatement();
				rs = s.executeQuery( sql );
				
				rs.next();
				double df =rs.getDouble("df");
				s.close();
				
				//Calcul du RSV:
				RSV = RSV + tf *(1/df);
				String str= RSV.toString();

			}
			
			list.add(new ObjetRSV(dbo,RSV));
			
		}
		//Trie
		Collections.sort(list,Collections.reverseOrder());
		
		//Creation lsite finale
		List<JSONObject> l = new ArrayList<>();
		int i = 0;
		ObjetRSV orsv;
		while( i<list.size() && (orsv = list.get(i)).getRsv() > 0  ){
			JSONObject jo = new JSONObject();
			jo.put( _ID, orsv.getDbo().get( _ID ) );
			jo.put( AUTHOR_ID, orsv.getDbo().get( AUTHOR_ID ) );
			jo.put( LOGIN, orsv.getDbo().get( LOGIN ) );
			jo.put( CONTENT, orsv.getDbo().get( CONTENT ) );
			jo.put( DATE, orsv.getDbo().get( DATE ) );
			l.add( jo );
			i++;
		}

		c.close();
		Database.closeMongo();

		return l;
	}

}
