package IOTDevices.BL;

import IOTDevices.BL.GenericIOTUtils;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.json.JSONObject;


/**
 * Servlet implementation class ProductRegister
 */
@WebServlet(name = "IOTdevice", urlPatterns = { "/IOTdevice" })
public class IOTdevice extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public IOTdevice() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json = new JSONObject();
		json.put("table", "IOTs");
		json.put("mainField", "sn");
		json.put("command", "getiot");
		json.put("sn", request.getParameter("sn"));
		request.setAttribute("jsonData", json.toString());
		System.out.println(json);
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/gate");
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json = GenericIOTUtils.CreateJsonObj(request);
		request.setAttribute("jsonData", json.toString());
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/gate");
        dispatcher.forward(request, response);
    }
	
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json = GenericIOTUtils.CreateJsonObj(request);
		request.setAttribute("jsonData", json.toString());
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/gate");
        dispatcher.forward(request, response);
    }
	

}
