package com.finde.topoos.api;

import java.net.URLEncoder;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

public class Operations {

	private static final String API_DOMAIN = "https://api.topoos.com/";
	private static final String API_OP_REGISTERPOSITION = "1/positions/add.json";
	private static final String API_OP_REGISTERTRACK = "1/tracks/add.json";
	
	public static final int POSITION_TYPE_NORMAL = 3;
	public static final int POSITION_TYPE_TRACK_END = 2;

	public static Position RegisterPosition(double latitude, double longitude,
			int positionType, int trackId, String token) {
		Position pos = null;
		try {
			String OperationResult = "";

			String OpURI = API_DOMAIN
					+ API_OP_REGISTERPOSITION
					+ "?lat="
					+ URLEncoder.encode(Double.toString(latitude).replace(',',
							'.'))
					+ "&lng="
					+ URLEncoder.encode(Double.toString(longitude).replace(',',
							'.')) + "&oauth_token=" + token + "&postype="
					+ positionType + "&track=" + trackId;

			// HTTP Request
			HttpClient hc = new DefaultHttpClient();
			HttpPost post = new HttpPost(OpURI);

			HttpResponse rp = hc.execute(post);

			if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				OperationResult = EntityUtils.toString(rp.getEntity());
			}

			// Procesando el resultado
			JSONObject jObject = (JSONObject) new JSONTokener(OperationResult)
					.nextValue();

			// Extracting content
			Integer id = jObject.getInt("id");
			Integer track_id = jObject.getInt("track_id");

			pos = new Position(id, track_id);

		} catch (Exception ex) {
			// exception
		}

		return pos;
	}

	/**
	 * GetTrack obtiene un track asociado al token de un usuario de Topoos
	 * Devuelve un identificador del track.
	 * 
	 * @param token
	 * @return newTrackId
	 */
	public static int RegisterTrack(String token) {
		int track_id = -1;

		try {
			String OperationResult = "";

			String OpURI = API_DOMAIN + API_OP_REGISTERTRACK
					+ "?oauth_token=" + token;

			// HTTP Request
			HttpClient hc = new DefaultHttpClient();
			HttpPost post = new HttpPost(OpURI);

			HttpResponse rp = hc.execute(post);

			if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				OperationResult = EntityUtils.toString(rp.getEntity());
			}

			// Procesando el resultado
			JSONObject jObject = (JSONObject) new JSONTokener(OperationResult).nextValue();

			// Extracting content
			track_id = jObject.getInt("id");

			return track_id;

		} catch (Exception ex) {
			Log.e("Operations", ex.getMessage());
		}

		return -1;
	}

}
