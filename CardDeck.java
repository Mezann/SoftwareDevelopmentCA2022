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

public class CardDeck {
    //attributes
    private Integer deckNumber;
    public ArrayList<Integer> deckList = new ArrayList<Integer>();
    public final ReentrantLock deckLock;

    //constructor
    public CardDeck(Integer deckNumber){
        this.deckNumber = deckNumber;
        deckLock = new ReentrantLock();
    }


    // GET ATTRIBUTE VALUES
    public Integer getDeckNumber() { return deckNumber; }
    public ArrayList<Integer> getDeckList() { return deckList; }

    /**
     * SETS CARDS IN DECK
    **/
    public void setDeck(Integer cardValue) {
            this.deckList.add(cardValue);
    }

}
