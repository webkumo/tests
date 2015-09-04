package net.webcumo.dealbook.entity;

public class RestoreBid extends Restore {
    public RestoreBid(Integer price, int volume, OrderBook book) {
        super(price, volume, book);
    }

    @Override
    public void run() {
        book.restoreBid(price, volume);
    }
}
