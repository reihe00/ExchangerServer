

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class MainServer {
	static ServerSocket welcomeSocket;
	static ArrayList<Socket> allConnections = new ArrayList<Socket>();
	static ArrayList<ChatMessage> allMessages = new ArrayList<ChatMessage>();
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Thread myIO = new ServerIO();
		myIO.start();
		  welcomeSocket = new ServerSocket(21245);

		  while (true) {
			  Thread st = new ServerThread();
			  ((ServerThread)st).connectionSocket=welcomeSocket.accept();
			  allConnections.add(((ServerThread)st).connectionSocket);
			  st.start();
		  
		  }
	}
	
	public static void killServer() {
		try {
			addMessage(new ChatMessage("#disconnect#",""));
			welcomeSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	public static void addMessage(ChatMessage message) {
		allMessages.add(message);
		for(Socket s : allConnections) {
		DataOutputStream outToClient;
		if(s.isConnected()&&!s.isClosed()) {
			try {
				outToClient = new DataOutputStream(s.getOutputStream());
				outToClient.writeBytes(message.toSend());
				if(message.toSend().equalsIgnoreCase("#disconnect#"))s.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		   
		}
	}

}
