package org.example;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.util.HashSet;
import java.util.Set;

/**
 * The class is used to update the value of the web constant through the networktables.
 */
public class NetworkTableConstant implements WebConstant {
    private static NetworkTable BASE_TABLE = null;
    private double defaultValue;
    private NetworkTableEntry constant;

    NetworkTableConstant(double defaultValue) {
        this.defaultValue = defaultValue;
    }

    void initialize(String table, String key) {
        if (BASE_TABLE == null) {
            BASE_TABLE = NetworkTableInstance.getDefault().getTable("Constants");
        }
        constant = BASE_TABLE.getSubTable(table).getEntry(key);
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
        defaultValue = value;
        constant.setDouble(value);
    }

    @Override
    public String toString() {
        return "NetworkTableConstant{" +
                "defaultValue=" + defaultValue +
                '}';
    }
}
