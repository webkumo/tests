package net.webcumo.dealbook.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MatchedEntry {
    private final Map<MatchedEntry, Integer> matchedValues;
    private final Operation operation;
    private final int price;
    private final AtomicInteger currentVolume;

    public MatchedEntry(Operation operation, int volume, int price) {
        this.price = price;
        this.operation = operation;
        matchedValues = new ConcurrentHashMap<>();
        currentVolume = new AtomicInteger(volume);
    }

    public int getPrice() {
        return price;
    }

    public Operation getOperation() {
        return operation;
    }

    public Collection<MatchedEntry> restoreMatchedVolumes() {
        Collection<MatchedEntry> entriesToRestore = new ArrayList<>();
        matchedValues.forEach((entry, volume) -> {
            entry.restore(this, volume);
            entriesToRestore.add(entry);
        });
        return entriesToRestore;
    }

    public int getVolume() {
        return currentVolume.get();
    }

    public void matchEntry(MatchedEntry entry) {
        boolean matchingEnded = false;
        while (!matchingEnded) {
            int matchedVolume = entry.getVolume();
            int volume = currentVolume.get();
            if (volume == 0 || matchedVolume == 0) {
                return;
            }
            int diff = matchedVolume - volume;
            if (diff >= 0) {
                matchingEnded = matchEntries(entry, matchedVolume, diff, volume);
            } else {
                matchingEnded = entry.matchEntries(this, volume, -diff, matchedVolume);
            }
        }
    }

    private void restore(MatchedEntry entry, Integer volume) {
        matchedValues.remove(entry);
        currentVolume.addAndGet(volume);
    }

    private boolean matchEntries(MatchedEntry entry, int matchedVolume, int diff, int volume) {
        boolean successfulSet = currentVolume.compareAndSet(volume, 0);
        if (successfulSet) {
            if (entry.setVolume(matchedVolume, diff, volume, this)) {
                matchedValues.put(entry, volume);
            } else {
                currentVolume.addAndGet(volume);
                successfulSet = false;
            }
        }
        return successfulSet;
    }

    private boolean setVolume(int previousValue, int newValue, int diff, MatchedEntry entry) {
        boolean isSet = currentVolume.compareAndSet(previousValue, newValue);
        if (isSet) {
            matchedValues.put(entry, diff);
        }
        return isSet;
    }
}
