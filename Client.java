package client;


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.*;
import java.net.*;
import java.util.Optional;
import java.util.regex.Pattern;

import javafx.*;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Client extends Application{
	public String ERROR_MESSAGE="";
	 BufferedReader in;
	 PrintWriter out;
	 Socket client;
	 String serverAddM;
	 int serverPortM;
	 String newusername;
	 int countLoop=0;
	 int userCount=0;
	 boolean flag;
	 boolean flagForThread=true;
	 String CHAT_AREA_MESSAGE="Hello! Please enter the appropriate Server \naddress of the Server that you want to connect. \nAlso enter a Unique username.\n"
	 		+ "Please make sure that you enter a UNIQUE username!";
	 //GUI
	 	TextField serverAddress,serverPort,chatMessage,username;
	 	Button connect,clear,send;
		TextArea chatArea;
		Tooltip Cleartip,connectTip,sendTip,userNotAvailable;
		Thread t;
	public Client() throws IOException
	{
		
		
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane border = new BorderPane();
		
		GridPane gridpane = new GridPane();
        gridpane.setAlignment(Pos.CENTER);
        gridpane.setHgap(10);
        gridpane.setVgap(10);
        gridpane.setPadding(new Insets(25, 25, 25, 25));
		
		serverAddress=new TextField();
		serverPort=new TextField("4556");
		serverPort.setEditable(false);
		chatMessage=new TextField();
		chatMessage.setEditable(false);
		username=new TextField();
		
		addTxtHandler();
		
		chatArea=new TextArea();
		chatMessage.setPrefWidth(467);
		//chatMessage.setEditable(false);
		connect=new Button("Connect");
		clear=new Button("Clear");
		send=new Button("Send");
		chatArea.setEditable(false);
		chatArea.setText(CHAT_AREA_MESSAGE);
		//Tooltips
		
		Cleartip=new Tooltip("Clears the chat window");
		connectTip=new Tooltip("Connects to Server");
		sendTip=new Tooltip("Send Message");
		clear.setTooltip(Cleartip);
		connect.setTooltip(connectTip);
		send.setTooltip(sendTip);
		
		//gridpane.add(new Label("Server Address: "),0,1);
		//gridpane.add(serverAddress, 1, 1);
		
		HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.TOP_LEFT);
        hbBtn.getChildren().add(new Label("Server Address: "));
        hbBtn.getChildren().add(serverAddress);
        gridpane.add(hbBtn, 0, 1);
        
        HBox hbport = new HBox(10);
        hbport.setAlignment(Pos.TOP_CENTER);
        hbport.getChildren().add(new Label("Port: "));
        hbport.getChildren().add(serverPort);
        gridpane.add(hbport, 1, 1);
        
        HBox hbsecondRow = new HBox(10);
        hbsecondRow.setAlignment(Pos.TOP_CENTER);
        hbsecondRow.getChildren().add(new Label("Username:         "));
        hbsecondRow.getChildren().add(username);
        gridpane.add(hbsecondRow, 0, 2);
        
        HBox hbbuttons = new HBox(10);
        hbbuttons.setAlignment(Pos.TOP_CENTER);
        hbbuttons.getChildren().add(connect);
        hbbuttons.getChildren().add(clear);
        gridpane.add(hbbuttons, 1, 2);
        
        HBox borderHBOXT = new HBox(10);
        borderHBOXT.setPadding(new Insets(15, 12, 15, 12));
        borderHBOXT.getChildren().add(gridpane);
        border.setTop(borderHBOXT);
        
        HBox borderHBOXC = new HBox(10);
        borderHBOXC.setPadding(new Insets(15, 12, 15, 12));
        borderHBOXC.getChildren().add(chatArea);
        border.setCenter(borderHBOXC);
        
        HBox borderHBOXB = new HBox(10);
        borderHBOXB.setPadding(new Insets(15, 12, 15, 12));
        borderHBOXB.getChildren().add(chatMessage);
        borderHBOXB.getChildren().add(send);
        border.setBottom(borderHBOXB);
        
        Scene scene =new Scene(border,560,500); //Create Scene
		primaryStage.setTitle("Chat Application");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setResizable(false);
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
               t.interrupt();
               System.exit(0);
            }
        });
		
		//Button operations
		clear.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				chatArea.setText("");
			}
			
		});
		
		connect.setOnAction(new EventHandler<ActionEvent>(){

			@SuppressWarnings("deprecation")
			@Override
			public void handle(ActionEvent event) {
				if(validateIPAndPort(serverAddress.getText(), serverPort.getText()))
				{
					if(ValidateUsername(username.getText()))
					{
						
						serverAddM=serverAddress.getText();
						serverPortM=Integer.parseInt(serverPort.getText());
						newusername=username.getText();
						try {
							if(hostAvailabilityCheck(serverAddM, serverPortM))
							{
								if(flagForThread)
								{
								client=new Socket(serverAddress.getText(), Integer.parseInt(serverPort.getText()));
								chatArea.setText("You are connected to the server successfully.\n");
								t.start();
								flagForThread=false;
								}
								else
								{
									flag=true;
									t.resume();
									out.println(username.getText());
								}
								 connect.setDisable(true);
							}
							else
							{
								ERROR_MESSAGE="Could not Find the Server! \n"
										+ "Please check the Server Address";
								showError();
							}
						} catch (NumberFormatException e) {
							ERROR_MESSAGE="Could not Find the Server! \n"
									+ "Please check the Server Address";
							showError();
						} catch (UnknownHostException e) {
							
							ERROR_MESSAGE="Could not Find the Server! \n"
									+ "Please check the Server Address";
							showError();
						} catch (IOException e) {
							ERROR_MESSAGE="Could not Find the Server! \n"
									+ "Please check the Server Address";
							showError();
						}
					}
					else
					{
						ERROR_MESSAGE="Invalid username";
						showError();
					}
				}
				else
				{
					showError();
				}
				
			}
			
		});
		
		
		//Send button
		send.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				
					if(!(client==null))
					{
						if(!client.isClosed())
						{
							if(!chatMessage.getText().isEmpty())
								{
									out.println(chatMessage.getText());
									chatMessage.setText("");
								}
							
						}
						else
						{
							ERROR_MESSAGE="You are not yet connected to the Server. "
									+ "First connect to the server to send message.";
							showError();
						}
					}
					else
					{
						ERROR_MESSAGE="You are not yet connected to the Server. "
								+ "First connect to the server to send message.";
						showError();
					}
					
				
			}
				
			
		});
		//Thread work!! DANGER!!
		
		// separate non-FX thread
         t=new Thread() {

            // runnable for that thread
            public void run() {
            	try {
					//send Message
					in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				    out = new PrintWriter(client.getOutputStream(), true);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
            	flag=true;
                while(true)
                {
                	 String line;
					try {
						line = in.readLine();
						
						if (line.startsWith("USERNAME")) {
							if(flag)
							{
								out.println(username.getText());
								flag=false;
							}
							else
							{
								chatArea.appendText(username.getText()+" is already in use! Please try a different username.\n");
								connect.setDisable(false);
								t.suspend();
								
							}
						} else if (line.startsWith("SUCCESS")) {
	                    	 chatArea.appendText("Welcome! Your Username is accepted by the Server."
	                    	 		+ "\nStart typing...\n");
	                    	 chatMessage.setEditable(true);
	                     } else if (line.startsWith("COM")) {
	                         chatArea.appendText(line.substring(3) + "\n");
	                     }
					} catch (IOException e) {
						
						e.printStackTrace();
					}
                     
                }
            }
        };
		
	}
	public boolean hostAvailabilityCheck(String serv, int port) { 
	    try (Socket s = new Socket(serv, port)) {
	    	
	        return true;
	    } catch (IOException ex) {
	        /* ignore */
	    }
	    return false;
	}
	//Send message when u press enter
	public void addTxtHandler() {
		chatMessage.addEventHandler(KeyEvent.KEY_PRESSED,
	            new EventHandler<KeyEvent>() {

	               @Override
	               public void handle(KeyEvent e) {
	                  if (e.getCode() == KeyCode.ENTER) {
	                	  if(!chatMessage.getText().equals(""))
	                	  {
		                     out.println(chatMessage.getText());
		                     chatMessage.setText("");
	                	  }
	                  }
	               }
	            });
	   }
	public boolean validateIPAndPort(String serverAddress, String port)
	{
		final Pattern PATTERN = Pattern.compile(
		        "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

		if(serverAddress.equals("") || port.equals(""))
		{
			ERROR_MESSAGE="Server Address or Port cannot be empty!";
			return false;
		}
		else if(!PATTERN.matcher(serverAddress).matches() || !validatePort(port))
		{
			ERROR_MESSAGE="You have entered invalid Server Address or Port number!";
			return false;
		}
		return true;
	}
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
		return true;
	}
	public void showError()
	{
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error Dialog");
		alert.setHeaderText("ERROR!");
		alert.setContentText(ERROR_MESSAGE);
		alert.showAndWait();
	}
	public boolean ValidateUsername(String usr)
	{
		if(usr.equals(""))
			return false;
		return true;
	}
	
	
	public static void main(String [] args) throws IOException
	{
		Application.launch(args);
	}

	
	
}
