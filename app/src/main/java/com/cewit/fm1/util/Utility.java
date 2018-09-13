package com.cewit.fm1.util;

import android.app.Activity;

import com.cewit.fm1.models.Transport;
import com.cewit.fm1.models.Travel;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
        DecimalFormat df = new DecimalFormat("#.0");
        double newDistance = ((double) distance) / 1000;
        String strDistance = df.format(newDistance);

        if (newDistance < 1) {
            strDistance = "0" + strDistance;
        }


        return strDistance + "Km";
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
                                if (t.getType().contains("Car")) return t;
                            }
                            break;
                        case 2: //bus
                            for (Transport t: transports) {
                                if (t.getType().contains("Bus")) return t;
                            }
                            break;
                            default: // both
                                Random r = new Random();
                                int rand =  r.nextInt();
                                int flag = rand%2;
                                if (flag == 0) {
                                    transports.get(0);
                                } else {
                                    transports.get(1);
                                }
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

        int newMin = curHour*60 + curMin + transportTime;
        int newHour = newMin/60;
        newMin = newMin%60;
        if(newMin < 10) return newHour + ":0" + newMin;
        else return newHour + ":" + newMin;
    }

    public static int computeTimeDiffer(String newDepartureTime, String previousArrivalTime) {
        int newHour = Integer.parseInt(newDepartureTime.substring(0,newDepartureTime.indexOf(":")));
        int newMin = Integer.parseInt(newDepartureTime.substring(newDepartureTime.indexOf(":") + 1));

        int prevHour = Integer.parseInt(previousArrivalTime.substring(0,previousArrivalTime.indexOf(":")));
        int prevMin = Integer.parseInt(previousArrivalTime.substring(previousArrivalTime.indexOf(":") + 1));

        return (newHour - prevHour) * 60 + (newMin - prevMin);
    }

    public static boolean isValidTimeChanged(String preArrivalTime, String newDepartureTime) {
        int newHour = Integer.parseInt(newDepartureTime.substring(0,newDepartureTime.indexOf(":")));
        int newMin = Integer.parseInt(newDepartureTime.substring(newDepartureTime.indexOf(":") + 1));

        int prevHour = Integer.parseInt(preArrivalTime.substring(0,preArrivalTime.indexOf(":")));
        int prevMin = Integer.parseInt(preArrivalTime.substring(preArrivalTime.indexOf(":") + 1));

        return (newHour * 60 + newMin) > (prevHour*60+prevMin);

    }
}
