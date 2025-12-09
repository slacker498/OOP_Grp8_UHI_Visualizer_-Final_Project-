package com.uhi_visualizer;

import org.json.JSONObject;
import org.json.JSONArray;

import com.uhi_visualizer.exceptions.DuplicateDataPointException;
import com.uhi_visualizer.exceptions.FileNotJSONException;
import com.uhi_visualizer.exceptions.InvalidColumnException;
import com.uhi_visualizer.exceptions.InvalidValueTypeException;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class JSONFileParser implements FileParsable  {

    @Override
    public Island readFromFile(String filePath) throws IOException, InvalidValueTypeException, DuplicateDataPointException, InvalidColumnException {
        Island island = new Island(); // Create island for return
        // Set island name
        island.setName(filePath.substring(filePath.lastIndexOf("\\")+1, filePath.lastIndexOf(".")));
        if (!filePath.contains(".json")){
            throw new FileNotJSONException("File type non-JSON! ");
        }

        JSONObject jsonObject = null;
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            jsonObject = new JSONObject(content);
        }
        catch (IOException x){
            throw x;
        }

        try {
            Set<String> keys = jsonObject.keySet();
            for(String key: keys){
                CityZone newCityZone = new CityZone(key);
                island.getCityZones().add(newCityZone);

                JSONArray dataPoint = jsonObject.getJSONArray(key);
                for (int i = 0; i < dataPoint.length(); i++) {
                    String data = dataPoint.get(i).toString();
                    String noBraces = data.substring(1, data.length()-1);
                    String[] dataPoints = noBraces.split(",");

                    int czPos = (new ArrayList<>(keys)).indexOf(key);
                    if (island.getCityZones().get(czPos).getDataPoints().contains(new DataPoint(dataPoints[0],Double.parseDouble(dataPoints[1])))) {
                        throw new DuplicateDataPointException("Repeated DataPoint in city zone");
                    }
                    newCityZone.getDataPoints().add(new DataPoint(dataPoints[0], Double.parseDouble(dataPoints[1])));
                }
            }
        } catch (NullPointerException e) {
            throw new InvalidColumnException("No JSON found!");
        } catch (NumberFormatException e) {
            throw new InvalidValueTypeException("Invalid temperature value.");
        }
        return island;
    }

    @Override
    public void writeToFile(Island island, String destinationPath) throws IOException {
         try {
             FileWriter fileWriter = new FileWriter(destinationPath);

             JSONObject jsonObject = new JSONObject();

             for(CityZone cityZone: island.getCityZones()){
                 String cityZoneName = cityZone.getName();
                 JSONArray jsonArray = new JSONArray();
                 for(DataPoint dataPoint: cityZone.getDataPoints()){
                     String dataPointName = dataPoint.getName();
                     double temperature = dataPoint.getTempValue();

                     Object[] dataPointPair = new Object[2];
                     dataPointPair[0] = dataPointName;
                     dataPointPair[1] = temperature;

                     String dataPointPairing = Arrays.deepToString(dataPointPair);

                     jsonArray.put(dataPointPair);
                 }
                 jsonObject.put(cityZoneName, jsonArray);
             }

             fileWriter.write(jsonObject.toString());

             fileWriter.flush();
             fileWriter.close();

             System.out.println("The JSON file has been successfully created");

         } catch (IOException e) {
             throw e;
         }

    }
}

