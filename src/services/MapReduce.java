package services;

import java.net.UnknownHostException;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.ErrorCodes;

import com.mongodb.MongoException;

import db.AuthentificationTools;
import db.DbException;
import db.MapReduceTools;

public class MapReduce {

	public static JSONObject createDFIndex() {
		try {

			MapReduceTools.fillTableDF();

		} catch ( DbException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( JSONException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JSON ) );
		} catch ( MongoException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( UnknownHostException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JAVA ) );
		} catch ( NamingException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JAVA ) );
		}
		return ServicesTools.ok();
	}

	public static JSONObject createTFIndex() {
		try {

			MapReduceTools.fillTableTF();

		} catch ( DbException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( JSONException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JSON ) );
		} catch ( MongoException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( UnknownHostException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JAVA ) );
		} catch ( NamingException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JAVA ) );
		}
		return ServicesTools.ok();
	}

	public static Object Search( String token, String text ) {
		JSONArray list;

		try {
			if ( AuthentificationTools.checkSession( token ) ) {

				list = new JSONArray( MapReduceTools.search( text ) );
			} else {
				return ServicesTools.error( "La session a expiré", ErrorCodes.SESSION_EXPIRED );
			}

		} catch ( DbException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );

		} catch ( MongoException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );

		} catch ( UnknownHostException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JAVA ) );
		} catch ( SQLException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( JSONException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JSON ) );
		} catch ( NamingException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JAVA ) );
		}

		return list;
	}

}
