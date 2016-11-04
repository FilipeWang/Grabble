package com.filipewang.grabble;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Filipe on 04-Nov-16.
 */
public class FileManager {
    private String root = Environment.getExternalStorageDirectory().toString();
    private String currDay;
    private String TAG = "FileManager";
    private CalendarManager cm;
    private ArrayList<MarkerData> markerList;

    public FileManager(){
        cm = new CalendarManager();
        currDay = cm.getCurrentDay();
    }

    public void setMarkerList(ArrayList<MarkerData> temp){
        markerList = temp;
    }
    public ArrayList<MarkerData> getMarkerList(){return markerList;}

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
}
