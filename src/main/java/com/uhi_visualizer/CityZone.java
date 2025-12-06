package com.uhi_visualizer;

import java.util.ArrayList;

public class CityZone extends Zone {
    private ArrayList<DataPoint> dataPoints;

    public CityZone(String name) {
        this.setName(name);
        this.setDataPoints(new ArrayList<>());
    }

    public CityZone(String name, ArrayList<DataPoint> dataPoints) {
        this.setName(name);
        this.setDataPoints(dataPoints);
    }

    public ArrayList<DataPoint> getDataPoints(){
        return this.dataPoints;
    }

    public void setDataPoints(ArrayList<DataPoint> dt_array){
        this.dataPoints = dt_array;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof CityZone)) return false;

        CityZone other = (CityZone) o;

        boolean nameFlag = this.getName().equals(other.getName());
        boolean dpFlag = this.getDataPoints().equals(other.getDataPoints());

        return nameFlag && dpFlag;
    }

}
