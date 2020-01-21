package com.mycompany.pokemonBattle;

import org.jspace.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

public class FightHandler {
    private Pokemon myPokemon, enemyPokemon;
    private Profile me, enemy;
    public RemoteSpace actions,data;
    public String action;
    public Profile user;
    public String URI;
    public SequentialSpace threadedComs;
    private boolean turn = false;


    public FightHandler(Profile user, String URI, SequentialSpace threadedComs) throws InterruptedException, IOException {
        this.user = user;
        this.URI = URI;
        this.threadedComs = threadedComs;
    }

    public void run(){
        try {

            // connecting to action and data spaces
            System.out.println("Connection to tcp://" + Config.fightsHost + "/" + URI + "/actions?keep...");
            System.out.println("Connection to tcp://" + Config.fightsHost + "/" + URI + "/data?keep...");
            actions = new RemoteSpace("tcp://" + Config.fightsHost + "/" + URI + "/actions?keep");
            data = new RemoteSpace("tcp://" + Config.fightsHost + "/" + URI + "/data?keep");
            System.out.println("Connected to data and actions spaces");

            //BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Waiting for opponent data reception...");
            String e = (String) actions.get(new ActualField(user.getUsername()), new FormalField(String.class))[1];
            System.out.println("Received data : " + e);
            enemy = Profile.fromJson(e);
            me = user;


            while (true) {


                retrievePokemons();

                //Draw pokemon


                System.out.println("Waiting for action input...");
                action = (String) actions.get(new FormalField(String.class), new ActualField(me.getUsername()))[0];
                System.out.println("ACTION RECEIVED : " + action);

                if (action.equals("BYE")) {
                    actions.put("BYE_ACK");
                    actions.put("END");
                    break;
                } else if (action.equals("START")) {
                    System.out.println("THE FIGHT CAN START !");
                } else if (action.equals("GO")) {
                    System.out.println("YOUR TURN");
                } else if (action.equals("ENEMYGO")) {
                    System.out.println("ENEMYS TURN");
                }

                System.out.println("Select action");
                Object t[] = threadedComs.get(new FormalField(String.class), new FormalField(String.class));

                switch ((String) t[0]) {
                    case "ABILITY":
                        Ability chosenAbility = Ability.fromJson((String) t[1]);
                        chosenAbility.Apply(myPokemon, enemyPokemon);
                        break;
                    case "ITEM":
                        Item chosenItem = Item.fromJson((String) t[1]);
                }

                updatePokemons();

            }

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void retrievePokemons() throws InterruptedException{
        myPokemon = Pokemon.fromJson((String) data.get(new FormalField(String.class), new ActualField(me.getUsername()))[0]);
        enemyPokemon = Pokemon.fromJson((String) data.get(new FormalField(String.class), new ActualField(enemy.getUsername()))[0]);
    }

    public void updatePokemons() throws InterruptedException{
        data.put(Pokemon.toJson(myPokemon),me.getUsername());
        data.put(Pokemon.toJson(enemyPokemon),enemy.getUsername());
    }

    public Pokemon getMyPokemon(){
        return myPokemon;
    }

    public Pokemon getEnemyPokemon(){
        return enemyPokemon;
    }

}
