import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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
    public HashMap<Integer, ArrayList<Integer>> playersHands = new HashMap<Integer, ArrayList<Integer>>();
    public HashMap<Integer, ArrayList<Integer>> decksHands = new HashMap<Integer, ArrayList<Integer>>();
    private volatile Player winner;
    public final ReentrantLock gameLock;
    

    /**
     * Starts all the threads and thus players
     * */
    public synchronized void runGame() {
        for (Thread thread : this.threadList){
            thread.start();
        }
    }

    /**
     * Locks the game at whatever instance it is in, then interrupting the threads
     * */
    public void stop() {
        this.gameLock.lock();
        for (Thread thread : this.threadList){
            thread.interrupt();
        }
        this.endGame();
    }


    /**
     * end game
     * */
    public void endGame() {
        
    }

    /**
     * Reads the pack file, converting it from a string into an integer array list
     * @return intList
     * @throws IOException
     * */
    public List<Integer> packGenerator(Scanner sc) throws IOException{
        System.out.println("Please enter the location of the pack to load: ");
        String fileName = sc.nextLine();        
        BufferedReader packLoc = new BufferedReader(new FileReader(fileName));
        // read entire line as string
        String line = packLoc.readLine();
        ArrayList<String> packInString = new ArrayList<>();
        // checking for end of file
        while (line != null) {
            packInString.add(line);
            line = packLoc.readLine();
        }
        // closing bufferreader object
        packLoc.close();
        // storing the data in arraylist to array
        String[] packInINT = packInString.toArray(new String[0]);
        List<Integer> intList = packInString.stream()
                .map(s -> Integer.parseInt(s))
                .collect(Collectors.toList());

        Boolean shuffleFlag = true;
        while (shuffleFlag) {
            System.out.println("Would you like to shuffle the cards? <y/n>");
            String toShuffle = sc.nextLine();
            if (toShuffle == "y") {
                System.out.println("The deck will be shuffled.");
                Collections.shuffle(intList);
                shuffleFlag = false;
            } if (toShuffle == "n") {
                System.out.println("The deck will not be shuffled.");
                shuffleFlag = false;
            } else {
                continue;
            }
        }
        return intList;
    }

    /**
     * Deals cards from pack into the players hands and the decks
     * @param playerCount
     */
    public void deal(Integer playerCount) {
        List<Integer> playerCards = new ArrayList<Integer>();
        List<Integer> deckCards = new ArrayList<Integer>();

        int packSize = pack.size();

        for (int i = 0; i < packSize/2; i++) {
            playerCards.add(pack.get(i));
        }
        for (int i= packSize/2; i < packSize; i= i++) {
            deckCards.add(pack.get(i));
        }
        int playerCardSize = playerCards.size();
        int deckCardSize = deckCards.size();

        //hashmap , playerid, list of cards
        //Deal for players to hashmap
        for (int i = 0; i < playerCardSize; i++) {
            Integer dealtCard = playerCards.get(i);
            for (int j = 0; j < playerCardSize; j++) {
                Integer playerNumber = j%playerCount + 1;
                playersHands.get(playerNumber).add(dealtCard); 
            }
        }
        for (int j = 0; j < playerCardSize; j++) {
            Integer playerNumber = j%playerCount + 1;
            ArrayList<Integer> handTransfer = playersHands.get(playerNumber); 
            Player specifiedPlayer = players.get(playerNumber);
            specifiedPlayer.setPlayerHand(handTransfer);
        }

        //Deal for decks to hashmap
        for (int i = 0; i < deckCardSize; i++) {
            Integer dealtCard = deckCards.get(i);
            for (int j = 0; j < deckCardSize; j++) {
                Integer deckNumber = j%playerCount + 1;
                decksHands.get(deckNumber).add(dealtCard); 
            }
        }

        for (int j = 0; j < playerCardSize; j++) {
            Integer deckNumber = j%playerCount + 1;
            ArrayList<Integer> handTransfer = playersHands.get(deckNumber); 
            CardDeck specifiedDeck = decks.get(deckNumber);
            specifiedDeck.setDeckHand(handTransfer);
        }
    }


    public class Player extends Thread {

        /**
         * Initialise variables
         * */
        public Boolean matchHand = false;
        public Integer playerNumber;
        public ArrayList<Integer> hand = new ArrayList<Integer>();
        
        // constructor
        public Player(Integer playerNumber) {
                this.playerNumber = playerNumber;
        }


        // GETTERS
        public Integer getPlayerNumber() { return playerNumber; }
        public ArrayList<Integer> getHand() { return hand; }
        public Boolean getMatchHand() { return matchHand; }

        //SETTERS
        public void addPlayerHand(Integer cardValue) {
                this.hand.add(cardValue);
        }

        public void setPlayerHand(ArrayList<Integer> playerHand) {
                this.hand = playerHand;
        }

        public void pickACard() {

        }

        /**
         * Checks if the player has a matching hand
         * @param ArrayList<Integer> currentHand
         * @return sum/4
         */
        public double checkHand(ArrayList<Integer> currentHand) {
                Integer potentialMatch = (Integer) currentHand.get(0);
                double sum = 0;
                for (int Number : currentHand) {
                        sum = Number + sum;
                }
                if (sum / 4 == potentialMatch) {
                        this.matchHand = true;
                }
                return sum / 4;
        }


        /**
         * run threads
         */
        @Override
        public void run(){
            
        }
    }


    //distribute cards
    //add players
    //make threads
    

    //Starts game || Constructor
    public CardGame() throws IOException{
        Scanner sc = new Scanner(System.in); 
        gameLock = new ReentrantLock();
        System.out.println("Welcome to the CardGame! \n"
                        + "You will need to enter how many players will play \n"
                        + "You will then need to enter the location of the relevant pack");
        System.out.println("Please enter the number of players: "); 
        this.playerCount = sc.nextInt();
        //Checks if 8N amount of players
        Boolean verifyCardAmount = true;
        while (verifyCardAmount) {
            this.pack = new ArrayList<>(packGenerator(sc));
            if (pack.size() == playerCount) {
                break;
            } else {
                System.out.println("Please ensure there are a valid number of cards in the pack.\n"
                                + "There should be eight times as many cards as there are players.");
            }  
        }
        
        // creates players and add them to the ArrayList. Create Threads with a player assigned and add to the threadlist
        for (int i=0; i<this.playerCount; i++){ 
            Player newPlayer = new Player(i+1);
            CardDeck newDeck = new CardDeck(i+1);
            this.players.add(newPlayer);
            playersHands.put(i+1, null);
            decksHands.put(i+1,null);
            this.decks.add(newDeck);
            this.threadList.add(new Thread(newPlayer));
        }
        deal(playerCount);


        this.runGame();

    }

    public static void main(String[] args) {
        //run game here
    }

    
}

