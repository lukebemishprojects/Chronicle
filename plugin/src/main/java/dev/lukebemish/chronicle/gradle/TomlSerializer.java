package dev.lukebemish.chronicle.gradle;

import com.moandjiezana.toml.TomlWriter;

public class TomlSerializer implements ResultSerializer {
    @Override
    public String serialize(Object o, boolean prettyPrint) {
        var writer = new TomlWriter();
        return writer.write(o);
    }
}
