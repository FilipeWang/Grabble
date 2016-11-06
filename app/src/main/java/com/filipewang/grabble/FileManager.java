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
 * Created by Filipe on 04-Nov-16.
 */
public class FileManager {
    private String root = Environment.getExternalStorageDirectory().toString();
    private String currDay;
    private String TAG = "FileManager";
    private CalendarManager cm;
    private ArrayList<MarkerData> markerList;
    private int [] letterCount;
    private boolean [] achievements;

    public FileManager(){
        cm = new CalendarManager();
        currDay = cm.getCurrentDay();
    }

    public void setMarkerList(ArrayList<MarkerData> temp){ markerList = temp; }
    public void setLetterCount(int [] temp){ letterCount = temp; }
    public void setAchievements(boolean [] temp) { achievements = temp; }

    public void storeMarkerList(){
        try{
            File del = new File(root + "/" + currDay + ".tmp");
            del.delete();
            FileOutputStream fos = new FileOutputStream(root + "/" + currDay + ".tmp");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(markerList);
            oos.close();
        } catch(Exception e){
            Log.d(TAG, "File creation error!");
        }
    }

    public ArrayList<MarkerData> retrieveMarkerList(){
        try{
            FileInputStream fis = new FileInputStream(root + "/" + currDay + ".tmp");
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<MarkerData> markers = (ArrayList<MarkerData>) ois.readObject();
            ois.close();
            return markers;
        } catch(Exception e){
            Log.d(TAG, "File retrieval error!");
            ArrayList<MarkerData> markers = null;
            return markers;
        }
    }

    public int [] retrieveLetters(){
        try{
            FileInputStream fis = new FileInputStream(root + "/data.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            int [] letters = (int []) ois.readObject();
            ois.close();
            return letters;
        } catch(Exception e){
            Log.d(TAG, "File retrieval error!");
            int [] letters = null;
            return letters;
        }
    }

    public void storeLetters(){
        try{
            File del = new File(root + "/data.dat");
            del.delete();
            FileOutputStream fos = new FileOutputStream(root + "/data.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(letterCount);
            oos.close();
        } catch(Exception e){
            Log.d(TAG, "File creation error!");
        }
    }

    public boolean [] retrieveAchievements(){
        try{
            FileInputStream fis = new FileInputStream(root + "/achievements.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            boolean [] achievementsFile = (boolean []) ois.readObject();
            ois.close();
            return achievementsFile;
        } catch(Exception e){
            Log.d(TAG, "File retrieval error!");
            boolean [] achievementsFile = null;
            return achievementsFile;
        }
    }

    public void storeAchievements(){
        try{
            File del = new File(root + "/achievements.dat");
            del.delete();
            FileOutputStream fos = new FileOutputStream(root + "/achievements.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(achievements);
            oos.close();
        } catch(Exception e){
            Log.d(TAG, "File creation error!");
        }
    }

    public ArrayList<MarkerData> parseKmlFile(String fileName){
        File KMLFile = new File(root + "/" + fileName);
        ArrayList<MarkerData> markerList = new ArrayList<>();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(KMLFile);
            doc.getDocumentElement().normalize();
            NodeList list = doc.getElementsByTagName("Placemark");

            for(int i = 0; i < list.getLength(); i++){
                String name = "";
                String coordinates = "";
                String letter = "";
                Node curr = list.item(i);
                if (curr.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) curr;
                    name = e.getElementsByTagName("name").item(0).getTextContent();
                    letter = e.getElementsByTagName("description").item(0).getTextContent();
                    NodeList list2 = e.getElementsByTagName("Point");
                    Node curr2 = list2.item(0);
                    if (curr2.getNodeType() == Node.ELEMENT_NODE){
                        Element e2 = (Element) curr2;
                        coordinates = e2.getElementsByTagName("coordinates").item(0).getTextContent();
                    }
                }

                MarkerData temp = new MarkerData(name,letter,coordinates);
                markerList.add(temp);
            }
        } catch (Exception e) {
            Log.d(TAG, "Error parsing KML");
        }
        return markerList;
    }

    public String getLetterCount() {
        String text = "";
        for (int i = 1; i < 27; i++) {
            if (i > 1 && (i - 1) % 5 == 0)
                text = text + "\n\n";
            text = text + Character.toString((char) (i + 64)) + ":  " + letterCount[i] + "     ";
        }
        text = text + "\n\n\nTotal letters collected: " + letterCount[0];
        Log.d(TAG,text);
        return text;
    }

    public void resetMarkers(){
        File del = new File(root + "/data.dat");
        del.delete();
    }
}
