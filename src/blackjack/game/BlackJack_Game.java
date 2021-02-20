package blackjack.game;

/**
 ** In this program, the user plays a game of Blackjack.  The
 * computer acts as the dealer.  The user plays by clicking
 * "Hit!" and "Stand!" buttons.  The user can place bets.
 * At the beginning of the game, the user is given $100.
 *
 * This program depends on the following classes:  Card, Hand,
 * BlackjackHand, Deck.  It also requires the image resource
 * file cards.png.
 * @author wakura
 */
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
public class BlackJack_Game extends Application{
    public static void main(String[] args){
        launch(args);
    }
    Button hitButton, standButton, newGameButton;
    Canvas canvas;
    ButtonBar btnBar;
    boolean gameInProgress;
    String message;
    BlackjackHand playerHand, dealerHand;
    Deck deck;
    Image cardsImage;
    
    public void start(Stage stage){
        cardsImage = new Image("file:///C:/Users/wakura/Desktop/notes/3.2/Object%20Oriented%20Programming/javanotes8.1.0-web-site/c6/cards.png");
        canvas = new Canvas(660, 530);
        hitButton = new Button("Hit");
        standButton = new Button("Stand");
        newGameButton = new Button("New Game");
        draw();
        
        Insets insets = new Insets(5);
//        hitButton.setStyle("-fx-margin: 10px");
        btnBar = new ButtonBar();
        btnBar.setButtonData(hitButton, ButtonData.APPLY);   
        btnBar.setButtonData(standButton, ButtonData.APPLY);
        btnBar.setButtonData(newGameButton, ButtonData.APPLY);
        btnBar.getButtons().addAll(hitButton, standButton, newGameButton);
//        btnBar.setStyle("-fx-background-color: green");
        btnBar.setMaxWidth(250);
        btnBar.setMaxHeight(50);
        
        BorderPane root = new BorderPane(canvas);
        root.setBottom(btnBar);
        buttonSwitch();
        root.setAlignment(btnBar, Pos.CENTER);
        root.setMargin(btnBar, insets);
        //event handling
        hitButton.setOnAction(e -> doHit());
        standButton.setOnAction(e -> doStand());
        newGameButton.setOnAction(e -> doNewGame());
        
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.setTitle("BlackJack Wizards");
        stage.show();
    }
    private void doNewGame(){
        //start the game
        gameInProgress = true;
        
        //create the deck and hands to be used for this game
        deck = new Deck();
        playerHand = new BlackjackHand();
        dealerHand = new BlackjackHand();
        
        //shuffle the deck before the game begins
        deck.shuffle();
        
        //deal two cards into each players hand
        for(int k = 0; k < 2; k++){
            playerHand.addCard(deck.dealCard());
            dealerHand.addCard(deck.dealCard());
        }
        
        //check for a blackjack
        if(playerHand.getBlackjackValue() == 21 || dealerHand.getBlackjackValue() == 21){
            gameInProgress = false;     //game over if any player has a blackjack
            
            //find out who is the winner
            if(dealerHand.getBlackjackValue() == 21)
                message = "Dealer wins with a blackjack";
            else 
                message = "You win with a blackjack";
        }
        else
            message = "Your cards are " + playerHand.getBlackjackValue() + " Hit or Stand?";
        draw();
        buttonSwitch();                 
    }
    private void doHit(){
        playerHand.addCard(deck.dealCard());
        
        //check if the player hand has more than 21
        if(playerHand.getBlackjackValue() > 21){
            gameInProgress = false;
            message = "You lost, your cards are above 21";
            draw();
            buttonSwitch();
        }
        else if(playerHand.getBlackjackValue() <= 21 && playerHand.getCardCount() == 5){
            gameInProgress = false;     //game over user hss five cards
            message = "You win with 5 cards with less than 21";
            draw();
            buttonSwitch();
        }
        else {
            message = "Your cards are " + playerHand.getBlackjackValue() + " Hit or Stand?";
            draw();
            buttonSwitch();
        }
    }
    private void doStand(){
        gameInProgress = false;
        
        //check dealers's card value. If it's less than 16, add cards to it
        
        while(dealerHand.getBlackjackValue() < 16){
            dealerHand.addCard(deck.dealCard());
        }
        
        //check for the winner now
        if(dealerHand.getBlackjackValue() > 21)
            message = "You win! Dealers cards are more than 21";
        else if (dealerHand.getBlackjackValue() >= playerHand.getBlackjackValue())
            message = "You loose, " + dealerHand.getBlackjackValue() + " to " + playerHand.getBlackjackValue();
        else
            message = "Congrats! You win, " + playerHand.getBlackjackValue() + " to " + dealerHand.getBlackjackValue();
        draw();
        buttonSwitch();
    }
    private void draw(){
        GraphicsContext g = canvas.getGraphicsContext2D();
        
        //fill canvas background and the borders. Also clears the canvas to begin new drawin
        g.setFill(Color.BLACK);
        g.setLineWidth(5);
        g.setStroke(Color.MAROON);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g.strokeRect(2.5, 2.5, canvas.getWidth() - 5, canvas.getHeight() - 5);
        
        //...........................drawing the cards.........................................................
        if(playerHand == null){
            //The game has not started
            g.setFill(Color.WHITE);
            g.setFont(Font.font("Book Antiqua",20));
            g.fillText("WELCOME TO THE BLACKJACK GAME", 50, canvas.getHeight()/2);
        }
        else{
            //draw dealer's cards
            g.setFill(Color.WHITE);
            g.setFont(Font.font("Book Antiqua",20));
            g.fillText(message, 20, 490);
            g.fillText("Dealer's Cards", 20, 35);
            if(gameInProgress){
                drawCard(g, dealerHand.getCard(0), 20, 50);
                drawCard(g, null, 150, 50);
            }
            else {//user should see all the dealers cards
                int x = 20;
                for(int k = 0; k < dealerHand.getCardCount(); k++){
                    drawCard(g, dealerHand.getCard(k), x, 50);
                    x += 130;
                }
            }
            
            //draw players hands
            g.fillText("Your Cards", 20, 260);
            int x = 20;
            for(int k = 0; k < playerHand.getCardCount(); k++){
                drawCard(g, playerHand.getCard(k), x, 285);
                x += 130;
            }
        }
        
    }
    
    private void drawCard(GraphicsContext g, Card c, double dx, double dy){
        double sx, sy;
        if(c == null){
            sx = 79 * 2;
            sy = 123 * 4;
        } 
        else{
            sx = 79 * (c.getValue()-1);
            sy = 123 * (3 - c.getSuit());
        } 
        
        g.drawImage(cardsImage, sx, sy, 79, 123, dx, dy, 102, 183);
            
    }
    private void buttonSwitch(){
        if(gameInProgress){
            hitButton.setDisable(false);
            standButton.setDisable(false);
            newGameButton.setDisable(true);
        }
        else{
            hitButton.setDisable(true);
            standButton.setDisable(true);
            newGameButton.setDisable(false);
        }
    }

}
