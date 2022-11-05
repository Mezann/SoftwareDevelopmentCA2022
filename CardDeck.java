import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;
import java.util.AbstractMap.SimpleEntry;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

public class CardDeck {

    /**
     * Initialise variables
     * */
    private Integer deckNumber;
    public ArrayList<Integer> deckList = new ArrayList<Integer>();
    public final ReentrantLock deckLock;

    /**
     * Constructs a deck, requires a deckNumber to identify which deck it is
     * @param deckNumber
     * */
    public CardDeck(Integer deckNumber){
        this.deckNumber = deckNumber;
        deckLock = new ReentrantLock();
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
    public ArrayList<Integer> getDeckList() { return deckList; }

    /**
     * Adds a card to the deck
     * @param cardValue
     * */
    public void addCard(Integer cardValue) {
            this.deckList.add(cardValue);
    }
    
    /**
     * Removes a card from the deck
     * @param 
     * */
    public void removeCard() {
        this.deckList.remove(0);
    }

}
