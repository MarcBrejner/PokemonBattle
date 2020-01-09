import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

public class Client {

	
	public static String name;
	public static String chatroomName;
	
	public static void main(String[] args) {

		try {
			
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

			// Set the URI of the chat space
			
			// Default value
			String lobbyUri = "tcp://127.0.0.1:9001/lobby?keep";
			
			// Connect to the remote chat space 
			System.out.println("Connecting to chat space " + lobbyUri + "...");
			RemoteSpace lobby = new RemoteSpace(lobbyUri);

			// Read user name from the console			
			System.out.print("Enter your name: ");
			name = input.readLine();
			
			System.out.print("Enter the desired chatroom: ");
			chatroomName = input.readLine();
			
			//join room
			System.out.println("Joining room...");
			lobby.put("connect",chatroomName,name);
			
			//awaiting acknowledgement
			Object[] t;
			try {
				t = lobby.get(new ActualField(name));
			} catch (InterruptedException e) {
				System.out.println("Ignored?");
				e.printStackTrace();
			}
			
			//joining room
			String chatroomUri = "tcp://127.0.0.1:9001/"+chatroomName+"?keep";
			RemoteSpace chatroom = new RemoteSpace(chatroomUri);
			System.out.println("so far so good");
			//start join listener
			new Thread(new msgListener(chatroomUri)).start();
			
			// Keep sending whatever the user types
			System.out.println("Start chatting...");
			while(true) {
				String message = input.readLine();
				chatroom.put(name, message);
			}


		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class msgListener implements Runnable {
    private RemoteSpace chat;

    public msgListener(String uri) {
        try {
			this.chat = new RemoteSpace(uri);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void run() {
    	Object[] t;
    	while (true) {
			try {
				t = chat.get(new FormalField(String.class), new FormalField(String.class), new ActualField(Client.name));
				if (t == null) {
				} else {
					System.out.println(t[0] + ": " + t[1]);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
}