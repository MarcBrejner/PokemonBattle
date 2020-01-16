package com.mycompany.pokemonBattle;

import org.jspace.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

public class FightHandler {
    private Pokemon myPokemon, enemyPokemon;
    private Profile me, enemy;
    public RemoteSpace actions;
    public String action;
    private boolean turn = false;


    public FightHandler(Profile user, String URI) throws InterruptedException {

            try {

                // connecting to action and data spaces
                System.out.println("Connection to tcp://" + Config.fightsHost + "/" + URI + "/actions?keep...");
                System.out.println("Connection to tcp://" + Config.fightsHost + "/" + URI + "/data?keep...");
                actions = new RemoteSpace("tcp://" + Config.fightsHost + "/" + URI + "/actions?keep");
                //data = new RemoteSpace("tcp://" + Config.fightsHost + "/" + URI + "/data?keep");
                System.out.println("Connected to data and actions spaces");

                //BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

                System.out.println("Waiting for opponent data reception...");
                String e = (String) actions.get(new ActualField(user.getUsername()), new FormalField(String.class))[1];
                System.out.println("Received data : " + e);
                enemy = Profile.fromJson(e);
                me = user;
                myPokemon = new Pokemon("Pikachu");
                enemyPokemon = new Pokemon("Pikachu");

                while(true) {


                    System.out.println("Waiting for action input...");
                    action = (String) actions.get(new FormalField(String.class),new ActualField(user.getUsername()))[0];
                    System.out.println("ACTION RECEIVED : " + action);

                    if(action.equals("BYE")) {
                        actions.put("BYE_ACK");
                        actions.put("END");
                        break;
                    } else if(action.equals("START")) {
                        System.out.println("THE FIGHT CAN START !");
                        turn = true;
                    } else {
                        // depending on the action different types of data and behaviours can occur
                        //System.out.println("Waiting to receive data...");
                        //String data_received = (String)data.get(new FormalField(String.class))[0];
                        //System.out.println("DATA : " + data_received);
                    }

                    System.out.println("Type an action to make :");
                    String action_input = input.readLine();
                    actions.put(action_input);
                    if(action_input.equals("BYE")){
                        System.out.println("Waiting for acknowledgment of BYE...");
                        actions.get(new ActualField("BYE_ACK"));
                        break;
                    }
                    //System.out.println("Type data to send :");
                    //String data_input = input.readLine();
                    //data.put(data_input);
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





        this.myProfile = myProfile;

        //Set enemy and own pokemonGame
        updatePokemons(threadedComs);

        //update GameView
        while (true) {
            threadedComs.get(new ActualField("TURN START"),new ActualField("PLAYERID"));
            takeTurn(threadedComs);

            if (myPokemon.getHP() <= 0) {
                threadedComs.put("DEAD","PLAYERID");
                break;
            }
        }
    }

    public void takeTurn(SequentialSpace threadedComs) throws InterruptedException{

        //Get pokemonGame states from ClientController
        updatePokemons(threadedComs);

        //Update GameView

        //Get selected ability from GameView
        Ability selectedAbility = new Ability("Slap"); //placeholder

        //Apply selected ability
        if(selectedAbility.isOnEnemy()){
            selectedAbility.Apply(enemyPokemon);
        }

        if(selectedAbility.isOnSelf()){
            selectedAbility.Apply(myPokemon);
        }

        //Update GameView

        //Update pokemonGame states via ClientController
        updatePokemons(threadedComs);


    }

    public void retrievePokemons(SequentialSpace threadedComs) throws InterruptedException{
        myPokemon = Pokemon.fromJson((String) threadedComs.get(new FormalField(String.class),new ActualField("PLAYERID"))[0]);
        enemyPokemon = Pokemon.fromJson((String) threadedComs.get(new FormalField(String.class),new ActualField("ENEMYPLAYERID"))[0]);
    }

    public void updatePokemons(SequentialSpace threadedComs) throws InterruptedException{
        threadedComs.put("PLAYER1POKEMON",Pokemon.toJson(myPokemon));
        threadedComs.put("PLAYER2POKEMON",Pokemon.toJson(enemyPokemon));
    }

    public Pokemon getMyPokemon(){
        return myPokemon;
    }

    public Pokemon getEnemyPokemon(){
        return enemyPokemon;
    }

}
