package datasecurity_authentication.server;

import datasecurity_authentication.models.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * PrintService define the method signatures for a Print Server.
 */
public interface PrintService extends Remote {
    /**
     * print prints file filename on the specific printer.
     * @param filename file to print.
     * @param printer printer to use.
     * @throws RemoteException
     */
    void print(String filename, String printer, Message msg) throws RemoteException;

    /**
     * queue lists the printer queue by jobnumber and filename.
     * @param printer the printer to get queue from.
     * @return Map<Integer, String>
     * @throws RemoteException
     */
    Map<Integer, String> queue(String printer, Message msg) throws RemoteException;

    /**
     * topQueue moves the job to the top of the print queue.
     */
    void topQueue(String printer, int job, Message msg) throws RemoteException;

    /**
     * start starts the print server.
     * @return true if the print server is started, otherwise false.
     * @throws RemoteException
     */
    boolean start(Message msg) throws RemoteException;

    /**
     * stop stops the print server.
     * @return true if the print server is stopped, otherwise false.
     * @throws RemoteException
     */
    boolean stop(Message msg) throws RemoteException;

    /**
     * restart restarts the print server.
     * @return true if the print server is restarted, otherwise false.
     * @throws RemoteException
     */
    boolean restart(Message msg) throws RemoteException;

    /**
     * status returns the status of the print.
     * @param printer printer to get status from.
     * @return status of the print server.
     * @throws RemoteException
     */
    String status(String printer, Message msg) throws RemoteException;

    /**
     * readConfig reads the config file and return the value of the assosiated parameter.
     * @param parameter the lookup parameter.
     * @return value of the parameter.
     * @throws RemoteException
     */
    String readConfig(String parameter, Message msg) throws RemoteException;

    /**
     * setConfig sets the given parameter with the value in the config.
     * @param parameter the parameter to set.
     * @param value the value of the parameter.
     * @throws RemoteException
     */
    void setConfig(String parameter, String value, Message msg) throws RemoteException;

    /**
     * login log in a user.
     * @param message the Message containing information about a user.
     * @return a Message containing the new Session for the user.
     * @throws RemoteException
     */
    Message login(Message message) throws RemoteException;

    /**
     * logout logs out a user.
     * @param message the Message containing a user's session.
     * @return true if the user is successfully logged out, otherwise false.
     * @throws RemoteException
     */
    boolean logout(Message message) throws RemoteException;
}