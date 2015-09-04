package net.webcumo.dealbook.entity;

public abstract class OrderWithId implements Runnable {
    private final int id;

    public OrderWithId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
