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

public class CommentServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		@SuppressWarnings("unchecked")
		Map<String, String> params = req.getParameterMap();
		String sessionId, comment;
		String adresse;
		double lat, lng;
		
		if (params.containsKey("session_id") && params.containsKey("adresse") && params.containsKey("comment")) {
			sessionId = req.getParameter("session_id");
			adresse = req.getParameter("adresse");
			LatLng latLng = Tools.getLatLng(adresse);
			lat = latLng.lat;
			lng = latLng.lng;
			comment = req.getParameter("comment");
			
			resp.getWriter().write(CommentRateService.commentRate(sessionId, adresse, lat, lng, comment, true));
			//resp.getWriter().write(CommentRateByZonesService.commentRate(sessionId, adresse, lat, lng, comment, true));
			
		} else {
			resp.getWriter().write(Tools.erreurParam);
		}
	}
	
}
