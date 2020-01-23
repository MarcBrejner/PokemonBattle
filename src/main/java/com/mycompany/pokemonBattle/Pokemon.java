package com.mycompany.pokemonBattle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Pokemon {
	
	public int id;
	private int HP, level, XP, requiredXP;
	private int maxHP;
    private String name, element, status, frontImage, backImage;
    private List<Ability> abilities = new ArrayList<Ability>();
	private boolean alive;
	
	public Pokemon(int id, String name, String element, String status, Boolean alive, int HP, int maxHP, int level, int XP, int requiredXP) {
		this.id = id;
		this.name = name;
		this.element = element;
		this.status = status;
		this.HP = HP;
		this.maxHP = maxHP;
		this.alive = alive;
		this.level = level;
		this.XP = XP;
		this.requiredXP = requiredXP;
	}
	
	private static String[] all_pokemons = new String[] {
			"Bulbasaur", "Charmander", "Eevee", "Gastly", "Geodude", "Hitmonchan", "Kadabra", "Machop", "Pidgeotto", "Pikachu",
			"Psyduck", "Squirtle"
			};

    public Pokemon(String name) {
		this.name = name;
		this.status = "None";
		this.level = 1;
		this.XP = 0;
		this.requiredXP = 2;
        switch(name){
            case "Bulbasaur":
            	bulbasaur();
            	break;
            case "Charmander":
				charmander();
                break;
            case "Eevee":
            	eevee();
            	break;
            case "Gastly":
            	gastly();
            	break;
            case "Geodude":
            	geodude();
            	break;
            case "Hitmonchan":
            	hitmonchan();
            	break;
            case "Kadabra":
            	kadabra();
            	break;
            case "Machop":
            	machop();
            	break;
            case "Pidgeotto":
            	pidgeotto();
            	break;
            case "Pikachu":
                pikachu();
                break;
            case "Psyduck":
            	psyduck();
            	break;
            case "Squirtle":
            	squirtle();
            	break;
        }
    }

    public int getHP() {
        return HP;
	}

	public void setHP(int HP) {
		this.HP = HP;
	}

	public int getMaxHP() {
        return maxHP;
	}
	
	public void setMaxHP(int maxHP) {
		this.maxHP = maxHP;
	}

    public String getName() {
        return name;
	}
	
	public void setName(String name) {
		this.name = name;
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

    public String getElement() {
        return element;
    }

    public List<Ability> getAbilities() {
        return abilities;
	}
	
	public void setAbilities(List<Ability> abilities) {
		this.abilities = abilities;
	}

	public void addAbility(Ability ability) {
		abilities.add(ability);
	}

    public boolean isAlive() {
        return alive;
	}

	public void toggleAlive() {
		alive = !alive;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

    public static String toJson(Pokemon pokemon){
        Gson gson = new Gson();
        String json = gson.toJson(pokemon);
        return json;
    }

    public static Pokemon fromJson(String json){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Pokemon gsonPokemon = gson.fromJson(json, Pokemon.class);
        return gsonPokemon;
	}
    
    public static Pokemon pickRandom() {
		Random random = new Random(); 
		int i = random.nextInt(all_pokemons.length);
		return new Pokemon(all_pokemons[i]);
	}
	
	// one function for each type of pokemon that can be generated with its capacities

    private void bulbasaur() {
    	maxHP = 45;
    	HP = maxHP;
    	element = "Grass&Poison";
    	abilities.add(new Ability("Acid"));
    	alive = true;
    }
    
    private void charmander(){
        maxHP = 39;
		HP = maxHP;
        element = "Fire";
        abilities.add(new Ability("Slap"));
        alive = true;
    }
    
    private void eevee() {
    	maxHP = 55;
		HP = maxHP;
        element = "Normal";
        abilities.add(new Ability("Bind"));
        alive = true;
    }
    
    private void gastly() {
    	maxHP = 30;
		HP = maxHP;
        element = "Poison";
        abilities.add(new Ability("Acid"));
        alive = true;
    }
    
    private void geodude() {
    	maxHP = 40;
 		HP = maxHP;
        element = "Rock";
        abilities.add(new Ability("Rock Throw"));
        alive = true;
    }
    
    private void hitmonchan() {
    	maxHP = 50;
 		HP = maxHP;
        element = "Fighting";
        abilities.add(new Ability("Double Kick"));
        alive = true;
    }
    
    private void kadabra() {
    	maxHP = 40;
 		HP = maxHP;
        element = "Psychic";
        abilities.add(new Ability("Confusion"));
        alive = true;
    }
    
    private void machop() {
    	maxHP = 70;
		HP = maxHP;
        element = "Fighting";
        abilities.add(new Ability("Double Kick"));
        alive = true;
    }
    
    private void pidgeotto() {
    	maxHP = 63;
		HP = maxHP;
        element = "Flying";
        abilities.add(new Ability("Gust"));
        alive = true;
    }
    
	private void pikachu(){
		maxHP = 35;
		HP = maxHP; 
		element = "Electric";
        abilities.add(new Ability("Bolt"));
        alive = true;
    }
	
	private void psyduck() {
		maxHP = 50;
		HP = maxHP; 
		element = "Water";
        abilities.add(new Ability("Bubble"));
        alive = true;
	}

   private void squirtle() {
	   	maxHP = 44;
		HP = maxHP; 
		element = "Water";
		abilities.add(new Ability("Bubble"));
		alive = true;
   }

}
