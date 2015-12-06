package services;

import org.json.JSONException;
import org.json.JSONObject;

public class ServicesTools {

	public static JSONObject ok() {

		try {
			JSONObject o = new JSONObject();
			o.put( "ok", "ok" );
			return o;
		} catch ( JSONException e ) {
		}

		return null;

	}

	public static JSONObject error( String message, int codeerreur ) {
		try {
			JSONObject json = new JSONObject();
			json.put( "error", message );
			json.put( "error_code", codeerreur );
			return json;
		} catch ( JSONException e ) {
		}
		return null;
	}

}
