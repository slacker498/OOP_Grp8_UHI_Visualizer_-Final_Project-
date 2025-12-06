package com.uhi_visualizer.exceptions;

import java.io.IOException;

public class FileNotJSONException extends IOException {

    public FileNotJSONException(String message){
        super(message);
    }

}

