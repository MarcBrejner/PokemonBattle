package com.mycompany.pokemonBattle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class Controller {

    public String state;
    public SequentialSpace threadedComs;
    public SpaceRepository localRepository;
    public MenuLogic menu;
    public static Profile user;
    private List<Ability> abilityList;
    public String choosenPokemon = "Pikachu";
    public String usedAbility = "";

	public static boolean loggedIn = false;
    String username = "", password = "";
    boolean shifted = false, caps = false;
    public static final int MAX_ABILITY = 4;
    public String selectedAbility = "";
    public boolean hasLost = false;
    
    public boolean profileLevelUp;
    public String newPokemon;
    public boolean pokemonLevelUp;
    public String newAbility;

    GameElements gameElements;

    public Controller() {
        this.state = "welcome";
        threadedComs = new SequentialSpace();
        
        new Thread(new ClientController(threadedComs)).start();
    }

    public void handleKeyboard(String code, String text, boolean isShifted){
    	shifted = isShifted;
        if (code == "ENTER"){
			if (gameElements.textBoxIsTyping()) {
				gameElements.speedUpTextBoxTyping();
			} else if (gameElements.textBoxExists()) {
				gameElements.removeTextBox();
			} else {
				performAction(menu.getAction());
			}
        } else if (code == "UP" || code == "DOWN" || code == "RIGHT" || code == "LEFT"){
            menu.move(code);
        } else if (code == "TAB") {
        	menu.move("DOWN");
        } else if (menu.getAction() == "form" || menu.getAction() == "password") {
        	menu.currentMenu.typingHandler(code, text, shifted);
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
        		
        	case "form":
        		menu.move("DOWN");
        		break;
        	
        	case "password":
        		menu.move("DOWN");
        		break;
        		
        	case "disconnect":
				try {
					threadedComs.put("DISCONNECT");
					state = "disconnect";
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}
        		break;
        		
        	case "signin":
        		// send credentials to the ClientController for authentication
        		username = menu.currentMenu.getForms()[0];
        		password = menu.currentMenu.getForms()[1];
        		System.out.println("Username: "+username+", password: "+password);
				try {
					threadedComs.put("CONNECT", username, password);
					state = "signIn";
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
        		break;
        		
        	case "initialBulbasaur":
				try {
					threadedComs.put("INITIAL", "Bulbasaur");
					state = "initial_choice";
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}
        		break;
        		
        	case "initialCharmander":
        		try {
					threadedComs.put("INITIAL", "Charmander");
					state = "initial_choice";
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}
        		break;
        		
        	case "initialSquirtle":
        		try {
					threadedComs.put("INITIAL", "Squirtle");
					state = "initial_choice";
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}
        		break;
        		
        	case "submitSignup":
        		// send credentials to the ClientController for creation
				try {
					username = menu.currentMenu.getForms()[0];
	        		password = menu.currentMenu.getForms()[1];
	        		System.out.println("Username: "+username+", password: "+password);
					threadedComs.put("SIGNUP", username, password);
					state = "submittedSignUp";
				} catch (InterruptedException e1) {
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
					e1.printStackTrace();
				}
        		break;
        		
            case "fight":
				state = "choosePokemon";
				hasLost = false;
				break;

            case "hide":
            	gameElements.pokemon1View.remove();
            	gameElements.hpBar1.remove();
            	break;
            case "show":
            	gameElements.pokemon1View.draw();
            	gameElements.hpBar1.draw();
            	break;
            case "does not matter":
            	state = "preFightAnimation1";
            	menu.changeMenu("inFight");
            	break;
            	
            case "ability1":
				Ability ab1 = abilityList.get(0);
				usedAbility = ab1.getName();
				try {
					threadedComs.put("ABILITY", Ability.toJson(ab1));
					System.out.println("Used ability: "+usedAbility);
					state = "right after your turn";
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
            case "ability2":
            	Ability ab2 = abilityList.get(1);
            	usedAbility = ab2.getName();
				try {
					threadedComs.put("ABILITY", Ability.toJson(ab2));
					System.out.println("Used ability: "+usedAbility);
					state = "right after your turn";
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
            case "ability3":
				Ability ab3 = abilityList.get(2);
				usedAbility = ab3.getName();
				try {
					threadedComs.put("ABILITY", Ability.toJson(ab3));
					System.out.println("Used ability: "+usedAbility);
					state = "right after your turn";
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
            case "ability4":
				Ability ab4 = abilityList.get(3);
				usedAbility = ab4.getName();
				try {
					threadedComs.put("ABILITY", Ability.toJson(ab4));
					System.out.println("Used ability: "+usedAbility);
					state = "right after your turn";
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
        }
        
        if (state == "choosePokemon2") {
	        if (action.length() >= 9) {
	        	if (action.subSequence(0, 9).equals("choosen: ")){
	        		//choosing pokemon
	        		choosenPokemon = (String) action.subSequence(9, action.length());
	       
	        		state = "waitingForOtherPlayer1";
	        	}	
	        }
        }
    }

    public void initGameElements(){
        //creating game elements
        menu = new MenuLogic(InGame.gc);
        gameElements = new GameElements(InGame.root);
    }

    public void stateHandler(){
    	int tbx = 50;
    	int tby = 500;
    	
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
					e1.printStackTrace();
				}
				break;
        		
        	case "signIn":
        		// check if authentication is successful or not
				try {
					String r = (String)threadedComs.get(new ActualField("CONNECT_ACK"), new FormalField(String.class))[1];
					if(r.equals("OK")) {
						String t = (String)threadedComs.get(new ActualField("PROFILE"), new FormalField(String.class))[1];
						if(t.equals("INITIAL")) {
							state = "initial_choice_draw";
							menu.changeMenu("initial_choice");
						} else {
							user = Profile.fromJson(t);
							state = "mainMenu";
							menu.changeMenu("mainMenu");
						}
						loggedIn = true;
						
					} else {
						state = "connect";
					}
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				break;
				
        	case "initial_choice_draw":
        		menu.draw();
        		break;
        	
        	case "initial_choice":
				try {
					String r = (String)threadedComs.get(new ActualField("INITIAL_ACK"), new FormalField(String.class))[1];
					if(r.equals("Forbidden")) {
						System.out.println("ERROR for initial choice : Forbidden");
						state = "initial_choice_draw";
					} else {
						user = Profile.fromJson(r);
						state = "mainMenu";
						menu.changeMenu("mainMenu");
					}
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}
        		break;
				
        	case "submittedSignUp":
        		// check if user creation was successful or not
				try {
					String r = (String)threadedComs.get(new ActualField("SIGNUP_ACK"), new FormalField(String.class))[1];
					if(r.equals("OK")) {
						String t = (String)threadedComs.get(new ActualField("PROFILE"), new FormalField(String.class))[1];
						if(t.equals("INITIAL")) {
							state = "initial_choice_draw";
							menu.changeMenu("initial_choice");
						} else {
							user = Profile.fromJson(t);
							state = "mainMenu";
							menu.changeMenu("mainMenu");
						}
						loggedIn = true;
					} else {
						state = "signup";
					}
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				break;
        		
            case "mainMenu":
                menu.draw();
                break;

			case "useItem":
				break;

			case "choosePokemon":
            	//when splash screen is done drawing, choose pokemon
            	
            	//generation of pokemons menu
                ArrayList<String[]> labelsPokemons = new ArrayList<String[]>();
                for(Pokemon p : user.getPokemons()) {
                	labelsPokemons.add(new String[]{p.getName() + " - " + p.getElement(), "choosen: "+p.getName()});
                }
                labelsPokemons.add(new String[]{"Back", "mainMenu"});
                menu.menus.put("pokemons", new MenuList(menu.gc, 100, 100, labelsPokemons));
        		state = "pokemons";
        		menu.changeMenu("pokemons");
        		
        		state = "choosePokemon2";
        		break;
        		
            case "choosePokemon2":
            	menu.draw();
            	break;

            	
            case "waitingForOtherPlayer1":
            	//start drawing splash screen
            	InGame.splashScreen.draw();
            	state = "waitingForOtherPlayer2";
            	break;
            	
            case "waitingForOtherPlayer2":
            	if (InGame.splashScreen.isDrawing()) break;
            	//when done drawing, signal ready to fight
            	try {
					threadedComs.put("FIGHT");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

            	//create ability menu
				int i = 1;
				ArrayList<String[]> labelsAbilities = new ArrayList<String[]>();
				abilityList = user.getPokemons().get(0).getAbilities();

				for(Ability a : abilityList){
					labelsAbilities.add(new String[]{a.getName(), "ability"+i});
					i++;
				}
				menu.menus.put("abilities", new MenuList(menu.gc, 100, 100, labelsAbilities));
				
				//go to next state
				state = "waitingForOtherPlayer3";
				gameElements.createTextBox(260, 300, "Waiting for other players", false);
            	break;

            
            case "waitingForOtherPlayer3":
				Object[] temp222 = threadedComs.getp(new ActualField("GOTFIGHT"));
				if (temp222 == null) {
					break;
				}
				
				try {
					threadedComs.put("choosen pokemon",choosenPokemon);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        		System.out.println("waiting for pokemon ack, "+choosenPokemon);
				
				state = "waitingForPokeAck";
				break;
				
				
            case "waitingForPokeAck":
            	Object[] temp2222 = threadedComs.getp(new ActualField("got pokemon"));
				if (temp2222 == null) {
					break;
				}
				state = "got pokemon";
        		break;
        	
            case "got pokemon":
				//found an opponent
				InGame.splashScreen.draw();
				gameElements.forceRemoveTextBox();
				
				//draw trainer intro
                menu.changeMenu("not your turn");
                state = "waitingForSplash";
                break;
              
            case "waitingForSplash":
            	if (!InGame.splashScreen.isDrawing()) {
                    //wait until splash animation is over
            		gameElements.getPokemonViews();
    				gameElements.draw();
    				gameElements.pokemon1View.glide(100);
    				gameElements.pokemon2View.glide(400);
					state = "waiting for glide";
                }
                break;
                
            case "waiting for glide":
            	if (!gameElements.pokemon1View.isRunning()) {
            		gameElements.drawBars();
            		Object[] temp = threadedComs.getp(new ActualField("GO"));
                	if (temp != null) {
                		state = "right before your turn4";
                	} else {
                		state = "not your turn";
                	}
            	}
            	break;
				
			case "your turn":
				menu.draw();
				break;
                
            case "not your turn":
            	menu.draw();
				try {
					Object[] el = threadedComs.getp(new FormalField(String.class));
					if (el == null) break;
					switch((String)el[0]) {
					case "GO":
						state = "right before your turn";
						break;
					case "WINNER":
						gameElements.pokemon2View.fadeOut();
						gameElements.createTextBox(300, 400, "You've won!");
						
						user.setXP(user.getXP()+4);
						
						threadedComs.put("WINNER_ACK");
						state = "endOfFight";
						break;
					case "LOSER":
						hasLost = true;
						state = "right before your turn";
						break;
					case "DRAW":
						gameElements.pokemon1View.fadeOut();
						gameElements.pokemon2View.fadeOut();
						gameElements.createTextBox(300, 400, "It's a draw!");
						
						user.setXP(user.getXP()+3);
						threadedComs.put("DRAW_ACK");
						state = "endOfFight";
						break;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            	break;
				
			case "endOfFight":
				if (!gameElements.textBoxExists()) {
					InGame.splashScreen.draw();
					gameElements.removeAll();
					refreshUser();
					menu.changeMenu("mainMenu");
					state = "endOfFight1.1";
				}
				break;
				
			case "endOfFight1.1":
				pokemonLevelUp = false;
				checkPokemonStatus();
				if (pokemonLevelUp) {
					gameElements.createTextBox(100, 300, new String[] {"Your pokemon has leveled up!","You've unlocked a new Ability : "+newAbility});
				}
				state = "endOfFight1.2";
				break;
			
			case "endOfFight1.2":
				if(gameElements.textBoxExists()) break;
				profileLevelUp = false;
				checkProfileStatus();
				if (profileLevelUp) {
					gameElements.createTextBox(100, 300, new String[] {"You have leveled up!","You've unlocked a new Pokemon : "+newPokemon});
				}
				state = "endOfFight2";
				break;
			
			case "endOfFight2":
				if(gameElements.textBoxExists()) break;
				if (!InGame.splashScreen.isDrawing()) {
					InGame.splashScreen.draw();
					state = "mainMenu";
				}
				break;
            	
            case "right before your turn":
			try {
				usedAbility = (String) threadedComs.get(new ActualField("last ability"),new FormalField(Integer.class), new FormalField(String.class))[2];
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            	gameElements.createTextBox(300, 350, gameElements.pokemon2.getName()+" used "+usedAbility);
            	state = "right before your turn2";
            	break;
            
            case "right before your turn2":
            	if (!gameElements.textBoxExists()) {
            		//do effects
            		gameElements.updateBars();
                	gameElements.pokemon1View.shake();
            		gameElements.createTextBox(300, 350, "It was very effective");
                	state = "right before your turn3";
            	}
            	break;
           
            case "right before your turn3":
            	if (!gameElements.textBoxExists()) {
                	state = "right before your turn4";
            	}
            	break;
            	
            case "right before your turn4":	
            	System.out.println("Have I lost??"+hasLost);
            	if (hasLost) {
					gameElements.pokemon1View.fadeOut();
					gameElements.createTextBox(300, 400, "You've lost!");
					
					user.setXP(user.getXP()+2);
					try {
						threadedComs.put("LOSER_ACK");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					state = "endOfFight";
					break;
				}
            	state = "your turn";
        		menu.changeMenu("abilities");
            	break;
            
            case "right after your turn":
            	gameElements.createTextBox(300, 350, gameElements.pokemon1.getName()+" used "+usedAbility);
            	state = "right after your turn2";
            	break;
            
            case "right after your turn2":
            	if (!gameElements.textBoxExists()) {
            		//do effects
            		gameElements.updateBars();
                	gameElements.pokemon2View.shake();
            		gameElements.createTextBox(300, 350, "It was very effective");
                	state = "right after your turn3";
            	}
            	break;
            
            case "right after your turn3":
            	if (!gameElements.textBoxExists()) {
                	state = "right after your turn4";
            	}
            	break;
            
            case "right after your turn4":
            	threadedComs.getp(new ActualField("last ability"),new FormalField(Integer.class), new FormalField(String.class));
            	menu.changeMenu("not your turn");
            	state = "not your turn";
            	break;
                
            case "fightIntroTEMP":
                InGame.splashScreen.draw();
                gameElements.draw();
                gameElements.trainer1View.glide(100);
                gameElements.trainer2View.glide(450);
                String[] fightIntroTexts = new String[] {
                		"You: Oh geez thats Fanya, my russian friend",
                		"Fanya: Ї ЩЇLL ҪЯЏSҤ ҰФЏ",
                		"You: ...",
                		"*Choose your pokemon*"};
                gameElements.createTextBox(tbx,tby, fightIntroTexts);
                menu.changeMenu("inFight");
                state = "fightIntro2";
                break;
                
            case "fightIntro2":
            	if (!gameElements.textBoxExists()) {
            		state = "choose pokemon";
            		menu.changeMenu("choose pokemon");
            	}
            	break;
            	
            case "choose pokemon":
            	menu.draw();
            	break;
            
            case "preFightAnimation4":
            	if (!gameElements.textBoxExists()) {
            		state = "fight";
            		gameElements.drawBars();
            	}
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

            case "fade out":
            	if (!gameElements.textBoxExists()) {
            		gameElements.trainer2View.glide(700);
            	}
            	break;
        }
    }
    
    // function to call at the end of a fight, after the profile has been updated
    public void checkProfileStatus() {
		int XP = user.getXP(), rXP = user.getRequiredXP();
		while(XP >= rXP) {
			try {
				System.out.println("TESTING PROFILE STATUS...");
				threadedComs.put("USER_LEVEL_UP");
				String ack = (String)threadedComs.get(new ActualField("USER_LEVEL_UP_ACK"), new FormalField(String.class))[1];
				if (ack.equals("OK")) {
					user.setLevel(user.getLevel()+1);
					XP = XP - rXP;
					rXP = user.getLevel()*4;
					user.setXP(XP);
					user.setRequiredXP(rXP);
					// receive Pokemon and list of Pokemons from ClientController
					Object[] elems = threadedComs.get(new ActualField("USER_LEVEL_UP_ACK"), new FormalField(String.class), new FormalField(String.class));
					Pokemon new_pokemon = Pokemon.fromJson((String)elems[1]);
					GsonBuilder builder = new GsonBuilder();
			        Gson gson = builder.create();
					Pokemon[] p_table = gson.fromJson((String)elems[2], Pokemon[].class);
					// update user's list of pokemons
					user.setPokemons(new ArrayList<>(Arrays.asList(p_table)));
					System.out.println("[!] Received new pokemon : "+new_pokemon.getName());
					profileLevelUp = true;
					newPokemon = new_pokemon.getName();
				} else {
					System.out.println("An error occured for request 'USER_LEVEL_UP' : " + ack);
					// if action is forbidden, we synchronize with the server to get the right value
					if (ack.equals("Forbidden")) {
						refreshUser();
						break;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
    
    // function to call at the end of a fight, after the profile has been updated
    public void checkPokemonStatus() {
    	List<Pokemon> list_pokemons = user.getPokemons();
    	boolean failed = false;
		for(Pokemon p : list_pokemons) {
			int xp = p.getXP(), rXp = p.getRequiredXP();
			while(xp >= rXp) {
				try {
					System.out.println("TESTING POKEMON STATUS...");
					threadedComs.put("POKEMON_LEVEL_UP");
					String ack = (String)threadedComs.get(new ActualField("POKEMON_LEVEL_UP_ACK"), new FormalField(String.class))[1];
					if (ack.equals("OK")) {
						Object[] elem = threadedComs.get(new ActualField("POKEMON_LEVEL_UP_ACK"), new FormalField(Integer.class), new FormalField(String.class), new FormalField(String.class));
						if((int)elem[1] == p.id) {
							p.setLevel(p.getLevel()+1);
							xp = xp - rXp;
							rXp = p.getLevel()*2;
							p.setXP(xp);
							p.setRequiredXP(rXp);
							int mHP = p.getMaxHP();
							p.setMaxHP(mHP + (p.getLevel()-1)*(int)(mHP*0.2));
							p.setHP(p.getMaxHP());
							// receive Ability and list of Abilities from ClientController
							Ability new_ability = Ability.fromJson((String)elem[2]);
							GsonBuilder builder = new GsonBuilder();
					        Gson gson = builder.create();
					        Ability[] a_table = gson.fromJson((String)elem[3], Ability[].class);
					        // update pokemon's list of abilities
							p.setAbilities(new ArrayList<>(Arrays.asList(a_table)));
							System.out.println("[!] Pokemon " + p.getName() + " received new ability : "+new_ability.getName());
							pokemonLevelUp = true;
							newAbility = new_ability.getName();
						} else {
							System.out.println("Error : received an ability for another pokemon.");
							failed = true;
						}
					} else {
						System.out.println("An error occured for request 'POKEMON_LEVEL_UP' on pokemon " + p.getName() + " : " + ack);
						// if action is forbidden, we synchronize with the server to get the right value
						if (ack.equals("Forbidden")) {
							failed = true;
							break;
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		if (failed) refreshUser();
	}

	public static Profile getUser() {
		return user;
	}

	public void setPokemon(int number, Pokemon pokemon) {
		if (number == 1) {
			gameElements.pokemon1.setHP(pokemon.getHP());
		} else {
			gameElements.pokemon2.setHP(pokemon.getHP());
		}
	}

	public static boolean isLoggedIn() {
		return loggedIn;
	}

	public void refreshUser() {
		try {
			System.out.println("REFRESHING USER...");
			threadedComs.put("GET_PROFILE");
			String profile_string = (String)threadedComs.get(new ActualField("GET_PROFILE_ACK"), new FormalField(String.class))[1];
			user = Profile.fromJson(profile_string);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}