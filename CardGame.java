import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;



public class CardGame {

    //Attributes
    private Integer playerCount;
    private List<Player> players = new ArrayList<Player>();
    private List<Card> decks = new ArrayList<Card>();
    private List<Thread> threadPool = new ArrayList<Thread>();
    
    //MAKES THREADS
    public synchronized void runGame(){
        for (Thread thread : this.threadPool){
            thread.start();
        }
    }
    public void endGame() {
        
    }

    //distribute cards
    //add players
    //make threads

    //Starts game || Constructor
    public CardGame(){
    //Read pack.txt
    Scanner sc = new Scanner(System.in); 
    System.out.println("Welcome to the CardGame! \n"
                    + "You will need to enter how many players will play \n"
                    + "You will then need to enter the location of the relevant pack");

    System.out.println("Please enter the number of players: "); 
    this.playerCount = sc.nextInt();
    System.out.println("Please enter the location of the desired pack: ");
    sc.nextLine();


    for (int i=0; i<this.playerCount; i++){ // create players and add them to the ArrayList. Create Threads with a player assigned and add to the threadpool
        this.players.add(new Player(i));
        this.threadPool.add(new Thread(players.get(i)));
    }
    this.runGame();

    }






    
}

