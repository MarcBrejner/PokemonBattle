import javafx.scene.Group;

class GameElements {

    Pokemon pokemon1, pokemon2;

    public GameElements(Group root){
        pokemon1 = new Pokemon(root, 400, 100, "Pikachu");
        pokemon2 = new Pokemon(root, 100, 400, "Pikachu");
    }

    public void draw(){
        pokemon1.draw();
        pokemon2.draw();
    }

}