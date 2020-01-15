package com.mycompany.pokemonBattle;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.jspace.*;

public class ServerController {

	public static void main(String[] args) {
		try {

			SequentialSpace lobby = new SequentialSpace();
			SequentialSpace connectedMembers = new SequentialSpace();
			SpaceRepository repository = new SpaceRepository();

			repository.add("lobby", lobby);
			repository.add("members", connectedMembers);
			repository.add("fighters", new SequentialSpace()); // ideally a FIFO space

			repository.addGate("tcp://" + Config.serverHost + "/?keep");
			
			Database database = new Database("pokemonGame", "root", Config.db_password);

			new Thread(new FightCreationHandler(database)).start();

			String username, password;
			Object[] lobbyRequest;
			while (true) {
				// lobby
				System.out.println("Waiting for requests");
				lobbyRequest = lobby.get(new ActualField("connect"), new FormalField(String.class), new FormalField(String.class));

				System.out.println("Got a connect request");
				username = (String) lobbyRequest[1];
				password = (String) lobbyRequest[2];

				if (database.authenticate(username, password)) {
					if (connectedMembers.queryp(new ActualField(username)) != null) {
						// user already connected
						System.out.println("ERROR : connection on already connected profile");
						lobby.put(username, "Already connected");
					} else {
						// register member and creating handler
						connectedMembers.put(username);
						new Thread(new UserHandler(username, repository, database)).start();
						lobby.put(username, "OK");
					}
				} else {
					lobby.put(username, "Forbidden");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// Keep reading incoming actions
		System.out.println("Handler " + profile.getUsername() + " is running.");
		System.out.println("Sending profile data to " + name);
		String profile_string = Profile.toJson(profile); 
		try {
			
			handler.put("CLIENT", profile_string);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("Data sent (" + profile_string.getClass() + ") : " + profile_string);
		Boolean running = true;
		while (running) {
			String t; // message
			try {
				status = "IDLE";
				t = (String) handler.get(new ActualField("SERVER"), new FormalField(String.class))[1];
				System.out.println("Handler for " + profile.getUsername() + " received ACTION : " + t);
				running = actionHandler(t);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Handler " + profile.getUsername() + " has stopped.");
	}

	private boolean actionHandler(String action) {
		try {
			Boolean result = true;
			switch (action) {
			case "MEMBERS":
				List<Object[]> list = members.queryAll(new FormalField(String.class));
				/* Object[] connectedMembers = list.toArray(new Object[0]);
				for(Object elem : connectedMembers) {
					elem = elem[0];
				} */
				handler.put("CLIENT", list.size());
				for (Object[] elem : list) {
					handler.put("CLIENT", elem[0]);
				}
				//handler.put(connectedMembers);
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
				System.out.println("Waiting for ACK to shutdown Thread...");
				handler.get(new ActualField("SERVER"), new ActualField("OK_ACK"));
				handlers.remove("handlers/" + profile.getUsername());
				System.out.println("Handler " + profile.getUsername() + " removed from repository");
				result = false;
				break;
			}
			return result;
		} catch (InterruptedException e) {
			e.printStackTrace();
			//handler.put("CLIENT", "ERROR");
			return false;
		}

	}
}

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
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
				System.out.println("ADDING SPACE tcp://" + Config.fightsHost + "/" + fightURI + "/actions?keep");
				System.out.println("ADDING SPACE tcp://" + Config.fightsHost + "/" + fightURI + "/data?keep");
				fightsRepository.add(fightURI + "/actions", actions);
				fightsRepository.add(fightURI + "/data", data);
				new Thread(new Fight(fightURI, fightsRepository, actions, data, database, fighter1, fighter2)).start();
	
				fighters.put(fighter1, "FIGHT", fightURI);
				fighters.put(fighter2, "FIGHT", fightURI);
	
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

class Fight implements Runnable {

	SequentialSpace actions, data; // might be better as FIFOSpaces as well
	SpaceRepository repository;
	String fightURI;
	Database database;
	Profile fighter1, fighter2;

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
		// TODO Auto-generated method stub
		try {
			actions.put(fighter1.getUsername(), Profile.toJson(fighter2));
			actions.put(fighter2.getUsername(), Profile.toJson(fighter1));
			actions.put("START");
			System.out.println("START put");
			actions.get(new ActualField("END"));
			System.out.println("Fight " + fightURI + " ended !");
			repository.remove(fightURI + "/actions");
			repository.remove(fightURI + "/data");
			System.out.println("Removed actions and data spaces of " + fightURI);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}