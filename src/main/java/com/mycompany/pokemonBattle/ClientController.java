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
	private SequentialSpace threadedComs;
	
	//from fightHandler:
	private Pokemon myPokemon, enemyPokemon;
    private Profile me, enemy;
    public RemoteSpace actions,data;
    public String action;
    public Profile user;
    public String URI;
    
    private boolean fighting = true;
	
	public ClientController(SequentialSpace threadedComs) {
		this.threadedComs = threadedComs;
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
				System.out.println("Waiting for connection credentials from threadedComs...");
				Object[] credentials = threadedComs.get(new FormalField(String.class), new FormalField(String.class), new FormalField(String.class));
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
						threadedComs.put(request + "_ACK", "OK");
						//joining room
						String serverControllerURI = "tcp://"+ Config.serverHost +"/handlers/"+username+"?keep";
						RemoteSpace serverController = new RemoteSpace(serverControllerURI);
						status = "REGISTERED AS " + username + " - CONNECTED TO PERSONNAL HANDLER";
						System.out.println("Connected to personnal handler");
						
						System.out.println("Waiting for data reception...");
						String t = (String)serverController.get(new ActualField("CLIENT"), new FormalField(String.class))[1];
						System.out.println("Received data : " + t);
						threadedComs.put("PROFILE", t);
						Profile profile = Profile.fromJson(t);

						// Keep sending whatever the user types
						Boolean registered = true;
						while(registered) {
							System.out.println(status);
							System.out.println("Waiting for an action (MEMBERS, POKEMONS, ITEMS, FIGHT, DISCONNECT) from threadedComs...");
							String action = (String) threadedComs.get(new FormalField(String.class))[0];
							if(action.equals("MEMBERS") || action.equals("FIGHT") || action.equals("DISCONNECT") || action.equals("POKEMONS") || action.equals("ITEMS")) {
								serverController.put("SERVER", action);
								switch(action){
									case "MEMBERS":
										String connectedMembers_string = (String)serverController.get(new ActualField("CLIENT"), new FormalField(String.class))[1];
										threadedComs.put("MEMBERS_ACK", connectedMembers_string);
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
											threadedComs.put("DISCONNECT_ACK", "OK");
											registered = false;
											System.out.println("Succesfully disconnected !");
											status = "CONNECTED TO LOBBY";
										} else {
											System.out.println("ERROR : " + action + " FAILED - response : " + response);
											threadedComs.put("DISCONNECT_ACK", response);
										}
										break;
								}
							} else {
								System.out.println("Action Forbidden.");
							}
						}
					} else {
						System.out.println("Registration failed : " + resp);
						threadedComs.put(request + "_ACK", "ERROR");
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
            actions = new RemoteSpace("tcp://" + Config.fightsHost + "/" + URI + "/actions?keep");
            data = new RemoteSpace("tcp://" + Config.fightsHost + "/" + URI + "/data?keep");
            System.out.println("Connected to data and actions spaces");

            //BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Waiting for opponent data reception...");
            String e = (String) actions.get(new ActualField(user.getUsername()), new FormalField(String.class))[1];
            System.out.println("Received data : " + e);
            enemy = Profile.fromJson(e);
            me = user;


            while (fighting) {
            	System.out.println("Waiting for server response signal");
                Object[] serverResponse = actions.get(new ActualField(me.getUsername()),new FormalField(String.class));
                System.out.println("Got from server: "+(String)serverResponse[1]);
                switch((String) serverResponse[1]){
                    case "GO":
                    	//waiting for ability from controller
                    	threadedComs.put("Excuse me sire, it is their turn");
                    	Object[] temp = threadedComs.get(new ActualField("Outgoing"), new FormalField(String.class));
                    	String ability = (String) temp[1];
                        actions.put(me.getUsername(),"ABILITY",ability);
                        System.out.println("Send to server: "+ability);
                        break;
                    case "DC":
                        fighting = false;
                        System.out.println("Opponent disconnected");
                        break;
                    case "END":
                        System.out.println("Match has ended");
                        break;
                }

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