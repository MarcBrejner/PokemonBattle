package com.mycompany.pokemonBattle;

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

class TextBox {
    int x, y, w, h;
    String label;
    Timeline timeline;
    Text text1, text2;
    Rectangle rect1, rect2;
    boolean exists = false;
    String[] labels;
    int labelIdx = 0;
    boolean skipable = true;

    public TextBox(int x, int y, int h, String label){
        this.x = x;
        this.y = y;

        this.h = h;
        this.labels = new String[] {label};
        this.label = label;
    }
    
    public TextBox(int x, int y, int h, String[] labels){
        this.x = x;
        this.y = y;

        this.h = h;
        this.labels = labels;
        label = labels[0];
    }
    
    public TextBox(int x, int y, int h, String label, boolean skipable){
        this.x = x;
        this.y = y;

        this.h = h;
        this.labels = new String[] {label};
        this.label = label;
        this.skipable = skipable;
    }
    
    public boolean exists() {
    	return exists;
    }
    
    public void remove(Group root) {
    	root.getChildren().removeAll(text1,text2,rect1,rect2);
    	labelIdx++;
    	if (labelIdx < labels.length) {
	    	label = labels[labelIdx];
	    	drawTyping(root);
    	} else {
    		exists = false;
    	}
    }

    public void drawBox(Group root){
    	exists = true;

        int th = 3;

        int tx = 0; int ty = 0;
        text1 = new Text(tx, ty, label);
        text1.setFont(Font.font("Courier New",18));
        text1.setFill(Color.BLACK);
        int tw = (int) text1.getLayoutBounds().getWidth();

        int w = tw + 30;
        rect1 = new Rectangle(x, y, w, h);
        rect1.setFill(Color.DARKGREY);
        rect2 = new Rectangle(x+th, y+th, w-(2*th), h-(2*th));
        rect2.setFill(Color.LIGHTGRAY);

        tx = x+(w/2)-(tw/2);
        ty = y+(h/2)+4;
        text1.setX(tx);
        text1.setY(ty);

        root.getChildren().addAll(rect1, rect2, text1);
    }
    
    public void draw(Group root){
    	exists = true;
    	text1 = new Text(x, y, label);
        text1.setFont(Font.font("Courier New",18));
        text1.setFill(Color.BLACK);
        
        if (!skipable) text1.toFront();
        root.getChildren().add(text1);
    }
    
    public boolean isTyping() {
    	return timeline.getStatus() == Status.RUNNING;
    }
    
    public void speedUpTyping(Group root) {
    	if (!skipable) {
    		return;
    	}
    	timeline.jumpTo("end");
    	timeline.stop();
    	root.getChildren().removeAll(text1,text2,rect1,rect2);
    	draw(root);
    }
    
    public void drawTyping(Group root) {
    	exists = true;
    	text2 = new Text(x,y,"");
    	text2.setFont(Font.font("Courier New",18));
        text2.setFill(Color.BLACK);
    	root.getChildren().add(text2);
        final IntegerProperty i = new SimpleIntegerProperty(0);
        timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(
                Duration.seconds(0.05),
                new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
					    if (i.get() > label.length()) {
					        timeline.stop();
					    } else {
					        text2.setText(label.substring(0, i.get()));
					        i.set(i.get() + 1);
					    }
					}
				});
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(timeline.INDEFINITE);

        timeline.play();
    }



}