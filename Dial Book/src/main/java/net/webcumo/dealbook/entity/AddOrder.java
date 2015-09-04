package net.webcumo.dealbook.entity;

public class AddOrder extends OrderWithId {
    private Integer price;
    private Operation operation;
    private int volume;
    private OrderBook book;

    public AddOrder(int id, String price, String operation, int volume) {
        super(id);
        this.price = Integer.parseInt(price.trim().replace(".", ""));
        this.operation = Operation.valueOf(operation);
        this.volume = volume;
    }

    public boolean isAsk() {
        return operation == Operation.SELL;
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
}
