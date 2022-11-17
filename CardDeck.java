import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CardDeck {

    /**
     * Initialise variables
     * */
    private Integer deckNumber;
    public ArrayList<Integer> deck = new ArrayList<Integer>();
    public ArrayList<Card> cardDeck = new ArrayList<Card>();
    public final ReentrantLock deckLock;
    private FileWriter fileWriter;

    /**
     * Constructs a deck, requires a deckNumber to identify which deck it is. Creates deck output files
     * @param deckNumber
     * */
    public CardDeck(Integer deckNumber){
        this.deckNumber = deckNumber;
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
     * Obtains the list of cards that are in the deck
     * @return cardDeck
     */
    public ArrayList<Card> getDeckCards() { return cardDeck; }

    /**
     * Adds a card to the deck
     * @param cardValue
     * */
    public synchronized void addDeckHand(Integer cardValue) {
        this.deck.add(cardValue);
    }

    /**
     * Adds a card to the deck
     * @param card
     */
    public synchronized void addDeckCard(Card card) {
        this.cardDeck.add(card);
    }

    /**
     * Sets the whole deck
     * @param deck
     */
    public synchronized void setDeckHand(ArrayList<Integer> deck) {
        this.deck = deck;
    }

    /**
     * Sets the entire deck
     * @param cardDeck
     */
    public synchronized void setDeckCards(ArrayList<Card> cardDeck) {
        this.cardDeck = cardDeck;
    }
    
    /**
     * Removes a card from the deck
     * @param 
     * */
    public synchronized Integer removeCard() {
        //Integer cardRemoved = this.cardDeck.get(0).getCardValue();
        Integer intPicked = this.deck.get(0);
        this.deck.remove(0);
        //this.cardDeck.remove(0);
        return intPicked;
    }

    /**
     * Closes output file
     */
    public void closeFile() {
        ArrayList<Card> cardDeck = this.getDeckCards();
        ArrayList<Integer> intCardDeck = new ArrayList<Integer>();
        for (Card card : cardDeck) {
            Integer cardIntValue = card.getCardValue();
            intCardDeck.add(cardIntValue);
        }
        try{
            this.fileWriter.write("Deck" + deckNumber + " contents: " 
                + this.getDeckHand().toString().replace("[", "").replace("]", "").replace(",", ""));
            // this.fileWriter.write("Deck" + deckNumber + " contents: " 
            //                     + intCardDeck.toString().replace("[", "").replace("]", "").replace(",", ""));
            this.fileWriter.flush();
            this.fileWriter.close();
        }catch (IOException e){
            System.out.println("An IOException occurred.");
            e.printStackTrace();
        }
    }
}
