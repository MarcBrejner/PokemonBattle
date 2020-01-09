import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;


public class Server {
	
	public static void main(String[] args) {
		try {
		
			SequentialSpace lobby = new SequentialSpace();
			Hashtable<String, chatRoom> rooms = new Hashtable<String, chatRoom>();
			SpaceRepository repository = new SpaceRepository();
			
			repository.add("lobby", lobby);
			
			repository.addGate("tcp://127.0.0.1:9001/?keep");
			
			String requestedChatRoomName;
			String username;
			
			Object[] lobbyRequest;
			chatRoom requestedChatRoom;
			while (true) {
				//lobby
				System.out.println("Waiting for requests");
				lobbyRequest = lobby.get(new ActualField("connect"), new FormalField(String.class), new FormalField(String.class));

				System.out.println("Got a connect request");
				requestedChatRoomName = (String) lobbyRequest[1];
				username = (String) lobbyRequest[2];
				
				if (rooms.containsKey(requestedChatRoomName)) {
					//rooms exists
					requestedChatRoom = rooms.get(requestedChatRoomName);
				} else {
					//create room
					requestedChatRoom = new chatRoom(requestedChatRoomName);
					rooms.put(requestedChatRoomName, requestedChatRoom);
					repository.add(requestedChatRoomName, requestedChatRoom.chat);
					new Thread(requestedChatRoom).start();
				}
				requestedChatRoom.members.add(username);
				lobby.put(username);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class chatRoom implements Runnable {
	public String name;
	public SequentialSpace chat;
	public ArrayList<String> members = new ArrayList<String>();
	
	public chatRoom(String name) {
		this.name = name;
		this.chat = new SequentialSpace();
	}

	@Override
	public void run() {
		// Keep reading chat messages and printing them 
		while (true) {
			Object[] t; //message
			try {
				System.out.println("chatroom "+name+" is running.");
				t = chat.get(new FormalField(String.class), new FormalField(String.class));
				System.out.println(name + ":: " + t[0] + ": " + t[1]);
				for (String member : members) {
					if (!member.equals((String) t[0])) {
						chat.put(t[0],t[1],member);
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}	
}
