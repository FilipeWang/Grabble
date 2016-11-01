package com.filipewang.grabble;

import com.google.android.gms.maps.model.LatLng;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Filipe on 01-Nov-16.
 */
public class MarkerData {
    String name;
    String letter;
    LatLng coordinates;
    String sCoordinates;

    public MarkerData(String n, String l, String s){
        this.name = n;
        this.letter = l;
        this.sCoordinates = s;
        convertCoordinates(s);
    }

    public void convertCoordinates(String s){
        String patternString = 	"(-?\\d+(\\.)\\d+)(,)(-?\\d+(\\.)\\d+)(,)(\\d)";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(s);
        if(matcher.find()){
            double lon = Double.parseDouble(matcher.group(1));
            double lat = Double.parseDouble(matcher.group(4));
            coordinates = new LatLng(lat,lon);
        }
    }

}
