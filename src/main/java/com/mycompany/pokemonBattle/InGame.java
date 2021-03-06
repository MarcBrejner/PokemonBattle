package com.mycompany.pokemonBattle;


import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;

public class InGame extends Application{

	public static Controller controller;
	public static Color background;
	public static GraphicsContext gc;
	public static SplashScreen splashScreen;
	public static Group root;

	public static void main(String[] args) {
		//creating instances
		controller = new Controller();
        launch(args);
    }
    
    public void start(Stage stage) throws InterruptedException, UnknownHostException, IOException, InvocationTargetException {
		//game logic
		final int sizeX = 600;
		final int sizeY = 600;

		Timeline gl = new Timeline();

		//Her initiere vi javafx
		//stage.setTitle("PokemonView user: "+controller.username+" on port: "+controller.port);
		stage.setTitle("PokemonBattle");
		
		// Opret root node til javafx
		root = new Group();

		// Opret Scene
		Scene scene = new Scene(root);
		stage.setScene(scene);
		background = Color.web("#e0dbcd");
		scene.setFill(background);

		// Opret canvas for tegning
		Canvas canvas = new Canvas(sizeX, sizeY);
		
		// Tilføj canvas til root node
		root.getChildren().add(canvas);

		// Få graphics context så den kan bruges af andre klasser
		gc = canvas.getGraphicsContext2D();

		//
		controller.initGameElements();

		//Starter stage
		stage.toFront();
		stage.requestFocus();
		stage.show();

		//effects
		splashScreen = new SplashScreen(root, sizeX, sizeY, 60);
		splashScreen.init();

		//keyboard input
		scene.setOnKeyPressed(
				new EventHandler<KeyEvent>() {
					public void handle(KeyEvent e) {
						String text = e.getText();
						String code = e.getCode().toString();
						controller.handleKeyboard(code, text, e.isShiftDown());
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
						controller.stateHandler();
					}
				});

		//playing gameloop
		gl.getKeyFrames().add(kf);
		gl.play();
	}
}