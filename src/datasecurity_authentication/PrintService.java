package datasecurity_authentication;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface PrintService extends Remote {
    /**
     * print prints file filename on the specific printer.
     * @param filename file to print.
     * @param printer printer to use.
     * @throws 
     */
    void print(String filename, String printer, Message msg) throws RemoteException;

    /**
     * queue lists the printer queue by jobnumber and filename.
     * @param printer the printer to get queue from.
     * @return Map<Integer, String>
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

    Message login(Message msg) throws RemoteException;

    boolean logout(Message msg) throws RemoteException;
}