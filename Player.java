import java.util.ArrayList;

public class Player {

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
        public double checkHand(ArrayList<Integer> s) {
                Integer n = (Integer) s.get(0);
                double sum = 0;
                for (int Number : s) {
                        sum = Number + sum;
                }
                if (sum / 4 == n) {
                        this.matchHand = true;
                }
                return sum / 4;
       }
}