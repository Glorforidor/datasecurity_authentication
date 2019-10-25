package datasecurity_authentication;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;

public class Main {
	public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
		PrintService service = (PrintService) Naming.lookup("rmi://localhost:5099/print");

		service.print("MySecretDocument", "DTUPrinter");
		service.print("MySecretDocument1", "DTUPrinter1");
		service.print("MySecretDocument2", "DTUPrinter2");
		service.print("MySecretDocument3", "DTUPrinter3");

		Map<Integer, String> queue = service.queue();
		for (Map.Entry<Integer, String> m: queue.entrySet()) {
			System.out.println(String.format("Job number: %d, filename: %s", m.getKey(), m.getValue()));
		}

		service.topQueue(4);

		queue = service.queue();
		for (Map.Entry<Integer, String> m: queue.entrySet()) {
			System.out.println(String.format("Job number: %d, filename: %s", m.getKey(), m.getValue()));
		}
	}
}
