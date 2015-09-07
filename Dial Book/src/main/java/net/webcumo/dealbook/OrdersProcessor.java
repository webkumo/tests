package net.webcumo.dealbook;

import net.webcumo.dealbook.entity.AddOrder;
import net.webcumo.dealbook.entity.OrderBook;
import net.webcumo.dealbook.entity.RemoveOrder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

class OrdersProcessor {
    private final Map<String, OrderBook> book = new HashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final long startTime = System.currentTimeMillis();

    public void addOrder(String bookName, AddOrder order) {
        OrderBook orderBook = book.computeIfAbsent(bookName, name -> new OrderBook());
        order.setBook(orderBook);
        executor.execute(order);
    }

    public void removeOrder(String bookName, int orderId) {
        OrderBook orderBook = book.computeIfAbsent(bookName, name -> new OrderBook());
        RemoveOrder order = new RemoveOrder(orderBook, orderId, executor::execute);
        executor.execute(order);
    }

    public void ordersStop() {
        executor.execute(() -> {
            executor.shutdown();
            try {
                executor.awaitTermination(100, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long elapsed = System.currentTimeMillis() - startTime;
            book.forEach((name, book) -> {
                System.out.println("Order book: " + name);
                System.out.println(book);
            });
            System.out.println("Time elapsed: " + elapsed + " ms");
        });
    }
}
