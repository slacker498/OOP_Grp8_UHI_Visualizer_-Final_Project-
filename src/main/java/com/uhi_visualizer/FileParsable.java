package com.uhi_visualizer;

import com.uhi_visualizer.exceptions.*;

import java.io.IOException;

public interface FileParsable {

    // A subclass implementing this method must read from a file, regardless of type and extract components to create an Island object
    public Island readFromFile(String filePath) throws IOException, InvalidColumnException, InvalidValueTypeException, DuplicateDataPointException;

    // A subclass implementing this method must take an Island object and a file path, and create a specified file
    public void writeToFile(Island island, String destinationPath);

}