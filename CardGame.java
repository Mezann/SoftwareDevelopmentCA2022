/**
 * Card Game 
 * 
 * @version 1.0
 * @author Mezan AHMED & Omar AL GHAITHY
 */

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.lang.Thread;


public class CardGame {

    /**
     * Initialise variables
     * */
    private Integer playerCount;
    private List<Player> players = new ArrayList<Player>();
    private List<CardDeck> decks = new ArrayList<CardDeck>();
    private List<Integer> pack = new ArrayList<Integer>();
    private List<Thread> threadList = new ArrayList<Thread>();
    private volatile Player winner;
    

    /**
     * Starts all the player threads 
     * */
    public void runGame() throws InterruptedException{
        for (Thread thread : this.threadList){
            thread.start();
        }
    }

    /**
     * Locks the game at whatever instance it is in, then interrupting all the threads
     * */
    public void stop() {
        for (Thread thread : this.threadList){
            thread.interrupt();
        }
        this.endGame();
    }


    /**
     * Writes the end messages to each player output file as well as closing every file before ending the program
     * */
    public void endGame() {
        //Writes end message in player files, 
        for (Player player : this.players) {
            player.closeFile();
        }

        for (CardDeck cardDeck : this.decks) {
            cardDeck.closeFile();
        }

        System.out.println("Player " + winner.getPlayerNumber() + " wins" + "\n"
                        + "All files are closed." + "\n"
                        + "Thank you for playing." + "\n"
                        + "The program will now end.");

        System.exit(0);
    }



    /**
     * Reads the pack file, converting it from a string into an integer array list
     * @return intList
     * @throws IOException
     * */
    public List<Integer> packGenerator(Scanner sc) throws IOException {
        //Obtains file, removes any whitespaces
        System.out.println("Please enter the location of the pack to load: ");
        String fileName = sc.next();
        String replacedFile = fileName.replace("\n", "").replace(" ", "");
        String trimmedFile = replacedFile.trim();

        BufferedReader packLoc = new BufferedReader(new FileReader(trimmedFile));
        
        // read entire line as string
        String line = packLoc.readLine();
        ArrayList<String> packInString = new ArrayList<>();
        // checking for end of file4

        while (line != null) {
            packInString.add(line);
            line = packLoc.readLine();
        }

        // closing bufferreader object
        packLoc.close();
        // storing the data in arraylist to array
        List<Integer> intList = packInString.stream()
                .map(s -> Integer.parseInt(s))
                .collect(Collectors.toList());

        return intList;
    }

    /**
     * Deals cards from pack into the players hands and the decks
     * @param playerCount
     */
    public void deal(Integer playerCount) {
        int packSize = pack.size();

        //Deal from pack to players
        for (int i = 0; i < playerCount; i++) {
            //Adds 4 cards to each player in one go. E.g. gives player 1 card 1,5,9,13
            for (int j = 0; j < packSize/2; j++) {
                Integer pickedCard = pack.get(j);
                Card dealtCard = new Card(pickedCard);
                int playerNumber = j % playerCount + 1;
                if (playerNumber == i + 1) {
                    // //Remove nl
                    // players.get(playerNumber-1).addPlayerHand(pickedCard);
                    players.get(playerNumber-1).addPlayerCardHand(dealtCard);
                }
            }
            Player specifiedPlayer = players.get(i%playerCount);

            //REMOVE TEST DELETE
            System.out.println("Player " + (i%playerCount + 1) + " hand: " + specifiedPlayer.getPlayerCards());

            try {
                specifiedPlayer.getFileWriter().write("Player " + (i%playerCount + 1) + " initial hand "
                     + specifiedPlayer.getPlayerCards().toString().replace("[", "").replace("]", "").replace(",", "") + "\n");
            } catch (IOException e) {
                System.out.println("Deal filewriter error, player" + (i%playerCount + 1));
                e.printStackTrace();
            }
        }

        //Deal from pack to decks
        for (int i = 0; i < playerCount; i++) {
            for (int j = packSize/2; j < packSize; j++) {
                Integer pickedCard = pack.get(j);
                int deckNumber = j % playerCount + 1;
                if (deckNumber == i +1) {
                    decks.get(deckNumber-1).addDeckHand(pickedCard);
                }
            }
            //REMOVE TEST DELETE
            CardDeck specificDeck = decks.get(i%playerCount);
            System.out.println("Deck " + (i%playerCount + 1) + " hand: " + specificDeck.getDeckCards());
        }

    }


    class Player extends Thread {

        /**
         * Initialise variables
         * */
        private Boolean matchHand = false;
        private Integer playerNumber;
        private ArrayList<Card> cardHand = new ArrayList<Card>();
        // //remove nl test
        // private ArrayList<Integer> hand = new ArrayList<Integer>();
        private Random random = new Random();
        private CardDeck drawDeck;
        private CardDeck discardDeck;
        private FileWriter fileWriter;
        private CardGame currentGame;
        
        /**
         * Constructor for a player. Initialises playerNumber and creates player output files.
         * @param playerNumber
         */
        public Player(Integer playerNumber, CardGame currentGame) {
                this.playerNumber = playerNumber;
                this.currentGame = currentGame;
                this.drawDeck = decks.get(playerNumber-1);

                //Creates player files
                String filename = "player" + playerNumber.toString() + "_output.txt";
                File file = new File(filename);
                try {
                    //Checks if file exists
                    if (file.exists() && !file.isDirectory()) {
                        this.fileWriter = new FileWriter(filename); 
                    } else {
                        file.createNewFile();
                        this.fileWriter = new FileWriter(filename);
                    }
                } catch (IOException e) {
                    System.out.println("File creation error, player " + playerNumber);
                    e.printStackTrace();
                }
        }

        /**
         * Returns the player's playerNumber 
         * @return playerNumber
         */
        public Integer getPlayerNumber() { return playerNumber; }

        /**
         * Returns the player's card hand as a interger array
         * @return intCardHand
         */
        public ArrayList<Integer> getPlayerCards() { 
            ArrayList<Integer> intCardHand = new ArrayList<Integer>();
            for (Card card : cardHand) {
                int cardValue = card.getCardValue();
                intCardHand.add(cardValue);
            }
            return intCardHand; 
        }

        /**
         * Returns the player's boolean matchHand value
         * @return matchHand
         */
        public Boolean getMatchHand() { return matchHand; }
        
        /**
         * Retursn the player's filewriter
         * @return fileWriter
         */
        public FileWriter getFileWriter() { return fileWriter; }


        /**
         * Adds one card to the player hand, as a card object
         * @param card
         */
        public void addPlayerCardHand (Card card) {
            this.cardHand.add(card);
        }

        /**
         * Sets the deck to which the player will discard a card to
         * @param deckNumber
         */
        public void setDiscardDeck (Integer deckNumber) {
            if (playerNumber == playerCount) {
                deckNumber = 0;
            } else {
                deckNumber = playerNumber;
            }
            this.discardDeck = decks.get(deckNumber);
        }


        /**
         * Checks if the player has a matching hand
         * @return matchHand
         */
        public Boolean checkHand() {
            //Gets the first card in the player hand and adds any duplicate cards to the testHand array
            Integer potentialMatch = cardHand.get(0).getCardValue();
            ArrayList<Integer> testHand = new ArrayList<Integer>();
            for (Card card : cardHand) {
                if (card.getCardValue() == potentialMatch) {
                    testHand.add(card.getCardValue());
                } else {
                    continue;
                }
            }
            //If there are 4 of the same card, the player has a matching hand
            Integer matchTest = testHand.size();
            if (matchTest == 4) {
                this.matchHand = true;
                return matchHand;
            } else {
                return matchHand;
            }
        }

        /**
         * Draws a card from the relevant card deck, then adds it to the player hand
         */
        public synchronized void drawCard() {
            Card drawnCard = drawDeck.removeCard();
            this.addPlayerCardHand(drawnCard);

            // //remove nl test
            // this.addPlayerHand(drawnCard.getCardValue());

            // //REMOVE TEST DELETE
            // System.out.println("player " + playerNumber +" draws a " + drawnCard.getCardValue() +" from deck " + (playerNumber) + "\n"
            //                 + "Player " + playerNumber + " current hand is " + getPlayerHand().toString().replace("[", "").replace("]", "").replace(",", "") + "\n"
            //                 + "deck " + (playerNumber) + ": " + drawDeck.getDeckHand() + "drawn");

            try {
                int finalCard = (cardHand.size()-1);
                this.fileWriter.write("Player " + playerNumber + " draws a " + cardHand.get(finalCard).getCardValue() + " from deck " + (playerNumber) + "\n");
                this.fileWriter.flush();
            } catch (IOException e) {
                System.out.println("Draw card filewriter error, player " + playerNumber);
                e.printStackTrace();
            }
        }

        /**
         * Selects a card that is not the players favourite card
         * Sends it to the required deck
         */
        public synchronized void discardCard() {
            boolean favouriteCard=true;
            while (favouriteCard) {
                int rand = random.nextInt(cardHand.size());
                int theCard = cardHand.get(rand).getCardValue();
                if (playerNumber != theCard){
                    cardHand.remove(rand);
                    discardDeck.addDeckHand(theCard);

                    // //remove nl test
                    // hand.remove(rand);
                    
                    // //REMOVE TEST DELETE
                    // System.out.println("Player " + playerNumber + " discards a " + theCard + " to deck " + (playerNumber%playerCount+1) + "\n"
                    // + "deck " + (playerNumber%playerCount+1) + ": " + discardDeck.getDeckHand() + " discarded" + "\n"
                    // + "player "+playerNumber+" hand: "+getPlayerCards() + " discarded");

                    try {    
                        this.fileWriter.write("Player " + playerNumber + " discards a " + theCard + " to deck " + (playerNumber%playerCount+1) + "\n"
                            + "Player " + playerNumber + " current hand is " + getPlayerCards().toString().replace("[", "").replace("]", "").replace(",", "") + "\n");
                        this.fileWriter.flush();
                    } catch (IOException e) {
                        System.out.println("Discard filewritter card error");
                        e.printStackTrace();
                    }
                    favouriteCard= false;
                }
            }

        }

        /**
         * Writes exit message to player file before closing the file
         */
        public void closeFile() {

            //Writes final winner message and closes file
            try{
                if (this == winner) {
                    this.fileWriter.write("Player " + playerNumber + " wins" + "\n"
                        + "Player " + playerNumber + " exits" + "\n"
                        + "Player " + playerNumber + " final hand: " + getPlayerCards().toString().replace("[", "").replace("]", "").replace(",", ""));
                    this.fileWriter.flush();
                } else {
                    this.fileWriter.write("Player " + winner.getPlayerNumber() + " has informed " + "Player " + playerNumber
                    + " that Player " + winner.getPlayerNumber() + " has won \n"
                    + "Player " + getPlayerNumber() + " exits" + "\n"
                    + "Player " + getPlayerNumber() + " hand: " + getPlayerCards().toString().replace("[", "").replace("]", "").replace(",", ""));
                    this.fileWriter.flush();
                }
                this.fileWriter.close();
            }catch (IOException e){
                System.out.println("Close filewriter error. Player " + getPlayerNumber());
                e.printStackTrace();
            }
        }


        /**
         * Run threads
         */
        @Override
        public void run() {
            //Creates winner flag, then starts the game until a winner is found and a thread is not interrupted
            Boolean winningHand = false;
            winningHand = checkHand(); 
            while (!Thread.currentThread().isInterrupted() && !winningHand) {
                winningHand = checkHand();

                try {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        System.out.println("Sleep error draw player" + playerNumber);
                        e.printStackTrace();
                    }
                    drawCard();
                } catch (Exception e) {
                    System.out.println("Failed to execute draw action, player " + playerNumber);
                    e.printStackTrace();
                }

                try {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        System.out.println("Sleep error discard, player " + playerNumber);
                        e.printStackTrace();
                    }
                    discardCard();
                } catch (Exception e) {
                    System.out.println("Failed to execute discard action, player " + playerNumber);
                    e.printStackTrace();
                }
                
                winningHand = checkHand();
                
            }
            //If the winning hand flag is true, the game goes into its end phase and starts its winner declaration
            if (winningHand) {
                //Sets the variable winner to the winning player object
                currentGame.winner = this;
                currentGame.stop();
            }
        }
    }


    /**
     * Constructor for the main class, CardGame. Starts the game; sets player, verifies pack file, deals cards, and starts threads
     * @throws IOException
     */
    public CardGame() throws IOException{
        Scanner sc = new Scanner(System.in); 
        System.out.println("Welcome to the CardGame! \n"
                        + "You will need to enter how many players will play \n"
                        + "You will then need to enter the location of the relevant pack");

        
        //Converts pack file into usable cards and verifies if the size is applicable otherwise looping until they are
        Boolean verifyCardAmount = true;
        while (verifyCardAmount) {
            System.out.println("Please enter the number of players: "); 
            this.playerCount = sc.nextInt();

            this.pack = new ArrayList<>(packGenerator(sc));

            // //REMOVE TEST DELETE
            // System.out.println("Pack: " + pack);
            // System.out.println("Pack size: " + pack.size());

            //Sets the negative flag to true if there is a card with a negative value in the pack
            Boolean negativeFlag = false;;
            for (int i = 0; i < pack.size(); i++) {
                Integer cardValue = pack.get(i);
                if (cardValue < 0) {
                    negativeFlag = true;
                    System.out.println("There can not be negative numbers in the pack.");
                }
            }
            if (negativeFlag) {
                continue;
            }
            
            //Determines if the pack is the adequate size
            if (pack.size() == playerCount*8) {
                verifyCardAmount = false;
            } else {
                System.out.println("Please ensure there are a valid number of cards in the pack or change the player count. \n"
                                + "There should be eight times as many cards as there are players. \n");
            }
            
        }
        
        // creates players and add them to the ArrayList. Creates player threads and adds them to the threadlist
        for (int i=0; i<this.playerCount; i++){ 
            CardDeck newDeck = new CardDeck(i+1);
            this.decks.add(newDeck);
            Player newPlayer = new Player(i+1, this);
            this.players.add(newPlayer);
            this.threadList.add(new Thread(newPlayer));
        }
        //Sets the decks which the players will discard to
        for (int i=0; i<this.playerCount; i++) {
            players.get(i).setDiscardDeck(playerCount);
        }
        //Deals the cards 
        this.deal(playerCount);
        
    }

    public static void main(String[] args) throws IOException {
        CardGame game = new CardGame();
        try {
            game.runGame();
        } catch (InterruptedException e) {
            System.out.println("runGame error");
            e.printStackTrace();
        }
    }

    
}

