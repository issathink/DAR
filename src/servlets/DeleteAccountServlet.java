package servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import services.DeleteAccountService;
import tools.Tools;

public class DeleteAccountServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		@SuppressWarnings("unchecked")
		Map<String, String> params = req.getParameterMap();
		String session_id, pw;
		
		if (params.containsKey("pw") && params.containsKey("session_id")) {
			pw = req.getParameter("pw");
			session_id = req.getParameter("session_id");
			resp.getWriter().write(DeleteAccountService.deleteAccount(session_id, pw));
		} else {
			resp.getWriter().write(Tools.erreurParam);
		}
	}

}
