package com.mycompany.pokemonBattle;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;

class GameElements {

    public PokemonView pokemon1View, pokemon2View, trainer1View, trainer2View;
    public Pokemon pokemon1, pokemon2;
    Bar hpBar1, hpBar2;
    public TextBox tb;
    public Group root;

    public GameElements(Group root){
    	//String path = System.getProperty("user.dir") + "/src/main/java/com/mycompany/pokemonBattle/";
        //pokemon1 = new PokemonView(root, 400, 100, path+"pikaFront.png");
        //pokemon2 = new PokemonView(root, 100, 400, path+"pikaBack.png");

    	this.root = root;
    	pokemon1 = new Pokemon("Pikachu");
    	pokemon2 = new Pokemon("Pikachu");
    	String sprite1 = "com/mycompany/pokemonBattle/pikaBack.png";
    	String sprite2 = "com/mycompany/pokemonBattle/machamp.png";
    	String sprite3 = "com/mycompany/pokemonBattle/trainer1.png";
    	String sprite4 = "com/mycompany/pokemonBattle/trainer2.png";
    	hpBar1 = new Bar(root,100-40,300-40,100,pokemon1.getHP(),Color.RED);
    	hpBar2 = new Bar(root,400-40,100-40,100,pokemon2.getHP(),Color.RED);

    	pokemon1View = new PokemonView(root, 800, 300, sprite1,2);
        pokemon2View = new PokemonView(root, -200, 100, sprite2,2);
        trainer1View = new PokemonView(root,800, 300, sprite3,2);
        trainer2View = new PokemonView(root,-200, 100, sprite4,2);
    }
    
    public void createTextBox(int x, int y, String text) {
    	tb = new TextBox(x,y,18,text);
        tb.drawTyping(root);
    }
    
    public void createTextBox(int x, int y, String[] texts) {
    	tb = new TextBox(x,y,18,texts);
        tb.drawTyping(root);
    }
    
    public boolean textBoxIsTyping() {
    	if (tb == null) {
    		return false;
    	}
    	return tb.isTyping();
    }
    
    public boolean textBoxExists() {
    	if (tb == null) {
    		return false;
    	}
    	return tb.exists();
    }
    
    public void speedUpTextBoxTyping() {
    	tb.speedUpTyping(root);
    }
    
    public void removeTextBox() {
    	tb.remove(root);
    }
    
    public void commenceAttack() {
    	pokemon2.setHP(pokemon2.getHP()-200);
    	pokemon2View.shake();
    	hpBar2.changeContent(pokemon2.getHP());
    }
    
    public void slideOut(){
    	pokemon1View.glide(800);
    	pokemon2View.glide(-200);
    }
    
    public void slideIn(){
    	pokemon1View.glide(100);
    	pokemon2View.glide(400);
    }

    public void draw(){
    	trainer1View.draw();
    	trainer2View.draw();
    	pokemon1View.draw();
    	pokemon2View.draw();
    }
    
    public void drawBars() {
    	hpBar1.draw();
    	hpBar2.draw();
    }
}