package com.mycompany.pokemonBattle;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;

class GameElements {
	
    public PokemonView pokemon1View, pokemon2View, trainer1View, trainer2View;
    public static Pokemon pokemon1, pokemon2;
    Bar hpBar1, hpBar2;
    public TextBox tb;
    public Group root;

    public GameElements(Group root){
    	this.root = root;
    }
    
    public void getPokemonViews() {
    	String path = System.getProperty("user.dir") + "/src/main/java/com/mycompany/pokemonBattle/";
    	String pokemon1name = pokemon1.getName().toLowerCase();
    	String pokemon2name = pokemon2.getName().toLowerCase();
    	pokemon1View = new PokemonView(root, -200, 400, path+pokemon1name+"Back.png",2);
        pokemon2View = new PokemonView(root, 700, 100, path+pokemon2name+"Front.png",2);
       
    	hpBar1 = new Bar(root,100-40,300-40,100,pokemon1.getHP(),Color.RED);
    	hpBar2 = new Bar(root,400-40,100-40,100,pokemon2.getHP(),Color.RED);
    }
    
    public void createTextBox(int x, int y, String text) {
    	tb = new TextBox(x,y,18,text);
        tb.drawTyping(root);
    }
    
    public void createTextBox(int x, int y, String[] texts) {
    	tb = new TextBox(x,y,18,texts);
        tb.drawTyping(root);
    }
    
    public void createTextBox(int x, int y, String text, boolean skipable) {
    	tb = new TextBox(x,y,18,text, skipable);
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
    	if (tb.skipable) tb.remove(root);
    }
    
    public void forceRemoveTextBox() {
    	tb.remove(root);
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
    	System.out.println("Drawing the following pokemons (lol) : "+pokemon1.getName()+" "+pokemon2.getName());
    	pokemon1View.draw();
    	pokemon2View.draw();
    }
    
    public void drawBars() {
    	hpBar1.draw();
    	hpBar2.draw();
    }
    
    public void removeAll() {
    	pokemon1View.remove();
    	pokemon2View.remove();
    	hpBar1.remove();
    	hpBar2.remove();
    }
    
    public void updateBars() {
    	hpBar1.changeContent(pokemon1.getHP());
    	hpBar2.changeContent(pokemon2.getHP());
    }
}