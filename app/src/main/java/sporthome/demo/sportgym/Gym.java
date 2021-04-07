/*
 * Copyright (C) 2021 Baidu, Inc. All Rights Reserved.
 */
package sporthome.demo.sportgym;

import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;

public class Gym {
    private String name;
    private double lat;
    private double lng;
    private String locatDisc;
    private String routeDisc;
    private int id;
    private int mon;
    private int tue;
    private int wed;
    private int tus;
    private int fri;
    private int sat;
    private int sun;
    private int startHour;
    private int startMin;
    private int endHour;
    private int endMin;
    private String tag;
    private String provence;
    private String city;
    private String district;
    ArrayList<Integer> a = new ArrayList<Integer>();

    public Gym() {
        super();
    }

    public Gym(String name, double lat, double lng, String locatDisc, String routeDisc, int id, int mon, int tue, int wed, int tus, int fri, int sat, int sun, int startHour, int startMin, int endHour, int endMin, String tag, String provence, String city, String district) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.locatDisc = locatDisc;
        this.routeDisc = routeDisc;
        this.id = id;
        this.mon = mon;
        this.tue = tue;
        this.wed = wed;
        this.tus = tus;
        this.fri = fri;
        this.sat = sat;
        this.sun = sun;
        this.startHour = startHour;
        this.startMin = startMin;
        this.endHour = endHour;
        this.endMin = endMin;
        this.tag = tag;
        this.provence = provence;
        this.city = city;
        this.district = district;
    }

    public Gym(String name, LatLng ll, ArrayList<Integer> a) {
        this.name = name;
        this.lat = ll.latitude;
        this.lng = ll.longitude;
        this.a = a;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getFri() {
        return fri;
    }

    public int getMon() {
        return mon;
    }

    public int getSat() {
        return sat;
    }

    public int getSun() {
        return sun;
    }

    public int getTue() {
        return tue;
    }

    public int getTus() {
        return tus;
    }

    public int getWed() {
        return wed;
    }

    public void setFri(int fri) {
        this.fri = fri;
    }

    public void setMon(int mon) {
        this.mon = mon;
    }

    public void setSat(int sat) {
        this.sat = sat;
    }

    public void setSun(int sun) {
        this.sun = sun;
    }

    public void setTue(int tue) {
        this.tue = tue;
    }

    public void setTus(int tus) {
        this.tus = tus;
    }

    public void setWed(int wed) {
        this.wed = wed;
    }

    public String getProvence() {
        return provence;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setProvence(String provence) {
        this.provence = provence;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getName() {
        return name;
    }

    public String getLocatdisc() {
        return locatDisc;
    }

    public String getRoutedisc() {
        return routeDisc;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public int getId() {
        return id;
    }

    public int getEndhour() {
        return endHour;
    }

    public int getStarthour() {
        return startHour;
    }

    public int getEndmin () {
        return endMin;
    }

    public int getStartmin() {
        return startMin;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEndhour(int endHour) {
        this.endHour = endHour;
    }

    public void setEndmin(int endMin) {
        this.endMin = endMin;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setLocatdisc(String locatDisc) {
        this.locatDisc = locatDisc;
    }

    public void setRoutedisc(String routeDisc) {
        this.routeDisc = routeDisc;
    }

    public void setStarthour(int startHour) {
        this.startHour = startHour;
    }

    public void setStartmin(int startMin) {
        this.startMin = startMin;
    }

    public String toString() {
        return id+":"+name+"("+lat+","+lng+")"+locatDisc+routeDisc+mon+tue+wed+tus+fri+sat+sun+"Time"+startHour+":"+startMin+"~"+endHour+":"+endMin+provence+city+district;
    }

    public ArrayList<Integer> getTypes() {
        return a;
    }

    public LatLng getLatLang() {
        return new LatLng(lat, lng);
    }

}
