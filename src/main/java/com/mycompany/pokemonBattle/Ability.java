package com.mycompany.pokemonBattle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Ability{
	
	public int id;
    private boolean enemy, self;
    private int cost;
    private int value, selfValue;
    private String name, element, enemyStatus, selfStatus;
    
    public Ability(int id, String name, String element, boolean enemy, boolean self, int cost, int value, int selfValue, String enemyStatus, String selfStatus) {
    	this.id = id;
    	this.name = name;
    	this.element = element;
    	this.enemy = enemy;
    	this.self = self;
    	this.cost = cost;
    	this.selfValue = value;
    	this.enemyStatus = enemyStatus;
    	this.selfStatus = selfStatus;
    }

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
        }
    }

    private void bolt(){
		this.value = -30;
		this.selfValue = 0;
        this.enemy = true;
        this.self = false;
        this.cost = 2;
        this.element = "Electric";
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
        Ability gsonAbility = gson.fromJson(json, Ability.class);
        return gsonAbility;
	}

	public void Apply(Pokemon pokemon){

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

    public String getEnemyStatus() {
        return enemyStatus;
	}
	
	public String getSelfStatus() {
		return selfStatus;
	}

}

