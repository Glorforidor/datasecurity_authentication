package datasecurity_authentication.client;

import datasecurity_authentication.models.Message;
import datasecurity_authentication.models.Session;
import datasecurity_authentication.server.PrintService;
import datasecurity_authentication.utils.DataUtil;
import datasecurity_authentication.utils.EncryptionHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Client application to interact with the Print Server.
 */
public class Client {
    private static Session session;

    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
        PrintService service = (PrintService) Naming.lookup("rmi://localhost:5099/printserver");
        try {
            new CLI(service).run();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Bye");
    }

    /**
     * CLI for a client.
     */
    static class CLI {
        PrintService srv;
        EncryptionHandler encryptionHandler = EncryptionHandler.getInstance();
        DataUtil dataUtil = DataUtil.getInstance();

        public CLI(PrintService service) {
            this.srv = service;
        }

        void menu() {
            System.out.println("Crypto Printer v0.0.2");
            System.out.println("=".repeat(100));
            System.out.println("Menu");
            System.out.println("-".repeat(100));
            System.out.println("(1) Status");
            System.out.println("(2) Print");
            System.out.println("(3) Queue");
            System.out.println("(4) topQueue");
            System.out.println("(5) Start");
            System.out.println("(6) Stop");
            System.out.println("(7) Restart");
            System.out.println("(8) Quit");
            System.out.println("(9) Login");
            System.out.println("(10) Logout");
            System.out.println("(?) print menu");
        }

        private String readInput(BufferedReader br, String msg) throws IOException {
            System.out.print(msg);
            return br.readLine();
        }

        // run runs the cli in a loop until the client decides to quit.
        void run() throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            var running = true;
            menu();
            while (running) {
                var input = br.readLine();
                switch (input) {
                // status
                case "1": {
                    if (session != null) {
                        var printer = readInput(br, "Which printer: ");
                        byte[] combined = dataUtil.incrementAndSplitSession(session);
                        try {
                            System.out.println(srv.status(printer, encryptionHandler.encrypt(combined)));
                        } catch (RemoteException e) {
                            System.out.println(e.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                // print
                case "2": {
                    if (session != null) {
                        var filename = readInput(br, "Write input file: ");
                        var printer = readInput(br, "Write printer: ");
                        byte[] combined = dataUtil.incrementAndSplitSession(session);
                        try {
                            srv.print(filename, printer, encryptionHandler.encrypt(combined));
                        } catch (RemoteException e) {
                            System.out.println(e.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                // queue
                case "3": {
                    if (session != null) {
                        var printer = readInput(br, "Which printer: ");
                        byte[] combined = dataUtil.incrementAndSplitSession(session);
                        try {
                            var queue = srv.queue(printer, encryptionHandler.encrypt(combined));
                            for (Map.Entry<Integer, String> q : queue.entrySet()) {
                                System.out.printf("Job number: %d, filename: %s\n", q.getKey(), q.getValue());
                            }
                        } catch (RemoteException e) {
                            System.out.println(e.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                // topQueue
                case "4": {
                    if (session != null) {
                        var printer = readInput(br, "Which printer: ");
                        var job = readInput(br, "Job number: ");
                        try {
                            // first successfully parse job to an integer before calling print server
                            int j = Integer.parseInt(job);
                            byte[] combined = dataUtil.incrementAndSplitSession(session);
                            srv.topQueue(printer, j, encryptionHandler.encrypt(combined));
                        } catch (RemoteException e) {
                            System.out.println(e.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                // start
                case "5": {
                    if (session != null) {
                        byte[] combined = dataUtil.incrementAndSplitSession(session);
                        try {
                            srv.start(encryptionHandler.encrypt(combined));
                        } catch (RemoteException e) {
                            System.out.println(e.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                // stop
                case "6": {
                    if (session != null) {
                        byte[] combined = dataUtil.incrementAndSplitSession(session);
                        try {
                            srv.stop(encryptionHandler.encrypt(combined));
                        } catch (RemoteException e) {
                            System.out.println(e.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                // restart
                case "7": {
                    if (session != null) {
                        byte[] combined = dataUtil.incrementAndSplitSession(session);
                        try {
                            srv.restart(encryptionHandler.encrypt(combined));
                        } catch (RemoteException e) {
                            System.out.println(e.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                // quit
                case "8":
                    // stop the cli loop
                    running = false;
                    break;
                // login
                case "9": {
                    var username = readInput(br, "Write your username: ");
                    var password = readInput(br, "Write your password: ");
                    byte[] combined = dataUtil.splitUser(username, password);
                    try {
                        Message serverSession = srv.login(encryptionHandler.encrypt(combined));
                        var bytes = encryptionHandler.decrypt(serverSession);
                        session = dataUtil.combineToSession(bytes);
                    } catch (RemoteException e) {
                        System.out.println(e.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                // logout
                case "10": {
                    if (session != null) {
                        byte[] combined = dataUtil.incrementAndSplitSession(session);
                        try {
                            srv.logout(encryptionHandler.encrypt(combined));
                        } catch (RemoteException e) {
                            System.out.println(e.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                case "?":
                    menu();
                    break;
                default:
                    System.out.println("Wrong input");
                    break;
                }
            }
        }
    }
}
