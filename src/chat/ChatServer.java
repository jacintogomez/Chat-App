package chat;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.*;


public class ChatServer extends JFrame implements Runnable {

	private static int WIDTH = 400;
	private static int HEIGHT = 300;
	public int clientNo=0;
	private JTextArea ta;
	public ArrayList<HandleAClient> clients;
	
	public ChatServer() {
		super("Chat Server");
		this.clients=new ArrayList<HandleAClient> ();
		this.setSize(ChatServer.WIDTH, ChatServer.HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createMenu();
		this.setVisible(true);
		Thread t=new Thread(this);
		//clients.add(t);
		t.start();
	}
	
	private void createMenu() {
		ta=new JTextArea(10,10);
		JScrollPane sp=new JScrollPane(ta);
		this.add(sp);
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener((e) -> System.exit(0));
		menu.add(exitItem);
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
	}

	@Override
	public void run() {
	  try {
        // Create a server socket
        ServerSocket serverSocket = new ServerSocket(9898);
        ta.append("MultiThreadServer started at " 
          + new Date() + '\n');
    
        while (true) {
          // Listen for a new connection request
          Socket socket = serverSocket.accept();
    
          // Increment clientNo
          clientNo++;
          
          ta.append("Client " + clientNo +" has joined - " + new Date() + '\n');

            // Find the client's host name, and IP address
            InetAddress inetAddress = socket.getInetAddress();
            ta.append("With host name "+ inetAddress.getHostName() + " and IP Address "+ inetAddress.getHostAddress() + "\n");
          
          // Create and start a new thread for the connection
          HandleAClient x=new HandleAClient(socket, clientNo);
          clients.add(x);
          new Thread(x).start();
        }
      }
      catch(IOException ex) {
        System.err.println(ex);
      }
	}
	class HandleAClient implements Runnable {
	    private Socket socket; // A connected socket
	    private int clientNum;
	    private DataOutputStream outputToClient; // Declare outputToClient here
	    
	    /** Construct a thread */
	    public HandleAClient(Socket socket, int clientNum) {
	      this.socket = socket;
	      this.clientNum = clientNum;
	      
	      try {
	        // Initialize outputToClient
	        outputToClient = new DataOutputStream(socket.getOutputStream());
	      } catch (IOException ex) {
	        ex.printStackTrace();
	      }
	    }


	    /** Run a thread */
	    public void run() {
	      try {
	        // Create data input and output streams
	        DataInputStream inputFromClient = new DataInputStream(
	          socket.getInputStream());
	        DataOutputStream outputToClient = new DataOutputStream(
	          socket.getOutputStream());

	        // Continuously serve the client
	        while (true) {
	          String msg = inputFromClient.readUTF();
	          //outputToClient.writeUTF(msg+"+-*%"+'\n');
	          for (HandleAClient client : clients) {
                  // skip sending the message back to the sender
                  if(client.clientNum!=this.clientNum) {

                  // send the message to all other clients
	                  client.outputToClient.writeUTF("client " + this.clientNum + ": " + msg);
	                  client.outputToClient.flush();
                  }else {
                	  client.outputToClient.writeUTF("me: "+msg);
                	  client.outputToClient.flush();
                  }
              }
	          ta.append("client " + this.clientNum + ": " +msg + '\n');	
//	          String others=this.clientNum+": "+msg;
//	          outputToClient.writeUTF(others);
//	          outputToClient.flush();
	        }
	      }
	      catch(IOException ex) {
	        ex.printStackTrace();
	      }
	  }
	}
	public static void main(String[] args) {
		ChatServer chatServer = new ChatServer();
	}
}