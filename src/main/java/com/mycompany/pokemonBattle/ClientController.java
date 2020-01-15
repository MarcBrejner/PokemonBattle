package com.mycompany.pokemonBattle;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ClientController implements Runnable {
	
	public static String username, password, status;
	
	public ClientController(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public void run() {

		try {
			
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

			// Set the URI of the chat space
			
			// Default value
			String lobbyUri = "tcp://"+ Config.serverHost +"/lobby?keep";
			
			// Connect to the remote chat space 
			System.out.println("Connecting to server lobby " + lobbyUri + "...");
			RemoteSpace lobby = new RemoteSpace(lobbyUri);
			status = "CONNECTED TO LOBBY";

			Boolean connected = true;
			while(connected) {
				// Read user name from the console			
				//System.out.print("Enter your username: ");
				//username = input.readLine();
				
				//System.out.print("Enter your password: ");
				//password = input.readLine();
				
				//join room
				System.out.println("Registering to the server...");
				lobby.put("connect",username,password);
				
				//awaiting acknowledgement
				try {
					String resp = (String)lobby.get(new ActualField(username), new FormalField(String.class))[1];
					if (resp.equals("OK")) {
						System.out.println("Registration done");
						//joining room
						String serverControllerURI = "tcp://"+ Config.serverHost +"/handlers/"+username+"?keep";
						RemoteSpace serverController = new RemoteSpace(serverControllerURI);
						status = "REGISTERED AS " + username + " - CONNECTED TO PERSONNAL HANDLER";
						System.out.println("Connected to personnal handler");
						
						System.out.println("Waiting for data reception...");
						String t = (String)serverController.get(new ActualField("CLIENT"), new FormalField(String.class))[1];
						System.out.println("Received data : " + t);
						Profile profile = Profile.fromJson(t);

						// Keep sending whatever the user types
						Boolean registered = true;
						while(registered) {
							System.out.println(status);
							System.out.println("Give an action (MEMBERS, POKEMONS, ITEMS, FIGHT, DISCONNECT):");
							String action = input.readLine();
							if(action.equals("MEMBERS") || action.equals("FIGHT") || action.equals("DISCONNECT") || action.equals("POKEMONS") || action.equals("ITEMS")) {
								serverController.put("SERVER", action);
								switch(action){
									case "MEMBERS":
										//String[] connectedMembers = (String[])serverController.get(new FormalField(Object.class))[0];
										int numConnectedMembers = (int) serverController.get(new ActualField("CLIENT"), new FormalField(Integer.class))[1];
										System.out.println("List of connected members : ");
										String member;
										for(int i = 0; i < numConnectedMembers; i++) {
											member = (String) serverController.get(new ActualField("CLIENT"), new FormalField(String.class))[1];
											System.out.println("- " + member);
										}
										/* for(String elem : connectedMembers) {
											System.out.println("- " + elem);
										} */
										break;
										
									case "POKEMONS":
										System.out.println("List of your Pokemons :");
										for(Pokemon pokemon : profile.getPokemons()) {
											System.out.println("- Pokemon " + pokemon.getName() + " has abilities :");
											for(Ability ability : pokemon.getAbilities()) {
												System.out.println("	- " + ability.getName() + " (" + ability.getElement() + ")");
											}
										}
										break;
										
									case "ITEMS":
										System.out.println("List of your items :");
										for(Item item : profile.getItems()) {
											System.out.println("- Item " + item.getName() + " (x" + item.getNumber() + ")");
										}
										break;

									case "FIGHT":
										System.out.println("Looking for an opponent...");
										String fightURI = (String)serverController.get(new ActualField("CLIENT"), new FormalField(String.class))[1];
										System.out.println("Got a fight ! At " + fightURI);
										fightHandler(fightURI, profile);
										System.out.println("Fight has ended !");
										break;

									case "DISCONNECT":
										String response = (String) serverController.get(new ActualField("CLIENT"), new FormalField(String.class))[1];
										if (response.equals("OK")) {
											serverController.put("SERVER", "OK_ACK");
											registered = false;
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
						System.out.println("Registration failed : " + resp);
					}
				} catch (InterruptedException e) {
					System.out.println("Ignored?");
					e.printStackTrace();
				}
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static void fightHandler(String URI, Profile user) {
		
		try {
			// connecting to action and data spaces
			System.out.println("Connection to tcp://" + Config.fightsHost + "/" + URI + "/actions?keep...");
			System.out.println("Connection to tcp://" + Config.fightsHost + "/" + URI + "/data?keep...");
			RemoteSpace actions = new RemoteSpace("tcp://" + Config.fightsHost + "/" + URI + "/actions?keep");
			RemoteSpace data = new RemoteSpace("tcp://" + Config.fightsHost + "/" + URI + "/data?keep");
			System.out.println("Connected to data and actions spaces");
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Waiting for opponent data reception...");
			String e = (String)actions.get(new ActualField(user.getUsername()), new FormalField(String.class))[1];
			System.out.println("Received data : " + e);
			Profile enemy = Profile.fromJson(e);
			String action;
			while(true) {
				System.out.println("Waiting for action input...");
				action = (String) actions.get(new FormalField(String.class))[0];
				System.out.println("ACTION RECEIVED : " + action);
				if(action.equals("BYE")) {
					actions.put("BYE_ACK");
					actions.put("END");
					break;
				} else if(action.equals("START")) {
					System.out.println("THE FIGHT CAN START !");
				} else {
					// depending on the action different types of data and behaviours can occur
					System.out.println("Waiting to receive data...");
					String data_received = (String)data.get(new FormalField(String.class))[0];
					System.out.println("DATA : " + data_received);
				}
				System.out.println("Type an action to make :");
				String action_input = input.readLine();
				actions.put(action_input);
				if(action_input.equals("BYE")){
					System.out.println("Waiting for acknowledgment of BYE...");
					actions.get(new ActualField("BYE_ACK"));
					break;
				}
				System.out.println("Type data to send :");
				String data_input = input.readLine();
				data.put(data_input);
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