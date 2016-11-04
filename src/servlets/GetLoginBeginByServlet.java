package servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;

import services.GetLoginBeginByService;

public class GetLoginBeginByServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		@SuppressWarnings("unchecked")
		Map<String, String> params = req.getParameterMap();

		if(params.containsKey("begin_by")) {
			String beginBy = req.getParameter("begin_by");
			beginBy = StringEscapeUtils.escapeHtml4(beginBy);
			String response = GetLoginBeginByService.getLoginList(beginBy);
			resp.getWriter().write(response);
		}
		else { // Gerer cas erreur
			resp.getWriter().write("{ \"Erreur\" : \"Argument 'begin_by' n'existe pas\" }");
		}
	}

}
