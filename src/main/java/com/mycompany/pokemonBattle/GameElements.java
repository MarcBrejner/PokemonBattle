package com.mycompany.pokemonBattle;

import javafx.scene.Group;

class GameElements {

    PokemonView pokemon1, pokemon2;

    public GameElements(Group root){
    	String path = System.getProperty("user.dir") + "/src/main/java/com/mycompany/pokemonBattle/";
        pokemon1 = new PokemonView(root, 400, 100, path+"pikaFront.png");
        pokemon2 = new PokemonView(root, 100, 400, path+"pikaBack.png");
    }

    public void draw(){
        pokemon1.draw();
        pokemon2.draw();
    }

}