package datasecurity_authentication;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class PrintServer {
    public static void main(String[] args) throws RemoteException {
        Registry registry = LocateRegistry.createRegistry(5099);
        registry.rebind("print", new PrintServant());
        registry.rebind("queue", new PrintServant());
        registry.rebind("topQueue", new PrintServant());
    }
}