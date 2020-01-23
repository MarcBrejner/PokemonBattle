package com.mycompany.pokemonBattle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {

	private Connection conn;
    private PreparedStatement insertProfile, getProfile, setProfile;
    private PreparedStatement insertItem, getItems, setItem, deleteItem;
    private PreparedStatement insertPokemon, getPokemons, setPokemon;
    private PreparedStatement insertAbility, getAbilities;
    private PreparedStatement insertCredentials, getCredentials, setPassword;

	public static void main(String[] args) {
		Database database = new Database("pokemonGame", "root", Config.db_password);
		
		//Profile p1 = database.getProfile("vincent");
		//Profile p2 = database.getProfile("franck");
		//database.authenticate("vincent", "password");
		//database.changePassword("vincent", "pwd");
		//System.out.println("User created : " + database.createUser("vincent", "pwd"));
	}

	public Database(String db, String account, String pwd) {
		try {

            //Registering the driver
            Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("Driver found...");

            //connection to database
            this.conn = DriverManager.getConnection("jdbc:mysql://" + Config.db_host + ":3306 /" + db + "?serverTimezone=UTC", account, pwd);
            System.out.println("Established connection...");

            // Prepared Statement
            this.insertCredentials = this.conn.prepareStatement("INSERT INTO Authentication (username, password) VALUES (?, ?);");
            this.insertProfile = this.conn.prepareStatement("INSERT INTO Profile (username) VALUES (?);");
            
            this.getCredentials = this.conn.prepareStatement("SELECT * FROM Authentication WHERE username = ?;");
            this.getProfile = this.conn.prepareStatement("SELECT * FROM Profile WHERE username = ?;");
            this.getItems = this.conn.prepareStatement("SELECT * FROM Item WHERE ownerId = ?;");
            this.getPokemons = this.conn.prepareStatement("SELECT * FROM Pokemon WHERE masterId = ?;");
            this.getAbilities = this.conn.prepareStatement("SELECT * FROM Ability WHERE pokemonId = ?;");
            
            this.setPassword = this.conn.prepareStatement("UPDATE Authentication SET password = ? WHERE username = ?;");
            this.setProfile = this.conn.prepareStatement("UPDATE Profile SET username = ?, level = ? WHERE id = ?;");
            this.setItem = this.conn.prepareStatement("UPDATE Item SET number = ? WHERE id = ?;");
            this.setPokemon = this.conn.prepareStatement("UPDATE Pokemon SET hp = ?, maxHp = ?, status = ?, alive = ? WHERE id = ?;");
            
            this.deleteItem = this.conn.prepareStatement("DELETE FROM Item WHERE id = ?;");

        }  catch (SQLException ex) {
            ex.printStackTrace(System.err);
            System.exit(-1);
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean authenticate(String username, String password) {
		try {
            this.getCredentials.setString(1, username);
        } catch (SQLException ex) {
            ex.printStackTrace();
		}
		
		int id=0;
		String username_="default", password_="default";
		
		try {
            ResultSet rs = this.getCredentials.executeQuery();
        	while(rs.next()){
            	id = rs.getInt("id");
				username_ = rs.getString("username");
				password_ = rs.getString("password");
			}
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
		
		System.out.println("Testing password for username : " + username_);	
		return password.equals(password_);
		
	}
	
	public boolean createUser(String username, String password) {
		try {
			// authentication in the first place
			this.insertCredentials.setString(1, username);
			this.insertCredentials.setString(2, password);
            this.insertCredentials.execute();
			
			// then the profile
            this.insertProfile.setString(1, username);
            this.insertProfile.execute();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
		}
		return true;
		
	}

	public Profile getProfile(String username) {
      	try {
            this.getProfile.setString(1, username);
        } catch (SQLException ex) {
            ex.printStackTrace();
		}
		
      	int id=0, level=0;
		String username_ = "default";
     
        try {
            ResultSet rs = this.getProfile.executeQuery();
            while(rs.next()){
            	id = rs.getInt("id");
				username_ = rs.getString("username");
				level = rs.getInt("level");
			}
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
		
		Profile profile = new Profile(id, username_, level);
		System.out.println("Profile object for " + username_ + "[id:" + id + "] created... with level : " + level);
		this.getItems(profile);
		this.getPokemons(profile);
		
		System.out.println("All pokemons of " + profile.getUsername() + " :");
		for(Pokemon pokemon : profile.getPokemons()) {
			System.out.println("	- Pokemon " + pokemon.getName() + " has abilities :");
			for(Ability ability : pokemon.getAbilities()) {
				System.out.println("		- " + ability.getName());
			}
		}
		
		return profile;
	}
	
	public void updateProfile(Profile profile) {
		try {
            this.setProfile.setString(1, profile.getUsername());
            this.setProfile.setInt(2, profile.getLevel());
            this.setProfile.setInt(3, profile.id);
            this.setProfile.executeUpdate();
            
            for(Item item : profile.getItems()) {
            	updateItem(item);
            }
            for(Pokemon pokemon : profile.getPokemons()) {
            	updatePokemon(pokemon);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
		}
	}
	
	public void getItems(Profile profile) {
		try {
            this.getItems.setInt(1, profile.id);
        } catch (SQLException ex) {
            ex.printStackTrace();
		}
		
		int id = 0, value = 0, number = 0;
		String name = "default", type = "default";
     
        try {
            ResultSet rs = this.getItems.executeQuery();
            while(rs.next()){
            	id = rs.getInt("id");
            	name = rs.getString("name");
            	type = rs.getString("type");
            	value = rs.getInt("value");
            	number = rs.getInt("number");
            	
            	Item item = new Item(id, name, type, value, number);
        		System.out.println("Got item " + item.getName() + " from database");
        		profile.addItem(item);
        		System.out.println(profile.getUsername() + " got (" + item.getNumber() + ") item " + item.getName() + " of type " + item.getType());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
	}
	
	public void updateItem(Item item) {
		if(item.getNumber() > 0) {
			try {
	            this.setItem.setInt(1, item.getNumber());
	            this.setItem.setInt(2, item.id);
	            this.setItem.executeUpdate();
	        } catch (SQLException ex) {
	            ex.printStackTrace();
			}
		} else {
			try {
				this.deleteItem.setInt(1, item.id);
				this.setItem.execute();
			} catch (SQLException ex) {
	            ex.printStackTrace();
			}
		}
	}
	
	public void getPokemons(Profile profile) {
		try {
            this.getPokemons.setInt(1, profile.id);
        } catch (SQLException ex) {
            ex.printStackTrace();
		}
		
		int id = 0, HP = 0, maxHP = 0;
		String name = "default", element = "default", status = "default";
		boolean alive = false;
		
		try {
            ResultSet rs = this.getPokemons.executeQuery();
            while(rs.next()){
            	id = rs.getInt("id");
            	name = rs.getString("name");
            	element = rs.getString("element");
            	status = rs.getString("status");
            	HP = rs.getInt("hp");
            	maxHP = rs.getInt("maxHp");
            	alive = rs.getBoolean("alive");
            	
            	Pokemon pokemon = new Pokemon(id, name, element, status, alive, HP, maxHP);
            	System.out.println(profile.getUsername() + " got pokemon " + pokemon.getName());
            	this.getAbilities(pokemon);
            	profile.addPokemon(pokemon);
            	
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
	}
	
	public void updatePokemon(Pokemon pokemon) {
		try {
            this.setPokemon.setInt(1, pokemon.getHP());
            this.setPokemon.setInt(2, pokemon.getMaxHP());
            this.setPokemon.setString(3, pokemon.getStatus());
            this.setPokemon.setBoolean(4, pokemon.isAlive());
            this.setPokemon.setInt(5,  pokemon.id);
            this.setPokemon.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
		}
	}
	
	public void getAbilities(Pokemon pokemon) {
		try {
            this.getAbilities.setInt(1, pokemon.id);
        } catch (SQLException ex) {
            ex.printStackTrace();
		}
		
		int id = 0, value=0, selfValue=0, cost=0;
		boolean enemy=false, self=false;
		String name="default", element="default", enemyStatus="default", selfStatus="default", type="default";
		
		try {
            ResultSet rs = this.getAbilities.executeQuery();
            while(rs.next()){
            	id = rs.getInt("id");
            	name = rs.getString("name");
            	element = rs.getString("element");
            	type = rs.getString("type");
            	enemyStatus = rs.getString("enemyStatus");
            	selfStatus = rs.getString("selfStatus");
            	value = rs.getInt("value");
            	selfValue = rs.getInt("selfValue");
            	cost = rs.getInt("cost");
            	enemy = rs.getBoolean("enemyEffect");
            	self = rs.getBoolean("selfEffect");
            	
            	Ability ability = new Ability(id, name, element, enemy, self, cost, value, selfValue, enemyStatus, selfStatus);
            	System.out.println("Extracted ability " + ability.getName() + " from database");
            	pokemon.addAbility(ability);
            	System.out.println(pokemon.getName() + " got ability " + ability.getName() + "(element " + ability.getElement() + ")");
            	
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
	}
	
	public void changePassword(String username, String password) {
		try {
            this.setPassword.setString(2, username);
            this.setPassword.setString(1, password);
            this.setPassword.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
		}
		
		boolean check = authenticate(username, password);
		if(check) {
			System.out.println("Password correctly updated !");
		} else {
			System.out.println("An error occured...");
		}
	}
}