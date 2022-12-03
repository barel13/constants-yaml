package org.example.constants;

/**
 * The interface is used to update constants through the network table.
 */
public interface WebConstant {
    /**
     * Gets the current value of the constant.
     *
     * @return the value of the constant.
     */
    double get();

    void set(double value);
}
