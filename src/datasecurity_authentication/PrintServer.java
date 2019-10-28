package datasecurity_authentication;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class PrintServer {
    public static void main(String[] args) throws RemoteException {
        Registry registry = LocateRegistry.createRegistry(5099);
        PrintServant servant = new PrintServant();
        registry.rebind("print", servant);
        registry.rebind("queue", servant);
        registry.rebind("topQueue", servant);
        registry.rebind("status", servant);
        registry.rebind("start", servant);
        registry.rebind("restart", servant);
        registry.rebind("stop", servant);
        registry.rebind("readConfig", servant);
        registry.rebind("setConfig", servant);
        registry.rebind("login", servant);
        registry.rebind("logout", servant);
    }
}