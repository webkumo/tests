package net.webcumo.dealbook.entity;

public class RestoreAsk extends Restore {
    public RestoreAsk(Integer price, int volume, OrderBook book) {
        super(price, volume, book);
    }

    @Override
    public void run() {
        book.restoreAsk(price, volume);
    }
}
