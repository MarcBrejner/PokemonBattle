package com.mycompany.pokemonBattle;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;

class MenuLogic {
    
    public String menuState;
    public Hashtable<String, Menu> menus;
    public Menu currentMenu;
    public GraphicsContext gc;

    public MenuLogic(GraphicsContext gc){
        this.gc = gc;

        //menus
        menus = new Hashtable<String, Menu>();
        menuState = "welcome";
        
        //Welcome menu
        ArrayList<String[]> labelsWelcome = new ArrayList<String[]>();
        labelsWelcome.add(new String[]{"Connect", "connect"});
        labelsWelcome.add(new String[]{"Sign up", "signup"});
        menus.put("welcome", new MenuList(gc, 100, 100, labelsWelcome));
        
        //Connection menu
        ArrayList<String[]> labelsConnection = new ArrayList<String[]>();
        labelsConnection.add(new String[]{"Username :", "form"});
        labelsConnection.add(new String[]{"Password :", "password"});
        labelsConnection.add(new String[]{"Sign in", "signin"});
        labelsConnection.add(new String[]{"Cancel", "welcome"});
        menus.put("connect", new MenuList(gc, 100, 100, labelsConnection));
        
        //Signing-up menu
        ArrayList<String[]> labelsSignup = new ArrayList<String[]>();
        labelsSignup.add(new String[]{"Username :", "form"});
        labelsSignup.add(new String[]{"Password :", "password"});
        labelsSignup.add(new String[]{"Submit", "submitSignup"});
        labelsSignup.add(new String[]{"Cancel", "welcome"});
        menus.put("signup", new MenuList(gc, 100, 100, labelsSignup));

        //main menu
        ArrayList<String[]> labelsMain = new ArrayList<String[]>();
        labelsMain.add(new String[]{"Fight", "fight"});

        labelsMain.add(new String[]{"Pokemons", "pokemons"});
        labelsMain.add(new String[]{"Items", "items"});
        labelsMain.add(new String[]{"Members", "members"});
        labelsMain.add(new String[]{"Disconnect", "disconnect"});
        menus.put("mainMenu", new MenuList(gc, 100, 100, labelsMain));

        //menu 1
        //generateAbilityMenu(Controller.getUser().getPokemons().get(0).getAbilities(),menus);


        //menu 2
        ArrayList<String[]> labels2 = new ArrayList<String[]>();
        labels2.add(new String[]{"Pikachu", "does not matter"});
        labels2.add(new String[]{"Achtuuu!", "does not matter"});
        labels2.add(new String[]{"Bless you", "does not matter"});
        menus.put("choose pokemon", new MenuList(gc, 100, 100, labels2));

        //menu 3
        ArrayList<String[]> labels3 = new ArrayList<String[]>();
        labels3.add(new String[]{"DSadsa", ""});
        labels3.add(new String[]{"dsad", ""});
        labels3.add(new String[]{"sad", ""});
        labels3.add(new String[]{"Cool", ""});
        labels3.add(new String[]{"Duck", ""});
        labels3.add(new String[]{"F", ""});
        labels3.add(new String[]{"Back", "mainMenu"});
        menus.put("menu3", new MenuRect(gc, 100, 100, labels3, 0, 4));

        currentMenu = menus.get(menuState);
    }

    public void move(String code){
        currentMenu.move(code);
    }

    public String getAction(){
        return currentMenu.getAction();
    }

    public void draw(){
        currentMenu.draw();
    }
    
    public void changeMenu(String newMenu) {
    	menuState = newMenu;
    	currentMenu = menus.get(menuState);
    }
    
    public void updateForm(String newForm) {
    	currentMenu.updateForm(newForm);
    }
    
    public void updateCredentialsButton(String code) {
    	currentMenu.setButtonText(code);
    }

    public void generateAbilityMenu(List<Ability> abilityList, Hashtable<String, Menu> menus){
            ArrayList<String[]> labels1 = new ArrayList<String[]>();
            for(int i = 1; i <= Controller.MAX_ABILITY; i++ ){
                labels1.add(new String[]{abilityList.get(i-1).getName() , "Ability"+i});
            }
            menus.put("inFight", new MenuList(gc, 100, 400, labels1));
    }
}