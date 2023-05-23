package IOTDevices.DL;

import IOTDevices.ICrud;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Updates;
import org.json.JSONObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.Arrays;

import static com.mongodb.client.model.Filters.eq;

public class Mongo implements ICrud<JSONObject, JSONObject> {
    MongoClient connection = null;
    MongoDatabase database = null;

    public Mongo(MongoClient connection, MongoDatabase database) {
        this.connection = connection;
        this.database = database;
    }

    @Override
    public JSONObject create(JSONObject json) {
        MongoCollection<Document> collection = database.getCollection(json.getString("table"));
        cleanJson(json, null);
        Document doc = Document.parse(json.toString());
        collection.insertOne(doc);
        String[] Data = {};
        doc.append("updates", Arrays.asList(Data));
        return json;
    }

    @Override
    public JSONObject read(JSONObject json) {
        MongoCollection<Document> collection = database.getCollection(json.getString("table"));
        String mainField = json.getString("mainField");
        int id = json.getInt(mainField);
        FindIterable<Document> iter = collection.find(eq(mainField, id));
        Document document = iter.first();
        String newJson = document.toJson();
        return new JSONObject(newJson);
    }

    @Override
    public JSONObject update(JSONObject json) {
        MongoCollection<Document> collection = database.getCollection(json.getString("table"));
        String mainField = json.getString("mainField");
        int id = json.getInt(mainField);
        cleanJson(json, mainField);
        collection.updateOne(
                eq(mainField, id),
                Updates.push("updates", json.toString())
        );
        return json;
    }


    @Override
    public void delete(JSONObject json) {

    }

    private void cleanJson(JSONObject json, String mainField){
        json.remove("table");
        json.remove("command");
        json.remove(mainField);
        json.remove("mainField");
    }
}
