package servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import services.ChangePwService;
import services.GetInfosService;
import tools.Tools;

public class GetInfosServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		@SuppressWarnings("unchecked")
		Map<String, String> params = req.getParameterMap();
		String session_id;
		
		if (params.containsKey("session_id")) {
			session_id = req.getParameter("session_id");
			resp.getWriter().write(GetInfosService.getInfos(session_id));
		} else {
			resp.getWriter().write(Tools.erreurParam);
		}
	}

}
