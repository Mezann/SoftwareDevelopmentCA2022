import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;
import java.util.AbstractMap.SimpleEntry;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class CardDeck {

    /**
     * Initialise variables
     * */
    private Integer deckNumber;
    public ArrayList<Integer> deck = new ArrayList<Integer>();
    public ArrayList<Card> cardDeck = new ArrayList<Card>();
    public final ReentrantLock deckLock;
    private FileWriter fileWriter;
    private CardGame currentCardGame;

    /**
     * Constructs a deck, requires a deckNumber to identify which deck it is. Creates deck output files
     * @param deckNumber
     * */
    public CardDeck(Integer deckNumber, CardGame currentCardGame){
        this.deckNumber = deckNumber;
        this.currentCardGame = currentCardGame;
        deckLock = new ReentrantLock();

        String filename = "deck" + deckNumber.toString() + "_output.txt";
                File file = new File(filename);

                try {
                    //Checks if file exists
                    if (file.exists() && !file.isDirectory()) {
                        this.fileWriter = new FileWriter(filename); 
                    } else {
                        file.createNewFile();
                        this.fileWriter = new FileWriter(filename);
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    System.out.println("File creation error");
                    e.printStackTrace();
                }
    }

    /**
     * Obtains the number of the deck to identify it
     * @return deckNumber
     * */
    public Integer getDeckNumber() { return deckNumber; }
    /**
     * Obtains the list of cards that are in the deck
     * @return deckNumber
     * */
    public ArrayList<Integer> getDeckHand() { return deck; }

    /**
     * Adds a card to the deck
     * @param cardValue
     * */
    public void addDeckHand(Integer cardValue) {
        this.deck.add(cardValue);
    }

    /**
     * Sets the whole deck
     * @param deck
     */
    public void setDeckHand(ArrayList<Integer> deck) {
        this.deck = deck;
    }
    
    /**
     * Removes a card from the deck
     * @param 
     * */
    public Integer removeCard() {
        Integer pickedCard = this.deck.get(0);
        this.deck.remove(0);
        return pickedCard;
    }

    /**
     * Closes output file
     */
    public void closeFile() {
        try{
            this.fileWriter.write("Deck" + deckNumber + " contents: " + this.getDeckHand());
            this.fileWriter.flush();
            this.fileWriter.close();
        }catch (IOException e){
            System.out.println("An IOException occurred.");
            e.printStackTrace();
        }
    }
}
