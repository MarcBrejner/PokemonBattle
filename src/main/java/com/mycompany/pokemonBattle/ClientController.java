package com.mycompany.pokemonBattle;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ClientController implements Runnable {
	
	public String username, password, status;
	private SequentialSpace mainController;
	
	public ClientController(SequentialSpace mainController) {
		this.mainController = mainController;
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
				System.out.println("Waiting for connection credentials from mainController...");
				Object[] credentials = mainController.get(new FormalField(String.class), new FormalField(String.class), new FormalField(String.class));
				String request = (String)credentials[0];
				username = (String)credentials[1];
				password = (String)credentials[2];
				
				//authenticate or sign-up and join room
				System.out.println("Registering to the server...");
				lobby.put(request,username,password);
				
				//awaiting acknowledgement
				try {
					String resp;
					if(request.equals("CONNECT")) {
						resp = (String)lobby.get(new ActualField(username), new FormalField(String.class))[1];
					} else {
						resp = (String)lobby.get(new ActualField("SIGNUP"), new ActualField(username), new FormalField(String.class))[2];
					}
					if (resp.equals("OK")) {
						System.out.println("Authentication successful");
						mainController.put(request + "_ACK", "OK");
						//joining room
						String serverControllerURI = "tcp://"+ Config.serverHost +"/handlers/"+username+"?keep";
						RemoteSpace serverController = new RemoteSpace(serverControllerURI);
						status = "REGISTERED AS " + username + " - CONNECTED TO PERSONNAL HANDLER";
						System.out.println("Connected to personnal handler");
						
						System.out.println("Waiting for data reception...");
						String t = (String)serverController.get(new ActualField("CLIENT"), new FormalField(String.class))[1];
						System.out.println("Received data : " + t);
						mainController.put("PROFILE", t);
						Profile profile = Profile.fromJson(t);

						// Keep sending whatever the user types
						Boolean registered = true;
						while(registered) {
							System.out.println(status);
							System.out.println("Waiting for an action (MEMBERS, POKEMONS, ITEMS, FIGHT, DISCONNECT) from mainController...");
							String action = (String) mainController.get(new FormalField(String.class))[0];
							if(action.equals("MEMBERS") || action.equals("FIGHT") || action.equals("DISCONNECT") || action.equals("POKEMONS") || action.equals("ITEMS")) {
								serverController.put("SERVER", action);
								switch(action){
									case "MEMBERS":
										String connectedMembers_string = (String)serverController.get(new ActualField("CLIENT"), new FormalField(String.class))[1];
										mainController.put("MEMBERS_ACK", connectedMembers_string);
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
											mainController.put("DISCONNECT_ACK", "OK");
											registered = false;
											System.out.println("Succesfully disconnected !");
											status = "CONNECTED TO LOBBY";
										} else {
											System.out.println("ERROR : " + action + " FAILED - response : " + response);
											mainController.put("DISCONNECT_ACK", response);
										}
										break;
								}
							} else {
								System.out.println("Action Forbidden.");
							}
						}
					} else {
						System.out.println("Registration failed : " + resp);
						mainController.put(request + "_ACK", "ERROR");
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

	public void fightHandler(String URI, Profile user) {
		
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
			mainController.put("FIGHT_ACK");
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