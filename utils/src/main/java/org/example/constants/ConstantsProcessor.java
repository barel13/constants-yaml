package org.example.constants;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes("org.example.constants.ProcessFile")
@AutoService(javax.annotation.processing.Processor.class)
public class ConstantsProcessor extends AbstractProcessor {
    private static final String PACKAGE = "org.example.constants";

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (var element : roundEnv.getElementsAnnotatedWith(ProcessFile.class)) {
            var annotation = element.getAnnotation(ProcessFile.class);
            var fileName = annotation.name();
            try {
                var resource = processingEnv.getFiler().getResource(StandardLocation.SOURCE_PATH, "", "constants.yaml");
                if (resource == null) {
                    processingEnv.getMessager()
                            .printMessage(javax.tools.Diagnostic.Kind.ERROR, "Couldn't find resource " + fileName);
                    return false;
                }

                var values = getValues(resource.openInputStream());
                writeFile(fileName, values);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    private void writeFile(String fileName, Map<String, List<YamlConstant>> values) throws IOException {
        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(toTitleCase(fileName));

        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            fileHeader(fileName, out);
            values.forEach((subsystem, value) -> {
                out.println("\tpublic static final class " + subsystem + " {");
                for (var constant : value) {
                    addConstant(out, subsystem, constant);
                }
                out.println("\t}");
            });
            out.println("}");
        }
    }

    private void addConstant(PrintWriter out, String subsystem, YamlConstant constant) {
        out.print("\t\tpublic static final WebConstant " + constant.getKey() + " = new ");
        if (constant.isTunable()) {
            out.println("NetworkTableConstant(\"" + subsystem + "\", \"" + constant.getKey() + "\", " + constant.getValue()
                    .doubleValue() + ");");
        } else {
            out.println("WrapperConstant(" + constant.getValue().doubleValue() + ");");
        }
    }

    private void fileHeader(String fileName, PrintWriter out) {
        out.println("package " + PACKAGE + ";");
        out.println("import org.example.constants.WebConstant;");
        out.println("import org.example.constants.WrapperConstant;");
        out.println("import org.example.constants.NetworkTableConstant;");
        out.println("\n");
        out.println("public final class " + toTitleCase(fileName) + " {");
    }

    private Map<String, List<YamlConstant>> getValues(InputStream file) throws IOException {
        var mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerModule(new Jdk8Module());
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        var typeRef = new TypeReference<Map<String, List<YamlConstant>>>() {
        };
        return mapper.readValue(file, typeRef);
    }

    private String toTitleCase(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
