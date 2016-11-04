package api;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TestMain {

	public static void main(String[] args) throws JSONException, Exception {

		double latitude = 48.8469029;
		double longitude = 2.3428312999999434;
		double distance = 5000;
		
		//JSONArray j = EtablissementScolairePreBacAPI.getEtablissementScolaireJSON(latitude, longitude, distance);
		
		JSONArray j2 = EtablissementScolairePostBacAPI.getEtablissementScolaireJSON(latitude, longitude, distance);
		
//		for(int i=0 ; i<j.length() ; i++) {
//			JSONObject o = j.getJSONObject(i);
//			System.out.println(o.toString());
//		}
		System.out.println("-------------------------------");
		
		for(int i=0 ; i<j2.length() ; i++) {
			JSONObject o = j2.getJSONObject(i);
			System.out.println(o.toString());
		}
		
	}

}
