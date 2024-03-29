import java.net.*;
import java.io.*;
import java.util.*;

import com.github.sarxos.webcam.Webcam;

/*
 * The Client that can be run both as a console or a GUI
 */

public class Client {
	// for I/O
	private ObjectInputStream sInput; // to read from the socket
	private ObjectOutputStream sOutput; // to write on the socket
	private Socket socket;
	// if I use a GUI or not
	private ClientGUI cg;
	// the server, the port and the username
	private Webcam webcam = null;
	private String server, username;

	private int port;

	/*
	 * 
	 * Constructor called by console mode
	 * 
	 * server: the server address
	 * 
	 * port: the port number
	 * 
	 * username: the username
	 * 
	 */

	Client(String server, int port, String username) {
		// which calls the common constructor with the GUI set to null
		this(server, port, username, null);

		try {
			//			webcam.open();
		} catch(NoClassDefFoundError e) {
			e.printStackTrace();
		}

	}

	/*
	 * 
	 * Constructor call when used from a GUI
	 * 
	 * in console mode the ClienGUI parameter is null
	 * 
	 */

	Client(String server, int port, String username, ClientGUI cg) {

		this.server = server;

		this.port = port;

		this.username = username;

		// save if we are in GUI mode or not

		this.cg = cg;

	}

	public boolean start() {
		try {
			socket = new Socket(server, port);
		} catch (Exception ec) {
			display("Error connectiong to server:" + ec);
			return false;
		}
		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);

		try {
			sInput = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}
		new ListenFromServer().start();
		// Send our username to the server this is the only message that we
		// will send as a String. All other messages will be ChatMessage objects
		try {

			sOutput.writeObject(username);

		} catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;

		}
		return true;
	}

	/*
	 * 
	 * To send a message to the console or the GUI
	 * 
	 */

	private void display(String msg) {

		if (cg == null)
			System.out.println(msg); // println in console mode
		else
			cg.append(msg + "\n"); // append to the ClientGUI JTextArea (or
									// tever)
	}

	/*
	 * 
	 * To send a message to the server
	 * 
	 */

	void sendMessage(Message msg) {
		try {
			sOutput.writeObject(msg);
		} catch (IOException e) {
			display("Exception writing to server: " + e);
		}
	}

	/*
	 * 
	 * When something goes wrong
	 * 
	 * Close the Input/Output streams and disconnect not much to do in the catch
	 * clause
	 * 
	 */

	private void disconnect() {

		try {
			if (sInput != null)
				sInput.close();
		} catch (Exception e) {
		} // not much else I can do
		
		try {
			if (sOutput != null)
				sOutput.close();
		}
		catch (Exception e) {
		} // not much else I can do
		try {
			if (socket != null)
				socket.close();
		} catch (Exception e) {
		} // not much else I can do
		// inform the GUI
		if (cg != null)
			cg.connectionFailed();
	}

	public static void main(String[] args) {

		// default values
		int portNumber = 1500;
		String serverAddress = "localhost";
		String userName = "Anonymous";
		// depending of the number of arguments provided we fall through
		switch (args.length) {
		// > javac Client username portNumber serverAddr
		case 3:
			serverAddress = args[2];
			// > javac Client username portNumber
		case 2:
			try {
				portNumber = Integer.parseInt(args[1]);
			}
			catch (Exception e) {
				System.out.println("Invalid port number.");
				System.out.println("Usage is: > java Client [username] [portNumber] [verAddress]");
				return;
			}
			// > javac Client username
		case 1:
			userName = args[0];
			// > java Client
		case 0:
			break;
		// invalid number of arguments
		default:
			System.out.println("Usage is: > java Client [username] [portNumber] rverAddress]");
			return;

		}
		// create the Client object
		Client client = new Client(serverAddress, portNumber, userName);
		// test if we can start the connection to the Server
		// if it failed nothing we can do
		if (!client.start())
			return;

		// wait for messages from user
		Scanner scan = new Scanner(System.in);
		// loop forever for message from the user

		while (true) {
			System.out.print("> ");
			// read message from user
			String msg = scan.nextLine();
			// logout if message is LOGOUT
			if (msg.equalsIgnoreCase("LOGOUT")) {
				client.sendMessage(new Message(Message.LOGOUT, ""));
				// break to do the disconnect
				break;
			} else if (msg.equalsIgnoreCase("WHOISIN")) {
				// client.sendMessage(new Message(Message.WHOISIN, );
			} else { // default to ordinary message
				client.sendMessage(new Message(Message.MESSAGE, msg));
			}
		}
		client.disconnect();
	}

	/*
	 * 
	 * a class that waits for the message from the server and append them to the
	 * xtArea
	 * 
	 * if we have a GUI or simply System.out.println() it in console mode
	 * 
	 */

	class ListenFromServer extends Thread {

		public void run() {
			while (true) {
				try {
					String msg = (String) sInput.readObject();
					if (cg == null) {
						System.out.println(msg);
						System.out.print("> ");
					}
					else {
						cg.append(msg);
					}
				} catch (IOException e) {
					display("Server has close the connection: " + e);
					if (cg != null)
						cg.connectionFailed();
					break;
				} catch (ClassNotFoundException e2) {

				}

			}

		}

	}
}
