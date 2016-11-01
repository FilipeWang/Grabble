package com.filipewang.grabble;

import android.os.Environment;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Filipe on 01-Nov-16.
 */

public class KMLParser {

    File KMLFile;

    public KMLParser(String fileName){
        String root = Environment.getExternalStorageDirectory().toString();
        KMLFile = new File(root + "/" + fileName);
    }
    public ArrayList<MarkerData> parseFile(){
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
            e.printStackTrace();
        }
        return markerList;
    }

}
