package org.example;

public class Main {
    public static boolean debug = true;

    public static void main(String[] args) {

        System.out.println(Constants.get("SwerveDrive.kP"));
        System.out.println(Constants.getConstants());

//        Constants.initializeAll();
    }
}