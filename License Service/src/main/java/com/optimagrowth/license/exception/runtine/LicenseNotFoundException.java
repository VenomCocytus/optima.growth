package com.optimagrowth.license.exception.runtine;

import java.io.FileNotFoundException;

public class LicenseNotFoundException extends FileNotFoundException {

    public LicenseNotFoundException(String message) {
        super(message);
    }
}
