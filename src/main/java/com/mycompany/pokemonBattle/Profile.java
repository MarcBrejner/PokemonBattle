package com.mycompany.pokemonBattle;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Profile {
	
	public int id;
	private String username;
	private List<Pokemon> pokemons = new ArrayList<Pokemon>();
	private List<Item> items = new ArrayList<Item>();
	private int level, XP, requiredXP;

	public Profile(int id, String username, int level, int XP, int requiredXP) {
		this.id = id;
		this.username = username;
		this.level = level;
		this.XP = XP;
		this.requiredXP = requiredXP;
	}
	
	public static String toJson(Profile profile){
        Gson gson = new Gson();
        String json = gson.toJson(profile);
        return json;
    }

    public static Profile fromJson(String json){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Profile gsonProfile = gson.fromJson(json, Profile.class);
        return gsonProfile;
	}

	public String getUsername() {
		return username;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public int getXP() {
		return XP;
	}
	
	public void setXP(int XP) {
		this.XP = XP;
	}

	public int getRequiredXP() {
		return requiredXP;
	}
	
	public void setRequiredXP(int XP) {
		requiredXP = XP;
	}
	
	public List<Pokemon> getPokemons() {
		return pokemons;
	}

	public void setPokemons(List<Pokemon> pokemons) {
		this.pokemons = pokemons;
	}

	public void addPokemon(Pokemon pokemon) {
		this.pokemons.add(pokemon);
	}

	public List<Item> getItems() {
		return items;
	}
	
	public void setItems(List<Item> items) {
		this.items = items;
	}

	public void addItem(Item item) {
		this.items.add(item);
	}

	public void removeItem(Item item) {
		items.remove(item);
	}

}
