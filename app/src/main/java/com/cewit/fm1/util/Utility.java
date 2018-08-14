package com.cewit.fm1.util;

import android.app.Activity;

import com.cewit.fm1.models.Transport;
import com.cewit.fm1.models.Travel;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Taeyu Im on 18. 7. 18.
 * qvo@cs.stonybrook.edu
 */

public class Utility {

    public static String loadJSONFromAsset(Activity activity, String filename) {
        String json = null;
        try {
            InputStream is = activity.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static String formatCost(int cost) {
        String strCost = cost + "";
        strCost = strCost.substring(0, strCost.length() - 3) + "," +
                strCost.substring(strCost.length() - 3);
        return strCost + "원";
    }

    public static String formatDistance(int distance) {
        return ((float) distance) / 1000 + "Km";
    }

    public static String formatTime(int time) {
        return time + "분";
    }

    public static Timestamp toTimestamp(String strTime) {
        final Timestamp timestamp =
                Timestamp.valueOf(
                        new SimpleDateFormat("yyyy-MM-dd ")
                                .format(new Date()) // get the current date as String
                                .concat(strTime)        // and append the time
                );
        System.out.println(timestamp);
        return timestamp;
    }

    public static Travel getTravel(List<Travel> travels, String fromId, String toId) {
        if (travels != null && travels.size() > 0) {
            for (Travel travel : travels) {
                if (travel.getFrom().equals(fromId) && travel.getTo().equals(toId)) {
                    return travel;
                }
            }
        }
        return null;
    }

    public static Transport getTransport(List<Travel> travels, String fromId, String toId, int preferTransport) {
        if (travels != null && travels.size() > 0) {
            for (Travel travel : travels) {
                if (travel.getFrom().equals(fromId) && travel.getTo().equals(toId)) {
                    List<Transport> transports = new ArrayList<Transport>(travel.getTransports().values());
                    Transport transport;
                    switch (preferTransport) {
                        case 1: //car
                            for (Transport t: transports) {
                                if (t.getType().equals("Car")) return t;
                            }
                            break;
                        case 2: //bus
                            for (Transport t: transports) {
                                if (t.getType().equals("Bus")) return t;
                            }
                            break;
                            default: // both
                                return transports.get(0);
                    }
                    return null;
                }
            }
        }
        return null;
    }

    public static String computeTime(String strCurTime, int transportTime) {
        int curHour = Integer.parseInt(strCurTime.substring(0,strCurTime.indexOf(":")));
        int curMin = Integer.parseInt(strCurTime.substring(strCurTime.indexOf(":") + 1));
        int newMin = curMin + transportTime;
        int newHour = curHour + newMin/60;
        newMin = newMin%60;
        if(newMin < 10) return newHour + ":0" + newMin;
        else return newHour + ":" + newMin;
    }
}
