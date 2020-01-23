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
    private static String[] all_abilities = new String[] {"Bolt", "Recover", "Slap", "Sleep"};

    public Ability(String name){
        this.name = name;
        switch(name){
            case "Bolt":
                bolt();
                break;
            case "Recover":
                recover();
                break;
            case "Slap":
                slap();
                break;
            case "Sleep":
            	sleep();
            	break;
        }
    }

    private void bolt(){
		this.value = -30;
		this.selfValue = 0;
        this.enemy = true;
        this.self = false;
        this.cost = 2;
        this.element = "Electric";
        this.type = "Damage";
		this.enemyStatus = "Paralyzed";
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

    private void sleep(){
		this.value = 0;
		this.selfValue = 0;
        this.enemy = true;
        this.self = false;
        this.cost = 2;
        this.element = "Grass";
        this.type = "None";
		this.enemyStatus = "Sleepy";
		this.selfStatus = "None";
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

}

