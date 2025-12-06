package com.uhi_visualizer;

public class DataPoint extends Zone{
   private double tempValue;
   
   public DataPoint(String name, double tempValue) {
      setName(name);
      setTempValue(tempValue);
   }

   public double getTempValue(){
       return this.tempValue;
   }

   public void setTempValue(double tempValue){
       this.tempValue = tempValue;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof DataPoint)) return false;

        DataPoint other = (DataPoint) o;

        return (this.getName().strip()).equals(other.getName().strip()) && ((Double)this.tempValue).equals(other.getTempValue());
    }

}


