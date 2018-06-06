import java.io.*;
import java.net.*;
import java.util.*;

public class SimpleChatServer {
	ArrayList<PrintWriter> clientOutputStream;
	
	public class ClientHandler implements Runnable {
		BufferedReader reader;
		Socket sock;
		public ClientHandler(Socket clientSocket) {
			try {
				sock = clientSocket;
				reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		
		public void run() {
			String message;
			try {
				while((message = reader.readLine()) != null) {
					tellEveryone(message);
				}
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	public static void main(String args[]) {
		new SimpleChatServer().go();
	}
	
	public void go() {
		clientOutputStream = new ArrayList<>();
		try {
			ServerSocket serverSock = new ServerSocket(5000);
			System.out.println("Server Started");
			while(true) {
				Socket socket = serverSock.accept();
				PrintWriter writer = new PrintWriter(socket.getOutputStream());
				clientOutputStream.add(writer);
				Thread t = new Thread(new ClientHandler(socket));
				t.start();
				System.out.println("got a connection");
			}
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public void tellEveryone(String message) {
		Iterator<PrintWriter> it = clientOutputStream.iterator();
		while(it.hasNext()) {
			try {
				PrintWriter writer = (PrintWriter)it.next();
				writer.println(message);
				writer.flush();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}