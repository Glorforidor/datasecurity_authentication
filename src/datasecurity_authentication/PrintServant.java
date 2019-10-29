package datasecurity_authentication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class PrintServant extends UnicastRemoteObject implements PrintService {
    private static final long serialVersionUID = 8627793523520780643L;
    private Map<String, LinkedList<String>> printerQueues = new HashMap<>();
    private boolean isRunning;
    private Map<String, String> config = new HashMap<>();
    private LinkedList<User> users = new LinkedList<>();
    {
        try (BufferedReader csvReader = new BufferedReader(new FileReader("users.csv"))) {
            String row;
            while ((row = csvReader.readLine()) != null) {
                System.out.println(row);
                String[] data = row.split(",");
                users.add(new User(data[0], data[1], data[2]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PrintServant() throws RemoteException {
        super();
    }

    private void log(String msg) {
        // TODO: perhaps make this logger more interessting by log to a file.
        System.out.println(msg);
    }

    @Override
    public void print(String filename, String printer) throws RemoteException {
        if (!isRunning) {
            // TODO: maybe a bit much
            throw new RemoteException("Printer is not running");
        }

        log(String.format("Forwarding: %s, to printer: %s", filename, printer));
        if (printerQueues.containsKey(printer)) {
            printerQueues.get(printer).add(filename);
        } else {
            LinkedList<String> queue = new LinkedList<>();
            queue.add(filename);
            printerQueues.put(printer, queue);
        }
    }

    @Override
    public Map<Integer, String> queue(String printer) throws RemoteException {
        if (!printerQueues.containsKey(printer)) {
            throw new RemoteException("Printer does not exist");
        }

        var printerQueue = printerQueues.get(printer);
        Map<Integer, String> queue = new HashMap<>();
        for (int i = 0; i < printerQueue.size(); i++) {
            queue.put(i + 1, printerQueue.get(i));
        }

        return queue;
    }

    @Override
    public void topQueue(String printer, int job) throws RemoteException {
        if (job < 1) {
            throw new IllegalArgumentException("job must be above 0");
        }
        if (!printerQueues.containsKey(printer)) {
            throw new RemoteException("Printer does not exist");
        }  
        
        // remove the job from queue and then added it back as the first element.
        var printerQueue = printerQueues.get(printer);
        var jobToMove = printerQueue.remove(job - 1);
        printerQueue.addFirst(jobToMove);
    }

    @Override
    public boolean start() throws RemoteException {
        // TODO: might rethink the return value
        log("Starting server");
        this.isRunning = true;
        return isRunning;
    }

    @Override
    public boolean stop() throws RemoteException {
        // TODO: might rethink the return value
        log("Stopping server");
        this.isRunning = false;
        return isRunning;
    }

    @Override
    public boolean restart() throws RemoteException {
        if (isRunning) {
            log("Stopping Server");
            isRunning = false;

            log("Clear all printer queues");
            printerQueues.clear();

            log("Starting server");
            isRunning = true;
        } else {
            log("Server was stopped; starting server");
            isRunning = true;
        }

        return isRunning;
    }

    @Override
    public String status(String printer) throws RemoteException {
        if (!printerQueues.containsKey(printer)) {
            throw new RemoteException("Printer does not exist");
        }
        String status = "Printer is %s; Remaining jobs: " + printerQueues.get(printer).size();
        if (isRunning) {
            status = String.format(status, "running");
        } else {
            status = String.format(status, "not running");
        }

        return status;
    }

    @Override
    public String readConfig(String parameter) throws RemoteException {
        log(String.format("Reading configuration with parameter: %s", parameter));
        return config.get(parameter);
    }

    @Override
    public void setConfig(String parameter, String value) throws RemoteException {
        log(String.format("Setting configuration with parameter: %s, with value: %s", parameter, value));
        config.put(parameter, value);
    }

    @Override
    public boolean login(String name, String pass) throws RemoteException {
        boolean success = false;
        for (User u : users) {
            if (u.getName().equals(name)) {
                try {
                    MessageDigest sha = MessageDigest.getInstance("SHA-256");
                    sha.update((u.getSalt() + pass).getBytes());
                    var b = Base64.getEncoder().encodeToString(sha.digest());
                    success = b.equals(u.getPass());
                    if (success) {
                        log(String.format("User: %s has logged in", name));
                    } else {
                        log(String.format("Login failed"));
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return success;
    }

    @Override
    public boolean logout(String name, String pass) throws RemoteException {
        boolean success = false;
        for (User u : users) {
            if (u.getName().equals(name)) {
                try {
                    MessageDigest sha = MessageDigest.getInstance("SHA-256");
                    sha.update((u.getSalt() + pass).getBytes());
                    var b = Base64.getEncoder().encodeToString(sha.digest());
                    success = b.equals(u.getPass());
                    if (success) {
                        log(String.format("User: %s has logged in", name));
                    } else {
                        log(String.format("Login failed"));
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return success;
    }
}