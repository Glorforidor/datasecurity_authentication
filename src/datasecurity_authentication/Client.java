package datasecurity_authentication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;

public class Client {
	public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
		PrintService service = (PrintService) Naming.lookup("rmi://localhost:5099/printserver");
		try {
			new CLI(service).run();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Bye");
	}

	static class CLI {
		PrintService srv;

		public CLI(PrintService service) {
			this.srv = service;
		}

		void menu() {
			System.out.println("Crypto Printer v0.0.1");
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
					var printer = readInput(br, "Which printer: ");
					System.out.println(srv.status(printer));
					break;
				}
				case "2": {
					var filename = readInput(br, "Write input file: ");
					var printer = readInput(br, "Write printer: ");
					try {
						srv.print(filename, printer);
					} catch (RemoteException e) {
						System.out.println("Seems the printer is not running, try start it first");
					}
					break;
				}
				case "3": {
					var printer = readInput(br, "Which printer: ");
					var queue = srv.queue(printer);
					for (Map.Entry<Integer, String> q : queue.entrySet()) {
						System.out.println(String.format("Job number: %d, filename: %s", q.getKey(), q.getValue()));
					}
					break;
				}
				case "4": {
					var printer = readInput(br, "Which printer: ");
					var job = readInput(br, "Job number: ");
					srv.topQueue(printer, Integer.parseInt(job));
					break;
				}
				case "5":
					srv.start();
					break;
				case "6":
					srv.stop();
					break;
				case "7":
					srv.restart();
					break;
				case "8":
					running = false;
					break;
				case "9": {
					var username = readInput(br, "Write your username: ");
					var password = readInput(br, "Write your password: ");
					srv.login(username, password);
					break;
				}
				case "10": {
					var username = readInput(br, "Write your username: ");
					var password = readInput(br, "Write your password: ");
					srv.logout(username, password);
					break;
				}
				case "?":
					menu();
					break;
				default:
					System.out.println("Wrong input");
				}
			}
		}
	}
}