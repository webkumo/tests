package net.webcumo.dealbook.entity;

public class AddOrder extends OrderWithId {
    private final Integer price;
    private final Operation operation;
    private final int volume;
    private OrderBook book;

    public AddOrder(int id, String price, String operation, int volume) {
        super(id);
        this.price = Integer.parseInt(price.trim().replace(".", ""));
        this.operation = Operation.valueOf(operation);
        this.volume = volume;
    }

    public void setBook(OrderBook book) {
        this.book = book;
    }

    @Override
    public void run() {
        if (isAsk()) {
            book.addAsk(price, volume, getId());
        } else {
            book.addBid(price, volume, getId());
        }
    }

    private boolean isAsk() {
        return operation == Operation.SELL;
    }
}
