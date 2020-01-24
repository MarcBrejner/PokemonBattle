package com.mycompany.pokemonBattle;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import java.io.IOException;
import java.net.UnknownHostException;

public class ClientController implements Runnable {
	
	public String username, password, status;
	private SequentialSpace mainController;
	
	//from fightHandler:
	private Pokemon myPokemon, enemyPokemon;
    private Profile me, enemy;
    public RemoteSpace actions,data;
    public String action;
    public Profile user;
    public String URI;
    
    private boolean fighting = false;
	
	public ClientController(SequentialSpace mainController) {
		this.mainController = mainController;
	}
	
	public void run() {

		try {
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
				Object[] credentials = mainController.get(new FormalField(String.class), new FormalField(String.class), new FormalField(String.class));
				String request = (String)credentials[0];
				username = (String)credentials[1];
				password = (String)credentials[2];
				
				//authenticate or sign-up and join room
				System.out.println("Registering to the server...");
				lobby.put(request,username,password);
				
				//awaiting acknowledgement
				try {
					String resp = (String)lobby.get(new ActualField(username), new FormalField(String.class))[1];
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
						Profile profile = null;
						if(t.equals("INITIAL")) {
							try {
								mainController.put("PROFILE", "INITIAL");
								while(true) {
									String pokemon_name = (String) mainController.get(new ActualField("INITIAL"), new FormalField(String.class))[1];
									if (pokemon_name.equals("Bulbasaur") || pokemon_name.equals("Charmander") || pokemon_name.equals("Squirtle")) {
										serverController.put("SERVER", "INITIAL", pokemon_name);
										String initial_resp = (String)serverController.get(new ActualField("CLIENT"), new FormalField(String.class))[1];
										if(initial_resp.equals("OK")) {
											String updated_profile_string = (String)serverController.get(new ActualField("CLIENT"), new FormalField(String.class))[1];
											profile = Profile.fromJson(updated_profile_string);
											mainController.put("INITIAL_ACK", updated_profile_string);
											break;
										} else {
											mainController.put("INITIAL_ACK", initial_resp);
										}
									} else {
										mainController.put("INITIAL_ACK", "Forbidden");
									}
								}
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	
						} else {
							mainController.put("PROFILE", t);
							profile = Profile.fromJson(t);
						}
						
						// Keep sending whatever the user types
						Boolean registered = true;
						while(registered) {
							System.out.println(status);
							System.out.println("Waiting for an action (MEMBERS, POKEMONS, ITEMS, FIGHT, DISCONNECT) from mainController...");
							String action = (String) mainController.get(new FormalField(String.class))[0];
							if (action.equals("GET_PROFILE")
								|| action.equals("MEMBERS")
								|| action.equals("FIGHT")
								|| action.equals("DISCONNECT")
								|| action.equals("USER_LEVEL_UP")
								|| action.equals("POKEMON_LEVEL_UP")
							){
								serverController.put("SERVER", action);
								String response;
								switch(action){
									case "GET_PROFILE":
										String profile_string = (String)serverController.get(new ActualField("CLIENT"), new FormalField(String.class))[1];
										mainController.put("GET_PROFILE_ACK", profile_string);
										profile = Profile.fromJson(profile_string);
										break;
										
									case "MEMBERS":
										String connectedMembers_string = (String)serverController.get(new ActualField("CLIENT"), new FormalField(String.class))[1];
										mainController.put("MEMBERS_ACK", connectedMembers_string);
										break;

									case "FIGHT":
										System.out.println("Looking for an opponent...");
										String fightURI = (String) serverController.get(new ActualField("CLIENT"), new FormalField(String.class))[1];
										mainController.put("GOTFIGHT");
										System.out.println("Got a fight ! At " + fightURI);
										fightHandler(fightURI,profile);
										System.out.println("Fight has ended !");
										break;
										
									case "USER_LEVEL_UP":
										response = (String)serverController.get(new ActualField("CLIENT"), new FormalField(String.class))[1];
										if (response.equals("OK")) {
											mainController.put("USER_LEVEL_UP_ACK", "OK");
											Object[] elem = serverController.get(new ActualField("CLIENT"), new FormalField(String.class), new FormalField(String.class));
											mainController.put("USER_LEVEL_UP_ACK", elem[1], elem[2]);
										} else {
											mainController.put("USER_LEVEL_UP_ACK", response);
										}
										break;
										
									case "POKEMON_LEVEL_UP":
										response = (String)serverController.get(new ActualField("CLIENT"), new FormalField(String.class))[1];
										if (response.equals("OK")) {
											mainController.put("POKEMON_LEVEL_UP_ACK", "OK");
											Object[] obj = serverController.get(new ActualField("CLIENT"), new FormalField(Integer.class), new FormalField(String.class), new FormalField(String.class));
											mainController.put("POKEMON_LEVEL_UP_ACK", (int)obj[1], (String)obj[2], (String)obj[3]);
										} else {
											mainController.put("POKEMON_LEVEL_UP_ACK", response);
										}
										break;

									case "DISCONNECT":
										response = (String) serverController.get(new ActualField("CLIENT"), new FormalField(String.class))[1];
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
								System.out.println("Action : " + action + " Forbidden.");
								mainController.put(action + "_ACK", "Forbidden");
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
			fighting = true;

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

            String myPokemon = (String) mainController.get(new ActualField("choosen pokemon"),new FormalField(String.class))[1];
            System.out.println("Client got the pokemon: "+myPokemon);
            data.put(me.getUsername()+" to server",myPokemon);

            retreivePokemons();
            
            mainController.put("got pokemon");
            
            while (fighting) {

            	System.out.println("Waiting for server response signal");
                Object[] serverResponse = actions.get(new ActualField(me.getUsername()),new FormalField(String.class));
                
                
                if(!fighting){
                    break;
                }

                //System.out.println("Got from server: "+(String)serverResponse[1]);
                switch((String) serverResponse[1]){
                    case "GO":
						System.out.println("Your Turn");
                    	//waiting for ability from controller
						mainController.put("GO");
                    	Object[] temp = mainController.get(new FormalField(String.class), new FormalField(String.class));

                    	String actionType = temp[0].equals("ABILITY") ? "ABILITY":"ITEM";

                        actions.put(me.getUsername(),actionType,temp[1]);

                        break;
                    case "UPDATE":
                    	getNewestAction();
        				retreivePokemons();
        				actions.put("SERVER", me.getUsername(), "UPDATE_ACK");
                    	break;
                    case "DC":
                        fighting = false;
                        System.out.println("Opponent disconnected");
                        break;
                    case "WINNER":
                    	fighting = false;
                    	mainController.put("WINNER");
                    	System.out.println("You won !");
                    	mainController.get(new ActualField("WINNER_ACK"));
                        break;
                    case "LOSER":
                    	fighting = false;
                    	mainController.put("LOSER");
                    	System.out.println("You lost...");
                    	mainController.get(new ActualField("LOSER_ACK"));
                    	break;
                    case "DRAW":
                    	fighting = false;
                    	mainController.put("DRAW");
                    	System.out.println("It's a draw !");
                    	mainController.get(new ActualField("DRAW_ACK"));
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

	public void retreivePokemons() throws InterruptedException {
		System.out.println("Retrieving Pokemons...");
		String temp = (String) data.query(new ActualField(me.getUsername()),new FormalField(String.class))[1];
		System.out.println("MOEWMOEWMOEW "+temp);
		myPokemon = Pokemon.fromJson(temp);
		enemyPokemon = Pokemon.fromJson((String) data.query(new ActualField(enemy.getUsername()),new FormalField(String.class))[1]);

		System.out.println("I have pokemon: "+myPokemon.getName()+" with HP "+myPokemon.getHP());
		System.out.println("My opponent has pokemon: "+enemyPokemon.getName()+" with HP "+enemyPokemon.getHP());

		GameElements.pokemon1 = myPokemon;
		GameElements.pokemon2 = enemyPokemon;

	}

	public void getNewestAction() throws InterruptedException{
		System.out.println("Retrieving action...");
		Object t[] = data.query(new FormalField(String.class),new FormalField(String.class),new FormalField(String.class));
		if(t != null){
			String aType = t[1].equals("ABILITY") ? "ability":"item";
			System.out.println(t[0]+" used the "+aType+" "+t[2]);
			mainController.put("last ability",2,(String) t[2]);
		}
	}

}