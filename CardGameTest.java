import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.junit.After;
import org.junit.AfterClass;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

public class CardGameTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void testSetUp() throws IOException {

        //Makes defaultPack, pack.txt
        File defaultPack = new File("pack.txt");
        if (defaultPack.exists() && !defaultPack.isDirectory()) {
            FileWriter defaultWrite = new FileWriter(defaultPack);
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
        } else {
            defaultPack.createNewFile();
            FileWriter defaultWrite = new FileWriter(defaultPack);
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
        
        //Makes testPack1
        File testPack1 = new File("testPack1.txt");
        int valid = 2*8; //16
        if (testPack1.exists() && !testPack1.isDirectory()) {
            FileWriter defaultWrite = new FileWriter(testPack1);
            for (int i = 1; i < (valid/2)+1; i++) {
                if (i==valid) { 
                    defaultWrite.write("" + i);
                    defaultWrite.flush();
                } else {
                    defaultWrite.write("" + i + "\n");
                    defaultWrite.flush();
                }
            }
            for (int i = (valid/2)+1; i < valid+1; i++) {
                if (i==valid) { 
                    defaultWrite.write("" + i);
                    defaultWrite.flush();
                } else {
                    defaultWrite.write("" + i + "\n");
                    defaultWrite.flush();
                }
            }
        } else {
            defaultPack.createNewFile();
            FileWriter defaultWrite = new FileWriter(testPack1);
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

    @Test
    public void setPlayerCountTest() throws IOException, CardGame.InvalidPackSizeException, CardGame.NegativeCardException {
        String filename = "pack.txt";
        var testGame = new CardGame(4, filename);
        testGame.setPlayerCount(4);
        Integer testingPlayerCount = testGame.getPlayerCount();
        assertSame(4, testingPlayerCount);
    }

    @Test
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
    public void drawCardTest() throws IOException, CardGame.InvalidPackSizeException, CardGame.NegativeCardException {
        String filename = "pack.txt";
        var testGame = new CardGame(4, filename);

        List<CardGame.Player> players = testGame.getPlayers();
        players.get(0).drawCard();
        int finalCard = players.get(0).getPlayerCards().get(players.get(0).getPlayerCards().size()-1);
        assertSame(17, finalCard);
    }

    @Test
    public void discardCardTest() throws IOException, CardGame.InvalidPackSizeException, CardGame.NegativeCardException {
        String testFileName = "testPack1.txt";
        var testGame = new CardGame(4, testFileName);

        



    }

    // checks the round-robin algorithim
    @Test
    public void dealTest() throws CardGame.NegativeCardException, CardGame.InvalidPackSizeException, IOException {
    // testing the round-robin output for player 1
    CardGame testD = new CardGame(4,"pack.txt");
    int[] PcardsInArray = new int[4];
    int[] DcardsInArray = new int[4];

    for (int i = 0; i < 4; i++) {
      int card=  testD.getPlayerHand(0).get(i);
      PcardsInArray[i]=card;
    }
    assertArrayEquals(new int[]{1,5,9,13},PcardsInArray);


    int[] DeckCards = new int[4];
    for (int i = 0; i < 4; i++) {
        int card=  testD.getDeckHand(0).get(i);
        DcardsInArray[i]=card;
    }
    assertArrayEquals(new int[]{17, 21, 25, 29},DcardsInArray);

    }
}