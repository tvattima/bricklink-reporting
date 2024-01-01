package com.vattima.bricklink.reporting.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OrderGroup {
    Map<Store, Set<StoreWantedItemLotsForSale>> orders = new HashMap();

    public void addStoreForSaleItem(final Store store, final StoreWantedItemLotsForSale storeWantedItemLotsForSale) {
        Set<StoreWantedItemLotsForSale> itemsForSaleHolders = orders.computeIfAbsent(store, (s) -> new HashSet<>());
        itemsForSaleHolders.add(storeWantedItemLotsForSale);
        orders.put(store, itemsForSaleHolders);
    }

//    public Double getOrderTotal(final Store store) {
//        return orders.getOrDefault(store, Collections.emptySet())
//                     .stream()
//                     .mapToDouble(v -> v.getWantedItem()
//                                        .getQuantity() * v.getItemForSale()
//                                                          .getSalePrice())
//                     .sum();
//    }
}
