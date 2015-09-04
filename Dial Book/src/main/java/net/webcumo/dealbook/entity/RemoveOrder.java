package net.webcumo.dealbook.entity;


import java.util.function.Consumer;

public class RemoveOrder extends OrderWithId {
    private final OrderBook book;
    private final Consumer<Runnable> consumer;

    public RemoveOrder(OrderBook book, int id, Consumer<Runnable> consumer) {
        super(id);
        this.book = book;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        MatchedEntry entry = book.remove(getId());
        if (entry == null) {
            return;
        }
        if (entry.getOperation() == Operation.BUY) {
            entry.getMatchedValues().forEach(
                    (price, volume) -> consumer.accept(new RestoreBid(price, volume, book))
            );
        } else {
            entry.getMatchedValues().forEach(
                    (price, volume) -> consumer.accept(new RestoreAsk(price, volume, book))
            );
        }
    }
}
