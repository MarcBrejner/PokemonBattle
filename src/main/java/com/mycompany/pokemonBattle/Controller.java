package com.mycompany.pokemonBattle;

import java.util.ArrayList;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.scene.control.*;

class Controller {

    public String state;
    public SequentialSpace threadedComs;
    public SpaceRepository localRepository;
    public MenuLogic menu;
    public Profile user;
    String username = "", password = "";
    boolean shifted = false, caps = false;

    GameElements gameElements;

    //public Controller(int port, String username, String password)
    public Controller() {
        this.state = "welcome";
        //SPACES
        threadedComs = new SequentialSpace();
        
        new Thread(new ClientController(threadedComs)).start();
    }

    public void handleKeyboard(String code, String text, boolean isShifted){
    	shifted = isShifted;
        if (code == "ENTER"){
            performAction(menu.getAction());
        } else if (code == "UP" || code == "DOWN" || code == "RIGHT" || code == "LEFT"){
            menu.move(code);
        } else if(code == "CAPS") {
        	caps = !caps;
    	} else if (menu.getAction() == "username" || menu.getAction() == "password") {
        	typingHandler(code, text);
        }
    }

    public void performAction(String action){
    	System.out.println("ACTION : " + action);
    	
        switch (action) {
	        case "welcome":
	        	menu.changeMenu("welcome");
	    		state = "welcome";
	    		break;
	    		
        	case "connect":
        		menu.changeMenu("connect");
        		state = "connect";
        		break;
        		
        	case "signup":
        		menu.changeMenu("signup");
        		state = "signup";
        		break;
        		
        	case "disconnect":
				try {
					threadedComs.put("DISCONNECT");
					state = "disconnect";
				} catch (InterruptedException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
        		break;
        		
        	case "signin":
        		// send credentials to the ClientController for authentication
				try {
					threadedComs.put("CONNECT", username, password);
					state = "signIn";
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        		break;
        		
        	case "submitSignup":
        		// send credentials to the ClientController for creation
				try {
					threadedComs.put("SIGNUP", username, password);
					state = "submittedSignUp";
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        		break;
        		
        	case "mainMenu":
        		menu.changeMenu("mainMenu");
        		state = "mainMenu";
        		break;
        		
        	case "pokemons":
        		//generation of pokemons menu
                ArrayList<String[]> labelsPokemons = new ArrayList<String[]>();
                for(Pokemon p : user.getPokemons()) {
                	labelsPokemons.add(new String[]{p.getName() + " - " + p.getElement(), "idPokemon"+p.id});
                }
                labelsPokemons.add(new String[]{"Back", "mainMenu"});
                menu.menus.put("pokemons", new MenuList(menu.gc, 100, 100, labelsPokemons));
        		state = "pokemons";
        		menu.changeMenu("pokemons");
        		break;
        		
        	case "items":
        		//generation of items menu
                ArrayList<String[]> labelsItems = new ArrayList<String[]>();
                for(Item i : user.getItems()) {
                	labelsItems.add(new String[]{i.getName() + " (x" + i.getNumber() + ")" , "idItem"+i.id});
                }
                labelsItems.add(new String[]{"Back", "mainMenu"});
                menu.menus.put("items", new MenuList(menu.gc, 100, 100, labelsItems));
        		state = "items";
        		menu.changeMenu("items");
        		break;
        		
        	case "members":
        		try {
					threadedComs.put("MEMBERS");
					String connectedMembers_string = (String)threadedComs.get(new ActualField("MEMBERS_ACK"), new FormalField(String.class))[1];
					GsonBuilder builder = new GsonBuilder();
			        Gson gson = builder.create();
			        String[] connectedMembers = gson.fromJson(connectedMembers_string, String[].class);
					for(String elem : connectedMembers) {
						System.out.println("- " + elem);
					}
					state = "members";
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        		break;
        		
            case "fight":
				try {
					threadedComs.put("FIGHT");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                state = "waitingForOtherPlayer";
                break;
        }
    }

    public void initGameElements(){
        //creating game elements
        menu = new MenuLogic(InGame.gc);
        gameElements = new GameElements(InGame.root);
    }

    public void stateHandler(){
        switch (state) {
        	case "welcome":
        		
        		menu.draw();
        		break;
        		
        	case "connect":
        		menu.draw();
        		break;
        		
        	case "signup":
        		menu.draw();
        		break;
        		
        	case "disconnect":
        		// check if disconnection is successful or not
				try {
					String r = (String)threadedComs.get(new ActualField("DISCONNECT_ACK"), new FormalField(String.class))[1];
					if(r.equals("OK")) {
						state = "welcome";
						menu.changeMenu("welcome");
					}
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;
        		
        	case "signIn":
        		// check if authentication is successful or not
				try {
					String r = (String)threadedComs.get(new ActualField("CONNECT_ACK"), new FormalField(String.class))[1];
					if(r.equals("OK")) {
						String t = (String)threadedComs.get(new ActualField("PROFILE"), new FormalField(String.class))[1];
						user = Profile.fromJson(t);
						state = "mainMenu";
						menu.changeMenu("mainMenu");
					} else {
						state = "connect";
					}
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;
				
        	case "submittedSignUp":
        		// check if user creation was successful or not
				try {
					String r = (String)threadedComs.get(new ActualField("SIGNUP_ACK"), new FormalField(String.class))[1];
					if(r.equals("OK")) {
						String t = (String)threadedComs.get(new ActualField("PROFILE"), new FormalField(String.class))[1];
						user = Profile.fromJson(t);
						state = "mainMenu";
						menu.changeMenu("mainMenu");
					} else {
						state = "signup";
					}
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;
        		
            case "mainMenu":
                menu.draw();
                break;

            case "waitingForOtherPlayer":
            	// Maybe add a loading screen here ?
                //get from tuple space...
                try {
                    //found opponent
                    threadedComs.get(new ActualField("FIGHT_ACK"));
                } catch (InterruptedException e){
                    e.printStackTrace();
                }

                InGame.splashScreen.draw();
                state = "waitingForSplash";
                break;

            case "waitingForSplash":
                if (!InGame.splashScreen.isDrawing()) {
                    //wait until splash animation is over
                    state = "fightIntro";
                }
                break;

            case "fightIntro":
                InGame.splashScreen.draw();
                state = "fight";
                gameElements.draw();
                menu.changeMenu("inFight");
                break;

            case "fight":
                menu.draw();
                break;
                
            case "pokemons":
            	menu.draw();
            	break;
            
            case "items":
            	menu.draw();
            	break;
            	
            case "members":
            	// for now just redirect to the main menu but in the end should display the list of connectedMembers
            	state = "mainMenu";
            	break;
        }
    }
    
    public void typingHandler(String code, String text) {
    	if(code == "BACK_SPACE") {
    		switch(menu.getAction()) {
    			case "username":
    				if (username != null && username.length()>0) {
    					username = username.substring(0, username.length()-1);
    				}
    				break;
    			case "password":
    				if (password != null && password.length()>0) {
    					password = password.substring(0, password.length()-1);
    				}
    				break;
    		}
    	} else if (text.length()>0) {
    		if (shifted && caps) {
    			text = text.toLowerCase();
    		} else if (shifted) {
    			text = text.toUpperCase();
    		}
    		switch(menu.getAction()) {
    			case "username":
    				username = username + text;
    				break;
    			case "password":
    				password = password + text;
    				break;
    		}
    	}
    	switch(menu.getAction()) {
			case "username":
				menu.updateCredentialsButton(username);
				break;
			case "password":
				String stars_password = "";
				for (int i=0; i < password.length(); i++) {
					stars_password += "*";
				}
				menu.updateCredentialsButton(stars_password);
				break;
    	}
    }
}