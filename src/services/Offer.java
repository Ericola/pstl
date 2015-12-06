package services;

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.NamingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.ErrorCodes;
import db.AuthentificationTools;
import db.CvTools;
import db.DbException;
import db.OfferTools;
import db.UserTools;

import com.mongodb.MongoException;

public class Offer {

	public static JSONObject createOfferInfo( String token, String title, String location, String start_date,
			String mission_duration, String business, String offerId ) {
		try {
			// Verification de la validite de la cle
			if ( AuthentificationTools.checkSession( token ) ) {
				int user_id = AuthentificationTools.getUserId( token );
				JSONObject o = OfferTools.createOfferInfo( user_id, title, location, start_date, mission_duration,
						business, offerId );
				if ( o == null )
					return ServicesTools.ok();
				return o;
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

	public static JSONObject createOfferContact( String token, String contact, String offerId ) {
		try {
			// Verification de la validite de la cle
			if ( AuthentificationTools.checkSession( token ) ) {
				int user_id = AuthentificationTools.getUserId( token );
				JSONObject o = OfferTools.createOfferContact( user_id, contact, offerId );
				if ( o == null )
					return ServicesTools.ok();
				return o;
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
		}
	}

	public static JSONObject createOfferDesc( String token, String desc, String offerId ) {
		try {
			// Verification de la validite de la cle
			if ( AuthentificationTools.checkSession( token ) ) {
				int user_id = AuthentificationTools.getUserId( token );
				JSONObject o = OfferTools.createOfferDesc( user_id, desc, offerId );
				if ( o == null )
					return ServicesTools.ok();
				return o;
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

	public static JSONObject createOfferComp( String token, String[] comp, String offerId ) {
		try {
			// Verification de la validite de la cle
			if ( AuthentificationTools.checkSession( token ) ) {
				int user_id = AuthentificationTools.getUserId( token );

				if ( comp == null )
					comp = new String[0];
				int[] req_comp = OfferTools.addCompetences( user_id, comp );
				JSONObject o = OfferTools.createOfferComp( user_id, comp, offerId );
				if ( o == null )
					return ServicesTools.ok();
				return o;
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

	public static JSONObject getSpecOffer( String id ) {
		try {
			// Verification de la validite de la cle
			return OfferTools.getSpecOffer( id );

		} catch ( MongoException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( UnknownHostException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( JSONException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JSON ) );
		}
	}

	public static Object getAllUserOffer( String token ) {
		try {
			// Verification de la validite de la cle
			if ( AuthentificationTools.checkSession( token ) ) {
				int user_id = AuthentificationTools.getUserId( token );
				return new JSONArray( OfferTools.getAllUserOffer( user_id ) );
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

	public static Object getAllOffer() {
		try {
			return new JSONArray( OfferTools.getAllOffer() );
		} catch ( MongoException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( UnknownHostException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( JSONException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.JSON ) );
		}
	}

	public static JSONObject deleteOffer( String token, String id ) {
		try {
			// Verification de la validite de la cle
			if ( AuthentificationTools.checkSession( token ) ) {
				OfferTools.deleteOffer( id );
				return ServicesTools.ok();
			} else {
				return ServicesTools.error( "La session a expire", ErrorCodes.SESSION_EXPIRED );
			}
		} catch ( DbException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( UnknownHostException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( MongoException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch ( NamingException e ) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		} catch (SQLException e) {
			return ( ServicesTools.error( e.getMessage(), ErrorCodes.SQL ) );
		}
	}

	public static Object getPotentialOffers( String token ) {
		try {
			// Verification de la validite de la cle
			if ( AuthentificationTools.checkSession( token ) ) {
				int user_id = AuthentificationTools.getUserId( token );
				return new JSONArray( OfferTools.getPotentials( user_id ) );
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

	public static Object search(String[] param, String business, String location){
		try {
			return new JSONArray( OfferTools.search(param, business, location));
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
}
