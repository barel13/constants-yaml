package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.Main.debug;

public class Constants {
    private static final Map<String, WebConstant> constants = new HashMap<>();
    private static boolean initialized = false;

    static {
        try {
            load();
        } catch (IOException e) {
            System.out.println("Couldn't load constants");
            e.printStackTrace();
        }
    }

    public static void load() throws IOException {
        Map<String, List<Constant>> values = getValues();
        if (values == null) {
            System.out.println("Couldn't find resource");
            return;
        }

        values.forEach((table, value) -> {
            for (var constant : value) {
                String path = String.join(".", table, constant.getKey());
                WebConstant constantToSave = getConstant(constant, path);
                constants.put(path, constantToSave);
            }
        });
    }

    private static WebConstant getConstant(Constant constant, String path) {
        if (constant.isTunable() && debug) {
            var constantToSave = new NetworkTableConstant(constant.getValue().doubleValue());
            if (initialized) {
                initialize(path, constantToSave);
            }
            return constantToSave;
        }
        return new WrapperConstant(constant.getValue().doubleValue());
    }

    private static Map<String, List<Constant>> getValues() throws IOException {
        var resource = Thread.currentThread().getContextClassLoader().getResource("constants.yaml");

        var mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerModule(new Jdk8Module());
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        if (resource == null) {
            return null;
        }

        var typeRef = new TypeReference<Map<String, List<Constant>>>() {
        };
        return mapper.readValue(resource.openStream(), typeRef);
    }

    private static void initialize(String path, WebConstant constant) {
        if (constant instanceof NetworkTableConstant) {
            String[] split = path.split("\\.");
            ((NetworkTableConstant) constant).initialize(split[0], split[1]);
        }
    }

    public static void initializeAll() {
        constants.forEach(Constants::initialize);
        initialized = true;
    }

    public static WebConstant get(String path) {
        if (constants.containsKey(path)) {
            return constants.get(path);
        }
        return new WrapperConstant(0);
    }

    public static Map<String, WebConstant> getConstants() {
        return constants;
    }
}
