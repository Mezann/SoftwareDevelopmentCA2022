import java.util.ArrayList;
import javax.management.monitor.GaugeMonitor;

public class Player {

        // attributes


        public Boolean matchHand = false;
        public Integer playerNumber;
        public ArrayList<Integer> hand;
        // constructor

        public Player(Integer playerNumber) {
                this.playerNumber = playerNumber;

        }


        // methods
        public Integer getPlayerNumber() {
                return playerNumber;
        }

        public ArrayList getHand() {
                return hand;
        }


        public Boolean getMatchHand() {

                return matchHand;

        }


        public void setPlayerHand() {


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