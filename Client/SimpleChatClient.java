import java.io.*;
import java.net.*;
import java.util.*;
import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.event.*;
import javafx.geometry.*;

public class SimpleChatClient extends Application {
	TextArea incoming;
	TextField outgoing;
	BufferedReader reader;
	PrintWriter writer;
	Socket socket;
	Label netEst;
	
	public static void main(String args[]) {
		launch(args);
	}
	
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Ludicrously Simple Chat Client");
		FlowPane rootNode = new FlowPane(10,10);
		rootNode.setAlignment(Pos.CENTER);
		primaryStage.setScene(new Scene(rootNode,700,500));
		incoming = new TextArea();
		incoming.setPrefRowCount(15);
		incoming.setPrefColumnCount(50);
		incoming.setWrapText(true);
		incoming.setEditable(false);
		ScrollPane qScroller = new ScrollPane(incoming);
		qScroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		qScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		outgoing = new TextField();
		outgoing.setPrefColumnCount(20);
		outgoing.setOnAction(new SendListener());
		Button sendButton = new Button("Send");
		sendButton.setOnAction(new SendListener());
		netEst = new Label();
		setUpNetworking();
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
		rootNode.getChildren().addAll(qScroller,outgoing,sendButton,netEst);
		primaryStage.show();
		primaryStage.setOnCloseRequest(new CloseListener());
	}
	
	public void setUpNetworking() {
		try {
			socket = new Socket("127.0.0.1",5000);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream());
			netEst.setText("networking established");
		}
		catch(IOException exc) {
			exc.printStackTrace();
		}
	}
	
	public class SendListener implements EventHandler<ActionEvent> {
		public void handle(ActionEvent ae) {
			try {
				writer.println(outgoing.getText());
				writer.flush();
			}
			catch(Exception exc) {
				exc.printStackTrace();
			}
			outgoing.setText("");
			outgoing.requestFocus();
		}
	}
	
	public class IncomingReader implements Runnable {
		public void run() {
			try {
				String message;
				while((message = reader.readLine()) != null) {
					incoming.appendText(message+"\n");
				}
			}
			catch(Exception exc) {
				exc.printStackTrace();
			}
		}
	}
	
	public class CloseListener implements EventHandler<WindowEvent> {
		public void handle(WindowEvent we) {
			try {
				writer.close();
				reader.close();	
				socket.close();
			}
			catch(IOException exc) {}
		}
	}
}