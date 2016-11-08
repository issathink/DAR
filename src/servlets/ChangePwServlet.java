package servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import services.ChangePwService;
import tools.Tools;

public class ChangePwServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		@SuppressWarnings("unchecked")
		Map<String, String> params = req.getParameterMap();
		String session_id, prec_pw, new_pw;
		
		if (params.containsKey("prec_pw") && params.containsKey("new_pw") && params.containsKey("session_id")) {
			prec_pw = req.getParameter("prec_pw");
			new_pw = req.getParameter("new_pw");
			session_id = req.getParameter("session_id");
			resp.getWriter().write(ChangePwService.changePw(session_id, prec_pw, new_pw));
		} else {
			resp.getWriter().write(Tools.erreurParam);
		}
	}
	
}
