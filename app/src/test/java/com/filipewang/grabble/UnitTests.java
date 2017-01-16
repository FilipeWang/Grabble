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
 * This class contains unit tests for 3 of the required marking criteria:
 * - Calculate the correct value of a given word
 * - Check whether a word is valid or not
 * - Loading the right map for a given day
 *
 * Cases are in this format in the comments:
 * Case caseNum: Description, expected outcome
 */

public class UnitTests {

    /** Testing different words in the dictionary against their calculated values.
     *  We use words that cover the whole alphabet.
     *  We first test without bonus letters (double value) and then with them.
     *  The function getValue takes a string with capital letters and a character which is the bonus
     *  letter which has double the value.
     *
     *  Note: To test without bonuses we send as a bonus letter a zero.
     */

    // 3+3+8+4+6+5+13 = 42
    @Test
    public void checkWordValue_AARONIC() throws Exception{assertEquals(42,WordScreen.getValue("AARONIC",'0'));}

    // 3+20+5+11+5+2+6 = 52
    @Test
    public void checkWordValue_ABILITY() throws Exception{assertEquals(62,WordScreen.getValue("ABILITY",'0'));}

    // 20+4+11+2+3+18+1 = 59
    @Test
    public void checkWordValue_BOLTAGE() throws Exception{assertEquals(59,WordScreen.getValue("BOLTAGE",'0'));}

    // 15+8+3+6+13+1+7 = 53
    @Test
    public void checkWordValue_FRANCES() throws Exception{assertEquals(53,WordScreen.getValue("FRANCES",'0'));}

    // 19+3+1+4+6+5+13 = 51
    @Test
    public void checkWordValue_PAEONIC() throws Exception{assertEquals(51,WordScreen.getValue("PAEONIC",'0'));}

    // 7+1+8+19+1+6+2 = 44
    @Test
    public void checkWordValue_SERPENT() throws Exception{assertEquals(44,WordScreen.getValue("SERPENT",'0'));}

    // 18+4+10+9+1+3+10 = 55
    @Test
    public void checkWordValue_GODHEAD() throws Exception{assertEquals(55,WordScreen.getValue("GODHEAD",'0'));}

    // 9+12+21+1+11+16+22 = 92
    @Test
    public void checkWordValue_HUVELYK() throws Exception{assertEquals(92,WordScreen.getValue("HUVELYK",'0'));}

    // 26+4+4+2+3+23+16 = 78
    @Test
    public void checkWordValue_ZOOTAXY() throws Exception{assertEquals(78,WordScreen.getValue("ZOOTAXY",'0'));}

    // 17+5+13+9+2+25+1 = 72
    @Test
    public void checkWordValue_WICHTJE() throws Exception{assertEquals(72,WordScreen.getValue("WICHTJE",'0'));}

    // 24+12+3+1+10+3+14 = 67
    @Test
    public void checkWordValue_QUAEDAM() throws Exception{assertEquals(67,WordScreen.getValue("QUAEDAM",'0'));}

    /**
     * Now let's try some with bonus letters.
     * Case 1: No bonus letter matches (calculate normal value)
     * Case 2: Single bonus letter
     * Case 3: Multiple bonus letters
     */

    // 19+3+1+4+6+5+13 = 51
    @Test
    public void checkWordValue_BonusPAEONIC() throws Exception{assertEquals(51,WordScreen.getValue("PAEONIC",'U'));}

    // 17+5+13+9+2+25+1*2 = 73
    @Test
    public void checkWordValue_BonusWICHTJE() throws Exception{assertEquals(73,WordScreen.getValue("WICHTJE",'E'));}

    // 3*2+3*2+8+4+6+5+13 = 48
    @Test
    public void checkWordValue_BonusAARONIC() throws Exception{assertEquals(48,WordScreen.getValue("AARONIC",'A'));}

    // Test that all letters have appeared at least once
    @Test
    public void checkWordValue_ALL() throws Exception{
        String [] wordsTested = {"AARONIC","ABILITY","BOLTAGE","FRANCES","PAEONIC","SERPENT","GODHEAD",
                                "HUVELYK","ZOOTAXY","WICHTJE","QUAEDAM"};
        int [] letterCount = new int[26];

        // Calculate character frequency
        for(String word: wordsTested){
            for(int i = 0; i<word.length(); i++){
                char c = word.charAt(i);
                int numValue = (int) c;
                int indexLetter = numValue - 65;
                letterCount[indexLetter]++;
            }
        }

        // Check if a letter has not been used
        boolean flag = true;
        for(int j: letterCount){
            if(j == 0)
                flag = false;
        }
        assertTrue(flag);
    }

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

    /**
     * Tests with the inventory and word creation.
     * Case 1: Possible to create the word, return true
     * Case 2: One letter is missing in the inventory, return false
     * Case 3: Multiple letters missing, return false
     * Case 4: All letters missing, return false
     * Case 5: Word has 2 instances of 1 letter and inventory only has 1, return false
     * Case 6: Multiple instances of a letter and inventory has them, return true
     *
     * The inventory is setup that index 0 is a count of how many letters have been collected
     * and the rest are the counts for each letter present in the inventory.
     *
     * So an inventory, like the one below, has 1 A, 2 B, 3 C, ... , 26 Z.
     * int [] inventory = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26};
     */
    @Test
    public void inventory_AllPossible() throws Exception{
        int [] inventory = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26};
        String word = "WICHTJE";
        assertTrue(WordScreen.checkInventory(word,inventory));
    }

    @Test
    public void inventory_OneMissing() throws Exception{

        // This inventory has no A.
        int [] inventory = {0,0,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26};
        String word = "GODHEAD";
        assertFalse(WordScreen.checkInventory(word,inventory));
    }

    @Test
    public void inventory_SomeMissing() throws Exception{

        // This inventory has no A, E or G.
        int [] inventory = {0,0,2,3,4,0,6,7,8,0,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26};
        String word = "PAEONIC";
        assertFalse(WordScreen.checkInventory(word,inventory));
    }

    @Test
    public void inventory_AllMissing() throws Exception{

        // This inventory is empty.
        int [] inventory = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        String word = "ABILITY";
        assertFalse(WordScreen.checkInventory(word,inventory));
    }

    @Test
    public void inventory_MultipleMissing() throws Exception{

        // This inventory requires 2 A but only has 1.
        int [] inventory = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26};
        String word = "AARONIC";
        assertFalse(WordScreen.checkInventory(word,inventory));
    }

    @Test
    public void inventory_MultiplePossible() throws Exception{

        // This inventory has everything needed.
        int [] inventory = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26};
        String word = "MILLING";
        assertTrue(WordScreen.checkInventory(word,inventory));
    }

    /**
     * Test that the right week day is used for a given day (this ensures the right data)
     * is loaded for the map. Also one test to check if it gets the correct day.
     * Note the set method has the months starting at 0.
     */

    @Test
    public void calendarTest_Monday() throws Exception{
        String correctDate = "monday";
        CalendarManager cm = new CalendarManager();
        cm.cal.set(2016,11,5); //December 5th, 2016
        String testDate = cm.getDayOfWeek();
        assertEquals(correctDate,testDate);
    }

    @Test
    public void calendarTest_Tuesday() throws Exception{
        String correctDate = "tuesday";
        CalendarManager cm = new CalendarManager();
        cm.cal.set(2016,8,6); //Septermber 6th, 2016
        String testDate = cm.getDayOfWeek();
        assertEquals(correctDate,testDate);
    }

    @Test
    public void calendarTest_Wednesday() throws Exception{
        String correctDate = "wednesday";
        CalendarManager cm = new CalendarManager();
        cm.cal.set(2017,5,28); //June 28th, 2017
        String testDate = cm.getDayOfWeek();
        assertEquals(correctDate,testDate);
    }


    @Test
    public void calendarTest_Thursday() throws Exception{
        String correctDate = "thursday";
        CalendarManager cm = new CalendarManager();
        cm.cal.set(2017,5,29); //June 29th, 2017
        String testDate = cm.getDayOfWeek();
        assertEquals(correctDate,testDate);
    }

    @Test
    public void calendarTest_Friday() throws Exception{
        String correctDate = "friday";
        CalendarManager cm = new CalendarManager();
        cm.cal.set(2010,1,19); //February 19th, 2010
        String testDate = cm.getDayOfWeek();
        assertEquals(correctDate,testDate);
    }

    @Test
    public void calendarTest_Saturday() throws Exception{
        String correctDate = "saturday";
        CalendarManager cm = new CalendarManager();
        cm.cal.set(2022,9,1); //October 1st, 2022
        String testDate = cm.getDayOfWeek();
        assertEquals(correctDate,testDate);
    }

    @Test
    public void calendarTest_Sunday() throws Exception{
        String correctDate = "sunday";
        CalendarManager cm = new CalendarManager();
        cm.cal.set(2016,10,6); //November 6th, 2016
        String testDate = cm.getDayOfWeek();
        assertEquals(correctDate,testDate);
    }

    @Test
    public void calendarTest_Day() throws Exception{
        String correctDay = "03122016";
        CalendarManager cm = new CalendarManager();
        cm.cal.set(2016,11,3); //December 3rd, 2016
        String testDay = cm.getCurrentDay();
        assertEquals(correctDay,testDay);
    }
}
