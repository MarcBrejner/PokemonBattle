package com.mycompany.pokemonBattle;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.animation.Animation.Status;
import java.util.ArrayList;

class SplashScreen {
    Group root;
    int sizeX, sizeY, gridSize;
    FadeTransition ft;
    ArrayList<FadeTransition> fades = new ArrayList<FadeTransition>();

    public SplashScreen (Group root, int sizeX, int sizeY, int gridSize){
        this.root = root;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.gridSize = gridSize;
    }

    public boolean isDrawing(){
        return fades.get(fades.size()/2).getStatus() == Status.RUNNING;
    }

    public void draw(){
        for (int i=0; i<fades.size()/2; i++){
            fades.get(i).playFromStart();
            fades.get(fades.size()-i-1).playFromStart();
        }
        reverse();
    }

    public void reverse(){
        for (FadeTransition fade:fades){
            if (fade.getFromValue() == 1){
                fade.setFromValue(0);
                fade.setToValue(1);
            } else {
                fade.setFromValue(1);
                fade.setToValue(0);
            }
        }
    }

    public void init(){
        int x = 0; 
        int y = 0;
        int delay = 100;
        while(true){
            Rectangle rec = new Rectangle(x, y, gridSize, gridSize);
            rec.setFill(Color.BROWN);
            rec.setOpacity(0);
            root.getChildren().add(rec);

            //fade in effect
            ft = new FadeTransition(Duration.millis(500),rec);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.setDelay(Duration.millis(delay));
            
            fades.add(ft);

            x = x + gridSize;
            delay = delay + 20;
            if (x > sizeX){
                x = 0;
                y = y + gridSize;
            }
            if (y > sizeY/2){
                break;
            }
        }
        x = sizeX-gridSize;
        y = sizeY-gridSize;
        delay = 100;
        while(true){
            Rectangle rec = new Rectangle(x, y, gridSize, gridSize);
            rec.setFill(Color.BROWN);
            rec.setOpacity(0);
            root.getChildren().add(rec);

            //fade in effect
            ft = new FadeTransition(Duration.millis(500),rec);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.setDelay(Duration.millis(delay));
            
            fades.add(ft);

            x = x - gridSize;
            delay = delay + 20;
            if (x < 0){
                x = sizeX;
                y = y - gridSize;
            }
            if (y < sizeY/2){
                break;
            }
        }
    }

}