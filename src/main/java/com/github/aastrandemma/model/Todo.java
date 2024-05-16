package com.github.aastrandemma.model;

import java.time.LocalDate;
import java.util.Objects;

import static java.util.Objects.hash;

public class Todo {
    private int id;
    private String title;
    private String description;
    private LocalDate deadline;
    private boolean done;
    private Person assignee;

    public Todo(String title) {
        setTitle(title);
    }

    public Todo(String title, LocalDate deadline, Person assignee) {
        setTitle(title);
        setDeadline(deadline);
        setAssignee(assignee);
    }

    public Todo(String title, String description, LocalDate deadline, Person assignee) {
        this(title, deadline, assignee);
        setDescription(description);
    }

    public Todo(int id, String title, String description, LocalDate deadline, boolean done) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.done = done;
    }

    public Todo(int id, String title, String description, LocalDate deadline, boolean done, Person assignee) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.done = done;
        this.assignee = assignee;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title can't be NULL or empty.");
        }
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeadline(LocalDate deadline) {
        Objects.requireNonNull(deadline, "Deadline can't be null.");
        this.deadline = deadline;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setAssignee(Person assignee) {
        Objects.requireNonNull(assignee, "Assignee can't be null.");
        this.assignee = assignee;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public Person getAssignee() {
        return assignee;
    }

    public boolean isDone() {
        return done;
    }

    // TodoItem is overdue if currentDate > deadLine
    public boolean isOverdue() {
        return LocalDate.now().isAfter(deadline);
    }

    @Override
    public int hashCode() {
        return hash(id, title, description, deadline, done);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Todo)) return false;
        return id == ((Todo) obj).getId() && Objects.equals(title, ((Todo) obj).getTitle()) && Objects.equals(description, ((Todo) obj).getDescription())
                && deadline == ((Todo) obj).getDeadline() && done == ((Todo) obj).isDone();
    }

    @Override
    public String toString() {
        String assignee;
        if (this.getAssignee() == null) {
            assignee = "task not assigned";
        } else {
            assignee = "assigned to:" + this.getAssignee().getFirstName() + " " + this.getAssignee().getLastName();
        }
        return "TodoItemInfo {id: " + getId() + ", title: " + getTitle() + ", taskDescription: " + getDescription()
                + ", deadline: " + getDeadline() + ", done: " + isDone() + ", " +  assignee + "}";
    }
}