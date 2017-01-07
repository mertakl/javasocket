
	import java.io.DataInputStream;
	import java.io.PrintStream;
	import java.io.IOException;
	import java.net.Socket;
	import java.net.ServerSocket;


	public class Server {

	  // The server socket.
	  private static ServerSocket serverSocket = null;
	  // The client socket.
	  private static Socket clientSocket = null;

	  // This chat server can accept up to maxClientsCount clients' connections.
	  private static final int maxClientsCount = 10;
	  private static final clientThread[] threads = new clientThread[maxClientsCount];

	  public static void main(String args[]) {

	    // The default port number.
	    int portNumber = 2222;
	    if (args.length < 1) {
	      System.out
	          .println("Usage: java MultiThreadChatServer <portNumber>\n"
	              + "Now using port number=" + portNumber);
	    } else {
	      portNumber = Integer.valueOf(args[0]).intValue();
	    }

	    try {
	      serverSocket = new ServerSocket(portNumber);
	    } catch (IOException e) {
	      System.out.println(e);
	    }


	    while (true) {
	      try {
	        clientSocket = serverSocket.accept();
	        int i = 0;
	        for (i = 0; i < maxClientsCount; i++) {
	          if (threads[i] == null) {
	            (threads[i] = new clientThread(clientSocket, threads)).start();
	            break;
	          }
	        }
	        if (i == maxClientsCount) {
	          PrintStream os = new PrintStream(clientSocket.getOutputStream());
	          os.println("Server too busy. Try later.");
	          os.close();
	          clientSocket.close();
	        }
	      } catch (IOException e) {
	        System.out.println(e);
	      }
	    }
	  }
	}


	class clientThread extends Thread {

	  private DataInputStream is = null;
	  private PrintStream os = null;
	  private Socket clientSocket = null;
	  private final clientThread[] threads;
	  private int maxClientsCount;

	  public clientThread(Socket clientSocket, clientThread[] threads) {
	    this.clientSocket = clientSocket;
	    this.threads = threads;
	    maxClientsCount = threads.length;
	  }

	  public void run() {
	    int maxClientsCount = this.maxClientsCount;
	    clientThread[] threads = this.threads;

	    try {

	      is = new DataInputStream(clientSocket.getInputStream());
	      os = new PrintStream(clientSocket.getOutputStream());
	      os.println("Enter your name.");
	      String name = is.readLine().trim();
	      os.println("Hello " + name
	          + " to our chat room.\nTo leave enter /quit in a new line");
	      for (int i = 0; i < maxClientsCount; i++) {
	        if (threads[i] != null && threads[i] != this) {
	          threads[i].os.println("*** A new user " + name
	              + " entered the chat room !!! ***");
	        }
	      }
	      while (true) {
	        String line = is.readLine();
	        if (line.startsWith("/quit")) {
	          break;
	        }
	        for (int i = 0; i < maxClientsCount; i++) {
	          if (threads[i] != null) {
	            threads[i].os.println("<" + name + "&gr; " + line);
	          }
	        }
	      }
	      for (int i = 0; i < maxClientsCount; i++) {
	        if (threads[i] != null && threads[i] != this) {
	          threads[i].os.println("*** The user " + name
	              + " is leaving the chat room !!! ***");
	        }
	      }
	      os.println("*** Bye " + name + " ***");


	      for (int i = 0; i < maxClientsCount; i++) {
	        if (threads[i] == this) {
	          threads[i] = null;
	        }
	      }
	      is.close();
	      os.close();
	      clientSocket.close();
	    } catch (IOException e) {
	    }
	  }
	}



