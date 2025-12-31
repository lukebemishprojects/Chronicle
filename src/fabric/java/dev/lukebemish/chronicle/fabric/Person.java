package dev.lukebemish.chronicle.fabric;

import dev.lukebemish.chronicle.core.Action;
import dev.lukebemish.chronicle.core.BackendMap;
import dev.lukebemish.chronicle.core.ChronicleMap;
import dev.lukebemish.chronicle.core.DslValidate;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

import java.util.Objects;

public class Person extends ChronicleMap {
    public Person(BackendMap backend) {
        super(backend);
    }

    public String getName() {
        return (String) Objects.requireNonNull(get("name"));
    }

    public void setName(String name) {
        putAt("name", name);
    }

    public void contact(@DelegatesTo(value = ContactInformation.class, strategy = Closure.DELEGATE_FIRST) Action<ContactInformation> action) {
        backend().configure("contact", action, ContactInformation.class);
    }

    @DslValidate
    public static void validate(BackendMap map) {
        if (!(map.get("name") instanceof String)) {
            throw new IllegalStateException("Expected 'name' to be present and a String");
        }
    }
}
