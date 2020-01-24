package com.mycompany.pokemonBattle;

import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Ability{
	
	public int id;
    private boolean enemy, self;
    private int cost;
    private int value, selfValue;
    private String name, element, type, enemyStatus, selfStatus;
    
    public Ability(int id, String name, String element, String type, boolean enemy, boolean self, int cost, int value, int selfValue, String enemyStatus, String selfStatus) {
    	this.id = id;
    	this.name = name;
    	this.element = element;
    	this.type = type;
    	this.enemy = enemy;
    	this.self = self;
    	this.cost = cost;
    	this.value = value;
    	this.selfValue = selfValue;
    	this.enemyStatus = enemyStatus;
    	this.selfStatus = selfStatus;
    }
    
    // list of all possible abilities, used to pick a random one
    private static String[] all_abilities = new String[] {
    			"Acid", "Bind", "Bolt", "Bonemerang", "Bubble", "Confusion", "Double Kick", "Flamethrower", "Gust", "Hypnosis",
    			"Razor Leaf", "Recover", "Rock Throw", "Rolling Kick", "Sky Attack", "Slap", "Sleep", "Splash", "Submission", "Thunder",
    			"Water Gun"
    		};

    public Ability(String name){
        this.name = name;
        switch(name){
        	case "Acid":
        		acid();
        		break;
        	case "Bind":
        		bind();
        		break;
            case "Bolt":
                bolt();
                break;
            case "Bonemerang":
            	bonemerang();
            	break;
            case "Bubble":
            	bubble();
            	break;
            case "Confusion":
            	confusion();
            	break;
            case "Double Kick":
            	doubleKick();
            	break;
            case "Flamethrower":
            	flamethrower();
            	break;
            case "Gust":
            	gust();
            	break;
            case "Hypnosis":
            	hypnosis();
            	break;
            case "Razor Leaf":
            	razorLeaf();
            	break;
            case "Recover":
                recover();
                break;
            case "Rock Throw":
            	rockThrow();
            	break;
            case "Rolling Kick":
            	rollingKick();
            	break;
            case "Sky Attack":
            	skyAttack();
            	break;
            case "Slap":
                slap();
                break;
            case "Sleep":
            	sleep();
            	break;
            case "Splash":
            	splash();
            	break;
            case "Submission":
            	submission();
            	break;
            case "Thunder":
            	thunder();
            	break;
            case "Water Gun":
            	waterGun();
            	break;
        }
    }
    
    public static String toJson(Ability ability){
        Gson gson = new Gson();
        String json = gson.toJson(ability);
        return json;
    }

    public static Ability fromJson(String json){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Ability gsonAbility = gson.fromJson(json, Ability.class); //this results in an error
        return gsonAbility;
	}

	public void Apply(Pokemon myPokemon, Pokemon enemyPokemon){
        if(isOnSelf()){
            myPokemon.setHP(myPokemon.getHP()+selfValue);
            myPokemon.setStatus(selfStatus);
        }
        if(isOnEnemy()){
            enemyPokemon.setHP(enemyPokemon.getHP()+value);
            enemyPokemon.setStatus(enemyStatus);
        }
    }

    public boolean isOnEnemy() {
        return enemy;
    }

    public boolean isOnSelf() {
        return self;
    }

    public int getCost() {
        return cost;
    }

    public int getValue() {
        return value;
	}
	
	public int getSelfValue() {
        return selfValue;
    }

    public String getName() {
        return name;
    }

    public String getElement() {
        return element;
    }
    
    public String getType() {
    	return type;
    }

    public String getEnemyStatus() {
        return enemyStatus;
	}
	
	public String getSelfStatus() {
		return selfStatus;
	}
	
	public static Ability pickRandom() {
		Random random = new Random(); 
		int i = random.nextInt(all_abilities.length);
		return new Ability(all_abilities[i]);
	}
	
	// one function per pokemon
    
    private void acid() {
    	this.value = -40;
    	this.selfValue = 0;
    	this.enemy = true;
    	this.self = false;
        this.cost = 1;
        this.element = "Poison";
        this.type = "Damage";
		this.enemyStatus = "None";
		this.selfStatus = "None";
    }
    
    private void bind() {
    	this.value = -15;
		this.selfValue = 0;
        this.enemy = true;
        this.self = false;
        this.cost = 1;
        this.element = "Normal";
        this.type = "Damage";
		this.enemyStatus = "Trapped";
		this.selfStatus = "None";
    }

    private void bolt(){
		this.value = -90;
		this.selfValue = 0;
        this.enemy = true;
        this.self = false;
        this.cost = 2;
        this.element = "Electric";
        this.type = "Damage";
		this.enemyStatus = "Paralyzed";
		this.selfStatus = "None";
    }
    
    private void bonemerang() {
    	this.value = -50;
		this.selfValue = 0;
        this.enemy = true;
        this.self = false;
        this.cost = 1;
        this.element = "Ground";
        this.type = "Damage";
		this.enemyStatus = "None";
		this.selfStatus = "None";
    }
    
    private void bubble() {
    	this.value = -40;
		this.selfValue = 0;
        this.enemy = true;
        this.self = false;
        this.cost = 1;
        this.element = "Water";
        this.type = "Damage";
		this.enemyStatus = "None";
		this.selfStatus = "None";
    }
    
    private void confusion() {
    	this.value = -50;
		this.selfValue = 0;
        this.enemy = true;
        this.self = false;
        this.cost = 1;
        this.element = "Psychic";
        this.type = "Damage";
		this.enemyStatus = "Confused";
		this.selfStatus = "None";
    }
    
    private void doubleKick() {
    	this.value = -60;
		this.selfValue = 0;
        this.enemy = true;
        this.self = false;
        this.cost = 2;
        this.element = "Fighting";
        this.type = "Damage";
		this.enemyStatus = "None";
		this.selfStatus = "None";
    }
    
    private void flamethrower() {
    	this.value = -90;
		this.selfValue = 0;
        this.enemy = true;
        this.self = false;
        this.cost = 1;
        this.element = "Fire";
        this.type = "Damage";
		this.enemyStatus = "Burned";
		this.selfStatus = "None";
    }
    
    private void gust() {
    	this.value = -40;
		this.selfValue = 0;
        this.enemy = true;
        this.self = false;
        this.cost = 1;
        this.element = "Flying";
        this.type = "Damage";
		this.enemyStatus = "None";
		this.selfStatus = "None";
    }
    
    private void hypnosis() {
    	this.value = 0;
    	this.selfValue = 0;
    	this.enemy = true;
    	this.self = false;
    	this.cost = 1;
    	this.element = "Psychic";
    	this.type = "Status";
    	this.enemyStatus = "Sleepy";
    	this.selfStatus = "None";
    }
    
    private void razorLeaf() {
    	this.value = -55;
		this.selfValue = 0;
        this.enemy = true;
        this.self = false;
        this.cost = 1;
        this.element = "Grass";
        this.type = "Damage";
		this.enemyStatus = "None";
		this.selfStatus = "None";
    }
    
    private void recover(){
		this.value = 0;
		this.selfValue = 30;
        this.enemy = false;
        this.self = true;
        this.cost = 3;
        this.element = "Normal";
        this.type = "Heal";
		this.enemyStatus = "None";
		this.selfStatus = "None";
    }
    
    private void rockThrow() {
    	this.value = -50;
		this.selfValue = 0;
        this.enemy = true;
        this.self = false;
        this.cost = 1;
        this.element = "Rock";
        this.type = "Damage";
		this.enemyStatus = "None";
		this.selfStatus = "None";
    }
    
    private void rollingKick() {
    	this.value = -60;
		this.selfValue = 0;
        this.enemy = true;
        this.self = false;
        this.cost = 1;
        this.element = "Fighting";
        this.type = "Damage";
		this.enemyStatus = "None";
		this.selfStatus = "None";
    }
    
    private void skyAttack(){
		this.value = -140;
		this.selfValue = -20;
        this.enemy = true;
        this.self = true;
        this.cost = 2;
        this.element = "Flying";
        this.type = "Damage";
		this.enemyStatus = "None";
		this.selfStatus = "None";
    }

    private void slap(){
		this.value = -30;
		this.selfValue = 0;
        this.enemy = true;
        this.self = false;
        this.cost = 1;
        this.element = "Normal";
        this.type = "Damage";
		this.enemyStatus = "None";
		this.selfStatus = "None";
    }

    private void sleep(){
		this.value = 0;
		this.selfValue = 100;
        this.enemy = false;
        this.self = true;
        this.cost = 2;
        this.element = "Grass";
        this.type = "Status";
		this.enemyStatus = "None";
		this.selfStatus = "Sleepy";
    }
    
    private void splash() {
    	this.value = 0;
    	this.selfValue = 0;
    	this.enemy = false;
    	this.self = false;
    	this.cost = 1;
    	this.element = "Normal";
    	this.type = "None";
    	this.enemyStatus = "None";
    	this.selfStatus = "None";
    }
    
    private void submission() {
    	this.value = -45;
    	this.selfValue = 0;
    	this.enemy = true;
    	this.self = false;
    	this.cost = 1;
    	this.element = "Fighting";
    	this.type = "Damage";
    	this.enemyStatus = "None";
    	this.selfStatus = "None";
    }
    
    private void thunder() {
    	this.value = -110;
    	this.selfValue = 0;
    	this.enemy = true;
    	this.self = false;
    	this.cost = 3;
    	this.element = "Electric";
    	this.type = "Damage";
    	this.enemyStatus = "None";
    	this.selfStatus = "None";
    }
    
    private void waterGun() {
    	this.value = -40;
    	this.selfValue = 0;
    	this.enemy = true;
    	this.self = false;
    	this.cost = 1;
    	this.element = "Water";
    	this.type = "Damage";
    	this.enemyStatus = "None";
    	this.selfStatus = "None";
    }
}

