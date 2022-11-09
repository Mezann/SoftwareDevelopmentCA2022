public class Card {
    
    /**
     * Initialise variables
     * */
    private Integer cardValue;

    /**
     * Constructor method for Card wrapper
     * @param cardValue
     */
    public Card(Integer cardValue) {
        this.cardValue = cardValue;
    }

    /**
     * Gets the value of the called card object
     * @return cardValue
     */
    public Integer getCardValue() {
        return cardValue;
    }

    /**
     * Sets the value of the card object
     * @param cardValue
     */
    public void setCardValue(int cardValue) {
        this.cardValue = cardValue;
    }



}

