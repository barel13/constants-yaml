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

public class Constants {
    private static final Map<String, WebConstant> constants = new HashMap<>();

    public static void load() throws IOException {
        var resource = Thread.currentThread().getContextClassLoader().getResource("constants.yaml");

        var mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerModule(new Jdk8Module());
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        if (resource == null) {
            System.out.println("Couldn't find resource");
            return;
        }

        var typeRef = new TypeReference<Map<String, List<Constant>>>() {
        };
        var values = mapper.readValue(resource.openStream(), typeRef);
        values.forEach((table, value) -> {
            for (var constant : value) {
                String path = String.join(".", table, constant.getKey());
                if (constant.isTunable()) {
                    constants.put(path, new NetworkTableConstant(constant.getValue().doubleValue()));
                } else {
                    constants.put(path, new WrapperConstant(constant.getValue().doubleValue()));
                }
            }
        });
    }

    static {
        try {
            load();
        } catch (IOException e) {
            System.out.println("Couldn't load constants");
            e.printStackTrace();
        }
    }

    public static WebConstant get(String path) {
        if (constants.containsKey(path)) {
            return constants.get(path);
        }
        return new WrapperConstant(0);
    }

}
