package org.example.constants;

import com.fasterxml.jackson.annotation.JsonAnySetter;

public class YamlConstant {
    private String key;
    private Number value;
    private boolean tunable = false;

    public YamlConstant() {
    }

    public YamlConstant(String key, Number value) {
        this.key = key;
        this.value = value;
    }

    public YamlConstant(String key, Number value, boolean tunable) {
        this.key = key;
        this.value = value;
        this.tunable = tunable;
    }

    @JsonAnySetter
    public void set(String key, Number value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    public boolean isTunable() {
        return tunable;
    }

    public void setTunable(boolean tunable) {
        this.tunable = tunable;
    }

    @Override
    public String toString() {
        return "Constant{" +
                "key='" + key + '\'' +
                ", value=" + value +
                ", tunable=" + tunable +
                '}';
    }
}
