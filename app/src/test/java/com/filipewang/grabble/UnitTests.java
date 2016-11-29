package com.filipewang.grabble;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by Filipe on 29-Nov-16.
 */

public class UnitTests {

    // Testing different words in the dictionary against their calculated values
    @Test
    public void checkWordValue_AARONIC() throws Exception{assertEquals(WordScreen.getValue("AARONIC"),42);}

    @Test
    public void checkWordValue_ABILITY() throws Exception{assertEquals(WordScreen.getValue("ABILITY"),62);}

    @Test
    public void checkWordValue_BOLTAGE() throws Exception{assertEquals(WordScreen.getValue("BOLTAGE"),59);}

    @Test
    public void checkWordValue_FRANCES() throws Exception{assertEquals(WordScreen.getValue("FRANCES"),53);}

    @Test
    public void checkWordValue_PAEONIC() throws Exception{assertEquals(WordScreen.getValue("PAEONIC"),51);}

    @Test
    public void checkWordValue_SERPENT() throws Exception{assertEquals(WordScreen.getValue("SERPENT"),44);}

    /**
     * Checking if a word is valid or not using different inputs.
     * Case 1: Valid word upper case, return true
     * Case 2: Word longer than 7 characters, return false
     * Case 3: Numbers, return false
     * Case 4: Valid word lower case, return true
     * Case 5: Mix of letters and numbers, return false
     * Case 6: Same as case 1 but lower case, return true
     */

    @Test
    public void checkWordValid_AARONIC() throws Exception{
        ArrayList<String> dict = getDict();
        assertTrue(WordScreen.checkWordValidity("AARONIC",dict));
    }

    @Test
    public void checkWordValid_AARONICA() throws Exception{
        ArrayList<String> dict = getDict();
        assertFalse(WordScreen.checkWordValidity("AARONICA",dict));
    }

    @Test
    public void checkWordValid_11() throws Exception{
        ArrayList<String> dict = getDict();
        assertFalse(WordScreen.checkWordValidity("11",dict));
    }

    @Test
    public void checkWordValid_tarwood() throws Exception{
        ArrayList<String> dict = getDict();
        assertTrue(WordScreen.checkWordValidity("tarwood",dict));
    }

    @Test
    public void checkWordValid_EE1WSS2() throws Exception{
        ArrayList<String> dict = getDict();
        assertFalse(WordScreen.checkWordValidity("EE1WSS2",dict));
    }

    @Test
    public void checkWordValid_aaronic() throws Exception{
        ArrayList<String> dict = getDict();
        assertTrue(WordScreen.checkWordValidity("aaronic",dict));
    }

    private ArrayList<String> getDict() throws Exception{
        ArrayList<String> dictionary = new ArrayList<>();
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("dictionary.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String word;
        while((word = br.readLine()) != null ){
            String wordFinal = word.toLowerCase();
            dictionary.add(wordFinal);
        }
        return dictionary;
    }
}
