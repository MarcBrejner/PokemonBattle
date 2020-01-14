import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import org.jspace.ActualField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

public class InGame extends Application{
    
	public static String menuState;
	public String state;
	public static String action;
	public PokemonView poke1, poke2;
	public Bar hp1Bar, hp2Bar;

	public int hp1, hp2;
	public int turn;
	public int me;

	public static int getPort() {
		return port;
	}

	public static int port;
	public static String username;

	
    public static void main(String[] args) {
		port =  Integer.parseInt(args[0]);
		username = args[1];
        launch(args);
    }
    
    public void start(Stage stage) throws InterruptedException, UnknownHostException, IOException, InvocationTargetException {
		//Set up local repository for communicating with ClientController thread
		SequentialSpace threadedComs = new SequentialSpace();
		SpaceRepository localRepository = new SpaceRepository();

		localRepository.add("threadedComs", threadedComs);
		localRepository.addGate("tcp://localhost:" + port + "/?keep");

		new Thread(new ClientController(username, "eba")).start();

		//game logic
		int sizeX = 600;
		int sizeY = 600;

		Timeline gl = new Timeline();

		//Her initiere vi javafx
		stage.setTitle("PokemonView user: "+username+" on port: "+port);

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
		labelsMain.add(new String[]{"Fight", "fight"});
		labelsMain.add(new String[]{"Settings", "menu2"});
		labelsMain.add(new String[]{"Duck", "menu3"});
		menus.put("mainMenu", new MenuList(gc, 100, 100, labelsMain));

		//menu 1
		ArrayList<String[]> labels1 = new ArrayList<String[]>();
		labels1.add(new String[]{"Attack", "attack"});
		labels1.add(new String[]{"Duck quack", ""});
		labels1.add(new String[]{"Back", "mainMenu"});
		menus.put("inFight", new MenuList(gc, 100, 400, labels1));

		//menu 2
		ArrayList<String[]> labels2 = new ArrayList<String[]>();
		labels2.add(new String[]{"RAZOR LEAF WAS VERY EFFECTIVE", "mainMenu"});
		menus.put("menu2", new MenuList(gc, 100, 100, labels2));

		//menu 3
		ArrayList<String[]> labels3 = new ArrayList<String[]>();
		labels3.add(new String[]{"DSadsa", ""});
		labels3.add(new String[]{"dsad", ""});
		labels3.add(new String[]{"sad", ""});
		labels3.add(new String[]{"Cool", ""});
		labels3.add(new String[]{"Duck", ""});
		labels3.add(new String[]{"F", ""});
		labels3.add(new String[]{"Back", "mainMenu"});
		menus.put("menu3", new MenuRect(gc, 100, 100, labels3, 0, 4));

		//effects
		SplashScreen spl = new SplashScreen(root, sizeX, sizeY, 60);
		spl.init();


		state = "mainMenu";
		action = "none";
			scene.setOnKeyPressed(
					new EventHandler<KeyEvent>() {
						public void handle(KeyEvent e) {
							String code = e.getCode().toString();
							//keyboard handling
							menus.get(menuState).move(code);
						}
					}
			);

			//game loop
			gl.setCycleCount(Timeline.INDEFINITE);

			KeyFrame kf = new KeyFrame(Duration.seconds(0.017),
					new EventHandler<ActionEvent>() {
						@Override

						public void handle(ActionEvent event) {
							//clearing canvas and drawing updated gameobjects
							gc.clearRect(0, 0, sizeX, sizeY);

							switch (state) {
								case "mainMenu":
									menuState = "mainMenu";
									menus.get(menuState).draw();
									switch (action) {
										case "fight":
											threadedComs.put("FIGHT");
											state = "waitingForOtherPlayer";
											action = "none";
									}
									break;

								case "waitingForOtherPlayer":
									Stage waitingStage = new Stage();
									waitingStage.initModality(Modality.APPLICATION_MODAL);
									waitingStage.initStyle(StageStyle.UTILITY);
									Label waitingLable = new Label("Waiting for opponent...");
									//get from tuple space...
									try {
										threadedComs.get(new ActualField("FIGHT_ACK"));
										//found opponent
										spl.draw();
									} catch (InterruptedException e){
										e.printStackTrace();
								}

									state = "waitingForSplash";
									action = "none";

								case "waitingForSplash":
									if (!spl.isDrawing()) {
										state = "fightIntro";
									}
									break;

								case "fightIntro":
									spl.draw();
									//bars
									hp1Bar = new Bar(root, 50, 70, 100, 145, Color.RED);
									hp2Bar = new Bar(root, 400, 50, 100, 145, Color.RED);

									//pokemons
									String pikaPath1 = "pikaBack.png";
									poke1 = new PokemonView(root, 700, 150, pikaPath1);

									String pikaPath2 = "pikaFront.png";
									poke2 = new PokemonView(root, -100, 100, pikaPath2);

									poke1.draw();
									poke2.draw();

									poke1.glide(100);
									poke2.glide(400);

									state = "waitingForPokemonsGliding";
									action = "none";
									break;

								case "waitingForPokemonsGliding":
									if (!poke1.isRunning()) {
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
									if (action == "attack") {
										hp2 = hp2 - 50;
										hp2Bar.changeContent(hp2);
										poke2.shake();
										action = "none";
										if (hp2 < 0) {
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
}

