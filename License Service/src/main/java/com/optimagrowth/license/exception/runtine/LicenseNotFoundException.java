package com.optimagrowth.license.exception.runtine;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.FileNotFoundException;

@Data
@EqualsAndHashCode(callSuper = true)
public class LicenseNotFoundException extends FileNotFoundException {

    private String message;
}
