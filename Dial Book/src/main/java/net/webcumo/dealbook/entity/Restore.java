package net.webcumo.dealbook.entity;


public abstract class Restore implements Runnable {
    protected final Integer price;
    protected final int volume;
    protected final OrderBook book;

    public Restore(Integer price, int volume, OrderBook book) {
        this.price = price;
        this.volume = volume;
        this.book = book;
    }

}
