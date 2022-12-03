package org.example;

import org.example.constants.Constants;
import org.example.constants.NetworkTableConstant;
import org.example.constants.ProcessFile;

@ProcessFile(name = "constants")
public class Main {
    public static boolean debug = true;

    public static void main(String[] args) {
        System.out.println(Constants.SwerveDrive.kI.get());
        System.out.println(Constants.SwerveDrive.name.get());
        System.out.println(Constants.SwerveModule.zeroPosition.get());
        
        if (debug) {
            NetworkTableConstant.initializeAll();
        }
//        Constants.initializeAll();
    }
}