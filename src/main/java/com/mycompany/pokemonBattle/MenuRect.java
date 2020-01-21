package com.mycompany.pokemonBattle;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;

class MenuRect implements Menu{
    int x, y, selectedIdx, width, effSize;
    GraphicsContext gc;
    ArrayList<Button> buttons;
    
    public MenuRect(GraphicsContext gc, int x, int y, ArrayList<String[]> labels, int spaceX, int width){
        this.gc = gc;
        this.x = x;
        this.y = y;
        this.width = width;
        this.buttons = new ArrayList<Button>();
        
        int bx = x;
        int by = y;
        int counter = 0;
        for (String[] label:labels) {
            //creates list of buttons
            String text = label[0];
            String state = label[1];
            bx = x + (counter%width)*100;
            if (counter%width == 0) {
                by = by + 22;
            }
            this.buttons.add(new Button(gc,bx,by,text,state,false));
            counter++;
        }
        this.selectedIdx = 0;
        this.buttons.get(selectedIdx).selected = true;
        
        for (int i = 0; i < 1000; i++) {
            if (i*width > buttons.size()) {
                this.effSize = i*width;
                break;
            }
        }
        
    }
    
    public String getAction(){
        return buttons.get(selectedIdx).state;
    }

    public void updateForm(String nothing) {
    	//do not delete this method
    }
    
    public void move(String dir) {
        if (dir == "UP") {
            buttons.get(selectedIdx).selected = false;
            selectedIdx = selectedIdx - width;
            if (selectedIdx < 0) {
                selectedIdx = effSize+selectedIdx;
                if (selectedIdx > buttons.size()-1) {
                    selectedIdx = buttons.size()-1;
                }
            }
            
            buttons.get(selectedIdx).selected = true;
        } else if (dir == "DOWN") {
            buttons.get(selectedIdx).selected = false;
            selectedIdx = (selectedIdx + width)%effSize;
            if (selectedIdx >= buttons.size()) {
                selectedIdx = buttons.size()-1;
            }
            buttons.get(selectedIdx).selected = true;
        } else if (dir == "RIGHT") {
            buttons.get(selectedIdx).selected = false;
            selectedIdx++;
            if (selectedIdx >= buttons.size()) {
                selectedIdx = 0;
            }
            buttons.get(selectedIdx).selected = true;
        } else if (dir == "LEFT") {
            buttons.get(selectedIdx).selected = false;
            selectedIdx--;
            if (selectedIdx < 0) {
                selectedIdx = buttons.size()-1;
            }
            buttons.get(selectedIdx).selected = true;
        }
    }
    
    public void draw() {
        for (Button button : buttons) {
            button.draw();
        }
    }

	public void setButtonText(String txt) {
		String fix_part = buttons.get(selectedIdx).label.split(":")[0];
    	buttons.get(selectedIdx).label = fix_part + ": " + txt;
	}

	public void typingHandler(String code, String text, boolean shifted) {
		//Dont delete this nonsense
	}

	public String[] getForms() {
		//ignore and dont delete
		return null;
	}
}