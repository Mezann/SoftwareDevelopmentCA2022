import java.util.ArrayList;
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
    private FileWriter fileWriter;

    /**
     * Constructs a deck, requires a deckNumber to identify which deck it is. Creates deck output files
     * @param deckNumber
     * */
    public CardDeck(Integer deckNumber){
        this.deckNumber = deckNumber;

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
     * Sets the entire deck of cards using an integer array
     * @return
     */
    public void setDeckCards(ArrayList<Integer> deckHand) {
        ArrayList<Card> cardTransfer = new ArrayList<Card>();
            for (Integer card: deckHand) {
                cardTransfer.add(new Card(card));
            }
            this.cardDeck = cardTransfer;
    }

    /**
     * Obtains the list of cards that are in the deck as an integer array
     * @return intDeckCards
     */
    public ArrayList<Integer> getDeckCards() { 
        ArrayList<Integer> intDeckCards = new ArrayList<Integer>();
        for (Card card : cardDeck) {
            int cardValue = card.getCardValue();
            intDeckCards.add(cardValue);
        }
        return intDeckCards; 
    }

    /**
     * Adds a card to the deck
     * @param cardValue
     * */
    public void addDeckHand(Integer cardValue) {
        // //remove nl test
        // this.deck.add(cardValue);
        this.cardDeck.add(new Card(cardValue));
    }

    
    /**
     * Removes a card from the deck
     * @param 
     * */
    public Card removeCard() {
        Card cardRemoved = this.cardDeck.get(0);
        // //remove nl test
        // this.deck.remove(0);
        this.cardDeck.remove(0);
        return cardRemoved;
    }

    /**
     * Closes output file
     */
    public void closeFile() {
        try{
            this.fileWriter.write("Deck" + deckNumber + " contents: " 
                + getDeckCards().toString().replace("[", "").replace("]", "").replace(",", ""));
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
