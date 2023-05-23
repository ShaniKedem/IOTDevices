package IOTDevices.BL;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.json.JSONObject;
import jakarta.servlet.RequestDispatcher;

/**
 * Servlet implementation class CompanyRegister
 */
@WebServlet(name = "companyRegister", value = { "/companyRegister" })
public class CompanyRegister extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CompanyRegister() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json = GenericIOTUtils.CreateJsonObj(request);
		
		//write to small sql
		GenericIOTUtils.companyRegister(json);
		
		request.setAttribute("jsonData", json.toString());
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/gate");
        dispatcher.forward(request, response);
	  
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doPost(request, response);   
    }
	
	  
}

 