package net.webcumo.dealbook.entity;

abstract class OrderWithId implements Runnable {
    private final int id;

    OrderWithId(int id) {
        this.id = id;
    }

    int getId() {
        return id;
    }
}
