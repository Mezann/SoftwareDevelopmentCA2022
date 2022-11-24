import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;  // Import the File class

public class CardGameTest {

    @BeforeClass
    public static void testSetUp() throws IOException {

        //Makes defaultPack, pack.txt
        File defaultPack = new File("pack.txt");
        if (defaultPack.exists() && !defaultPack.isDirectory()) {
            try (FileWriter defaultWrite = new FileWriter(defaultPack)) {
                int valid = 4*8;
                for (int i = 1; i < valid+1; i++) {
                    if (i==valid) { 
                        defaultWrite.write("" + i);
                        defaultWrite.flush();
                    } else {
                        defaultWrite.write("" + i + "\n");
                        defaultWrite.flush();
                    }
                }
            }
        } else {
            defaultPack.createNewFile();
            try (FileWriter defaultWrite = new FileWriter(defaultPack)) {
                int valid = 4*8;
                for (int i = 1; i < valid+1; i++) {
                    if (i==valid) { 
                        defaultWrite.write("" + i);
                        defaultWrite.flush();
                    } else {
                        defaultWrite.write("" + i + "\n");
                        defaultWrite.flush();
                    }
                }
            }
        }
        
        //Makes testPack1
        File testPack1 = new File("testPack1.txt");
        if (testPack1.exists() && !testPack1.isDirectory()) {
            try (FileWriter testWrite = new FileWriter(testPack1)) {
                int valid = 4*8;
                for (int i = 1; i < valid+1; i++) {
                    if (i==valid) { 
                        testWrite.write("" + i);
                        testWrite.flush();
                    } if(i == 5 || i ==  9) {
                        testWrite.write("1" + "\n");
                        testWrite.flush();
                    } else {
                        testWrite.write("" + i + "\n");
                        testWrite.flush();
                    }
                }
            }
        } else {
            testPack1.createNewFile();
            try (FileWriter testWrite = new FileWriter(defaultPack)) {
                int valid = 4*8;
                for (int i = 1; i < valid+1; i++) {
                    if (i==valid) { 
                        testWrite.write("" + i);
                        testWrite.flush();
                    } else {
                        testWrite.write("" + i + "\n");
                        testWrite.flush();
                    }
                }
            }
        }
    }

    @Test
    //Checks if packGenerator creates an integer list as desired
    public void packGeneratorTest() throws IOException, CardGame.InvalidPackSizeException, CardGame.NegativeCardException {
        String filename = "pack.txt";
        var testGame = new CardGame(4, filename);

        ArrayList<Integer>testArrayList = new ArrayList<>(testGame.packGenerator(filename));
        ArrayList<Integer>expectedArrayList = new ArrayList<Integer>();

        for (int value = 1; value < 32+1; value++) {
            expectedArrayList.add(value);
        }

        for (int i = 0; i < testArrayList.size(); i++) {
            assertSame(expectedArrayList.get(i), testArrayList.get(i));
        }

    }

    @Test
    //Checks if the draw card method works as intended
    public void drawCardTest() throws IOException, CardGame.InvalidPackSizeException, CardGame.NegativeCardException {
        String filename = "pack.txt";
        var testGame = new CardGame(4, filename);

        List<CardGame.Player> players = testGame.getPlayers();
        List<CardDeck> decks = testGame.getDecks();
        ArrayList<Integer> deckBefore = decks.get(0).getDeckCards();
        players.get(0).drawCard();
        ArrayList<Integer> deckAfter = decks.get(0).getDeckCards();
        int finalCard = players.get(0).getPlayerCards().get(players.get(0).getPlayerCards().size()-1);

        //Checks whether the card drawn is the first card of the relevant deck
        //Checks whether the deck loses a card
        //Checks whether the deck loses the correct card
        assertSame(17, finalCard);
        assertSame(deckAfter.size()+1, deckBefore.size());
        assertSame(21, deckAfter.get(0));
        assertSame(25, deckAfter.get(1));
        assertSame(29, deckAfter.get(2));
    }

    @Test
    //Checks whether the discard card method works as intended
    public void discardCardTest() throws IOException, CardGame.InvalidPackSizeException, CardGame.NegativeCardException {
        String testFileName = "testPack1.txt";
        var testGame = new CardGame(4, testFileName);

        List<CardGame.Player> players = testGame.getPlayers();
        List<CardDeck> decks = testGame.getDecks();
        ArrayList<Integer> deckHandBefore = decks.get(1).getDeckCards();
        ArrayList<Integer> playerHandBefore = testGame.getPlayerHand(0);
        players.get(0).discardCard();
        ArrayList<Integer> playerHandAfter = testGame.getPlayerHand(0);
        ArrayList<Integer> deckHandAfter = decks.get(1).getDeckCards();

        //Ensuring player actually discards a card
        //Ensuring whether the relevant deck gains a card
        //Checking if the discarded card is the same card that the deck gains
        assertSame(playerHandAfter.size() + 1, playerHandBefore.size());
        assertSame(deckHandBefore.size() + 1, deckHandAfter.size());
        assertSame(13, deckHandAfter.get(deckHandAfter.size()-1));
    }

    @Test
    //Checks the round-robin algorithim 
    public void dealTest() throws CardGame.NegativeCardException, CardGame.InvalidPackSizeException, IOException {
    // testing the round-robin output for player 1
    CardGame testD = new CardGame(4,"pack.txt");
    int[] PcardsInArray = new int[4];
    int[] DcardsInArray = new int[4];

    //Converts arrayList into list
    for (int i = 0; i < 4; i++) {
      int card=  testD.getPlayerHand(0).get(i);
      PcardsInArray[i]=card;
    }
    //Verifies if the correct cards are in the player's hand
    assertArrayEquals(new int[]{1,5,9,13},PcardsInArray);

    //Converts arrayList into list
    for (int i = 0; i < 4; i++) {
        int card=  testD.getDeckHand(0).get(i);
        DcardsInArray[i]=card;
    }
    //Verifies if the correct cards are in the deck's hand
    assertArrayEquals(new int[]{17, 21, 25, 29},DcardsInArray);
    }
}