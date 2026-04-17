package oop.project.library.argument;

@FunctionalInterface
public interface ArgumentType<T> {

    T parse(String value);

}