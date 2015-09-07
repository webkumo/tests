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
        Consumer<MatchedEntry> bookRestore = entry.getOperation() == Operation.BUY ? book::restoreBid : book::restoreAsk;
        entry.restoreMatchedVolumes().forEach(
                matchedEntry -> consumer.accept(() -> bookRestore.accept(matchedEntry))
        );
    }
}
