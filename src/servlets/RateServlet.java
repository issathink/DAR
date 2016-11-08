package servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import services.CommentRateService;
import tools.LatLng;
import tools.Tools;

public class RateServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		@SuppressWarnings("unchecked")
		Map<String, String> params = req.getParameterMap();
		String sessionId, note;
		String adresse;
		double lat, lng;
		
		if (params.containsKey("session_id") && params.containsKey("adresse") && params.containsKey("note")) {
			sessionId = req.getParameter("session_id");
			adresse = req.getParameter("adresse");
			LatLng latLng = Tools.getLatLng(adresse);
			if(latLng != null) {
				lat = latLng.lat;
				lng = latLng.lng;
				note = req.getParameter("note");
				
				resp.getWriter().write(CommentRateService.commentRate(sessionId, adresse, lat, lng, note, false));
			} else {
				resp.getWriter().write(Tools.erreur);
			}
			
		} else {
			resp.getWriter().write(Tools.erreurParam);
		}
	}
	
}
