import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import java.io.Serializable;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Pokemon {
    private int HP;
    private String name,ele,status,frontImage,backImage;
    private List<Ability> abilityList;
    private boolean alive;

    Group root;
    int x; 
    int y;
    String sprite;
    Image image;
    FadeTransition ft;
    ImageView iv;
    Timeline timeline, glideTimeline;

    public Bar hpBar;
    
    boolean shake = false;
    int vx = 0;
    int vy = 0;

    public Pokemon(Group root, int x, int y, String name){
        switch(name){
            case "Pikachu":
                this.sprite = "pikaFront.png";
                pikachu();
                this.name = name;
                break;
        }

        this.root = root;
        this.x = x;
        this.y = y;
    }

    private void pikachu(){
        this.HP = 80;
        this.ele = "Electric";
        this.alive = true;
    }

    private void charmander(){
        HP = 90;
        ele = "Fire";
        abilityList.add(new Ability("Slap"));
        alive = true;
    }

    public int getHP() {
        return HP;
    }

    public String getName() {
        return name;
    }

    public String getEle() {
        return ele;
    }

    public List<Ability> getAbilityList() {
        return abilityList;
    }

    public boolean isAlive() {
        return alive;
    }

    public static String toJson(Pokemon pokemon){
        Gson gson = new Gson();
        String json = gson.toJson(pokemon);
        return json;
    }

    public static Pokemon fromJson(String json){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Pokemon gsonPokemon = gson.fromJson(json, Pokemon.class);
        return gsonPokemon;
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
            sprite = "C:\\Users\\X\\Documents\\GitHub\\Distributed Pokemon\\PokemonBattle\\src\\pikaFront.png";
            image = new Image(new FileInputStream(sprite));
            iv = new ImageView();
            iv.setX(x);
            iv.setY(y);
            iv.setImage(image);
            Bar hpBar = new Bar(root, x-50, y-50, 100, 100, Color.RED);
            hpBar.draw();
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
