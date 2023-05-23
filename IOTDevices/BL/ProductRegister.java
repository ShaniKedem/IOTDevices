package IOTDevices.BL;

import IOTDevices.BL.GenericIOTUtils;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONObject;

/**
 * Servlet implementation class ProductRegister
 */
@WebServlet(name = "ProductRegister", value = { "/ProductRegister" })
public class ProductRegister extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProductRegister() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json = GenericIOTUtils.CreateJsonObj(request);
		
		//write to small sql not working
		GenericIOTUtils.productRegister(json);
		
		request.setAttribute("jsonData", json.toString());
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/gate");
        dispatcher.forward(request, response);
    }
	
	public static String requestToString(HttpServletRequest request) throws IOException {
        InputStream inputStream = request.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
    
        return stringBuilder.toString();
               
    } 
	
	
	

}
