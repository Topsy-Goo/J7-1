package ru.gb.antonov.j71.entities;

@FunctionalInterface
public interface Buildable<T> {
    T build ();
}
