package net.webcumo.dealbook.entity;

import java.util.LinkedHashMap;

public class MatchedEntry {
    private final LinkedHashMap<Integer, Integer> matchedValues;
    private final Operation operation;

    public MatchedEntry(Operation operation) {
        this.operation = operation;
        matchedValues = new LinkedHashMap<>(4);
    }

    public void put(Integer price, Integer volume) {
        matchedValues.put(price, volume);
    }

    public Operation getOperation() {
        return operation;
    }

    public LinkedHashMap<Integer, Integer> getMatchedValues() {
        return matchedValues;
    }
}
