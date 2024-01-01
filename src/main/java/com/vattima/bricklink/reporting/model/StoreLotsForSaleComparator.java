package com.vattima.bricklink.reporting.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;

public class StoreLotsForSaleComparator implements Comparator<Map.Entry<Store, Set<StoreWantedItemLotsForSale>>> {
    Comparator<StoreValueHolder> comparator = Comparator.comparingInt(StoreValueHolder::getSize)
                                                        .thenComparingDouble(StoreValueHolder::getCost)
                                                        .reversed();

    @Override
    public int compare(Map.Entry<Store, Set<StoreWantedItemLotsForSale>> store1, Map.Entry<Store, Set<StoreWantedItemLotsForSale>> store2) {
        return comparator.compare(StoreValueHolder.fromMapEntry(store1), StoreValueHolder.fromMapEntry(store2));
    }

    @Builder
    @Getter
    private static class StoreValueHolder {
        private final int size;
        private final double cost;

        public static StoreValueHolder fromMapEntry(Map.Entry<Store, Set<StoreWantedItemLotsForSale>> e) {
            return StoreValueHolder.builder()
                                   .size(e.getValue()
                                          .size())
                                   .cost(e.getKey()
                                          .getTotalOrderCost())
                                   .build();
        }
    }
}
