package IOTDevices.BL;

import IOTDevices.ThreadPool.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.json.JSONObject;
import java.util.concurrent.*;



/**
 * Servlet implementation class gate
 */
@WebServlet(name = "gate", value = { "/gate" })
public class gate extends HttpServlet {
	private static final long serialVersionUID = 1L;
	CommandFactory commandFactory  = CommandFactory.getFactory();
	ThreadPool<Integer> threadPool = new ThreadPool<Integer>(4);
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public gate() {
        super();
		 commandFactory.addCommand("cr", GenericIOTUtils::companyRegisterBD);
		 commandFactory.addCommand("pr", GenericIOTUtils::productRegisterBD);
         commandFactory.addCommand("iotr", GenericIOTUtils::IOTRegister);
         commandFactory.addCommand("iotu", GenericIOTUtils::IOTupdate);
         commandFactory.addCommand("getiot", GenericIOTUtils::getIOT);
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("im in get");
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("im in post");
		String jsonData = (String) request.getAttribute("jsonData");
		JSONObject jsonObject = new JSONObject(jsonData);
		String command = jsonObject.getString("command");
		System.out.println(jsonObject);
		Object newJson = sendToThreadPool(command, jsonObject, response);
		response.getWriter().println((JSONObject)newJson);
	}
	
	private Object sendToThreadPool(String command, JSONObject jsonObject, HttpServletResponse response) {
		Callable callableToPool = new Callable() {
	        @Override
	        public Object call() throws Exception {
	            return commandFactory.execute(command, jsonObject);
	        };
	    };

		Future future = threadPool.submit(callableToPool);
		Object newJson = null;

		try {
			newJson = future.get();
		} catch (InterruptedException | ExecutionException e) {
			System.out.println("catch future exception");
			e.printStackTrace();
		}
		return newJson;
	}
	
	
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		doPost(request, response);
	}
	

}
