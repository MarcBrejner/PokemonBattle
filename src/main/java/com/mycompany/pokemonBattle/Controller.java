package com.mycompany.pokemonBattle;

import org.jspace.ActualField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

class Controller {

    public String state;
    public SequentialSpace threadedComs;
    public SpaceRepository localRepository;
    public static int port;
    public static String username;
    public MenuLogic menu;

    GameElements gameElements;

    public Controller(int port, String username, String password){
        this.state = "mainMenu";
        this.port = port;

        //SPACES
        threadedComs = new SequentialSpace();
		localRepository = new SpaceRepository();

		localRepository.add("threadedComs", threadedComs);
		localRepository.addGate("tcp://localhost:" + port + "/?keep");
        
        new Thread(new ClientController(username, password)).start();
    }

    public void handleKeyboard(String code){
        if (code == "ENTER"){
            performAction(menu.getAction());
        } else {
            menu.move(code);
        }
    }

    public void performAction(String action){
        switch (action) {
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
            case "mainMenu":
                menu.draw();
                break;

            case "waitingForOtherPlayer":
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
                menu.menuState = "inFight";
                break;

            case "fight":
                menu.draw();
                break;
        }
    }


}