package services;

import java.net.UnknownHostException;

import javax.naming.NamingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.ErrorCodes;

import com.mongodb.MongoException;

import db.AuthentificationTools;
import db.DbException;
import db.UserTools;

public class User {

	public static JSONObject createUser( String username, String email, String lastName, String firstName,
			String password, Integer type ) {

		if ( ( username == null ) || ( password == null ) || ( email == null ) || ( type == null )
				|| ( lastName == null ) || ( firstName == null ) )
			return ( ServicesTools.error( "Wrong Arguments", ErrorCodes.ARGUMENT ) );

		try {
			// On vérifie que l'utilisateur existe sinon ERROR 1
			if ( AuthentificationTools.userExists( username ) )
				return ( ServicesTools.error( "User already exists", ErrorCodes.USER_EXISTS ) );

			//Insertion de l'utilisateur dans la base de données
			UserTools.createUser( username, email, lastName, firstName, password, type );

			return ServicesTools.ok();

		} catch ( DbException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( NamingException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JAVA ) );

		}
	}

	public static JSONObject login( String username, String password ) {
		if ( ( username == null ) || ( password == null ) )
			return ( ServicesTools.error( "Wrong Arguments", ErrorCodes.ARGUMENT ) );

		try {
			//Verifie que l’utilisateur existe sinon ERROR 1
			boolean is_user = AuthentificationTools.userExists( username );
			if ( !is_user )
				return ( ServicesTools.error( "Unknown user " + username, ErrorCodes.UNKNOW_USER ) );

			//Verifie que le password et l’utilisateur sont OK sinon ERROR 2
			boolean password_ok = AuthentificationTools.checkPassword( username, password );
			if ( !password_ok )
				return ( ServicesTools.error( "Bad password " + username, ErrorCodes.BAD_PASSWORD ) );

			//Récupère l’id de l’utilisateur
			int id_user = AuthentificationTools.getIdFromUsername( username );
			JSONObject userInfos = UserTools.getUserInfos( id_user );

			//Insère une nouvelle session dans la base de données
			String token = AuthentificationTools.insertSession( id_user );
			JSONObject json = new JSONObject();
			json.put( "token", token );
			json.put( "id", id_user );
			json.put( "username", username );
			json.put( "type", userInfos.get( "type" ) );
			json.put( "last_name", userInfos.get( "last_name" ) );
			json.put( "first_name", userInfos.get( "first_name" ) );

			return json;

		} catch ( JSONException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JSON ) );

		} catch ( DbException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );

		} catch ( Exception e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JAVA ) );
		}
	}

	public static JSONObject logout( String token ) {
		try {
			AuthentificationTools.deleteSession( token );
		} catch ( DbException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( NamingException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JAVA ) );

		}
		return ServicesTools.ok();

	}

	public static JSONObject getUserInfos( String token ) {
		// Vérification de la validité de la clé
		try {
			if ( AuthentificationTools.checkSession( token ) ) {
				int id = AuthentificationTools.getUserId( token );
				return UserTools.getUserInfos( id );
			} else {
				return ServicesTools.error( "La session a expiré", ErrorCodes.SESSION_EXPIRED );
			}
		} catch ( DbException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( JSONException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JSON ) );

		} catch ( NamingException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JAVA ) );

		}

	}

	public static Object SearchUser( String token, String text ) {
		JSONArray list;

		try {
			if ( AuthentificationTools.checkSession( token ) ) {

				list = new JSONArray( UserTools.searchUser( text ) );

			} else {
				return ServicesTools.error( "La session a expirÃ©", ErrorCodes.SESSION_EXPIRED );
			}

		} catch ( DbException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );

		} catch ( JSONException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JSON ) );

		} catch ( MongoException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );

		} catch ( UnknownHostException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( NamingException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JAVA ) );

		}

		return list;
	}

	public static JSONObject updateUser( String token, String column, String value, String password ) {
		// Vérification de la validité de la clé
		try {
			if ( AuthentificationTools.checkSession( token ) ) {
				int id = AuthentificationTools.getUserId( token );
				boolean password_ok = AuthentificationTools.checkPassword( UserTools.getUsername( id ), password );
				if ( !password_ok )
					return ( ServicesTools.error( "Bad password", ErrorCodes.BAD_PASSWORD ) );

				UserTools.updateUser( id, column, value );
				return ServicesTools.ok();
			} else {
				return ServicesTools.error( "La session a expiré", ErrorCodes.SESSION_EXPIRED );
			}
		} catch ( DbException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( NamingException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JAVA ) );

		}
	}

}
