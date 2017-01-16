package com.filipewang.grabble;

import android.os.Environment;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * This class is used to deal with Files in storage. It is also used to parse KML files.
 */
public class FileManager {

    // Set up necessary fields
    private String root = Environment.getExternalStorageDirectory().toString();
    private String currDay;
    private String TAG = "FileManager";
    private CalendarManager cm;
    private ArrayList<MarkerData> markerList;
    private int [] letterCount;
    private boolean [] achievements;

    // Constructor that sets the current day used as IDs for certain files.
    public FileManager(){
        cm = new CalendarManager();
        currDay = cm.getCurrentDay();
    }

    // Setters
    public void setMarkerList(ArrayList<MarkerData> temp){ markerList = temp; }
    public void setLetterCount(int [] temp){ letterCount = temp; }
    public void setAchievements(boolean [] temp) { achievements = temp; }

    // Method to store a marker list (a marker list has been set before this method is called).
    public void storeMarkerList(){
        try{
            File del = new File(root + "/" + currDay + ".tmp");
            del.delete();
            FileOutputStream fos = new FileOutputStream(root + "/" + currDay + ".tmp");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(markerList);
            oos.close();
            fos.close();
        } catch(Exception e){
            Log.d(TAG, "File creation error!");
        }
    }

    // Method to retrieve a marker list from storage.
    public ArrayList<MarkerData> retrieveMarkerList(){
        try{
            FileInputStream fis = new FileInputStream(root + "/" + currDay + ".tmp");
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<MarkerData> markers = (ArrayList<MarkerData>) ois.readObject();
            ois.close();
            fis.close();
            return markers;
        } catch(Exception e){
            Log.d(TAG, "File retrieval error!");
            return null;
        }
    }

    // Method to retrieve a marker list from storage.
    public int [] retrieveLetters(){
        try{
            FileInputStream fis = new FileInputStream(root + "/data.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            int [] letters = (int []) ois.readObject();
            ois.close();
            fis.close();
            return letters;
        } catch(Exception e){
            Log.d(TAG, "File retrieval error!");
            return null;
        }
    }

    // Method to store the inventory (the inventory has been set before this method is called).
    public void storeLetters(){
        try{
            File del = new File(root + "/data.dat");
            del.delete();
            FileOutputStream fos = new FileOutputStream(root + "/data.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(letterCount);
            oos.close();
            fos.close();
        } catch(Exception e){
            Log.d(TAG, "File creation error!");
        }
    }

    // Method to retrieve the achievements from storage.
    public boolean [] retrieveAchievements(){
        try{
            FileInputStream fis = new FileInputStream(root + "/achievements.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            boolean [] achievementsFile = (boolean []) ois.readObject();
            ois.close();
            fis.close();
            return achievementsFile;
        } catch(Exception e){
            Log.d(TAG, "File retrieval error!");
            return null;
        }
    }

    // Method to store the achievements (the achievements has been set before this method is called).
    public void storeAchievements(){
        try{
            File del = new File(root + "/achievements.dat");
            del.delete();
            FileOutputStream fos = new FileOutputStream(root + "/achievements.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(achievements);
            oos.close();
            fos.close();
        } catch(Exception e){
            Log.d(TAG, "File creation error!");
        }
    }

    // Method to parse a KML file, we work on it as if it was an XML file
    public ArrayList<MarkerData> parseKmlFile(String fileName){
        File KMLFile = new File(root + "/" + fileName);
        ArrayList<MarkerData> markerList = new ArrayList<>();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(KMLFile);
            doc.getDocumentElement().normalize();

            // Each point are separated by Placemark nodes
            NodeList list = doc.getElementsByTagName("Placemark");

            for(int i = 0; i < list.getLength(); i++){
                String name = "";
                String coordinates = "";
                String letter = "";
                Node curr = list.item(i);
                if (curr.getNodeType() == Node.ELEMENT_NODE) {

                    // Grab information from each of the sub nodes
                    Element e = (Element) curr;
                    name = e.getElementsByTagName("name").item(0).getTextContent();
                    letter = e.getElementsByTagName("description").item(0).getTextContent();
                    // Point has a sub node with coordinates so get that too
                    NodeList list2 = e.getElementsByTagName("Point");
                    Node curr2 = list2.item(0);
                    if (curr2.getNodeType() == Node.ELEMENT_NODE){
                        Element e2 = (Element) curr2;
                        coordinates = e2.getElementsByTagName("coordinates").item(0).getTextContent();
                    }
                }

                // Create an object MarkerData with all the information acquired and add it to the list
                MarkerData temp = new MarkerData(name,letter,coordinates);
                markerList.add(temp);
            }
        } catch (Exception e) {
            Log.d(TAG, "Error parsing KML");
        }
        // Return the list even if it's empty
        return markerList;
    }

    // Turn the inventory into a string so it's readable to the user
    public String getLetterCount() {
        String text = "";
        for (int i = 1; i < 27; i++) {
            if (i > 1 && (i - 1) % 5 == 0)
                text = text + "\n\n";
            text = text + Character.toString((char) (i + 64)) + ": " + letterCount[i] + "   ";
        }
        text = text + "\n\n\nTotal letters collected: " + letterCount[0];
        Log.d(TAG,text);
        return text;
    }

    // Reset marker data in case of an error
    public void resetMarkers(){
        File del = new File(root + "/data.dat");
        del.delete();
    }
}
