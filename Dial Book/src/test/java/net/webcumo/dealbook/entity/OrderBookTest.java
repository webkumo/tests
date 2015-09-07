package net.webcumo.dealbook.entity;


import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

public class OrderBookTest {
    private OrderBook book;

    @Before
    public void prepareTests() {
        book = new OrderBook();
    }

    @Test
    public void testAddOrders() {
        book.addAsk(100, 10, 1);
        book.addBid(105, 3, 2);
        assertTrue("Not found expected value", book.toString().contains("\t7@1.00"));
    }

    @Test
    public void testRemove() {
        book.addAsk(100, 10, 1);
        book.addBid(105, 4, 2);
        Executor executor = Executors.newSingleThreadExecutor();
        new RemoveOrder(book, 1, executor::execute).run();
        String bookToString = book.toString();
        assertTrue("Found not expected value", !bookToString.contains("\t6@1.00"));
        assertTrue("Not found expected value", bookToString.contains("4@1.05"));
    }
}
