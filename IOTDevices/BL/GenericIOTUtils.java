package IOTDevices.BL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.json.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import IOTDevices.DL.*;
public class GenericIOTUtils{

	private static SQL managementDB = new SQL("jdbc:mysql://localhost:3306/company_management", "shanikedem", "password");
	private static MongoClient connection = new MongoClient(new MongoClientURI("mongodb://localhost:27017/"));
	private static MongoDatabase database = connection.getDatabase("IOTInfo");
	private static Mongo iotDataBase = new Mongo(connection, database);



	public static JSONObject companyRegister(JSONObject json) {
		String query = "INSERT INTO companies_m (company_id_m, company_name_m) VALUES (?, ?)";
		Object[] params = {json.getInt("company_id_m"), json.getString("company_name_m")};
		return sendToSqlCrud(query, params, json);
	}
	
	public static JSONObject productRegister(JSONObject json) {
		String query = "INSERT INTO products_m (product_id_m, company_id_m) VALUES (?, ?)";
		Object[] params = {json.getInt("product_id_m"), json.getInt("company_id_m")};
		return sendToSqlCrud(query, params, json);
	}

	public static JSONObject companyRegisterBD(JSONObject json) {
		json.put("table", "companies");
		return iotDataBase.create(json);
	}

	public static JSONObject productRegisterBD(JSONObject json) {
		json.put("table", "products");
		return iotDataBase.create(json);
	}

	public static JSONObject IOTRegister(JSONObject json){
		json.put("table", "IOTs");
		return iotDataBase.create(json);
	}

	public static JSONObject IOTupdate(JSONObject json) {
		json.put("table", "IOTs");
		json.put("mainField", "sn");
		return iotDataBase.update(json);
	}

	public static JSONObject getIOT(JSONObject json){
		return iotDataBase.read(json);
	}

	public static JSONObject CreateJsonObj(HttpServletRequest request) throws IOException {
		InputStream inputStream = request.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder stringBuilder = new StringBuilder();
		String line;
		while((line = reader.readLine()) != null) {
			stringBuilder.append(line);
		}
		String jsonStr = stringBuilder.toString();
		JSONObject newjson = new JSONObject(jsonStr);
		return (new JSONObject(jsonStr));
	}

	private static JSONObject sendToSqlCrud(String query, Object[] params, JSONObject json){
		JSONObject newJson = new JSONObject();
		newJson.put("query", query);
		newJson.put("params", params);
		newJson = managementDB.create(newJson);
		json.put("new rows", newJson.getInt("new rows"));
		return json;
	}
	
}

