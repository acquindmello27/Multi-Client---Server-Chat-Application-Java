package server;

import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.util.HashSet;
import javax.swing.*;

import server.Server;


public class Server extends Thread{
	private static HashSet<String> usernames = new HashSet<String>();
	private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
	//UI
		JFrame jf;
		JPanel upper,mainp;
		JPanel lower;
		static JTextField port;
		static TextArea chatArea;
		JScrollPane scroll;
		JButton clear;
		public Server()
		{
			jf=new JFrame("Server");
			mainp=new JPanel();
			upper=new JPanel(new GridLayout(2, 4));
			lower=new JPanel();
			port=new JTextField("4556");
			port.setEnabled(false);
			chatArea=new TextArea();
			chatArea.setEditable(false);
			
			
			clear=new JButton("Clear");
			upper.add(new JLabel("Port Number: "));
			upper.add(port);
			upper.add(clear);
			
			lower.setSize(100, 100);
			lower.add(chatArea);
			mainp.add(upper);
			mainp.add(lower);
			
			jf.add(mainp);
			jf.setSize(600, 300);
			jf.setLocation(200,200);
			jf.setResizable(false);
			jf.setVisible(true);
			
			
			jf.addWindowListener(new WindowAdapter() {

				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
				});
			clear.addActionListener( new ActionListener()
			{
			    public void actionPerformed(ActionEvent e)
			    {
			        chatArea.setText("");
			    }
			});
			//clear.addActionListener((ActionListener) this);
		}
		//HAndle window closing event
		

	//To handle Clients
	public static class ClientService extends Thread
	{
		String uname;
		Socket socket;
		BufferedReader input;
		PrintWriter output;
		public ClientService(Socket socket) throws IOException
		{
			this.socket=socket;
		}
		public void run()
		{
			try {
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				output = new PrintWriter(socket.getOutputStream(), true);
		         
		         
		         while (true) {
		        	 output.println("USERNAME");
		        	  uname = input.readLine();
	                if (uname == null) {
	                     return;
	                 }
	                 synchronized (usernames) {
	                     if (!usernames.contains(uname)) {
	                    	 usernames.add(uname);
	                    	 chatArea.append(uname+" has joined the conversation\n");
	                         break;
	                     }
	                     else
	                     {
	                    	 chatArea.append("Username conflict - A user trying to use username '"+uname+"' again\n");
	                    	 
	                     }
	                    
	                 }
	             }
		         //Notify Client that the username has been accepted
		         output.println("SUCCESS");
	                writers.add(output);

	                // Accept messages from this client and broadcast them.
	                // Ignore other clients that cannot be broadcasted to.
	                while (true) {
	                    String getmessage = input.readLine();
	                    if(!(getmessage==null))
	                    {
	                    	chatArea.append("Message recieved from "+uname+"\n");
	                    }
	                    else
	                    {
	                    	chatArea.append(uname+" has left the conversation\n");
	                    	return;
	                    }
	                    for (PrintWriter writer : writers) {
	                        writer.println("COM" + uname + ": " + getmessage);
	                    }
	                }
			} catch (IOException e) {
				
			}finally {
				 if (output != null) {
	                    writers.remove(output);
	                }
                if (uname != null) {
                    usernames.remove(uname);
                }
               try {
                    socket.close();
                } catch (IOException e) {
                }
            }
		}
	}
	
	//Validate port
	public boolean validatePort(String port)
	{
		char [] chk=port.toCharArray();
		for(char c : chk)
		{
			if(!Character.isDigit(c))
				return false;
		}
		if(!(Integer.parseInt(port)>=1024) || !(Integer.parseInt(port)<=65535))
		{
			return false;
		}
		if(port.equals(""))
			return false;
		return true;
	}
	
	public static void main(String [] args) throws IOException
	{
		Server n=new Server();
		 try {
	           	ServerSocket listener = new ServerSocket(Integer.parseInt(port.getText()));
	           	chatArea.setText("Welcome! Server has Started..\n");
	               while (true) {
	                   new ClientService(listener.accept()).start();
	               }
	           } catch(IOException ex) {
	              
	           }
		}
}


