package com.uhi_visualizer.exceptions;

import java.io.IOException;
public class FileNotCSVException extends IOException {

    public FileNotCSVException(String message){
        super(message);
    }

}

