package api;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TestApiMain {

	public static void main(String[] args) throws JSONException, Exception {

		double latitude = 48.8426979;
		double longitude = 2.3521379000000024;
		double distance = 500;

		JSONArray j = APIs.getJSON(latitude, longitude, distance);

		for(int i=0 ; i<j.length() ; i++) {
			JSONObject o = j.getJSONObject(i);
			System.out.println(o.toString());
		}

	}

}
