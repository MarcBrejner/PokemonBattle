package com.mycompany.pokemonBattle;

import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Item {

	public int id;
	private String name, type;
	private int value;
	private int number;
	
	public Item(int id, String name, String type, int value, int number) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.value = value;
		this.number = number;
	}
	
	private static String[] all_items = new String[] {"Potion", "Super potion"};

	public Item(String name) {
		this.name = name;
		this.number = 1;
		switch(name) {
			case "Potion":
				potion();
			case "Super potion":
				superPotion();
		}
	}
	
	public static String toJson(Item item){
        Gson gson = new Gson();
        String json = gson.toJson(item);
        return json;
    }

    public static Item fromJson(String json){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Item gsonItem = gson.fromJson(json, Item.class);
        return gsonItem;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public int getValue() {
		return value;
	}
	
	public int getNumber() {
		return number;
	}
	
	public void add(int i) {
		number += i;
	}
	
	public void consume(int i) {
		number -= i;
	}
	
	public static Item pickRandom() {
		Random random = new Random(); 
		int i = random.nextInt(all_items.length);
		return new Item(all_items[i]);
	}

	// one function per type of item that can be generated

	private void potion() {
		type = "Heal";
		value = 25;
	}

	private void superPotion() {
		type = "Heal";
		value = 50;
	}

}

