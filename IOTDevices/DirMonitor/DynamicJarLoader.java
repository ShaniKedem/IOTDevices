package IOTDevices.DirMonitor;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class DynamicJarLoader {
    //private final String interfaceName;
    private final String jarPath;

    public DynamicJarLoader(String jarPath) {
        //this.interfaceName = interfaceName;
        this.jarPath = jarPath;
    }

    public void load(String interfaceName) throws ClassNotFoundException, MalformedURLException {
        File jarFile = new File(jarPath);
        URLClassLoader classLoader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()});
        Class<?> obj = classLoader.loadClass(interfaceName);
//        Method[] methods = obj.getMethods();
//        for (Method method : methods) {
//            System.out.println(method);
//        }
    }
    
    public static void main(String[] args) {
    	
        
    }
}
