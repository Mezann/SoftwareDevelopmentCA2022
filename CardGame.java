/**
 * Card Game 
 * 
 * @version 1.0
 * @author Mezan AHMED & Omar AL GHAITHY
 */

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.lang.Thread;
import java.util.HashMap;

public class CardGame {

    /**
     * Initialise variables
     * */
    private Integer playerCount;
    private List<Player> players = new ArrayList<Player>();
    private List<CardDeck> decks = new ArrayList<CardDeck>();
    private List<Integer> pack = new ArrayList<Integer>();
    private List<Thread> threadList = new ArrayList<Thread>();
    private HashMap<Integer, ArrayList<Integer>> playersHands = new HashMap<Integer, ArrayList<Integer>>();
    private HashMap<Integer, ArrayList<Card>> playersCardHands = new HashMap<Integer, ArrayList<Card>>();
    private HashMap<Integer, ArrayList<Card>> decksCardHands = new HashMap<Integer, ArrayList<Card>>();
    private HashMap<Integer, ArrayList<Integer>> decksHands = new HashMap<Integer, ArrayList<Integer>>();
    private volatile Player winner;
    public final ReentrantLock gameLock;
    

    /**
     * Starts all the player threads 
     * */
    public synchronized void runGame() {
        for (Thread thread : this.threadList){
            thread.start();
        }
    }

    /**
     * Locks the game at whatever instance it is in, then interrupting all the threads
     * */
    public void stop() {
        this.gameLock.lock();
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

        System.out.println("Player" + winner.getPlayerNumber() + " wins" + "\n"
                        + "All files are closed.\nThank you for playing." + "\n"
                        + "The program will now end.");
                        
        this.gameLock.unlock();
        System.exit(0);
    }



    /**
     * Reads the pack file, converting it from a string into an integer array list
     * @return intList
     * @throws IOException
     * */
    public List<Integer> packGenerator(Scanner sc) throws IOException {
        System.out.println("Please enter the location of the pack to load: ");
        String fileName = sc.next();
        String replacedFile = fileName.replace("\n", "");
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
        //String[] packInINT = packInString.toArray(new String[0]);
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
        List<Integer> playerCards = new ArrayList<Integer>();
        List<Card> playerCardCards = new ArrayList<Card>();
        List<Integer> deckCards = new ArrayList<Integer>();
        List<Card> deckCardCards = new ArrayList<Card>();

        int packSize = pack.size();

        for (int i = 0; i < packSize/2; i++) {
            playerCards.add(pack.get(i));
            Card packCard = new Card(pack.get(i));
            playerCardCards.add(packCard);
        }
        for (int i= packSize/2; i < packSize; i++) {
            deckCards.add(pack.get(i));
            Card packCard = new Card(pack.get(i));
            deckCardCards.add(packCard);
        }

        System.out.println("playerCards " + playerCards);
        System.out.println("deckCards " + deckCards);
        int playerCardSize = playerCardCards.size();
        int deckCardSize = deckCardCards.size();

        
        //hashmap , playerid, list of cards

        //Deal from playerCards to hashmap
        for (int i = 0; i < playerCount; i++) {
            for (int j = 0; j < playerCardSize; j++) {
                Integer pickedCard = playerCards.get(j);
                Card dealtCard = new Card(pickedCard);
                int playerNumber = j % playerCount + 1;
                if (playerNumber == i + 1) {
                    playersHands.get(playerNumber).add(pickedCard);
                    playersCardHands.get(playerNumber).add(dealtCard);
                }
            }
        }

        //Deal from deckCards to Hashmap
        for (int i = 0; i < playerCount; i++) {
            for (int j = 0; j < deckCardSize; j++) {
                Integer pickedCard = deckCards.get(j);
                Card dealtCard = new Card(pickedCard);
                int deckNumber = j % playerCount + 1;
                if (deckNumber == i +1) {
                    decksHands.get(deckNumber).add(pickedCard);
                    decksCardHands.get(deckNumber).add(dealtCard);
                }
            }
        }
        System.out.println("playersHands " + playersHands.values());
        System.out.println("decksHands " + decksHands.values());
        
        //deal from hashmap to player hand
        for (int j = 0; j < playerCount; j++) {
            Integer playerNumber = j%playerCount + 1;
            ArrayList<Integer> handTransfer = playersHands.get(playerNumber);
            ArrayList<Card> cardTransfer = playersCardHands.get(playerNumber);
            Player specifiedPlayer = players.get(playerNumber-1);
            specifiedPlayer.setPlayerHand(handTransfer);
            specifiedPlayer.setAllCards(cardTransfer);
            System.out.println("player" + playerNumber + " playerHand:" + specifiedPlayer.getPlayerHand());
            //Writes players initial hand to file
            try {
                specifiedPlayer.getFileWriter().write("Player " + playerNumber + " initial hand "
                     + specifiedPlayer.getPlayerHand().toString().replace("[", "").replace("]", "").replace(",", "") + "\n");
            } catch (IOException e) {
                System.out.println("Deal filewriter error, player" + playerNumber);
                e.printStackTrace();
            }
        }

        //deal from hashmap to deck hand
        for (int j = 0; j < playerCount; j++) {
            Integer deckNumber = j%playerCount + 1;
            ArrayList<Integer> handTransfer = decksHands.get(deckNumber);
            ArrayList<Card> cardTransfer = decksCardHands.get(deckNumber);
            CardDeck specifiedDeck = decks.get(deckNumber-1);
            specifiedDeck.setDeckHand(handTransfer);
            specifiedDeck.setDeckCards(cardTransfer);
            System.out.println("deck" + deckNumber + " deckHand:" + specifiedDeck.getDeckHand());

        }


    }


    public class Player extends Thread {

        /**
         * Initialise variables
         * */
        public Boolean matchHand = false;
        public Integer playerNumber;
        public ArrayList<Card> cardHand = new ArrayList<Card>();
        public ArrayList<Integer> hand = new ArrayList<Integer>();
        private Random random = new Random();
        private FileWriter fileWriter;
        private CardGame currentGame;
        
        
        // constructor
        /**
         * Constructor for a player. Initialises playerNumber and creates player output files.
         * @param playerNumber
         */
        public Player(Integer playerNumber, CardGame currentGame) {
                this.playerNumber = playerNumber;
                this.currentGame = currentGame;
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
         * Returns the player's hand as an integer array
         * @return hand
         */
        public ArrayList<Integer> getPlayerHand() { return hand; }

        /**
         * Returns the player's hand as a card type array
         * @return cardHand
         */
        public ArrayList<Card> getPlayerCards() { return cardHand; }

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
         * Adds one card to the player hand, as an integer
         */
        public void addPlayerHand(Integer cardValue) {
            this.hand.add(cardValue);
        }

        /**
         * Sets all the cards in the players hand, as integers
         * @param playerHand
         */
        public void setPlayerHand(ArrayList<Integer> playerHand) {
            this.hand = playerHand;
        }

        /**
         * Adds one card to the player hand, as a card object
         * @param card
         */
        public void addPlayerCardHand (Card card) {
            this.cardHand.add(card);
        }
        
        /**
         * Sets all the cards in the players hand, as card objects
         * @param cards
         */
        public void setAllCards(ArrayList<Card> cards) {
            this.cardHand = cards;
        }


        /**
         * Checks if the player has a matching hand
         * @return matchHand
         */
        public Boolean checkHand() {
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
            int desiredDeck = playerNumber -1;
            int drawnCard= decks.get(desiredDeck).removeCard();
            Card newCard = new Card(drawnCard);
            this.addPlayerCardHand(newCard);
            this.addPlayerHand(drawnCard);
            System.out.println("player " + playerNumber +" draws a " + drawnCard +" from deck " + (desiredDeck+1) + "\n");

            try {
                this.fileWriter.write("Player " + playerNumber +" draws a " + drawnCard +" from deck " + (desiredDeck+1) + "\n");
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
            int deckNumber;
            if(playerNumber==playerCount){
                deckNumber=0;
            }else{
                deckNumber=playerNumber;
            }

            boolean favouriteCard=true;
            while (favouriteCard) {
                int rand = random.nextInt(cardHand.size());
                int theCard = hand.get(rand);

                if (playerNumber!= theCard){
                    hand.remove(rand);
                    decks.get(deckNumber).addDeckHand(theCard);
                    try {
                        this.fileWriter.write("Player " + playerNumber + " discards a " + theCard + " to deck " + (deckNumber+1) + "\n"
                            + "Player " + playerNumber + " current hand is " + getPlayerHand().toString().replace("[", "").replace("]", "").replace(",", "") + "\n");
                        System.out.println("player "+playerNumber+" discards a "+theCard+" to deck "+ (deckNumber+1) + "\n"
                            + "deck "+(deckNumber+1)+decks.get(deckNumber).getDeckHand() + " discarded" + "\n"
                            + "player "+playerNumber+" hand: "+getPlayerHand() + " discarded");
                        this.fileWriter.flush();
                    } catch (IOException e) {
                        System.out.println("Discard filewritter card error");
                        e.printStackTrace();
                    }
                    favouriteCard= false;
                }else continue;
                
                
            }

        }

        /**
         * Writes exit message to player file before closing the file
         */
        public void closeFile() {
            //Converts card array to integer array
            ArrayList<Card> cardDeck = this.getPlayerCards();
            ArrayList<Integer> intCardHand = new ArrayList<Integer>();
            for (Card card : cardDeck) {
                Integer cardIntValue = card.getCardValue();
                intCardHand.add(cardIntValue);
            }

            //Writes final winner message and closes file
            try{
                if (this == winner) {
                    this.fileWriter.write("Player " + playerNumber + " wins" + "\n"
                        + "Player " + playerNumber + " exits" + "\n"
                        + "Player " + playerNumber + " final hand: " + hand.toString().replace("[", "").replace("]", "").replace(",", ""));
                    this.fileWriter.flush();
                } else {
                    this.fileWriter.write("Player " + winner.getPlayerNumber() + " has informed " + "Player " + playerNumber
                    + " that Player " + winner.getPlayerNumber() + " has won \n"
                    + "Player " + getPlayerNumber() + " exits" + "\n"
                    + "Player " + getPlayerNumber() + " hand: " + getPlayerHand().toString().replace("[", "").replace("]", "").replace(",", ""));
                    this.fileWriter.flush();
                }
                this.fileWriter.close();
            }catch (IOException e){
                System.out.println("Close filewriter error. Player " + getPlayerNumber());
                e.printStackTrace();
            }
        }


        /**
         * run threads
         */
        @Override
        public void run(){
            //Creates winner flag, then starts the game until a winner is found and a thread is not interrupted
            Boolean winningHand = false;
            winningHand = checkHand(); 
            while (!Thread.currentThread().isInterrupted() && !winningHand) {
                winningHand = checkHand();
                drawCard();
                discardCard();
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
        gameLock = new ReentrantLock();
        System.out.println("Welcome to the CardGame! \n"
                        + "You will need to enter how many players will play \n"
                        + "You will then need to enter the location of the relevant pack");
        

        //Converts pack file into usable cards and verifies if the size is applicable otherwise looping until they are
        Boolean verifyCardAmount = true;
        while (verifyCardAmount) {
            System.out.println("Please enter the number of players: "); 
            this.playerCount = sc.nextInt();

            this.pack = new ArrayList<>(packGenerator(sc));
            //remove test
            System.out.println("Pack: " + pack);
            System.out.println("Pack size: " + pack.size());

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
            Player newPlayer = new Player(i+1, this);
            CardDeck newDeck = new CardDeck(i+1);
            this.players.add(newPlayer);
            this.decks.add(newDeck);
            playersHands.put(i+1, newPlayer.getPlayerHand());
            playersCardHands.put(i+1, newPlayer.getPlayerCards());
            decksHands.put(i+1, newDeck.getDeckHand());
            decksCardHands.put(i+1, newDeck.getDeckCards());
            this.threadList.add(new Thread(newPlayer));
        }

        this.deal(playerCount);
        
    }

    public static void main(String[] args) throws IOException {
        CardGame game = new CardGame();
        game.runGame();
    }

    
}

