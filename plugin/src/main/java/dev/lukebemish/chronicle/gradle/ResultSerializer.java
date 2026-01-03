package dev.lukebemish.chronicle.gradle;

import java.io.Serializable;

public interface ResultSerializer extends Serializable {
    String serialize(Object o, boolean prettyPrint);
}
