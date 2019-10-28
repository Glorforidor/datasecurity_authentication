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
    void print(String filename, String printer) throws RemoteException;

    /**
     * queue lists the printer queue by jobnumber and filename.
     * @return Map<Integer, String>
     */
    Map<Integer, String> queue() throws RemoteException;

    /**
     * topQueue moves the job to the top of the print queue.
     */
    void topQueue(int job) throws RemoteException;

    /**
     * start starts the print server.
     * @return true if the print server is started, otherwise false.
     * @throws RemoteException
     */
    boolean start() throws RemoteException;

    /**
     * stop stops the print server.
     * @return true if the print server is stopped, otherwise false.
     * @throws RemoteException
     */
    boolean stop() throws RemoteException;

    /**
     * restart restarts the print server.
     * @return true if the print server is restarted, otherwise false.
     * @throws RemoteException
     */
    boolean restart() throws RemoteException;

    /**
     * status returns the status of the print server.
     * @return status of the print server.
     * @throws RemoteException
     */
    String status() throws RemoteException;

    /**
     * readConfig reads the config file and return the value of the assosiated parameter.
     * @param parameter the lookup parameter.
     * @return value of the parameter.
     * @throws RemoteException
     */
    String readConfig(String parameter) throws RemoteException;

    /**
     * setConfig sets the given parameter with the value in the config.
     * @param parameter the parameter to set.
     * @param value the value of the parameter.
     * @throws RemoteException
     */
    void setConfig(String parameter, String value) throws RemoteException;

    boolean login(String name, String pass) throws RemoteException;

    boolean logout(String name, String pass) throws RemoteException;
}