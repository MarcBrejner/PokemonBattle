package com.mycompany.pokemonBattle;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;



class MenuList implements Menu {
    int x, y, selectedIdx;
    GraphicsContext gc;
    ArrayList<Button> buttons;
    
    public MenuList(GraphicsContext gc, int x, int y, ArrayList<String[]> labels) {
        this.gc = gc;
        this.x = x;
        this.y = y;
        buttons = new ArrayList<Button>();
        
        int by = y;
        for (String[] label:labels) {
            //creates list of buttons
            String text = label[0];
            String state = label[1];
            if (state.equals("form") || state.equals("password")) {
            	buttons.add(new Button(gc,x,by,text,state,false,true,state.equals("password")));
            } else {
            	buttons.add(new Button(gc,x,by,text,state,false));
            }
            by = by + 22;
        }
        
        this.selectedIdx = 0;
        buttons.get(selectedIdx).selected = true;
    }

    public String getAction(){
        return buttons.get(selectedIdx).state;
    }
    
    public void updateForm(String newForm) {
    	buttons.get(selectedIdx).updateForm(newForm);
    }
    
    public String getForm() {
    	return buttons.get(selectedIdx).form;
    }
    
    public String[] getForms() {
    	int idx = 0;
    	String[] forms = new String[50]; //we presume no more than 50 forms
    	for (Button button: buttons) {
    		if (button.isForm) {
    			forms[idx] = button.form;
    			idx++;
    		}
    	}
    	return forms;
    }
    
    public void typingHandler(String code, String text, boolean shifted) {
    	Button currentButton = buttons.get(selectedIdx);
    	if(code == "BACK_SPACE") {
    		if (currentButton.form != null && currentButton.form.length()>0) {
    			currentButton.form = currentButton.form.substring(0, currentButton.form.length()-1);
			}
    	} else if (text.length()>0) {
    		if (shifted) {
    			text = text.toUpperCase();
    		} else {
    			text = text.toLowerCase();
    		}
    		
    		currentButton.form += text;
    	}
    }
    
    public void move(String dir) {
        if (dir == "UP") {
            buttons.get(selectedIdx).selected = false;
            selectedIdx--;
            if (selectedIdx < 0) {
                selectedIdx = buttons.size()-1;
            }
            buttons.get(selectedIdx).selected = true;
        } else if (dir == "DOWN") {
            buttons.get(selectedIdx).selected = false;
            selectedIdx++;
            if (selectedIdx >= buttons.size()) {
                selectedIdx = 0;
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
}