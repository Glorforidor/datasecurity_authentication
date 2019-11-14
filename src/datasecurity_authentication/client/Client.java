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
        EncryptionHandler eh = EncryptionHandler.getInstance();
        DataUtil dUtil = DataUtil.getInstance();

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
        }

        private String readInput(BufferedReader br, String msg) throws IOException {
            System.out.print(msg);
            return br.readLine();
        }

        void run() throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            var running = true;
            menu();
            while (running) {
                var input = br.readLine();
                switch (input) {
                case "1": {
                    if (session != null) {
                        var printer = readInput(br, "Which printer: ");
                        byte[] combined = dUtil.incrementAndSplitSession(session);
                        try {
                            System.out.println(srv.status(printer, eh.encrypt(combined)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                case "2": {
                    if (session != null) {
                        var filename = readInput(br, "Write input file: ");
                        var printer = readInput(br, "Write printer: ");
                        byte[] combined = dUtil.incrementAndSplitSession(session);
                        try {
                            srv.print(filename, printer, eh.encrypt(combined));
                        } catch (RemoteException e) {
                            System.out.println(e.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                case "3": {
                    if (session != null) {
                        var printer = readInput(br, "Which printer: ");
                        byte[] combined = dUtil.incrementAndSplitSession(session);
                        try {
                            var queue = srv.queue(printer, eh.encrypt(combined));
                            for (Map.Entry<Integer, String> q : queue.entrySet()) {
                                System.out.printf("Job number: %d, filename: %s\n", q.getKey(), q.getValue());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                case "4": {
                    if (session != null) {
                        var printer = readInput(br, "Which printer: ");
                        var job = readInput(br, "Job number: ");
                        try {
                            // first successfully parse job to an integer before calling print server
                            int j = Integer.parseInt(job);
                            byte[] combined = dUtil.incrementAndSplitSession(session);
                            srv.topQueue(printer, j, eh.encrypt(combined));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                case "5": {
                    if (session != null) {
                        byte[] combined = dUtil.incrementAndSplitSession(session);
                        try {
                            srv.start(eh.encrypt(combined));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                case "6": {
                    if (session != null) {
                        byte[] combined = dUtil.incrementAndSplitSession(session);
                        try {
                            srv.stop(eh.encrypt(combined));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                case "7": {
                    if (session != null) {
                        byte[] combined = dUtil.incrementAndSplitSession(session);
                        try {
                            srv.restart(eh.encrypt(combined));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                case "8":
                    running = false;
                    break;
                case "9": {
                    var username = readInput(br, "Write your username: ");
                    var password = readInput(br, "Write your password: ");

                    byte[] combined = dUtil.splitUser(username, password);
                    try {
                        Message serverSession = srv.login(eh.encrypt(combined));
                        var bytes = eh.decrypt(serverSession);
                        session = dUtil.combineToSession(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "10": {
                    if (session != null) {
                        byte[] combined = dUtil.incrementAndSplitSession(session);
                        try {
                            srv.logout(eh.encrypt(combined));
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
