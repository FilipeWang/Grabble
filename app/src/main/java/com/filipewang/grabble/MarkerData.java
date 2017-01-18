package com.filipewang.grabble;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is the data structure used to house the data for each marker.
 */
public class MarkerData implements Serializable{
    String name; // Name of point
    String letter; // Letter that the point contains
    double coordinateLon; // Longitude
    double coordinateLat; // Latitude
    String sCoordinates; // Raw string of coordinates from file

    public MarkerData(String n, String l, String s){
        this.name = n;
        this.letter = l;
        this.sCoordinates = s;
        convertCoordinates(s); // Parse the string into the correct coordinates
    }

    // This method parses strings like
    // −3.1862219117766553,55.94453310098754,0
    // into two distinct doubles containing each coordinate.
    public void convertCoordinates(String s){
        String patternString = 	"(-?\\d+(\\.)\\d+)(,)(-?\\d+(\\.)\\d+)(,)(\\d)";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(s);
        if(matcher.find()){
            coordinateLat = Double.parseDouble(matcher.group(1));
            coordinateLon = Double.parseDouble(matcher.group(4));
        }
    }

    // Returns a LatLng object with the coordinates of the current instance of the object
    public LatLng getCoordinates(){
        LatLng coordinates = new LatLng(coordinateLon,coordinateLat);
        return coordinates;
    }

}
