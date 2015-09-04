package net.webcumo.dealbook.entity;

import java.util.Comparator;

public class AskComparator implements Comparator<Integer> {
    @Override
    public int compare(Integer o1, Integer o2) {
        return o2 - o1;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AskComparator;
    }
}
