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
	private int level;

	public Profile(int id, String username, int level) {
		this.id = id;
		this.username = username;
		this.level = level;
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

	public void addItem(Item item) {
		this.items.add(item);
	}

	public void removeItem(Item item) {
		items.remove(item);
	}

}
