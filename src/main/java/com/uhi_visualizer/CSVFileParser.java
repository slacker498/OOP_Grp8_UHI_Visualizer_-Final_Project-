package com.uhi_visualizer;

import java.io.*;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;

import com.uhi_visualizer.exceptions.DuplicateDataPointException;
import com.uhi_visualizer.exceptions.FileNotCSVException;
import com.uhi_visualizer.exceptions.InvalidColumnException;
import com.uhi_visualizer.exceptions.InvalidValueTypeException;

public class CSVFileParser implements FileParsable {

    @Override
    //transform csv file into CityZone array
    public Island readFromFile (String filePath) throws IOException, InvalidColumnException, InvalidValueTypeException, DuplicateDataPointException {
        Island island = new Island(); // Island object for return
        // Set Island name
        island.setName(filePath.substring((filePath.lastIndexOf("\\")+1), filePath.indexOf("."))); // only works for Windows systems
        if (!(filePath.endsWith(".csv"))) { // check if file is csv
            throw new FileNotCSVException("File type: non-csv");
        }

        // This code block takes a file and iterates through each line. Each line is passed as a String array of
        // values and stored in the 'data' list.
        List<String[]> data = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath)); // reads data file path using FileReader and Buffered Reader
            String line;
            while ((line = bufferedReader.readLine()) != null) { // passes lines from csv file into data List above
                String[] values = line.split(",");
                if (!(values.length == 0 || values[0].isEmpty())) data.add(values); // handle empty lines
            }
            bufferedReader.close();
        } catch (IOException e) { // Exception-handling for the entire reading
            throw e;
        }

        // Exception handling to ensure all 3 columns are existent
        if (data.get(0).length != 3) throw new InvalidColumnException("Data File does not contain the required columns");

        // Set island's cityZones
        ArrayList<String> cityZoneNames = new ArrayList<>(); // List of CityZone Names (without repetitions)
        for (int row = 0; row < data.size(); row++) {
            String name = data.get(row)[0];
            if (!(cityZoneNames.contains(name))) {
                cityZoneNames.add(name);
                island.getCityZones().add(new CityZone(name)); // add CityZone objects to the island
            }
        }
        
        // Set dataPoints for each city zone
        for (int idx = 0; idx < data.size(); idx++) {
            try {
                String czName = data.get(idx)[0].strip(); // verification of names yet to be done (validateData method)
                String dpName = data.get(idx)[1].strip();
                double dpTemp = Double.parseDouble(data.get(idx)[2].strip());

                int czPos = cityZoneNames.indexOf(czName);

                if (island.getCityZones().get(czPos).getDataPoints().contains(new DataPoint(dpName, dpTemp))) {
                    throw new DuplicateDataPointException("A city zone has the same datapoint repeated");
                } else {
                    island.getCityZones().get(czPos).getDataPoints().add(new DataPoint(dpName, dpTemp));
                }
            } catch (NumberFormatException x) {
                throw new InvalidValueTypeException("Temperature value is not a valid integer/ floating-point.");
            }



        }
        return island;
    }

    @Override
    public void writeToFile(Island island, String destinationPath){
         try {
             FileWriter fileWriter = new FileWriter(destinationPath);
             for (CityZone city: island.getCityZones()){
                 for (DataPoint dataPoint: city.getDataPoints()) {
                     fileWriter.write(city.getName()+","+dataPoint.getName()+","+dataPoint.getTempValue()+"\n");
                 }
             }
             fileWriter.close();

         } catch (IOException e) {
             System.out.println(e.getMessage());
         }
    }


}

