package Network.Client;

import javax.swing.text.View;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class JavaView {
    private class ViewStructs {
        Object viewObject;
        Method viewMethod;
        public ViewStructs(Object viewObject, Method viewMethod) {
            this.viewObject = viewObject;
            this.viewMethod = viewMethod;
        }
    }

    private HashMap<String, ViewStructs> rpcMethodDictionary;

    public JavaView (){
        this.rpcMethodDictionary = new HashMap<>();
    }

    protected void addRpc(Object object, String methodName) {
        Class rpcClass = object.getClass();
        String header = rpcClass.getName() + "_" + methodName;
        try {
            Method rpcMethod = rpcClass.getDeclaredMethod(methodName, Object.class);
            ViewStructs viewStructs = new ViewStructs(object, rpcMethod);
            if(rpcMethodDictionary.get(header) != null){
                System.out.println("Rpc method has already in list");
                return;
            }
            rpcMethodDictionary.put(header, viewStructs);
        } catch (NoSuchMethodException e) {
            System.out.println("No such method, check if you have this rpc method");
        }
    }

    protected void callRpc(Object object, String methodName) {
        this.callRpc(object, methodName, null);
    }

    protected void callRpc(Object object, String methodName, Object args) {
        Class rpcClass = object.getClass();
        String header = rpcClass.getName() + "_" + methodName;

        ViewStructs viewStructs = rpcMethodDictionary.get(header);
        Method rpcMethod = viewStructs.viewMethod;
        try {
            if(args == null) {
                rpcMethod.invoke(viewStructs.viewObject);
            }else {
                rpcMethod.invoke(viewStructs.viewObject, args);
            }
            rpcMethod.invoke(viewStructs.viewObject, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

