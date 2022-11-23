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
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

public class CardGameTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void testSetUp() {
        File defaultPack = new File("defaultPack.txt");
        try {
            //Checks if file exists
            if (defaultPack.exists() && !defaultPack.isDirectory()) {
                this.fileWriter = new FileWriter(defaultPack); 
            } else {
                file.createNewFile();
                this.fileWriter = new FileWriter(defaultPack);
            }
        } catch (IOException e) {
            System.out.println("File creation error, player " + playerNumber);
            e.printStackTrace();
        }
    }

    @Test
    public void fakeTest() {
        assertEquals(4, 4);
    }

    @Test(expected = FileNotFoundException.class)
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

        String testFileName = "testPack1.txt";
        ArrayList<Integer>testArrayList = new ArrayList<>(testGame.packGenerator(testFileName));
        ArrayList<Integer>expectedArrayList = new ArrayList<Integer>();
        for (int value = 1; value < 6; value++) {
            expectedArrayList.add(value);
        }
        assertSame(expectedArrayList, testArrayList);
    }
    

}