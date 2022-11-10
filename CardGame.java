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
    private CardGame currentGame = this;
    private volatile static Player winner;
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
        ArrayList<Integer> winnersHand = new ArrayList<Integer>();
        winnersHand = winner.getPlayerHand();
        System.out.println("Player" + winner.getPlayerNumber() + " wins");
        
        for (Player player : this.players) {
            player.closeFile();
        }
        for (CardDeck cardDeck : this.decks) {
            cardDeck.closeFile();
        }
        System.out.println("All files are closed.\nThank you for playing.");
        System.out.println("The program will now end.");
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
        String[] packInINT = packInString.toArray(new String[0]);
        List<Integer> intList = packInString.stream()
                .map(s -> Integer.parseInt(s))
                .collect(Collectors.toList());

        //shuffles pack file
        // Boolean shuffleFlag = true;
        // while (shuffleFlag) {
        //     System.out.println("Would you like to shuffle the cards? <y/n>");
        //     String toShuffle = sc.nextLine();
        //     if (toShuffle == "y") {
        //         System.out.println("The deck will be shuffled.");
        //         Collections.shuffle(intList);
        //         shuffleFlag = false;
        //     } if (toShuffle == "n") {
        //         System.out.println("The deck will not be shuffled.");
        //         shuffleFlag = false;
        //     } else {
        //         continue;
        //     }
        // }
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
        for (int i= packSize/2; i < packSize; i++) {
            deckCards.add(pack.get(i));
        }
        System.out.println("playerCards " + playerCards);
        System.out.println("deckCards " + deckCards);
        int playerCardSize = playerCards.size();
        int deckCardSize = deckCards.size();

        
        //hashmap , playerid, list of cards

        //Deal from playerCards to hashmap
        for (int i = 0; i < playerCount; i++) {
            for (int j = 0; j < playerCardSize; j++) {
                Integer dealtCard = playerCards.get(j);
                int playerNumber = j % playerCount + 1;
                if (playerNumber == i + 1) {
                    playersHands.get(playerNumber).add(dealtCard);
                }
            }
        }

        //Deal from deckCards to Hashmap
        for (int i = 0; i < playerCount; i++) {
            for (int j = 0; j < deckCardSize; j++) {
                Integer dealtCard = deckCards.get(j);
                int deckNumber = j % playerCount + 1;
                if (deckNumber == i +1) {
                    decksHands.get(deckNumber).add(dealtCard);
                }
            }
        }
        System.out.println("playersHands " + playersHands.values());
        System.out.println("decksHands " + decksHands.values());
        
        //deal to players hands from hashmap
        for (int j = 0; j < playerCount; j++) {
            Integer playerNumber = j%playerCount + 1;
            ArrayList<Integer> handTransfer = playersHands.get(playerNumber);
            Player specifiedPlayer = players.get(playerNumber-1);
            specifiedPlayer.setPlayerHand(handTransfer);
            System.out.println("player" + playerNumber + " playerHand:" + specifiedPlayer.getPlayerHand());
        }

        //deal to decks from hashmap
        for (int j = 0; j < playerCount; j++) {
            Integer deckNumber = j%playerCount + 1;
            ArrayList<Integer> handTransfer = decksHands.get(deckNumber);
            CardDeck specifiedDeck = decks.get(deckNumber-1);
            specifiedDeck.setDeckHand(handTransfer);
            System.out.println("deck" + deckNumber + " deckHand:" + specifiedDeck.getDeckHand());

        }


    }


    public class Player extends Thread {

        /**
         * Initialise variables
         * */
        public Boolean matchHand = false;
        public Integer playerNumber;
        public ArrayList<Card> cards = new ArrayList<Card>();
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
                    System.out.println("File creation error");
                    e.printStackTrace();
                }
        }


        // GETTERS
        public Integer getPlayerNumber() { return playerNumber; }
        public ArrayList<Integer> getPlayerHand() { return hand; }
        public ArrayList<Card> getPlayerCards() { return cards; }
        public Boolean getMatchHand() { return matchHand; }

        //SETTERS
        public void addPlayerHand(Integer cardValue) {
            this.hand.add(cardValue);
        }

        public void setPlayerHand(ArrayList<Integer> playerHand) {
            this.hand = playerHand;
        }

        public void addCard(Card card) {
            this.cards.add(card);
        }

        public void setAllCards(ArrayList<Card> cards) {
            this.cards = cards;
        }


        /**
         * Checks if the player has a matching hand
         * @return matchHand
         */
        public Boolean checkHand() {
            Integer potentialMatch = hand.get(0);
            double sum = 0;
            for (Integer number : hand){
                sum = number + sum;
            }
            if (sum / 4 == potentialMatch) {
                try {
                    System.out.println("Player" + playerNumber + "wins!");
                    this.fileWriter.write("Player" + playerNumber + "wins!");
                } catch (Exception e) {
                    System.out.println("Checkhand error");
                    e.printStackTrace();
                }
                this.matchHand = true;
                return matchHand;
            } else {
                return matchHand;
            }
        }

        public void drawCard() {
            Integer desiredDeck = playerNumber - 1;
            Integer drawnCard = decks.get(desiredDeck).removeCard();
            this.addPlayerHand(drawnCard);
            System.out.println("hand " + hand);
            System.out.println("deck" + decks.get(desiredDeck).getDeckNumber() + "hand " + decks.get(desiredDeck).getDeckHand());
        }

        /**
         * Selects a card that is not the players favourite card
         * Sends it to the required deck
         */
        public void discardCard() {
            
            Boolean favouriteCard = true;
            Integer discardedCard;
            do  {
                int randint = random.nextInt(hand.size());
                discardedCard = hand.get(randint);
                if (discardedCard != playerNumber) {
                    hand.remove(randint);
                    break;
                }
            } while (favouriteCard);

            Integer destinationDeck = playerNumber;
            Integer oneLessPlayers = playerCount - 1;
            if (playerCount == playerNumber) {
                destinationDeck = playerCount - oneLessPlayers;
            }
            decks.get(destinationDeck).addDeckHand(discardedCard);
            try {
                this.fileWriter.write("Player" + playerNumber + " discards a " + discardedCard + " to deck" + destinationDeck + "\n");
                System.out.println("Player" + playerNumber + " discards a " + discardedCard + " to deck" + destinationDeck);
                this.fileWriter.flush();
            } catch (IOException e) {
                System.out.println("Discard card error");
                e.printStackTrace();
            }
        }

        public void closeFile() {
            try{
                this.fileWriter.close();
            }catch (IOException e){
                System.out.println("An IOException occurred.");
                e.printStackTrace();
            }
        }

        /**
         * run threads
         */
        @Override
        public void run(){
            Boolean winningHand = false;
            winningHand = checkHand(); 
            while (!Thread.currentThread().isInterrupted() && !winningHand) {
                drawCard();
                discardCard();
                winningHand = checkHand();
            }
            if (winningHand) {
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
        

        //Checks if 8N amount of players
        Boolean verifyCardAmount = true;
        while (verifyCardAmount) {
            System.out.println("Please enter the number of players: "); 
            this.playerCount = sc.nextInt();

            this.pack = new ArrayList<>(packGenerator(sc));
            System.out.println("Pack: " + pack);
            System.out.println("Pack size: " + pack.size());
            

            for (int i = 0; i < pack.size(); i++) {
                Integer cardValue = pack.get(i);
                if (cardValue < 0) {
                    System.out.println("There can not be negative numbers in the pack."); 
                }
            }
            
            if (pack.size() == playerCount*8) {
                verifyCardAmount = false;
                break;
            } else {
                System.out.println("Please ensure there are a valid number of cards in the pack or change the player count. \n"
                                + "There should be eight times as many cards as there are players. \n");
            }
            
        }
        
        // creates players and add them to the ArrayList. Create Threads with a player assigned and add to the threadlist
        for (int i=0; i<this.playerCount; i++){ 
            Player newPlayer = new Player(i+1, this);
            CardDeck newDeck = new CardDeck(i+1, this);
            this.players.add(newPlayer);
            playersHands.put(i+1, newPlayer.getPlayerHand());
            decksHands.put(i+1, newDeck.getDeckHand());
            this.decks.add(newDeck);
            this.threadList.add(new Thread(newPlayer));
        }
        
        //CREATE PLAYER AND DECK FILES

        deal(playerCount);
        
        this.runGame();
        
    }

    public static void main(String[] args) throws IOException {
        CardGame game = new CardGame();
    }

    
}

