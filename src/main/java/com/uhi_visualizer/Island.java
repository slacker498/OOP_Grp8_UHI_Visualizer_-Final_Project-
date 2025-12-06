package com.uhi_visualizer;

import java.util.ArrayList;

public class Island extends Zone{
    private ArrayList<CityZone> cityZones;
    
    public Island() {
        setName("Undefined name");
        setCityZones(new ArrayList<>());
    }

    public Island(String name, ArrayList<CityZone> cityZones) {
        setName(name);
        setCityZones(cityZones);
    }

    public ArrayList<CityZone> getCityZones() {
        return this.cityZones;
    }

    public void setCityZones(ArrayList<CityZone> zones){
        this.cityZones = zones;
    }

}
