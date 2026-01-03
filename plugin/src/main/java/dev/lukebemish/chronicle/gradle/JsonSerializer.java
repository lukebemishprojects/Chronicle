package dev.lukebemish.chronicle.gradle;

import groovy.json.JsonOutput;

public class JsonSerializer implements ResultSerializer {
    @Override
    public String serialize(Object o, boolean prettyPrint) {
        var out = JsonOutput.toJson(o);
        if (prettyPrint) {
            return JsonOutput.prettyPrint(out);
        } else {
            return out;
        }
    }
}
