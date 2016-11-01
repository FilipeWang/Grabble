package com.filipewang.grabble;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Filipe on 01-Nov-16.
 */
public class MarkerData implements Serializable{
    String name;
    String letter;
    double coordinateLon;
    double coordinateLat;
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
            coordinateLat = Double.parseDouble(matcher.group(1));
            coordinateLon = Double.parseDouble(matcher.group(4));
        }
    }

    public LatLng getCoordinates(){
        LatLng coordinates = new LatLng(coordinateLon,coordinateLat);
        return coordinates;
    }

}
