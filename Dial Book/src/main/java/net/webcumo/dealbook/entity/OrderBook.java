package net.webcumo.dealbook.entity;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class OrderBook {
    private final ConcurrentNavigableMap<Integer, Collection<MatchedEntry>> bid;
    private final ConcurrentNavigableMap<Integer, Collection<MatchedEntry>> ask;
    private final MatchedEntry[] entries;

    public OrderBook() {
        bid = new ConcurrentSkipListMap<>(new BidComparator());
        ask = new ConcurrentSkipListMap<>(new AskComparator());
        entries = new MatchedEntry[2_000_000];
    }

    public void addAsk(int price, int volume, int id) {
        MatchedEntry matchedEntry = new MatchedEntry(Operation.SELL, volume, price);
        Collection<Collection<MatchedEntry>> tailBid = bid.tailMap(price).values();
        matchEntry(matchedEntry, tailBid);
        Collection<MatchedEntry> value = ask.computeIfAbsent(price, key -> new CopyOnWriteArrayList<>());
        value.add(matchedEntry);
        entries[id] = matchedEntry;
    }

    public void addBid(int price, int volume, int id) {
        MatchedEntry matchedEntry = new MatchedEntry(Operation.BUY, volume, price);
        Collection<Collection<MatchedEntry>> tailAsk = ask.tailMap(price).values();
        matchEntry(matchedEntry, tailAsk);
        Collection<MatchedEntry> value = bid.computeIfAbsent(price, key -> new CopyOnWriteArrayList<>());
        value.add(matchedEntry);
        entries[id] = matchedEntry;
    }

    public void restoreAsk(MatchedEntry entry) {
        Collection<Collection<MatchedEntry>> tailBid = bid.tailMap(entry.getPrice()).values();
        matchEntry(entry, tailBid);
    }

    public void restoreBid(MatchedEntry entry) {
        Collection<Collection<MatchedEntry>> tailBid = ask.tailMap(entry.getPrice()).values();
        matchEntry(entry, tailBid);
    }

    public MatchedEntry remove(int id) {
        MatchedEntry entry = entries[id];
        if (entry != null) {
            if (entry.getOperation() == Operation.BUY) {
                bid.get(entry.getPrice()).remove(entry);
            } else {
                ask.get(entry.getPrice()).remove(entry);
            }
        }
        return entry;
    }

    @Override
    public String toString() {
        Iterator<Map.Entry<Integer, Collection<MatchedEntry>>> bidIterator = bid.descendingMap().entrySet().iterator();
        Iterator<Map.Entry<Integer, Collection<MatchedEntry>>> askIterator = ask.descendingMap().entrySet().iterator();

        StringBuilder sb = new StringBuilder("BID\t\t\t\t\tASK\nVolume@Price\t-\tVolume@Price\n");
        Map.Entry<Integer, Collection<MatchedEntry>> bidEntry = null;
        Map.Entry<Integer, Collection<MatchedEntry>> askEntry = null;
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
                appendEntry(sb, bidEntry.getKey(), volume(bidEntry.getValue()), "\t\t\t");
            } else {
                sb.append("\t\t\t\t\t\t");
            }
            if (hasVolume(askEntry)) {
                appendEntry(sb, askEntry.getKey(), volume(askEntry.getValue()), "\n");
            } else {
                sb.append("\n");
            }
            askEntry = bidEntry = null;
        }

        return sb.toString();
    }

    private void matchEntry(MatchedEntry matched, Collection<Collection<MatchedEntry>> tail) {
        Iterator<MatchedEntry> iterator = tail.stream().flatMap(Collection::stream).iterator();
        while(iterator.hasNext()) {
            MatchedEntry entry = iterator.next();
            entry.matchEntry(matched);
            if (matched.getVolume() == 0) {
                break;
            }
        }
    }

    private boolean hasVolume(Map.Entry<Integer, Collection<MatchedEntry>> entry) {
        return entry != null &&
                0 < volume(entry.getValue());
    }

    private int volume(Collection<MatchedEntry> entries) {
        return entries.stream().collect(Collectors.summingInt(MatchedEntry::getVolume));
    }

    private void appendEntry(StringBuilder sb, Integer price, int volume, String str) {
        int remainder = price % 100;
        price /= 100;
        sb.append(volume).append('@').append(price).append('.');
        if (remainder < 10) {
            sb.append('0');
        }
        sb.append(remainder);
        sb.append(str);
    }
}
