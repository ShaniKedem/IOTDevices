package IOTDevices.BL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.json.JSONObject;

public class CommandFactory{
	
	private final static CommandFactory INSTANCE = new CommandFactory();
	
    private final Map<String, Function<JSONObject, JSONObject>> commandMap = new HashMap<>();
    
    private CommandFactory() {
    }

    public void addCommand(String key, Function<JSONObject, JSONObject> command)
    {
    	commandMap.put(key, command);
    }

    public JSONObject execute(String key, JSONObject params)
    {
        return commandMap.get(key).apply(params);
    }
    
    public static CommandFactory getFactory() {
    	return INSTANCE; 
    }
}


//public class CommandFactory{
//	
//	private final static CommandFactory INSTANCE = new CommandFactory();
//	
//    private final Map<String, Function<JSONObject, Integer>> commandMap = new HashMap<>();
//    
//    private CommandFactory() {
//    }
//
//    public void addCommand(String key, Function<JSONObject, Integer> command)
//    {
//    	commandMap.put(key, command);
//    }
//
//    public int execute(String key, JSONObject params)
//    {
//        return commandMap.get(key).apply(params);
//    }
//    
//    public static CommandFactory getFactory() {
//    	return INSTANCE; 
//    }
//}
