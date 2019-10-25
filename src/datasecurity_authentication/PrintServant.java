package datasecurity_authentication;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class PrintServant extends UnicastRemoteObject implements PrintService {
    private static final long serialVersionUID = 8627793523520780643L;
    private LinkedList<String> printQueue = new LinkedList<>();
    private boolean isRunning;

    public PrintServant() throws RemoteException {
        super();
    }

    @Override
    public void print(String filename, String printer) throws RemoteException {
        System.out.println(String.format("I am printing: %s, on printer: %s", filename, printer));
        printQueue.add(filename);
    }

    @Override
    public Map<Integer, String> queue() throws RemoteException {
        Map<Integer, String> queue = new HashMap<>(); 
        for (int i = 0; i < printQueue.size(); i++) {
            queue.put(i+1, printQueue.get(i));
        }

        return queue;
    }

    @Override
    public void topQueue(int job) throws RemoteException {
        if (job < 1) {
            throw new IllegalArgumentException("job must be above 0");
        }
        // remove the job from queue and then added it back as the first element.
        var jobToMove = printQueue.remove(job-1);
        printQueue.addFirst(jobToMove);
    }

    @Override
    public boolean start() throws RemoteException {
        // TODO: might rethink the return value
        System.out.println("Starting server");
        this.isRunning = true;
        return isRunning;
    }

    @Override
    public boolean stop() throws RemoteException {
        // TODO: might rethink the return value
        System.out.println("Stopping server");
        this.isRunning = false;
        return isRunning;
    }

    @Override
    public boolean restart() throws RemoteException {
        if (isRunning) {
            System.out.println("Stopping Server");
            isRunning = false;
            System.out.println("Starting server");
            isRunning = true;
        } else {
            System.out.println("Server was stopped; starting server");
            isRunning = true;
        }

        return isRunning;
    }

    @Override
    public String status() throws RemoteException {
        String status = "Printer is %s; Remaining jobs: " + printQueue.size();
        if (isRunning) {
            status = String.format(status, "running");
        } else {
            status = String.format(status, "not running");
        }
        
        return status;
    }

    @Override
    public String readConfig(String parameter) throws RemoteException {
        return null;
    }

    @Override
    public void setConfig(String parameter, String value) throws RemoteException {

    }
}