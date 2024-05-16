package com.github.aastrandemma.model;

import java.util.Objects;

import static java.util.Objects.hash;

public class Person {
    private int id;
    private String firstName;
    private String lastName;

    public Person(String firstName, String lastName) {
        setFirstName(firstName);
        setLastName(lastName);
    }

    public Person(int id, String firstName, String lastName) {
        this(firstName, lastName);
        setId(id);
    }

    public void setFirstName(String firstName) {
        Objects.requireNonNull(firstName, "First name can't be null.");
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        Objects.requireNonNull(lastName, "Last name can't be null.");
        this.lastName = lastName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public int hashCode() {
        return hash(id, firstName, lastName);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Person)) return false;
        return id == ((Person) obj).getId() && Objects.equals(firstName, ((Person) obj).getFirstName())
                && Objects.equals(lastName, ((Person) obj).getLastName());
    }

    @Override
    public String toString() {
        return "PersonInfo {id: " + getId() + ", name: " + getFirstName() + " " + getLastName() + "}";
    }
}