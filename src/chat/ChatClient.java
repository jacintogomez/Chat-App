package chat;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.BorderLayout;

import javax.swing.*;


public class ChatClient extends JFrame implements Runnable {

	private static int WIDTH = 400;
	private static int HEIGHT = 300;
	JTextArea textarea=null;
	Socket socket=null;
	JTextField textfield=null;
	DataOutputStream toServer = null;
	DataInputStream fromServer = null;
	
	public ChatClient() {
		super("Chat Client");
		this.setSize(ChatClient.WIDTH, ChatClient.HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createMenu();
		this.setVisible(true);
		Thread t=new Thread(this);
		t.start();
	}
	
	public void startserver() {
		Thread t=new Thread(this);
		t.start();
	}
	
	public void createMenu() {
		JMenuBar menubar=new JMenuBar();
		JPanel controlpanel=new JPanel();
		textfield=new JTextField(5);
		textarea=new JTextArea(30,30);
		JMenu menu=new JMenu("File");
		JMenuItem exititem=new JMenuItem("Exit");
		exititem.addActionListener((e)->System.exit(0));
		JMenuItem conn=new JMenuItem("Connect");
		JButton openconnection=new JButton("Connect");
		JButton closeconnection=new JButton("Disconnect");
		controlpanel.add(openconnection);
		controlpanel.add(closeconnection);
		textfield.addActionListener(new TextFieldListener());
		conn.addActionListener(new openconnectionlistener());
		openconnection.addActionListener(new openconnectionlistener());
		closeconnection.addActionListener((e) -> { try { socket.close(); textarea.append("connection closed");} catch (Exception e1) {System.err.println("error"); }});
		menu.add(exititem);
		menu.add(conn);
		menubar.add(menu);
		this.setJMenuBar(menubar);
		this.add(textarea,BorderLayout.CENTER);
		this.add(controlpanel,BorderLayout.NORTH);
		this.add(textfield,BorderLayout.SOUTH);
	}
	
	class openconnectionlistener implements ActionListener{
		//startserver();
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			try {
				socket = new Socket("localhost", 9898);
				textarea.append("connected"+'\n');
				textarea.repaint();
				fromServer = new DataInputStream(socket.getInputStream());
	            toServer = new DataOutputStream(socket.getOutputStream());
	            toServer.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				textarea.append("connection Failure");
			}
		}
	}
	
	class TextFieldListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
				try {
			      // Create an input stream to receive data from the server
//			      fromServer = new DataInputStream(socket.getInputStream());
//
//			      // Create an output stream to send data to the server
//			      toServer = new DataOutputStream(socket.getOutputStream());
			      toServer.flush();
			    }
			    catch (IOException ex) {
			      textarea.append(ex.toString() + '\n');
			    }
		    
		    try {
		    	String message=textfield.getText().trim();
		    	//textarea.append("Me: "+message+'\n');
		        toServer.writeUTF(message);
		       
		        toServer.flush();
		        textfield.setText("");
		        
		        //socket.close();
		      }
		      catch (IOException ex) {
		        System.err.println(ex);
		      }	
		}
	  }
	
	public void run() {
		boolean connected=false;
		boolean cs=true;
		while(!connected) {
			try {
				socket=new Socket("localhost",9898);
				toServer=new DataOutputStream(socket.getOutputStream());
				fromServer=new DataInputStream(socket.getInputStream());
				connected=true;
				textarea.append("Connected"+'\n');
			}catch (IOException e) {
	            textarea.append("Connection to server failed. Retrying..." + '\n');
	            try {
	                Thread.sleep(1000); // Wait for 1 second before retrying
	            } catch (InterruptedException ex) {
	                ex.printStackTrace();
	            }
	        }
		}
		while(cs) {
			String msg="no message yet";
			try {
				msg = fromServer.readUTF();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(msg!=null) {
				System.out.println(msg);
				textarea.append(msg+'\n');
			}
		}
	}

	
	public static void main(String[] args) {
		ChatClient chatClient = new ChatClient();
	}
}