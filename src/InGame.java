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
import javafx.stage.Stage;
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

import java.awt.Point;
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

public class InGame extends Application{
    
	public String menuState, state, action;
	public Pokemon poke1, poke2;
	public Bar hp1Bar, hp2Bar;

	public int hp1, hp2;
	public int turn;
	public int me;

	
    public static void main(String[] args) {
        launch(args);
    }
    
    public void start(Stage stage) throws InterruptedException, UnknownHostException, IOException, InvocationTargetException {
    	
    	//game logic
		int sizeX = 600;
		int sizeY = 600;
    	
    	Timeline gl = new Timeline();
    	
    	//Her initiere vi javafx
        stage.setTitle("Pokemon");
        
        // Opret root node til javafx
        Group root = new Group();
        
        // Opret Scene
        Scene scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.web("#e0dbcd"));

        // Opret canvas for tegning
        Canvas canvas = new Canvas(sizeX, sizeY);
        // Tilføj canvas til root node
        root.getChildren().add(canvas);

        // Få graphics context så den kan bruges af andre klasser
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        //Her bør vi starte andre klasser, først Level klassen.
        //Opretter gameloop
        
        //Starter stage
        stage.toFront();
        stage.requestFocus();
        stage.show();	
    	
    	//game elements
    	
    	//menus
        Hashtable<String, Menu> menus = new Hashtable<String, Menu>();        
        menuState = "mainMenu";
        
        //main menu
    	ArrayList<String[]> labelsMain = new ArrayList<String[]>();
    	labelsMain.add(new String[] {"Fight","fight"});
    	labelsMain.add(new String[] {"Settings","menu2"});
    	labelsMain.add(new String[] {"Duck","menu3"});
    	menus.put("mainMenu", new MenuList(gc,100,100,labelsMain));
    	
    	//menu 1
    	ArrayList<String[]> labels1 = new ArrayList<String[]>();
    	labels1.add(new String[] {"Attack","attack"});
    	labels1.add(new String[] {"Duck quack",""});
    	labels1.add(new String[] {"Back","mainMenu"});
    	menus.put("inFight", new MenuList(gc,100,400,labels1));
    	
    	//menu 2
    	ArrayList<String[]> labels2 = new ArrayList<String[]>();
    	labels2.add(new String[] {"RAZOR LEAF WAS VERY EFFECTIVE","mainMenu"});
    	menus.put("menu2", new MenuList(gc,100,100,labels2));
    	
    	//menu 3
    	ArrayList<String[]> labels3 = new ArrayList<String[]>();
    	labels3.add(new String[] {"DSadsa",""});
    	labels3.add(new String[] {"dsad",""});
    	labels3.add(new String[] {"sad",""});
    	labels3.add(new String[] {"Cool",""});
    	labels3.add(new String[] {"Duck",""});
    	labels3.add(new String[] {"F",""});
    	labels3.add(new String[] {"Back","mainMenu"});
    	menus.put("menu3", new MenuRect(gc,100,100,labels3,0,4));
		
		//effects
		Splash spl = new Splash(root, sizeX, sizeY, 60);
		spl.init();

		

		state = "mainMenu";
		action = "none";
    	
        scene.setOnKeyPressed(
            new EventHandler<KeyEvent>(){
                public void handle(KeyEvent e){
                    String code = e.getCode().toString();
                    //keyboard handling
                    menus.get(menuState).move(code);
                }
            }
        );
     
        //game loop
        gl.setCycleCount(Timeline.INDEFINITE);
        
        KeyFrame kf = new KeyFrame(Duration.seconds(0.017),
        new EventHandler<ActionEvent>(){
			@Override
			
            public void handle(ActionEvent event) {
                //clearing canvas and drawing updated gameobjects
				gc.clearRect(0, 0, sizeX,sizeY);
				
				switch(state){
					case "mainMenu":
						menuState = "mainMenu";
						menus.get(menuState).draw();
						switch(action){
							case "fight":
								state = "waitingForOtherPlayer";
								action = "none";
						}
						break;

					case "waitingForOtherPlayer":
						//get from tuple space...

						//found opponent
						spl.draw();
									
						state = "waitingForSplash";
						action = "none";

					case "waitingForSplash":
						if (!spl.isDrawing()){
							state = "fightIntro";
						}
						break;

					case "fightIntro":
						spl.draw();
						//bars
						hp1Bar = new Bar(root,50,70,100,145,Color.RED);
						hp2Bar = new Bar(root,400,50,100,145,Color.RED);
						
						//pokemons
						String pikaPath1 = "pikaBack.png";
						poke1 = new Pokemon(root, 700, 150, pikaPath1);

						String pikaPath2 = "pikaFront.png";
						poke2 = new Pokemon(root, -100, 100, pikaPath2);

						poke1.draw();
						poke2.draw();

						poke1.glide(100);
						poke2.glide(400);

						state = "waitingForPokemonsGliding";
						action = "none";
						break;

					case "waitingForPokemonsGliding":
						if (!poke1.isRunning()){
							hp1 = 130;
							hp2 = 130;
							hp1Bar = new Bar(root, 100, 70, 100, hp1, Color.RED);
							hp1Bar.draw();
							hp2Bar = new Bar(root, 400, 50, 100, hp2, Color.RED);
							hp2Bar.draw();
							menuState = "inFight";
							state = "fight";
							turn = 1;
						}
						break;

					case "fight":
						menus.get(menuState).draw();
						if (action == "attack"){
							hp2 = hp2 - 50;
							hp2Bar.changeContent(hp2);
							poke2.shake();
							action = "none";
							if (hp2 < 0){
								poke2.fadeOut();
								//win
							}
						}
						break;

				}

                
            }
        });
        
        //playing gameloop
        gl.getKeyFrames().add(kf);
        gl.play();
	}
	

	class StateManager {

		String state;
		Group root;

		public StateManager(Group root){
			this.root = root;
		}

		public void handle(){
			
		}

	}
    
    class Pokemon {
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
    	
    	public Pokemon(Group root, int x, int y, String sprite) {
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
    
    class Bar {
    	Group root;
    	int x;
    	int y;
    	int width;
    	double startContent, content;
    	Color color;
    	Rectangle innerRect;
    	Timeline timeline;
    	
    	public Bar (Group root, int x, int y, int width, int content, Color color) {
    		this.root = root;
    		this.x = x;
    		this.y = y;
    		this.width = width;
    		this.content = (double) content;
    		this.startContent = this.content;
    		this.color = color;
    	}
    	
    	public void changeContent(int newValue) {
			System.out.println("newValue: "+newValue);
			content = (double) newValue;
			System.out.println("content: "+content);
    		double widthDouble = (double) width;
    		KeyValue wValue  = new KeyValue(innerRect.widthProperty(), widthDouble*(content/startContent)); 
    		KeyFrame keyFrame  = new KeyFrame(Duration.millis(500), wValue);
    		timeline  = new Timeline(); 
    		timeline.getKeyFrames().add(keyFrame);
    		timeline.play();
    	}
    	
    	public void draw() {
    		int border = 2;
    		int height = 4;
    		
    		Rectangle outerRect = new Rectangle();
    		outerRect.setFill(color.DARKGREY);
    		outerRect.setX(x-border);
    		outerRect.setY(y-border);
    		outerRect.setWidth(width+border*2);
    		outerRect.setHeight(height+border*2);
    		root.getChildren().add(outerRect);
    		
    		innerRect = new Rectangle();
    		innerRect.setFill(color);
    		innerRect.setX(x);
    		innerRect.setY(y);
    		double widthDouble = (double) width;
    		innerRect.setWidth(widthDouble*(content/startContent));
    		innerRect.setHeight(height);
			root.getChildren().add(innerRect);
    	}
    }
    
    
    interface Menu {
    	public void draw();
    	public void move(String dir);
    }
    
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
    			buttons.add(new Button(gc,x,by,text,state,false));
    			by = by + 22;
    		}
    		
    		this.selectedIdx = 0;
    		buttons.get(selectedIdx).selected = true;
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
    		} else if (dir == "ENTER") {
    			action = buttons.get(selectedIdx).state;
    		}
    	}
    	
    	public void draw() {
    		for (Button button : buttons) {
    			button.draw();
    		}
    	}
    }
    
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
    		} else if (dir == "ENTER") {
    			menuState = buttons.get(selectedIdx).state;
    		}
    	}
    	
    	public void draw() {
    		for (Button button : buttons) {
    			button.draw();
    		}
    	}
    }
	
	class Splash {
		Group root;
		int sizeX, sizeY, gridSize;
		FadeTransition ft;
		ArrayList<FadeTransition> fades = new ArrayList<FadeTransition>();

		public Splash (Group root, int sizeX, int sizeY, int gridSize){
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

    class Button {
    	int x, y;
    	String label,state;
    	GraphicsContext gc;
    	boolean selected;
    	public Button(GraphicsContext gc, int x, int y, String label, String state, boolean selected) {
    		this.x = x;
    		this.y = y;
    		this.label = label;
    		this.state = state;
    		this.gc = gc;
    		this.selected = selected;
    	}
    	
    	public void draw() {
    		gc.setFont(Font.font("Courier New",18));
    		gc.setFill(Color.BLACK);
    		gc.fillText(label, x, y);
    		if (selected) {
    			gc.fillRect(x-8, y-7, 6, 6);
    		}	
    	}
    }
}

