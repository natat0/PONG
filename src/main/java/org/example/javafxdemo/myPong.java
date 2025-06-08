package org.example.javafxdemo;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.net.URL;


public class myPong extends Application {
    private double y1 = 300, y2 = 300; //paddle locations
    private double dy = Math.random()*3 + 5, dx = 5, bX = 486, bY = 327, ballSpeed = 1; //initial location of ball, speed of ball & change in ball speed
    private final double speed = 10; //paddle speed
    private int p1Score = 0, p2Score  = 0; //Player scores
    private boolean up1, down1, up2, down2, start;
    private Runnable runBounce, runHit, runLose;


    @Override
    public void start(Stage stage){
        //bounce on paddle noise
        final URL noCLue = getClass().getResource("/sounds/bounce.mp3");
        if (noCLue != null) {
            final AudioClip bouncySound = new AudioClip(noCLue.toExternalForm());
            runBounce = bouncySound::play;
        }
        //sound effects for hitting on walls
        final URL hitting = getClass().getResource("/sounds/wallHit.mp3");
        if (hitting != null) {
            final AudioClip wallHit = new AudioClip(hitting.toExternalForm());
            wallHit.setVolume(0.3); //setting wall hit to be quite
            runHit = wallHit::play;
        }
        //Losing point sound effects
        final URL lose = getClass().getResource("/sounds/YejunScream.mp3");
        if (lose != null) {
            final AudioClip losePoint = new AudioClip(lose.toExternalForm());
            runLose = losePoint::play;
        }

        Group root = new Group();
        Scene scene = new Scene(root, 1000, 600);

        Canvas canvas = new Canvas(1000,600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        //Score board, rules and game title
        gc.setFill(Color.GOLD);
        gc.fillRect(0, 0, 1000, 100);
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", FontWeight.BLACK, 40));
        gc.fillText("PONG", 500 - 50, 50);


        Image background = loadImage("/images/background.png");
        if (background != null) {
            gc.drawImage(background, 0, 100);
        }else {
            scene.setFill(Color.BLACK);
        }

        Image p1 = loadImage("/images/player1.png");
        if (p1 != null) {
            gc.drawImage(p1, 0, 0);
        }else {
            gc.setFont(Font.font("Arial", FontWeight.BLACK, 60));
            gc.fillText("P1:", 1 ,60 );
        }

        Image p2 = loadImage("/images/player2.png");
        if (p2 != null) {
            gc.drawImage(p2, 800, 0);
        }else {
            gc.setFont(Font.font("Arial", FontWeight.BLACK, 60));
            gc.fillText("P2:", 800 ,60);
        }

        //paddles
        Rectangle p1Rect = new Rectangle(20,100);
        p1Rect.toFront();
        p1Rect.setFill(Color.WHITE);
        p1Rect.setArcWidth(20);
        p1Rect.setArcHeight(20);
        p1Rect.relocate(10, 400);

        Rectangle p2Rect = new Rectangle(20,100);
        p2Rect.setFill(Color.WHITE);
        p2Rect.setArcWidth(20);
        p2Rect.setArcHeight(20);
        p2Rect.relocate(970, 400);

        //ball
        Circle ball = new Circle(bX, bY, 15);
        ball.setFill(Color.WHITE);

        //Controls for paddles and to start moving ball
        scene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle (KeyEvent t) {
                if (t.getCode() == KeyCode.UP)up2 = true;
                if (t.getCode() == KeyCode.DOWN)down2 = true;
                if (t.getCode() == KeyCode.S)down1 = true;
                if (t.getCode() == KeyCode.W)up1 = true;
                if (t.getCode().equals(KeyCode.ENTER)) start = true;

            }
        });

        scene.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle (KeyEvent t) {
                if (t.getCode() == KeyCode.UP)up2 = false;
                if (t.getCode() == KeyCode.DOWN)down2 = false;
                if (t.getCode() == KeyCode.S)down1 = false;
                if (t.getCode() == KeyCode.W)up1 = false;
            }
        });
        //Setting rules in top bar
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", 15));
        gc.fillText("W,S & ↑,↓ ENTER to start", 430, 90);

        root.getChildren().addAll( canvas, ball, p1Rect, p2Rect);

        stage.setTitle("MY PONG!");
        Image icon = loadImage("/images/background.png");
        if (icon != null) {stage.getIcons().add(icon);}
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        new AnimationTimer(){
            @Override
            public void handle(long now){
                if (start) {
                    //changing coordinates of ball
                    bX+=ballSpeed*dx;
                    bY+=dy;
                    if (bY > scene.getHeight() - 20 || bY < 100) {//Collision detection for walls
                        new Thread(runHit).start(); //play sound when ball hits wall
                        dy = -dy;
                    }
                    if (bX > scene.getWidth() || bX < 0) { //Checking if ball goes out of bounds
                        //adding player scores
                        if (bX > scene.getWidth()) p1Score++;
                        if (bX < 0) p2Score++;
                        new Thread(runLose).start(); //playing laughing noises

                        //Updating score board
                        gc.setFill(Color.GOLD);
                        gc.fillRect(900, 10, 100, 51); //filling rectangle on top of old score
                        gc.fillRect(100, 10, 100, 51);
                        gc.setFont(Font.font("Arial", 50));
                        gc.setFill(Color.BLACK);
                        gc.fillText(p1Score+"", 100, 60); //printing new score
                        gc.fillText(p2Score+"", 900, 60);
                        bX = 486;
                        bY = 327;
                        ball.relocate(bX, bY); //relocating ball
                        ballSpeed = 1; //reset ball position
                        dy = Math.random()*3 + 5; //randomizing vertical velocity
                        start = false;
                    }
                }
                //updating paddle positions
                if (up1 && y1 -speed > 100) y1-=speed;
                if (down1 && y1  + speed < 500) y1+=speed;
                if (up2 && y2 - speed > 100) y2-=speed;
                if (down2 && y2 + speed < 500) y2+=speed;

                p1Rect.relocate(10, y1);
                p2Rect.relocate(970, y2);

                if (p1Rect.getBoundsInParent().intersects(ball.getBoundsInParent())){
                    new Thread(runBounce).start();
                    dx = -dx;
                    bX = p1Rect.getX() + p1Rect.getWidth() + ball.getRadius() + 5;
                    if (ballSpeed + 0.3 < 4.1) ballSpeed+=0.3;
                }
                if (p2Rect.getBoundsInParent().intersects(ball.getBoundsInParent())){
                    new Thread(runBounce).start();

                    dx=-dx;
                    bX = 970 - ball.getRadius() - p2Rect.getWidth() - 5;
                    if (ballSpeed + 0.3 < 4.1) ballSpeed+=0.3;
                }
                ball.relocate(bX, bY);
            }
        }.start();
    }
    public static void main(String[] args){launch(args);}

    private Image loadImage(String name) {
        try {
            URL playerImageUrl = getClass().getResource(name);
            return new Image(playerImageUrl.getPath().substring(1));
        } catch (NullPointerException e){
            return null;
        }
    }
}
