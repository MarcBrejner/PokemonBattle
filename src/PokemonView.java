import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

class PokemonView {
    Group root;
    int x; 
    int y;
    String sprite;
    Image image;
    FadeTransition ft;
    ImageView iv;
    Timeline timeline, glideTimeline;
    
    boolean shake = false;
    int vx = 0;
    int vy = 0;
    
    public PokemonView(Group root, int x, int y, String sprite) {
        this.root = root;
        this.x = x;
        this.y = y;
        this.sprite = sprite;
    }

    public boolean isRunning(){
        boolean ftBool = false;
        boolean tlBool = false;
        boolean gtlBool = false;
        if (ft != null){
            ftBool = ft.getStatus() == Status.RUNNING;
        }
        if (timeline != null){
            tlBool = timeline.getStatus() == Status.RUNNING;
        }
        if (glideTimeline != null){
            gtlBool = glideTimeline.getStatus() == Status.RUNNING;
        }
        return ftBool||tlBool||gtlBool;
    }

    public void glide(int toX){
        x = toX;
        glideTimeline  = new Timeline(); 
        KeyValue wValue1  = new KeyValue(iv.xProperty(), toX);
        KeyFrame keyFrame1  = new KeyFrame(Duration.millis(1000), wValue1);
        glideTimeline.getKeyFrames().add(keyFrame1);
        glideTimeline.playFromStart();
    }
    
    public void fadeOut() {
        ft.play();
    }
    
    public void shake() {
        timeline  = new Timeline(); 
        KeyValue wValue1  = new KeyValue(iv.xProperty(), x+20); 
        KeyValue wValue2  = new KeyValue(iv.xProperty(), x-20); 
        
        KeyFrame keyFrame1  = new KeyFrame(Duration.millis(20), wValue1);
        KeyFrame keyFrame2  = new KeyFrame(Duration.millis(20), wValue2);
        
        timeline  = new Timeline(); 
        timeline.getKeyFrames().addAll(keyFrame1,keyFrame2);
        timeline.setCycleCount(10);
        timeline.setAutoReverse(true);
        timeline.playFromStart();
    }
    
    public void draw() {
        try {
            image = new Image(new FileInputStream(sprite));
            iv = new ImageView();
            iv.setX(x);
            iv.setY(y);
            iv.setImage(image);
            root.getChildren().add(iv);
            
            //fade effect
            ft = new FadeTransition(Duration.millis(1000),iv);
            ft.setFromValue(1);
            ft.setToValue(0);
            
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}