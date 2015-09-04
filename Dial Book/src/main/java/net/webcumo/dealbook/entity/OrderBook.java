package net.webcumo.dealbook.entity;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderBook {
    private final ConcurrentNavigableMap<Integer, AtomicInteger> bid;
    private final ConcurrentNavigableMap<Integer, AtomicInteger> ask;
    private final MatchedEntry[] entries;

    public OrderBook() {
        bid = new ConcurrentSkipListMap<>(new BidComparator());
        ask = new ConcurrentSkipListMap<>(new AskComparator());
        entries = new MatchedEntry[2_000_000];
    }

    public void addAsk(int price, int volume, int id) {
        MatchedEntry matchedEntry = new MatchedEntry(Operation.SELL);
        ConcurrentNavigableMap<Integer, AtomicInteger> tailBid = bid.tailMap(price);
        volume = addEntry(volume, matchedEntry, tailBid);
        AtomicInteger value = ask.computeIfAbsent(price, key -> new AtomicInteger());
        value.addAndGet(volume);
        entries[id] = matchedEntry;
    }

    public void addBid(int price, int volume, int id) {
        MatchedEntry matchedEntry = new MatchedEntry(Operation.BUY);
        ConcurrentNavigableMap<Integer, AtomicInteger> tailAsk = ask.tailMap(price);
        volume = addEntry(volume, matchedEntry, tailAsk);
        AtomicInteger value = bid.computeIfAbsent(price, key -> new AtomicInteger());
        value.addAndGet(volume);
        entries[id] = matchedEntry;
    }

    public void restoreAsk(Integer price, int volume) {
        volume = restoreValue(volume, bid.tailMap(price));
        ask.get(price).addAndGet(volume);
    }

    public void restoreBid(Integer price, int volume) {
        volume = restoreValue(volume, ask.tailMap(price));
        bid.get(price).addAndGet(volume);
    }

    public MatchedEntry remove(int id) {
        MatchedEntry entry = entries[id];
        if (entry != null) {
            entries[id] = null;
        }
        return entry;
    }

    private int addEntry(int volume, MatchedEntry matched, ConcurrentNavigableMap<Integer, AtomicInteger> tail) {
        for (Map.Entry<Integer, AtomicInteger> entry: tail.entrySet()) {
            AtomicInteger i = entry.getValue();
            Integer price = entry.getKey();
            boolean successfulSet = false;
            while (!successfulSet) {
                int c = i.get();
                if (c == 0) {
                    successfulSet = true;
                    continue;
                }
                int diff = c - volume;
                if (diff >= 0) {
                    successfulSet = i.compareAndSet(c, diff);
                    if (successfulSet) {
                        matched.put(price, volume);
                        volume = 0;
                    }
                } else {
                    successfulSet = i.compareAndSet(c, 0);
                    if (successfulSet) {
                        matched.put(price, c);
                        volume = -diff;
                    }
                }
            }
            if (volume == 0) {
                break;
            }
        }
        return volume;
    }

    private int restoreValue(int volume, ConcurrentNavigableMap<Integer, AtomicInteger> tail) {
        for (AtomicInteger i: tail.values()) {
            boolean successfulSet = false;
            while (!successfulSet) {
                int c = i.get();
                if (c == 0) {
                    successfulSet = true;
                    continue;
                }
                int diff = c - volume;
                if (diff >= 0) {
                    successfulSet = i.compareAndSet(c, diff);
                    if (successfulSet) {
                        volume = 0;
                    }
                } else {
                    successfulSet = i.compareAndSet(c, 0);
                    if (successfulSet) {
                        volume = -diff;
                    }
                }
            }
            if (volume == 0) {
                break;
            }
        }
        return volume;
    }

    @Override
    public String toString() {
        Iterator<Map.Entry<Integer, AtomicInteger>> bidIterator = bid.descendingMap().entrySet().iterator();
        Iterator<Map.Entry<Integer, AtomicInteger>> askIterator = ask.descendingMap().entrySet().iterator();

        StringBuilder sb = new StringBuilder("BID\t\t\t\t\tASK\nVolume@Price\t–\tVolume@Price\n");
        Map.Entry<Integer, AtomicInteger> bidEntry = null;
        Map.Entry<Integer, AtomicInteger> askEntry = null;
        while (askIterator.hasNext() || bidIterator.hasNext()) {
            if (bidIterator.hasNext() &&
                    (bidEntry == null || !hasVolume(bidEntry))) {
                bidEntry = bidIterator.next();
            }
            if (askIterator.hasNext() &&
                    (askEntry == null || !hasVolume(askEntry))) {
                askEntry = askIterator.next();
            }
            if (askIterator.hasNext() && (!hasVolume(askEntry)) ||
                    bidIterator.hasNext() && (!hasVolume(bidEntry))) {
                continue;
            }
            if (hasVolume(bidEntry)) {
                appendEntry(sb, bidEntry, "\t\t\t");
            } else {
                sb.append("\t\t\t\t\t\t");
            }
            if (hasVolume(askEntry)) {
                appendEntry(sb, askEntry, "\n");
            } else {
                sb.append("\n");
            }
            askEntry = bidEntry = null;
        }

        return sb.toString();
    }

    private boolean hasVolume(Map.Entry<Integer, AtomicInteger> entry) {
        return entry != null && entry.getValue().get() > 0;
    }

    private void appendEntry(StringBuilder sb, Map.Entry<Integer, AtomicInteger> entry, String str) {
        int volume = entry.getValue().get();
        int priceInt = entry.getKey();
        int remainder = priceInt % 100;
        priceInt /= 100;
        sb.append(volume).append('@').append(priceInt).append('.').append(remainder);
        if (remainder == 0) {
            sb.append('0');
        }
        sb.append(str);
    }
}
