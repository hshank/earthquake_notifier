package com.example.harishshanker.yourfault;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by harishshanker on 10/9/15.
 */
public class Earthquake {
    public String mag;
    public String location;
    public double longi;
    public double lati;
    public double distance = 0;
    public String message = "";
    public String mapMessage = "";

    public Earthquake(String output){
        this.message = output;
        this.mag = output.split(":::")[0];
        this.location = output.split(":::")[1];
        this.longi = Double.parseDouble(output.split(":::")[2]);
        this.lati = Double.parseDouble(output.split(":::")[3]);
        this.distance = Double.parseDouble(output.split(":::")[4]);
        NumberFormat formatter = new DecimalFormat("#0.00");
        this.mapMessage = "\n    " + location + "   \n     Magnitude: " + mag + "\n     Distance: " + formatter.format(distance)+" km    \n";
    }

    public String toString(){
        NumberFormat formatter = new DecimalFormat("#0.00");
        String retVal = formatter.format(Double.parseDouble(mag)) + "M       " + location.toUpperCase();
        if (retVal.length() < 40)
            return retVal;
        return retVal.substring(0,36) +" ...";
    }
}
