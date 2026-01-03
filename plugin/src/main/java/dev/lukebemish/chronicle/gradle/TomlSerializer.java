package dev.lukebemish.chronicle.gradle;

import groovy.json.JsonOutput;
import io.github.wasabithumb.jtoml.JToml;
import io.github.wasabithumb.jtoml.option.JTomlOptions;

public class TomlSerializer implements ResultSerializer {
    @Override
    public String serialize(Object o, boolean prettyPrint) {
        var out = JToml.jToml(JTomlOptions.builder().build());
        return out.writeToString(out.toToml(o));
    }
}
