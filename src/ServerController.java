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

			new Thread(new FightCreationHandler()).start();

			String username, password;
			Object[] lobbyRequest;
			UserHandler userHandler;
			while (true) {
				// lobby
				System.out.println("Waiting for requests");
				lobbyRequest = lobby.get(new ActualField("connect"), new FormalField(String.class), new FormalField(String.class));

				System.out.println("Got a connect request");
				username = (String) lobbyRequest[1];
				password = (String) lobbyRequest[2];

				if (authenticate(username, password)) {
					if (connectedMembers.queryp(new ActualField(username)) != null) {
						// user already connected
						lobby.put(username, "Already connected");
					} else {
						// register member and creating handler
						userHandler = new UserHandler(username);
						connectedMembers.put(username);
						repository.add(username, userHandler.handler);
						new Thread(userHandler).start();
					}
					lobby.put(username, "OK");
				} else {
					lobby.put(username, "Forbidden");
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static boolean authenticate(String username, String password) {
		// check if authentication correct
		// TODO
		return true;
	}
}

class UserHandler implements Runnable {
	public String name;
	public SequentialSpace handler;
	public String status;
	String membersURI = "tcp://" + Config.serverHost + "/members?keep";
	String fightersURI = "tcp://" + Config.serverHost + "/fighters?keep";
	RemoteSpace members, fighters;

	public UserHandler(String name) {
		this.name = name;
		this.handler = new SequentialSpace();
		this.status = "INITIATING";
		try {
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
		System.out.println("Handler " + name + " is running.");
		while (true) {
			String t; // message
			try {
				status = "IDLE";
				t = (String) handler.get(new FormalField(String.class))[0];
				System.out.println("Handler for " + name + " received ACTION : " + t);
				actionHandler(t);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void actionHandler(String action) {
		try {
			switch (action) {
			case "MEMBERS":
				// PROBLEMS HERE
				List<Object[]> list = members.queryAll(new FormalField(String.class));
				// Object[] connectedMembers = list.toArray(new Object[0]);
				handler.put(list.size());
				for (Object[] elem : list) {
					handler.put(elem[0]);
				}
				System.out.println("Handler " + name + " has sent list of all connected members");
				break;

			case "FIGHT":
				fighters.put(name, "SEARCHING");
				status = "LOOKING_FOR_FIGHT";
				Object[] fight = fighters.get(new ActualField(name), new ActualField("FIGHT"), new FormalField(String.class));
				handler.put((String) fight[2]);
				status = "FIGHTING";
				System.out.println("Sent out URI of fight for " + name);
				break;

			case "DISCONNECT":
				members.getp(new ActualField(name));
				System.out.println("User " + name + "removed from connectedMembers");
				handler.put("OK");
				break;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			handler.put("ERROR");
		}

	}
}

class FightCreationHandler implements Runnable {

	String fightersURI = "tcp://" + Config.serverHost + "/fighters?keep";
	RemoteSpace fighters;
	SpaceRepository fightsRepository;

	public FightCreationHandler() {
		try {
			this.fighters = new RemoteSpace(fightersURI);
			this.fightsRepository = new SpaceRepository();
			this.fightsRepository.addGate("tcp://" + Config.fightsHost + "/fights/?keep");
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
				String fightURI = fighter1 + "vs" + fighter2;
				System.out.println("Creating fight " + fightURI);
				SequentialSpace actions = new SequentialSpace();
				SequentialSpace data = new SequentialSpace();
				fightsRepository.add(fightURI + "/actions", actions);
				fightsRepository.add(fightURI + "/data", data);
				new Thread(new Fight(fightURI, fightsRepository, actions, data)).start();
	
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

	public Fight(String fightURI, SpaceRepository repository, SequentialSpace actions, SequentialSpace data) {
		this.fightURI = fightURI;
		this.repository = repository;
		this.actions = actions;
		this.data = data;
		System.out.println("Fight generated");
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		actions.put("START");
		System.out.println("START put");
		try {
			actions.query(new ActualField("END"));
			repository.remove(fightURI + "/actions");
			repository.remove(fightURI + "/data");
			System.out.println("Removed actions and data spaces of " + fightURI);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}