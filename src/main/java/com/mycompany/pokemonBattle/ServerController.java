package com.mycompany.pokemonBattle;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.jspace.*;

import com.google.gson.Gson;

public class ServerController {

	public static void main(String[] args) {
		try {

			SequentialSpace lobby = new SequentialSpace();
			SequentialSpace connectedMembers = new SequentialSpace();
			SpaceRepository repository = new SpaceRepository();

			repository.add("lobby", lobby);
			repository.add("members", connectedMembers);
			repository.add("fighters", new SequentialSpace());

			repository.addGate("tcp://" + Config.serverHost + "/?keep");
			
			Database database = new Database("pokemonGame", "root", Config.db_password);

			new Thread(new FightCreationHandler(database)).start();

			String username, password;
			Object[] lobbyRequest;
			while (true) {
				System.out.println("Waiting for requests");
				lobbyRequest = lobby.get(new FormalField(String.class), new FormalField(String.class), new FormalField(String.class));
				String request = (String) lobbyRequest[0];
				username = (String) lobbyRequest[1];
				password = (String) lobbyRequest[2];
				
				if(request.equals("CONNECT")) {
					System.out.println("Got a connect request");
					if (database.authenticate(username, password)) {
						if (connectedMembers.queryp(new ActualField(username)) != null) {
							// user already connected
							System.out.println("ERROR : connection on already connected profile");
							lobby.put(username, "Already connected");
						} else {
							// register member and creating personal handler
							connectedMembers.put(username);
							new Thread(new UserHandler(username, repository, database)).start();
							lobby.put(username, "OK");
						}
					} else {
						lobby.put(username, "Forbidden");
					}
				} else if (request.equals("SIGNUP")){
					System.out.println("Got a SignUp request");
					if(database.createUser(username, password)) {
						connectedMembers.put(username);
						new Thread(new UserHandler(username, repository, database)).start();
						lobby.put(username, "OK");
					} else {
						lobby.put(username, "Already connected");
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class UserHandler implements Runnable {
	public Profile profile;
	public SequentialSpace handler;
	public String name, status;
	String membersURI = "tcp://" + Config.serverHost + "/members?keep";
	String fightersURI = "tcp://" + Config.serverHost + "/fighters?keep";
	RemoteSpace members, fighters;
	SpaceRepository handlers;
	Database database;

	public UserHandler(String name, SpaceRepository handlers, Database database) {
		this.database = database;
		this.name = name;
		this.profile = database.getProfile(name);;
		this.handlers = handlers;
		this.handler = new SequentialSpace();
		this.status = "INITIATING";
		try {
			this.handlers.add("handlers/" + profile.getUsername(), this.handler);
			this.members = new RemoteSpace(membersURI);
			this.fighters = new RemoteSpace(fightersURI);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// Keep reading incoming actions
		System.out.println("Handler " + profile.getUsername() + " is running.");
		
		// make sure that the user has at least one pokemon
		if(profile.getPokemons().size() == 0) {
			try {
				handler.put("CLIENT", "INITIAL");
				while(true) {
					String pokemon_name = (String) handler.get(new ActualField("SERVER"), new ActualField("INITIAL"), new FormalField(String.class))[2];
					if (pokemon_name.equals("Bulbasaur") || pokemon_name.equals("Charmander") || pokemon_name.equals("Squirtle")) {
						handler.put("CLIENT", "OK");
						System.out.println("Pokemon selected : " + pokemon_name);
						profile = database.addPokemon(profile, new Pokemon(pokemon_name));
						handler.put("CLIENT", Profile.toJson(profile));
						System.out.println("ADDED POKEMON TO USER " + profile.getUsername());
						break;
					} else {
						System.out.println("ERROR - Forbidden : " + pokemon_name);
						handler.put("CLIENT", "Forbidden");
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Sending profile data to " + name);
			String profile_string = Profile.toJson(profile); 
			try {
				handler.put("CLIENT", profile_string);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}

		Boolean running = true;
		status = "IDLE";
		while (running) {
			String t;
			try {
				t = (String) handler.get(new ActualField("SERVER"), new FormalField(String.class))[1];
				// refresh the profile instance after a fight to get changes
				if(status.equals("FIGHTING")) {
					refreshProfile();
					status = "IDLE";
				}
				System.out.println("Handler for " + profile.getUsername() + " received ACTION : " + t);
				running = actionHandler(t);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Handler " + profile.getUsername() + " has stopped.");
	}

	private boolean actionHandler(String action) {
		try {
			Boolean result = true;
			switch (action) {
			case "GET_PROFILE":
				handler.put("CLIENT", Profile.toJson(profile));
				break;
				
			case "USER_LEVEL_UP":
				int XP = profile.getXP(), rXP = profile.getRequiredXP();
				if(XP >= rXP) {
					profile.setLevel(profile.getLevel()+1);
					profile.setXP(XP - rXP);
					profile.setRequiredXP(profile.getLevel()*4);
					// pick randomly a new pokemon
					Pokemon new_pokemon = Pokemon.pickRandom();
					// then update the database and update current profile instance
					database.updateProfile(profile);
					profile = database.addPokemon(profile, new_pokemon);
					Gson gson = new Gson();
					handler.put("CLIENT", "OK");
					// send new pokemon as well as all the updated list of pokemons
					Pokemon[] array_pokemons = profile.getPokemons().toArray(new Pokemon[0]);
					handler.put("CLIENT", Pokemon.toJson(new_pokemon), gson.toJson(array_pokemons));
				} else {
					handler.put("CLIENT", "Forbidden");
				}
				break;
				
			case "POKEMON_LEVEL_UP":
				List<Pokemon> list_pokemons = profile.getPokemons();
				boolean success = false;
				for(Pokemon p : list_pokemons) {
					int xp = p.getXP(), rXp = p.getRequiredXP();
					if(xp >= rXp) {
						p.setLevel(p.getLevel()+1);
						p.setXP(xp - rXp);
						p.setRequiredXP(p.getLevel()*2);
						int mHP = p.getMaxHP();
						p.setMaxHP(mHP + (p.getLevel()-1)*(int)(mHP*0.2));
						p.setHP(p.getMaxHP());
						//pick randomly a new ability
						Ability new_ability = Ability.pickRandom();
						//update database and retrieve latest version of pokemon
						database.updatePokemon(p);
						p = database.addAbility(p, new_ability);
						Gson gson = new Gson();
						handler.put("CLIENT", "OK");
						// send new ability as well as all the updated list of abilities for this pokemon
						Ability[] array_abilities = p.getAbilities().toArray(new Ability[0]);
						handler.put("CLIENT", p.id, Ability.toJson(new_ability), gson.toJson(array_abilities));
						success = true;
						break;
					}
				}
				if(!success){handler.put("CLIENT", "Forbidden");};
				break;
				
			case "MEMBERS":
				System.out.println("Fetching members...");
				List<Object[]> list = members.queryAll(new FormalField(String.class));
				String[] connectedMembers = new String[list.size()];
				for(int i=0; i < list.size(); i++) {
					connectedMembers[i] = (String)list.get(i)[0];
				}
				Gson gson = new Gson();
				handler.put("CLIENT", gson.toJson(connectedMembers));
				System.out.println("Handler " + profile.getUsername() + " has sent list of all connected members");
				break;

			case "FIGHT":
				fighters.put(profile.getUsername(), "SEARCHING");
				status = "LOOKING_FOR_FIGHT";
				Object[] fight = fighters.get(new ActualField(profile.getUsername()), new ActualField("FIGHT"), new FormalField(String.class));
				handler.put("CLIENT", (String) fight[2]);
				status = "FIGHTING";
				System.out.println("Sent out URI of fight for " + profile.getUsername());
				break;

			case "DISCONNECT":
				members.getp(new ActualField(profile.getUsername()));
				System.out.println("User " + profile.getUsername() + " removed from connectedMembers");
				handler.put("CLIENT", "OK");
				// wait for the ACK to shutdown Thread
				handler.get(new ActualField("SERVER"), new ActualField("OK_ACK"));
				handlers.remove("handlers/" + profile.getUsername());
				System.out.println("Handler " + profile.getUsername() + " removed from repository");
				result = false;
				break;
			}
			return result;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}

	}
	
	private void refreshProfile() {
		profile = database.getProfile(name);
	}
}

// handler that will generate all the fights
class FightCreationHandler implements Runnable {

	String fightersURI = "tcp://" + Config.serverHost + "/fighters?keep";
	RemoteSpace fighters;
	SpaceRepository fightsRepository;
	Database database;

	public FightCreationHandler(Database database) {
		try {
			this.database = database;
			this.fighters = new RemoteSpace(fightersURI);
			this.fightsRepository = new SpaceRepository();
			this.fightsRepository.addGate("tcp://" + Config.fightsHost + "/?keep");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while(true) {
			try {
				String fighter1 = (String) fighters.get(new FormalField(String.class), new ActualField("SEARCHING"))[0];
				System.out.println("Got one fighter");
				String fighter2 = (String) fighters.get(new FormalField(String.class), new ActualField("SEARCHING"))[0];
				System.out.println("Got two fighters");
				String fightURI = "fights/" + fighter1 + "vs" + fighter2;
				System.out.println("Creating fight " + fightURI);
				SequentialSpace actions = new SequentialSpace();
				SequentialSpace data = new SequentialSpace();
				// add spaces to repository to allow remote connection from the clients
				fightsRepository.add(fightURI + "/actions", actions);
				fightsRepository.add(fightURI + "/data", data);
				new Thread(new Fight(fightURI, fightsRepository, actions, data, database, fighter1, fighter2)).start();
	
				fighters.put(fighter1, "FIGHT", fightURI);
				fighters.put(fighter2, "FIGHT", fightURI);
	
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

// handle a fight process
class Fight implements Runnable {

	SequentialSpace actions, data;
	SpaceRepository repository;
	String fightURI;
	Database database;
	Profile fighter1, fighter2;
	Ability rcvAbility;
	Item rcvItem;
	Pokemon fighterOnePokemon, fighterTwoPokemon;
	Boolean fightRunning = true;

	public Fight(String fightURI, SpaceRepository repository, SequentialSpace actions, SequentialSpace data, Database database, String fighter1, String fighter2) {
		this.database = database;
		this.fightURI = fightURI;
		this.repository = repository;
		this.actions = actions;
		this.data = data;
		this.fighter1 = database.getProfile(fighter1);
		this.fighter2 = database.getProfile(fighter2);
		System.out.println("Fight generated");
	}

	@Override
	public void run() {
		
		try {
			actions.put(fighter1.getUsername(), Profile.toJson(fighter2));
			actions.put(fighter2.getUsername(), Profile.toJson(fighter1));
			
			// retrieve the selected pokemon by each fighter
			String pokemon1name = (String) data.get(new ActualField(fighter1.getUsername()+" to server"), new FormalField(String.class))[1];
			String pokemon2name = (String) data.get(new ActualField(fighter2.getUsername()+" to server"), new FormalField(String.class))[1];
			
			System.out.println("Got both players pokemons:"+pokemon1name+" "+pokemon2name);
			fighterOnePokemon = fighter1.getPokemons().get(0);
			for (Pokemon poke:fighter1.getPokemons()) {
				if (poke.getName().equals(pokemon1name)) {
					fighterOnePokemon = poke;
					break;
				}
			}
			fighterTwoPokemon = fighter2.getPokemons().get(0);
			for (Pokemon poke:fighter2.getPokemons()) {
				if (poke.getName().equals(pokemon2name)) {
					fighterTwoPokemon = poke;
					break;
				}
			}

			updatePokemons();

			while(true){

				//Receive and process action of player 1.
				actions.put(fighter1.getUsername(),"GO");
				Object[] fighterOneAction = actions.get(new ActualField(fighter1.getUsername()),new FormalField(String.class),new FormalField(String.class)); // format: name, type, action
				processAction(fighterOneAction,1);
				updatePokemons();
				actions.put(fighter1.getUsername(), "UPDATE");
				actions.put(fighter2.getUsername(), "UPDATE");
				actions.get(new ActualField("SERVER"), new ActualField(fighter1.getUsername()), new ActualField("UPDATE_ACK"));
				actions.get(new ActualField("SERVER"), new ActualField(fighter2.getUsername()), new ActualField("UPDATE_ACK"));
				if(fightEnded()) break;

				//Receive and process action of player 2.
				actions.put(fighter2.getUsername(),"GO");
				Object[] fighterTwoAction = actions.get(new ActualField(fighter2.getUsername()),new FormalField(String.class),new FormalField(String.class));

				processAction(fighterTwoAction,2);
				updatePokemons();
				actions.put(fighter1.getUsername(), "UPDATE");
				actions.put(fighter2.getUsername(), "UPDATE");
				actions.get(new ActualField("SERVER"), new ActualField(fighter1.getUsername()), new ActualField("UPDATE_ACK"));
				actions.get(new ActualField("SERVER"), new ActualField(fighter2.getUsername()), new ActualField("UPDATE_ACK"));
				if(fightEnded()) break;
			}

			System.out.println("Fight " + fightURI + " ended !");
			repository.remove(fightURI + "/actions");
			repository.remove(fightURI + "/data");
			System.out.println("Removed actions and data spaces of " + fightURI);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void processAction(Object[] fighterAction, int fighterNumber) throws InterruptedException {
		switch((String) fighterAction[1]){
			case("ABILITY"):

				rcvAbility = Ability.fromJson((String) fighterAction[2]);

				newestActionUsed((String) fighterAction[0], (String) fighterAction[1],rcvAbility.getName());

				System.out.println("Ability: "+rcvAbility.getName()+" used");
				if(fighterNumber == 1){

					rcvAbility.Apply(fighterOnePokemon,fighterTwoPokemon);
				}else{
					rcvAbility.Apply(fighterTwoPokemon,fighterOnePokemon);}
				break;
			case("ITEM"):
				rcvItem = Item.fromJson((String) fighterAction[2]);

				newestActionUsed((String) fighterAction[0], (String) fighterAction[1] , rcvItem.getName());

				if(fighterNumber == 1){
					rcvItem.Apply(fighterOnePokemon);
				}else{
					rcvItem.Apply(fighterTwoPokemon);
				}
				break;
		}
	}
	
	//Check if any pokemon has HP <= 0 and end game if so.
	public boolean fightEnded() {
		try {
			if(fighterOnePokemon.getHP() <= 0 && fighterTwoPokemon.getHP() <= 0){
				// for now reset of HP
				fighterOnePokemon.setHP(fighterOnePokemon.getMaxHP());
				fighterTwoPokemon.setHP(fighterTwoPokemon.getMaxHP());
				// add experience and save progress
				fighterOnePokemon.setXP(fighterOnePokemon.getXP()+2);
				fighterTwoPokemon.setXP(fighterTwoPokemon.getXP()+2);
				fighter1.setXP(fighter1.getXP()+2);
				fighter2.setXP(fighter2.getXP()+2);
				database.saveProfile(fighter1);
				database.saveProfile(fighter2);
				actions.put(fighter1.getUsername(),"DRAW");
				actions.put(fighter2.getUsername(),"DRAW");
				return true;
			} else if(fighterOnePokemon.getHP() <= 0){
				// for now reset of HP
				fighterOnePokemon.setHP(fighterOnePokemon.getMaxHP());
				fighterTwoPokemon.setHP(fighterTwoPokemon.getMaxHP());
				// add experience and save progress
				fighterOnePokemon.setXP(fighterOnePokemon.getXP()+1);
				fighterTwoPokemon.setXP(fighterTwoPokemon.getXP()+3);
				fighter1.setXP(fighter1.getXP()+1);
				fighter2.setXP(fighter2.getXP()+3);
				database.saveProfile(fighter1);
				database.saveProfile(fighter2);
				actions.put(fighter1.getUsername(),"LOSER");
				actions.put(fighter2.getUsername(),"WINNER");
				return true;
			} else if(fighterTwoPokemon.getHP() <= 0){
				// for now reset of HP
				fighterOnePokemon.setHP(fighterOnePokemon.getMaxHP());
				fighterTwoPokemon.setHP(fighterTwoPokemon.getMaxHP());
				// add experience and save progress
				fighterOnePokemon.setXP(fighterOnePokemon.getXP()+3);
				fighterTwoPokemon.setXP(fighterTwoPokemon.getXP()+1);
				fighter1.setXP(fighter1.getXP()+3);
				fighter2.setXP(fighter2.getXP()+1);
				database.saveProfile(fighter1);
				database.saveProfile(fighter2);
				actions.put(fighter1.getUsername(),"WINNER");
				actions.put(fighter2.getUsername(),"LOSER");
				return true;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void updatePokemons() throws InterruptedException{
		//Update fighter ones local pokemon
		data.getp(new FormalField(String.class),new FormalField(String.class));
		data.getp(new FormalField(String.class),new FormalField(String.class));

		System.out.println("Updating pokemons. poke1hp: "+fighterOnePokemon.getHP()+", poke2hp: "+fighterTwoPokemon.getHP());
		data.put(fighter1.getUsername(),Pokemon.toJson(fighterOnePokemon));
		data.put(fighter2.getUsername(),Pokemon.toJson(fighterTwoPokemon));
	}

	public void newestActionUsed(String user, String type, String name) throws InterruptedException{
		data.getp(new FormalField(String.class), new FormalField(String.class),new FormalField(String.class));
		data.put(user,type,name);
	}


}