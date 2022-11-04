import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.lang.Runnable;
import java.lang.Thread;

public class CardGame {

    /**
     * Initialise variables
     */
    private Integer playerCount;
    private List<Player> players = new ArrayList<Player>();
    private List<Card> decks = new ArrayList<Card>();
    private List<Integer> pack = new ArrayList<Integer>();
    private List<Thread> threadList = new ArrayList<Thread>();
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
     */
    public void endGame() {
        
    }

    // public Card deckGenerator(){
    //     return i;
    // }



    public class Player extends Thread {

        // attributes
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
        public void setPlayerHand(Integer cardValue) {
                this.hand.add(cardValue);
        }

        /**
         * @param t
         * @return
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
    public CardGame(){
        Scanner sc = new Scanner(System.in); 
        gameLock = new ReentrantLock();

        System.out.println("Welcome to the CardGame! \n"
                        + "You will need to enter how many players will play \n"
                        + "You will then need to enter the location of the relevant pack");
        System.out.println("Please enter the number of players: "); 
        this.playerCount = sc.nextInt();
        System.out.println("Please enter the location of the desired pack: ");
        sc.nextLine();
            //read files


        //INCOMPLETE: CONSTRUCT THREAD CONSTRUCTOR
        // creates players and add them to the ArrayList. Create Threads with a player assigned and add to the threadlist
        for (int i=0; i<this.playerCount; i++){ 
            Player newPlayer = new Player(i);
            CardDeck newDeck = new CardDeck(i);
            this.players.add(newPlayer);
            this.decks.add(newDeck);
            this.threadList.add(new Thread(newPlayer));
        }
        
        this.runGame();

    }

    public static void main(String[] args) {
        //run game here
    }

    
}

