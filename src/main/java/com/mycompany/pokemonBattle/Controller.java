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
    String username = "", password = "";
    boolean shifted = false, caps = false;
    public static final int MAX_ABILITY = 4;

    GameElements gameElements;

    //public Controller(int port, String username, String password)
    public Controller() {
        this.state = "welcome";
        //SPACES
        threadedComs = new SequentialSpace();
        
        new Thread(new ClientController(threadedComs)).start();
    }

    public void handleKeyboard(String code, String text, boolean isShifted){
    	//System.out.println(code+", "+text);
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
					// TODO Auto-generated catch block
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

            case "attack":
            	gameElements.commenceAttack();	
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

			case "useItem":
				break;

			case "useAbility1":
				Ability ab1 = user.getPokemons().get(0).getAbilities().get(0);
				try {
					threadedComs.put("ABILITY",Ability.toJson(ab1));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;

			case "useAbility2":
				Ability ab2 = user.getPokemons().get(0).getAbilities().get(0);
				try {
					threadedComs.put("ABILITY",Ability.toJson(ab2));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;

			case "useAbility3":
				Ability ab3 = user.getPokemons().get(0).getAbilities().get(0);
				try {
					threadedComs.put("ABILITY",Ability.toJson(ab3));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;

			case "useAbility4":
				Ability ab4 = user.getPokemons().get(0).getAbilities().get(0);
				try {
					threadedComs.put("ABILITY",Ability.toJson(ab4));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;

            case "waitingForOtherPlayer":
            	// Maybe add a loading screen here ?
                //get from tuple space...
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
            	
            case "preFightAnimation1":
            	gameElements.createTextBox(tbx,tby, "You: GO *INSERT POKEMON NAME*");
            	gameElements.trainer1View.glide(-100);
            	gameElements.pokemon1View.glide(100);
            	state = "preFightAnimation2";
            	break;
            
            case "preFightAnimation2":
            	if (!gameElements.textBoxExists()) {
            		gameElements.createTextBox(tbx,tby, "Fanya: ԠДҪЊДԠP, DЄLЇVЄЯ MЄ ҢЇS SPЇЍЄ");
            		state = "preFightAnimation3";
            	}
            	break;
            
            case "preFightAnimation3":
            	if (!gameElements.textBoxExists()) {
            		gameElements.trainer2View.glide(700);
            		gameElements.pokemon2View.glide(400);
            		state = "preFightAnimation4";
            		gameElements.createTextBox(tbx, tby, "Machamp: I̴̤̍ ̶͊ ̵̯̉Ć̶͖H̷̢̆R̸̪̀U̶̥͂S̸͔̄H̴͚͆ ̴̪̈Y̴͈̏Ỏ̵̼U̴̜͒");
            	}
            	break;
            
            case "preFightAnimation4":
            	if (!gameElements.textBoxExists()) {
            		state = "fight";
            		gameElements.drawBars();
            	}
            	break;

            case "fight":
                menu.draw();
                if (gameElements.pokemon2.getHP() < 0 && !gameElements.pokemon2View.isRunning()) {
                	String[] fightIntroTexts2 = new String[] {
                     		"Pikachu used something*",
                     		"It was extremely effective"};
            		gameElements.createTextBox(tbx, tby, fightIntroTexts2);
                	state = "machamp is kil";
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
            
            case "machamp is kil":
            	if (!gameElements.textBoxExists()) {
	            	gameElements.pokemon2View.fadeOut();
	            	state = "machamp is fading";
            	}
            	break;
            
            case "machamp is fading":
            	System.out.println(gameElements.pokemon2View.isRunning());
            	if (!gameElements.pokemon2View.isRunning()) {
            		gameElements.pokemon1View.glide(-100);
            		gameElements.trainer1View.glide(100);
            		gameElements.trainer2View.glide(450);
            		gameElements.hpBar1.remove();
            		gameElements.hpBar2.remove();
            		 String[] fightIntroTexts2 = new String[] {
                     		"Fanya: *crying*",
                     		"Fanya: PФҚԐ ЇS ҜЇL",
                     		"Fanya: ЍФФФФФФ!",
                     		"*TO BE CONTINUED*"};
            		gameElements.createTextBox(tbx, tby, fightIntroTexts2);
            		state = "fade out";
            	}
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
					// TODO : some display of the events
					System.out.println("[!] Received new pokemon : "+new_pokemon.getName());
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
							// receive Ability and list of Abilities from ClientController
							Ability new_ability = Ability.fromJson((String)elem[2]);
							GsonBuilder builder = new GsonBuilder();
					        Gson gson = builder.create();
					        Ability[] a_table = gson.fromJson((String)elem[3], Ability[].class);
					        // update pokemon's list of abilities
							p.setAbilities(new ArrayList<>(Arrays.asList(a_table)));
							// TODO : some display of the events
							System.out.println("[!] Pokemon " + p.getName() + " received new ability : "+new_ability.getName());
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
	
	public void refreshUser() {
		try {
			threadedComs.put("GET_PROFILE");
			String profile_string = (String)threadedComs.get(new ActualField("GET_PROFILE_ACK"), new FormalField(String.class))[1];
			user = Profile.fromJson(profile_string);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}