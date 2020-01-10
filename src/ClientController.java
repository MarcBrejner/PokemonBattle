import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ClientController {
	
	public static String username, password, status;
	
	public static void main(String[] args) {

		try {
			
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

			// Set the URI of the chat space
			
			// Default value
			String lobbyUri = "tcp://"+ Config.serverHost +"/lobby?keep";
			
			// Connect to the remote chat space 
			System.out.println("Connecting to server lobby " + lobbyUri + "...");
			RemoteSpace lobby = new RemoteSpace(lobbyUri);
			status = "CONNECTED TO LOBBY";

			// Read user name from the console			
			System.out.print("Enter your username: ");
			username = input.readLine();
			
			System.out.print("Enter your password: ");
			password = input.readLine();
			
			//join room
			System.out.println("Registering to the server...");
			lobby.put("connect",username,password);
			
			//awaiting acknowledgement
			try {
				String resp = (String)lobby.get(new ActualField(username), new FormalField(String.class))[1];
				if (resp.equals("OK")) {
					System.out.println("Registration done");
					//joining room
					String serverControllerURI = "tcp://"+ Config.serverHost +"/"+username+"?keep";
					RemoteSpace serverController = new RemoteSpace(serverControllerURI);
					status = "REGISTERED AS " + username + " - CONNECTED TO PERSONNAL HANDLER";
					System.out.println("Connected to personnal handler");

					// Keep sending whatever the user types
					
					while(true) {
						System.out.println(status);
						System.out.println("Give an action (MEMBERS, FIGHT, DISCONNECT):");
						String action = input.readLine();
						if(action.equals("MEMBERS") || action.equals("FIGHT") || action.equals("DISCONNECT")) {
							serverController.put(action);
							switch(action){
								case "MEMBERS":
									// String[] connectedMembers = (String[])serverController.get(new FormalField(Object.class))[0];
									int numConnectedMembers = (int) serverController.get(new FormalField(Integer.class))[0];
									System.out.println("List of connected members : ");
									String member;
									for(int i = 0; i < numConnectedMembers; i++) {
										member = (String) serverController.get(new FormalField(String.class))[0];
										System.out.println("- " + member);
									}
									break;

								case "FIGHT":
									System.out.println("Looking for an opponent...");
									String fightURI = (String)serverController.get(new FormalField(String.class))[0];
									System.out.println("Got a fight ! At " + fightURI);
									fightHandler(fightURI);
									System.out.println("Fight has ended !");
								break;

							case "DISCONNECT":
								String response = (String) serverController.get(new FormalField(String.class))[0];
								if (response.equals("OK")) {
									// TODO disconnect from serverController if possible
									System.out.println("Succesfully disconnected !");
									status = "CONNECTED TO LOBBY";
								} else {
									System.out.println("ERROR : " + action + " FAILED - response : " + response);
								}
								break;
							}
						} else {
							System.out.println("Action Forbidden.");
						}
					}
				} else {
					System.out.println("Registration failed : " + resp + " " + resp.getClass());
				}

			} catch (InterruptedException e) {
				System.out.println("Ignored?");
				e.printStackTrace();
			}

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void fightHandler(String URI) {
		
		try {
			// connecting to action and data spaces
			RemoteSpace actions = new RemoteSpace("tcp://" + Config.fightsHost + "/" + URI + "/actions/?keep");
			RemoteSpace data = new RemoteSpace("tcp://" + Config.fightsHost + "/" + URI + "/data/?keep");
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Connected to data and actions spaces");
			String action;
			while(true) {
				action = (String) actions.get(new FormalField(String.class))[0]; //problem here NullPointerException
				System.out.println("ACTION RECEIVED : " + action);
				if(action.equals("END")) {
					break;
				} else if(action.equals("START")) {
					System.out.println("THE FIGHT CAN START !");
				} else {
					// depending on the action different types of data and behaviours can occur
					Object data_received = data.get(new FormalField(String.class));
					System.out.println("DATA : " + data_received);
				}
				System.out.println("Type an action to make :");
				String action_input = input.readLine();
				actions.put(action_input);
			}

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}