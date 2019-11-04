package datasecurity_authentication;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class PrintServant extends UnicastRemoteObject implements PrintService {
    private static final long serialVersionUID = 8627793523520780643L;
    private Map<String, LinkedList<String>> printerQueues;
    private boolean isRunning;
    private Map<String, String> config;
    private Map<String, Session> activeTokens;
    private EncryptionHandler eh;

    public PrintServant() throws RemoteException {
        super();
        // create the database file with populated users
        UsersManager.createDatabaseFile();
        this.printerQueues = new HashMap<>();
        this.config = new HashMap<>();
        this.activeTokens = new HashMap<>();
        eh = EncryptionHandler.getInstance();
    }

    private void log(String msg) {
        // TODO: perhaps make this logger more interessting by log to a file.
        System.out.println(msg);
    }

    private boolean checkSession(Message msg) throws Exception {
        var bytes = eh.decrypt(msg);
        var session = eh.splitter(bytes);
        var success = false;
        for (Map.Entry<String, Session> s: activeTokens.entrySet()) {
            var activeToken = s.getValue();
            var tokenCheck = Arrays.equals(activeToken.getToken(), session.getToken());
            if (tokenCheck && activeToken.getCount() + 1 == session.getCount()) {
                success = true;
                break;
            }
        }

        return success;
    }

    @Override
    public void print(String filename, String printer, Message msg) throws RemoteException {
        if (!isRunning) {
            // TODO: maybe a bit much
            throw new RemoteException("Printer is not running");
        }

        boolean correct = false;
        try {
            correct = checkSession(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!correct) {
            throw new RemoteException("Unauthorized");
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
    public Map<Integer, String> queue(String printer, Message msg) throws RemoteException {
        boolean correct = false;
        try {
            correct = checkSession(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!correct) {
            throw new RemoteException("Unauthorized");
        }


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
    public void topQueue(String printer, int job, Message msg) throws RemoteException {
        boolean correct = false;
        try {
            correct = checkSession(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!correct) {
            throw new RemoteException("Unauthorized");
        }
        
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
    public boolean start(Message msg) throws RemoteException {
        boolean correct = false;
        try {
            correct = checkSession(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!correct) {
            throw new RemoteException("Unauthorized");
        }
        // TODO: might rethink the return value
        log("Starting server");
        this.isRunning = true;
        return isRunning;
    }

    @Override
    public boolean stop(Message msg) throws RemoteException {
        boolean correct = false;
        try {
            correct = checkSession(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!correct) {
            throw new RemoteException("Unauthorized");
        }
        // TODO: might rethink the return value
        log("Stopping server");
        this.isRunning = false;
        return isRunning;
    }

    @Override
    public boolean restart(Message msg) throws RemoteException {
        boolean correct = false;
        try {
            correct = checkSession(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!correct) {
            throw new RemoteException("Unauthorized");
        }
        
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
    public String status(String printer, Message msg) throws RemoteException {
        boolean correct = false;
        try {
            correct = checkSession(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!correct) {
            throw new RemoteException("Unauthorized");
        }
        
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
    public String readConfig(String parameter, Message msg) throws RemoteException {
        boolean correct = false;
        try {
            correct = checkSession(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!correct) {
            throw new RemoteException("Unauthorized");
        }
        log(String.format("Reading configuration with parameter: %s", parameter));
        return config.get(parameter);
    }

    @Override
    public void setConfig(String parameter, String value, Message msg) throws RemoteException {
        boolean correct = false;
        try {
            correct = checkSession(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!correct) {
            throw new RemoteException("Unauthorized");
        }
        log(String.format("Setting configuration with parameter: %s, with value: %s", parameter, value));
        config.put(parameter, value);
    }

    @Override
    public byte[] login(Message msg) throws RemoteException {
        String name = null;
        String pass = null;
        try {
            byte[] b = eh.decrypt(msg);
            User u = eh.splitterLogin(b);
            name = u.getName();
            pass = u.getPass();

        } catch (Exception e) {
            e.printStackTrace();
        }
        var users = UsersManager.readUsers();
        byte[] by = new byte[64];
        boolean success = false;
        for (User u : users) {
            if (u.getName().equals(name)) {
                try {
                    MessageDigest sha = MessageDigest.getInstance("SHA-256");
                    sha.update((u.getSalt() + pass).getBytes());
                    var b = Base64.getEncoder().encodeToString(sha.digest());
                    if (b.equals(u.getPass())) {
                        success = true;
                        log(String.format("User: %s has logged in", name));
                        Session s = new Session(eh.generateSessionToken(), 0);
                        System.arraycopy(eh.combineAndIncrement(s), 0, by, 0, 64);
                        this.activeTokens.put(u.getName(), s);
                    } else {
                        log(String.format("Login failed"));

                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        if (!success) {
            throw new RemoteException("Failed to login");
        }
        return by;
    }

    @Override
    public boolean logout(Message msg) throws RemoteException {
        final boolean[] success = {false};
        byte[] b = null;

        try {
            b = eh.decrypt(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (b == null) throw new RemoteException();

        Session s = eh.splitter(b);
        final String[] elementToRemove = {null};
        this.activeTokens.forEach((key, session) -> {
            if (Arrays.equals(session.getToken(), s.getToken())) {
                success[0] = true;
                elementToRemove[0] = key;
            }
        });
        this.activeTokens.remove(elementToRemove[0]);
        return success[0];
    }
}