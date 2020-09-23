package colorpick;

import javafx.application.Application;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.BlurType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.FontWeight;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.beans.property.*;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.Random;


/**
 * @author 16101384 - Minh Tu Nguyen
 */
class ImageButton extends Button {

    private final String STYLE_NORMAL = "-fx-background-color: transparent; -fx-padding: 2, 2, 2, 2;";
    private final String STYLE_PRESSED = "-fx-background-color: transparent; -fx-padding: 3 1 1 3;";

    public ImageButton(Image originalImage, double h, double w) {

        ImageView image = new ImageView(originalImage);
        image.setFitHeight(h);
        image.setFitHeight(w);
        image.setPreserveRatio(true);
        setGraphic(image);
        setStyle(STYLE_NORMAL);

        setOnMousePressed(e -> setStyle(STYLE_PRESSED));
        setOnMouseReleased(e -> setStyle(STYLE_NORMAL));
    }
}

class Level {
    final public static int MAX_LEVEL = 55;
    private int level;
    private int dim;
    final private double[] radius = {
        0,
        0,
        80, // level 1-5
        60, // level 6-10,
        50, // level 11-15
        40, // level 16-20
        34, // level 21-25
        28, // level 26-30
        26, // level 31-35
        24, // level 36-40
        21.5, // level 41-45
        20, // level 46-50
        18, // level 51-55
    };
    final private double[][] coord = {
        {0, 0},
        {0, 0},
        {220, 240}, // level 1-5
        {195, 240}, // level 6-10
        {172, 205}, // level 11-15
        {165, 190}, // level 16-20
        {163, 185}, // level 21-25
        {160, 185}, // level 26-30
        {160, 185}, // level 31-35
        {156, 185}, // level 36-40
        {153, 185}, // level 41-45
        {150, 185}, // level 46-50
        {148, 185}, // level 51-55
    };
    
    public Level() {
    }
    
    public Level(int level) {
        this.level = level;
        this.dim = (level - 1) / 5 + 2;
    }
    
    public int getLevel() {
        return level;
    }
    public int getDim() {
        return dim;
    }
    public double getRadius(int dim) {
        return radius[dim];
    }
    public double getCoordX(int dim) {
        return coord[dim][0];
    }
    public double getCoordY(int dim) {
        return coord[dim][1];
    }
    public double getPadding(int dim) {
        if (level <= 30) return 10;
        else if (level <= 35) return 6;
        else if (level <= 45) return 5;
        else return 4;
    }
}

public class ColorPick extends Application {
    private Random rand;
    private Scene menuScene;
    private Scene playScene;
    private IntegerProperty curLevel;
    private IntegerProperty curLife;
    private IntegerProperty curScore;
    final private Color[] template = {Color.RED, Color. BLUE, Color.GREEN, Color.YELLOW, Color.CYAN, Color.PINK, Color.CORAL, Color.DARKORCHID, Color.BISQUE, Color.CRIMSON};
    
    
    final private Media theme_music = new Media(getClass().getResource("/assets/SuperMarioTheme.mp3").toString());
    final private Media buttonClicked = new Media(getClass().getResource("/assets/button_clicked.wav").toString());
    final private Media wrongSound = new Media(getClass().getResource("/assets/wrongSound.mp3").toString());
    final private Media correctSound = new Media(getClass().getResource("/assets/correctSound.mp3").toString());
    
    final private MediaPlayer mp = new MediaPlayer(theme_music);
    private MediaPlayer buttonPlayer;
    final private int MAX_WIDTH = 800;
    final private int MAX_HEIGHT = 800;
    
    
    public GridPane createMap(Group root, Stage primaryStage) {
        GridPane map = new GridPane();
        map.setAlignment(Pos.CENTER);
        
        Level level = new Level(curLevel.get());
        int dim = level.getDim();
        map.setHgap(level.getPadding(dim));
        map.setVgap(level.getPadding(dim));
        int ansX = rand.nextInt(dim), ansY = rand.nextInt(dim);
        Color color = template[rand.nextInt(template.length)];
        
        for (int i = 0; i < dim; ++i)
            for (int j = 0; j < dim; ++j) {
                Circle tmp = new Circle();
                tmp.setRadius(level.getRadius(dim));
                if (i == ansX && j == ansY) {
                    Color wrongColor = color;
                    double red = wrongColor.getRed();
                    double green = wrongColor.getGreen();
                    double blue = wrongColor.getBlue();
                    
                    int choice = rand.nextInt(3);
                    double delta;
                    int level_tmp = curLevel.get();
                    // set difficulty for different levels
                    if (level_tmp <= 5) {
                        delta = (rand.nextInt(6) + 40) * 1.0 / 100; // 0.4 - 0.45
                    }
                    else if (level_tmp <= 15) {
                        delta = (rand.nextInt(2) + 3) * 1.0 / 10; // 0.3 - 0.4
                    }
                    else if (level_tmp <= 25) {
                        delta = (rand.nextInt(6) + 25) * 1.0 / 100; // 0.25 - 0.3
                    }
                    else if (level_tmp <= 40) {
                        delta = (rand.nextInt(11) + 15) * 1.0 / 100; // 0.15 - 0.25
                    }
                    else if (level_tmp <= 50) {
                        delta = (rand.nextInt(6) + 5) * 1.0 / 100; // 0.05 - 0.1
                    }
                    else {
                        delta = (rand.nextInt(5) + 1) * 1.0 / 100; // 0.01 - 0.05
                    }
                    
                    
                    int add = rand.nextInt(2);
                    switch (choice) {
                        case 0: { // red
                            if (add == 0) {
                                if (red > delta) red -= delta;
                                else red += delta;
                            }
                            else {
                                if (red + delta < 1) red += delta;
                                else red -= delta;
                            }
                            break;
                        }
                        case 1: { // green
                            if (add == 0) {
                                if (green > delta) green -= delta;
                                else green += delta;
                            }
                            else {
                                if (green + delta < 1) green += delta;
                                else green -= delta;
                            }
                            break;
                        }
                        case 2: { // blue
                            if (add == 0) {
                                if (blue > delta) blue -= delta;
                                else blue += delta;
                            }
                            else {
                                if (blue + delta < 1) blue += delta;
                                else blue -= delta;
                            }
                            break;
                        }
                    }
                    wrongColor = new Color(red, green, blue, 1);
                    tmp.setFill(wrongColor);
                    tmp.setOnMouseClicked((e) -> {
                        buttonPlayer = new MediaPlayer(correctSound);
                        buttonPlayer.setVolume(0.3);
                        buttonPlayer.play();
                        buttonPlayer.seek(Duration.ZERO);
                        curLevel.set(curLevel.get() + 1);
                        // need to change score mechanism
                        curScore.set(curScore.get() + (curLevel.get() * curLife.get()) * 10);
                        
                        Label correctText = new Label("CORRECT!");
                        correctText.setId("correct");
                        correctText.setLayoutX(300);
                        correctText.setLayoutY(130);
                        
                        FadeTransition fade = new FadeTransition(Duration.millis(500), correctText);
                        fade.setFromValue(1.0);
                        fade.setToValue(0);
                        fade.play();
                        root.getChildren().addAll(correctText);
                        
                        if (curLevel.get() > Level.MAX_LEVEL) {
                            Alert win = new Alert(AlertType.INFORMATION, "Congratulations! You have won the game with current max level!");
                            win.show();
                            primaryStage.setScene(menuScene);
                        }
                        root.getChildren().remove(map);
                        GridPane newPane = createMap(root, primaryStage);
                        root.getChildren().addAll(newPane);
                    });
                }
                else {
                    tmp.setFill(color);
                    tmp.setOnMouseClicked((e) -> {
                        buttonPlayer = new MediaPlayer(wrongSound);
                        buttonPlayer.setVolume(0.3);
                        buttonPlayer.play();
                        buttonPlayer.seek(Duration.ZERO);
                        curLife.setValue(curLife.get() - 1);
                        if (curLife.get() == 0) {
                            Alert lose = new Alert(AlertType.INFORMATION, "Oops... Game over! Let's try again!");
                            lose.show();
                            primaryStage.setScene(menuScene);
                        }
                        
                        Label wrongText = new Label("TRY AGAIN!");
                        wrongText.setId("wrong");
                        wrongText.setLayoutX(280);
                        wrongText.setLayoutY(130);
                        
                        FadeTransition fade = new FadeTransition(Duration.millis(500), wrongText);
                        fade.setFromValue(1.0);
                        fade.setToValue(0);
                        fade.play();
                        root.getChildren().addAll(wrongText);
                    });
                }
                map.setConstraints(tmp, i, j);
                map.getChildren().addAll(tmp);
            }
        map.setLayoutX(level.getCoordX(dim));
        map.setLayoutY(level.getCoordY(dim));
        return map;
    }
   
    
    public void playScreen(Stage primaryStage) {
        curLevel = new SimpleIntegerProperty(1);
        curLife = new SimpleIntegerProperty(10);
        curScore = new SimpleIntegerProperty(0);
        
        Group root = new Group();
        // Back Button
        ImageButton backButton = new ImageButton(new Image(getClass().getResource("/assets/returnButton.png").toString()), 50, 50);
        backButton.setOnMouseClicked((e) -> {
            buttonPlayer = new MediaPlayer(buttonClicked);
            buttonPlayer.setVolume(1);
            buttonPlayer.play();
            buttonPlayer.seek(Duration.ZERO);
            primaryStage.setScene(menuScene);
        });
        backButton.setLayoutX(30.0);   backButton.setLayoutY(650.0);
        Text backButtonText = new Text("Back");
        backButtonText.setId("back-button");
        backButtonText.setFill(Color.YELLOW);
        backButtonText.setLayoutX(92.0); backButtonText.setLayoutY(685.0);
        
        // Score pane
        Rectangle scoreBox = new Rectangle(100, 50, 140, 70);
        scoreBox.setFill(Color.YELLOW);
        scoreBox.setArcWidth(25.0); scoreBox.setArcHeight(30.0);
        DropShadow scoreBoxShadow = new DropShadow();
        scoreBoxShadow.setColor(Color.YELLOW);
        scoreBoxShadow.setRadius(25);
        scoreBoxShadow.setSpread(0.25);
        scoreBox.setEffect(scoreBoxShadow);
        Label scoreText = new Label("SCORE");
        scoreText.setFont(Font.font("TimesRoman", FontWeight.BOLD, 18));
        scoreText.setPadding(new Insets(-25, 0, 0, 0));
        Label scoreNumber = new Label();
        scoreNumber.setFont(Font.font("Courier New", FontWeight.BLACK, 18));
        scoreNumber.setPadding(new Insets(20, 0, 0, 0));
        scoreNumber.textProperty().bind(curScore.asString());
        
        StackPane scorePane = new StackPane(scoreBox, scoreText, scoreNumber);
        scorePane.setLayoutX(60);  scorePane.setLayoutY(15);
        
        // Level pane
        Rectangle levelBox = new Rectangle(100, 50, 140, 70);
        levelBox.setFill(Color.ORANGE);
        levelBox.setArcWidth(25.0); levelBox.setArcHeight(30.0);
        DropShadow levelBoxShadow = new DropShadow();
        levelBoxShadow.setColor(Color.ORANGE);
        levelBoxShadow.setRadius(25);
        levelBoxShadow.setSpread(0.25);
        levelBox.setEffect(levelBoxShadow);
        Label levelText = new Label("LEVEL");
        levelText.setFont(Font.font("TimesRoman", FontWeight.BOLD, 18));
        levelText.setPadding(new Insets(-25, 0, 0, 0));
        Label levelNumber = new Label();
        levelNumber.setFont(Font.font("Courier New", FontWeight.BLACK, 18));
        levelNumber.setPadding(new Insets(20, 0, 0, 0));
        levelNumber.textProperty().bind(curLevel.asString());
        
        StackPane levelPane = new StackPane(levelBox, levelText, levelNumber);
        levelPane.setLayoutX(320);  levelPane.setLayoutY(15);
        
        // Life pane
        Rectangle lifeBox = new Rectangle(100, 50, 140, 70);
        lifeBox.setFill(Color.RED);
        lifeBox.setArcWidth(25.0); lifeBox.setArcHeight(30.0);
        DropShadow lifeBoxShadow = new DropShadow();
        lifeBoxShadow.setColor(Color.RED);
        lifeBoxShadow.setRadius(25);
        lifeBoxShadow.setSpread(0.25);
        lifeBox.setEffect(lifeBoxShadow);
        Label lifeText = new Label("LIFE");
        lifeText.setFont(Font.font("TimesRoman", FontWeight.BOLD, 18));
        lifeText.setPadding(new Insets(-25, 0, 0, 0));
        Label lifeNumber = new Label();
        lifeNumber.setFont(Font.font("Courier New", FontWeight.BLACK, 18));
        lifeNumber.setPadding(new Insets(20, 0, 0, 0));
        lifeNumber.textProperty().bind(curLife.asString());
        
        StackPane lifePane = new StackPane(lifeBox, lifeText, lifeNumber);
        lifePane.setLayoutX(580);   lifePane.setLayoutY(15);
        root.getChildren().addAll(backButton, backButtonText, scorePane, levelPane, lifePane);
        
        
        // Map
        GridPane map = createMap(root, primaryStage);
        root.getChildren().addAll(map);
        
        playScene = new Scene(root, MAX_WIDTH, MAX_HEIGHT, Color.BLACK);
        playScene.getStylesheets().add("style.css");
        primaryStage.setScene(playScene);
    }
    
    
    public void mainScreen(Stage primaryStage) {
        BorderPane rootPane = new BorderPane();
        rootPane.setId("menu");
        
        // Menu
        VBox menu = new VBox(30);
        menu.setAlignment(Pos.CENTER);
        Button startButton = new Button("START!");
        startButton.setId("start-button");
        startButton.setOnMouseClicked((e) -> {
            buttonPlayer = new MediaPlayer(buttonClicked);
            buttonPlayer.setVolume(1);
            buttonPlayer.play();
            buttonPlayer.seek(Duration.ZERO);
            playScreen(primaryStage);
        });
        Button modeButton = new Button("MODE");
        modeButton.setId("mode-button");
        modeButton.setOnMouseClicked((e) -> {
            buttonPlayer = new MediaPlayer(buttonClicked);
            buttonPlayer.setVolume(1);
            buttonPlayer.play();
            buttonPlayer.seek(Duration.ZERO);
        });
        Button scoreButton = new Button("HIGH SCORE");
        scoreButton.setId("score-button");
        scoreButton.setOnMouseClicked((e) -> {
            buttonPlayer = new MediaPlayer(buttonClicked);
            buttonPlayer.setVolume(1);
            buttonPlayer.play();
            buttonPlayer.seek(Duration.ZERO);
        });
        Button quitButton = new Button("EXIT GAME");
        quitButton.setId("quit-button");
        quitButton.setOnMouseClicked((e) -> {
            buttonPlayer = new MediaPlayer(buttonClicked);
            buttonPlayer.setVolume(1);
            buttonPlayer.play();
            primaryStage.close();
        });
        menu.getChildren().addAll(startButton, modeButton, scoreButton, quitButton);
        
        // Game Title
        BorderPane titlePane = new BorderPane();
        Label title = new Label("COLOR PICK");
        title.setId("title");
        titlePane.setMargin(title, new Insets(0, 12, 0, 50));
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.WHITE);
        dropShadow.setRadius(25);
        dropShadow.setSpread(0.25);
        dropShadow.setBlurType(BlurType.GAUSSIAN);
        title.setEffect(dropShadow);
        FadeTransition blink = new FadeTransition(Duration.seconds(1.2), title);
        blink.setFromValue(1.0);
        blink.setToValue(0.3);
        blink.setCycleCount(Animation.INDEFINITE);
        blink.setAutoReverse(true);
        blink.play();
        titlePane.setTop(title);
        
        // Intro
        Label intro = new Label("A simple game that tests your vision.\nBe sure to pick the different color!");
        intro.setId("intro");
        titlePane.setMargin(intro, new Insets(-50, 0, 40, 0));
        titlePane.setCenter(intro);
        
        // Footer
        VBox author = new VBox(10);
        author.setAlignment(Pos.CENTER);
        Label author1 = new Label("Designer: Minh Tu Nguyen");
        Label author2 = new Label("® Copyright 2020");
        author1.setId("author1");
        author2.setId("author2");
        author.getChildren().addAll(author1, author2);

        
        rootPane.setCenter(menu);
        rootPane.setTop(titlePane);
        rootPane.setBottom(author);
        rootPane.setMargin(author, new Insets(30, 0, 50, 0));
        menuScene = new Scene(rootPane, MAX_WIDTH, MAX_HEIGHT);
        
        menuScene.getStylesheets().add("style.css");
        primaryStage.setScene(menuScene);
        primaryStage.show();
    }
            
    
            
    @Override
    public void start(Stage primaryStage) {
        rand = new Random(System.currentTimeMillis());
        mp.setCycleCount(MediaPlayer.INDEFINITE);
        mp.play();
        
        mainScreen(primaryStage);
        //playScreen(primaryStage);
        primaryStage.setTitle("Color Pick");
        primaryStage.setMinWidth(MAX_WIDTH);
        primaryStage.setMaxWidth(MAX_WIDTH);
        primaryStage.setMaxHeight(MAX_HEIGHT);
        primaryStage.setMaxHeight(MAX_HEIGHT);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
