package IOTDevices.DL;
import org.json.JSONObject;
import IOTDevices.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQL implements ICrud<JSONObject, JSONObject> {
    private static String connectionName = null;
    private static String userName = null;
    private static String password =null;
    private static Connection connection = null;


    public SQL(String connectionName, String userName, String password) {
        this.connectionName = connectionName;
        this.userName = userName;
        this.password = password;
        getConnection();
    }

    @Override
    public JSONObject create(JSONObject json) {
        int rows = 0;
        PreparedStatement statement = makeStatement(json.getString("query"),  json.get("params"));
        try {
            rows = statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        json.put("new rows", rows);
        return json;
    }

    @Override
    public JSONObject read(JSONObject jsonObject) {
        return null;
    }

    @Override
    public JSONObject update(JSONObject record) {
        return null;
    }

    @Override
    public void delete(JSONObject jsonObject) {

    }


    private static void getConnection() {
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            connection = DriverManager.getConnection(connectionName,userName,password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PreparedStatement makeStatement(String query, Object params){
        Object[] paramsArr = (Object[])params;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            for (int i = 0; i < paramsArr.length; i++) {
                statement.setObject(i + 1, paramsArr[i]);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return statement;
    }
}

