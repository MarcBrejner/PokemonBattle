package com.mycompany.pokemonBattle;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

class Button {
    int x, y, counter;
    public String label,state, form;
    GraphicsContext gc;
    boolean selected, isForm, drawCursor, isPassword;
    public Button(GraphicsContext gc, int x, int y, String label, String state, boolean selected) {
        this.x = x;
        this.y = y;
        this.label = label;
        this.state = state;
        this.gc = gc;
        this.selected = selected;
        this.isForm = false;
        this.form = "";
        this.counter = 0;
    }
    
    public Button(GraphicsContext gc, int x, int y, String label, String state, boolean selected, boolean isForm, boolean isPassword) {
        this.x = x;
        this.y = y;
        this.label = label;
        this.state = state;
        this.gc = gc;
        this.selected = selected;
        this.isForm = true;
        this.form = "";
        this.isPassword = isPassword;
    }
    
    public void updateForm(String newForm) {
    	form = newForm;
    }
    
    public void draw() {
        gc.setFont(Font.font("Courier New",18));
        gc.setFill(Color.BLACK);
        counter++;
        if (counter > 60) {
        	counter = 0;
        }
        
        String cursor;
        
        if (isForm) { //draw the input text next to the label
        	if (counter > 30 && selected) {
        		cursor = "|";
        	} else {
        		cursor = "";
        	}
        	
        	if (isPassword) {
        		String hiddenForm = "";
        		for (int i = 0; i<form.length(); i++) {
        			hiddenForm = hiddenForm + "*";
        		}
        		gc.fillText(label+"   "+hiddenForm+cursor, x, y);
        	} else {
        		gc.fillText(label+"   "+form+cursor, x, y);
        	}
        	
        } else {
        	gc.fillText(label, x, y);
        }
        if (selected) {
            gc.fillRect(x-8, y-7, 6, 6);
        }
    }
}