package com.uhi_visualizer;

import java.security.InvalidParameterException;
import java.util.*;

// This is a utility class that provides static methods to extract summarized descriptive and inferential statistics from Zone objects
public abstract class StatisticsCalculator {
    public static final double HIGH_TEMP = 35; // in degree Celsius
    public static final double LOW_TEMP = 20; // in degree Celsius

    public static Object[] getIslandMaxTemperature(Island island) {
        if (island.getCityZones().isEmpty()) return new Object[] {"Unknown", "Unknown", 0};
        else {
            String czName = "Not set";
            double maxTemp = Double.MIN_VALUE;
            String dpName = "Not set";
            for (CityZone cz : island.getCityZones()) {
                for (DataPoint dp : cz.getDataPoints()) {
                    if (dp.getTempValue() > maxTemp) {
                        maxTemp = dp.getTempValue();
                        czName = cz.getName();
                        dpName = dp.getName();
                    }
                }
            }
            return new Object[] {czName, dpName, maxTemp};
        }
    }

    public static Object[] getIslandMinTemperature(Island island) {
        if (island.getCityZones().isEmpty()) return new Object[] {"Unknown", "Unknown", 0};
        else {
            String czName = "Not set";
            double minTemp = Double.MAX_VALUE;
            String dpName = "Not set";
            for (CityZone cz : island.getCityZones()) {
                for (DataPoint dp : cz.getDataPoints()) {
                    if (dp.getTempValue() < minTemp) {
                        minTemp = dp.getTempValue();
                        czName = cz.getName();
                        dpName = dp.getName();
                    }
                }
            }
            return new Object[] {czName, dpName, minTemp};
        }
    }

    public static double getSumOfTemperatures(Zone zone) {
        if (zone instanceof DataPoint) {
            return ((DataPoint) zone).getTempValue();
        }
        else if (zone instanceof CityZone) {
            double total = 0;
            for (DataPoint dp: ((CityZone) zone).getDataPoints()) {
                total += dp.getTempValue();
            }
            return total;

        }
        else if (zone instanceof Island) {
            double total = 0;
            for (CityZone cz : ((Island) zone).getCityZones()) {
                for (DataPoint dp: cz.getDataPoints()) {
                    total += dp.getTempValue();
                }
            }
            return total;

        } else {
            throw new InvalidParameterException("Argument is not of type Zone");
        }
    }


    public static double getMeanTemperature(Zone zone) throws InvalidParameterException {
        if (zone instanceof DataPoint) {
            return ((DataPoint) zone).getTempValue();
        }
        else if (zone instanceof CityZone) {
            double total = StatisticsCalculator.getSumOfTemperatures(zone);
            int count = ((CityZone) zone).getDataPoints().size();

            if (count == 0) return 0;
            else return total / count;

        }
        else if (zone instanceof Island) {
            double total = StatisticsCalculator.getSumOfTemperatures(zone);
            int count = 0;

            for (CityZone cz : ((Island) zone).getCityZones()) {
                for (DataPoint dp: cz.getDataPoints()) {
                    count++;
                }
            }
            if (count == 0) return 0;
            else return total / count;

        } else {
            throw new InvalidParameterException("Argument is not of type Zone");
        } 
        
    }


    public static double getMedianTemperature(Zone zone) throws InvalidParameterException {
        double medianTemp = 0;
        if (zone instanceof DataPoint) {
            medianTemp = ((DataPoint) zone).getTempValue();
        }
        else if (zone instanceof CityZone) {
            CityZone cityZone = (CityZone) zone;
            double[] temps = new double[cityZone.getDataPoints().size()];
            for (int i = 0; i < cityZone.getDataPoints().size(); i++) {
                temps[i] = cityZone.getDataPoints().get(i).getTempValue();
            }
            Arrays.sort(temps);
            int n = temps.length;
            if (n == 0) medianTemp = 0;
            else if (n % 2 == 0) medianTemp = (temps[((n-1)/2)]+temps[(n/2)])/2;
            else return temps[n / 2];

        }
        else if (zone instanceof Island) {
            Island island = (Island) zone;
            ArrayList<Double> temps = new ArrayList<>();
            for (CityZone cz: island.getCityZones()) {
                for (DataPoint dp: cz.getDataPoints()) {
                    temps.add(dp.getTempValue());
                }
            }
            Collections.sort(temps);
            int n = temps.size();
            if (n == 0) medianTemp = 0;
            else if (n % 2 == 0) medianTemp = (temps.get((n-1)/2)+temps.get(n/2))/2;
            else medianTemp = temps.get(n / 2);
        } else {
            throw new InvalidParameterException("Argument is not of type Zone");
        }
        return medianTemp; // Liable to change

    }

    public static double getModeTemperature(Zone zone) throws InvalidParameterException {
        if (zone instanceof DataPoint) {
            return ((DataPoint) zone).getTempValue();
        }
        else if (zone instanceof CityZone) {
            CityZone cityZone = (CityZone) zone;
            Double[] temps = new Double[cityZone.getDataPoints().size()];
            for (int i = 0; i < cityZone.getDataPoints().size(); i++) {
                temps[i] = cityZone.getDataPoints().get(i).getTempValue();
            }

            ArrayList<Double> tempsSet =  new ArrayList<>(new HashSet<Double>(Arrays.asList(temps)));

            if (temps.length == 0) return 0;
            else {
                double mode = tempsSet.get(0);
                int maxFrequency = Collections.frequency(Arrays.asList(temps), mode);
                for (double i: tempsSet) {
                    if (Collections.frequency(Arrays.asList(temps), i) > maxFrequency) {
                        mode = i;
                        maxFrequency = Collections.frequency(Arrays.asList(temps), i);
                    }
                }
                return mode;
            }
        }
        else if (zone instanceof Island) {
            Island island = (Island) zone;
            ArrayList<Double> temps = new ArrayList<>();
            for (CityZone cz: island.getCityZones()) {
                for (DataPoint dp: cz.getDataPoints()) {
                    temps.add(dp.getTempValue());
                }
            }

            ArrayList<Double> tempsSet =  new ArrayList<>(new HashSet<Double>(temps));

            if (temps.isEmpty()) return 0;
            else {
                double mode = tempsSet.get(0);
                int maxFrequency = Collections.frequency(temps, mode);
                for (double i: tempsSet) {
                    if (Collections.frequency(temps, i) > maxFrequency) {
                        mode = i;
                        maxFrequency = Collections.frequency(temps, i);
                    }
                }
                return mode;
            }
        } else {
            throw new InvalidParameterException("Argument is not of type Zone");
        }
    }

    public static double getSTDEVTemperature(Zone zone) throws InvalidParameterException {
        if (zone instanceof DataPoint) {
            return 0;
        }
        else if (zone instanceof CityZone) {
            double mean = StatisticsCalculator.getMeanTemperature(zone);
            int count = ((CityZone) zone).getDataPoints().size();
            double stDev = 0;
            for (DataPoint dp: ((CityZone) zone).getDataPoints()) {
                stDev += Math.pow(dp.getTempValue() - mean, 2);
            }
            if (count == 0) return 0;
            else return Math.sqrt(stDev / count);

        }
        else if (zone instanceof Island) {
            double mean = StatisticsCalculator.getMeanTemperature(zone);
            int count = 0;
            double stDev = 0;
            for (CityZone cz : ((Island) zone).getCityZones()) {
                for (DataPoint dp: cz.getDataPoints()) {
                    stDev += Math.pow(dp.getTempValue() - mean, 2);
                    count++;
                }
            }
            if (count == 0) return 0;
            else return Math.sqrt(stDev / count);

        } else {
            throw new InvalidParameterException("Argument is not of type Zone");
        }

    }

    public static HashMap<String, Double> getHighTempFlaggedZones(ArrayList<CityZone> zones) {
        HashMap<String, Double> highTemp = new HashMap<>();
        for (CityZone cz : zones) {
            double mean = StatisticsCalculator.getMeanTemperature(cz);   // overloaded version for a single CityZone
            if (mean >= HIGH_TEMP) {
                highTemp.put(cz.getName(), mean);
            }
        }
        return highTemp;
    }

    //Low Temp Flagged Zones
    public static HashMap<String, Double> getLowTempFlaggedZones(ArrayList<CityZone> zones) {
        HashMap<String, Double> lowTemp = new HashMap<>();
        for (CityZone cz : zones) {
            double mean = StatisticsCalculator.getMeanTemperature(cz);
            if (mean <= LOW_TEMP) {
                lowTemp.put(cz.getName(), mean);
            }
        }
        return lowTemp;
    }

    // Optimal Temp Zones
    public static HashMap<String, Double> getOptimalTempZones(ArrayList<CityZone> zones) {
        HashMap<String, Double> lowTemp = new HashMap<>();
        for (CityZone cz : zones) {
            double mean = StatisticsCalculator.getMeanTemperature(cz);
            if (mean >= LOW_TEMP && mean <= HIGH_TEMP) {
                lowTemp.put(cz.getName(), mean);
            }
        }
        return lowTemp;
    }

    //Temperature Differential Map (for an island) - used for smaller datasets - i.e. keys are cz-dp
    public static HashMap<String, Double> getMegaIslandTempDifferentialMap(Island island) {
        HashMap<String, Double> diffMap = new HashMap<>();
        double islandMeanTemp = StatisticsCalculator.getMeanTemperature(island);
        for (CityZone cz: island.getCityZones()) {
            for (DataPoint dp: cz.getDataPoints()) {
                diffMap.put(cz.getName() + "-" + dp.getName(), (dp.getTempValue()- islandMeanTemp));
            }
        }
        return diffMap;
    }

    //Temperature Differential Map (for an island) - used for larger datasets - i.e. keys are cz
    public static HashMap<String, Double> getNanoIslandTempDifferentialMap(Island island) { // name liable to change
        HashMap<String, Double> diffMap = new HashMap<>();
        double islandMeanTemp = StatisticsCalculator.getMeanTemperature(island);
        for (CityZone cz: island.getCityZones()) {
            diffMap.put(cz.getName(), (StatisticsCalculator.getMeanTemperature(cz) - islandMeanTemp));
        }
        return diffMap;
    }

    public static Integer getAllDataPointsInIsland(Island island) {
        int count = 0;
        for (CityZone cz: island.getCityZones()) {
            for (DataPoint dp: cz.getDataPoints()) {
                count++;
            }
        }
        return count;
    }


}


