package uk.ac.kmi.microwsmo.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.ServiceException;

import uk.ac.kmi.microwsmo.server.DomainOntologiesRetriever;
import uk.ac.kmi.microwsmo.server.ServicePropertiesRetriever;
import uk.ac.kmi.microwsmo.server.model.SemanticTreesModel;

public class Updater extends HttpServlet {

	private static final long serialVersionUID = -3398593514965014640L;

	/**
	 * Redirects the user to an error page. It means the user is not allowed to
	 * calls directly the page by a GET method.
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		createSession(request);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String method = request.getParameter("method");
		HttpSession session = request.getSession(true);
		String result = "";
		if( method.equals("getSp") ) {
			result = getServiceProperties(session);
		} else if( method.equals("getDo") ) {
			result = getDomainOntologies(session);
		}
		response.setContentType("text/plain");
		ServletOutputStream out = response.getOutputStream();
		out.print(result);
		out.close();
	}

	private void createSession(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		if( session.isNew() ) {
			try {
				ServicePropertiesRetriever spRetriever = new ServicePropertiesRetriever();
				session.setAttribute("spRetriever", spRetriever);
			} catch (ServiceException e) {
				e.printStackTrace();
			}
			try {
				DomainOntologiesRetriever doRetriever = new DomainOntologiesRetriever();
				session.setAttribute("doRetriever", doRetriever);
			} catch (ServiceException e) {
				e.printStackTrace();
			}
			session.setAttribute("treesModel", new SemanticTreesModel());
		}
	}

	private String getServiceProperties(HttpSession session) {
		SemanticTreesModel model = (SemanticTreesModel) session.getAttribute("treesModel");
		return model.getServiceProperties();
	}

	private String getDomainOntologies(HttpSession session) {
		SemanticTreesModel model = (SemanticTreesModel) session.getAttribute("treesModel");
		return model.getDomainOntologies();
	}

}
