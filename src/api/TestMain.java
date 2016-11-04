package api;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TestMain {

	public static void main(String[] args) throws JSONException, Exception {

		double latitude = 48.8469029;
		double longitude = 2.3428312999999434;
		double distance = 500;
		
		JSONArray j = EtablissementScolaireAPI.getEtablissementScolaireJSON(latitude, longitude, distance);
		for(int i=0 ; i<j.length() ; i++) {
			JSONObject o = j.getJSONObject(i);
			System.out.println(o.toString());
		}
		
	}

}
