package com.mycompany.pokemonBattle;

import javafx.scene.Group;

class GameElements {

    PokemonView pokemon1, pokemon2;

    public GameElements(Group root){
        pokemon1 = new PokemonView(root, 400, 100, "./pikaBack.png");
        pokemon2 = new PokemonView(root, 100, 400, "./pikaFront.png");
    }

    public void draw(){
        pokemon1.draw();
        pokemon2.draw();
    }

}