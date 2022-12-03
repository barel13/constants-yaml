package org.example.constants;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.util.HashSet;
import java.util.Set;

/**
 * The class is used to update the value of the web constant through the networktables.
 */
public class NetworkTableConstant implements WebConstant {
    private static final Set<NetworkTableConstant> constants = new HashSet<>();
    private static boolean robotInitialized = false;
    private static NetworkTable BASE_TABLE = null;
    private final String subsystem;
    private final String entry;
    private double defaultValue;
    private NetworkTableEntry constant;

    public NetworkTableConstant(String subsystem, String entry, double defaultValue) {
        this.subsystem = subsystem;
        this.entry = entry;
        this.defaultValue = defaultValue;
        if (robotInitialized) {
            initialize();
        } else {
            constants.add(this);
        }
    }

    public static void initializeAll() {
        if (BASE_TABLE == null) {
            BASE_TABLE = NetworkTableInstance.getDefault().getTable("Constants");
            constants.forEach(NetworkTableConstant::initialize);
            constants.clear();
            robotInitialized = true;
        }
    }

    private void initialize() {
        constant = BASE_TABLE.getSubTable(subsystem).getEntry(entry);
        constant.setDouble(defaultValue);
    }

    /**
     * Gets the value of the constant.
     *
     * @return the value of the constant or the default value.
     */
    @Override
    public double get() {
        return constant.getDouble(defaultValue);
    }

    @Override
    public void set(double value) {
        constant.setDouble(value);
        defaultValue = value;
    }

    @Override
    public String toString() {
        return "NetworkTableConstant{" +
                "defaultValue=" + defaultValue +
                '}';
    }
}
