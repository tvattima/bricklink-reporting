package com.vattima.bricklink.reporting.model;


import com.bricklink.api.ajax.model.v1.ItemForSale;
import com.bricklink.api.html.model.v2.WantedItem;
import com.google.common.util.concurrent.AtomicDouble;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Slf4j
public class StoreLotsForSale {

    @EqualsAndHashCode.Include
    private final WantedItem wantedItem;

    @EqualsAndHashCode.Include
    private final Store store;

    private double storeSalePrice = 0.0;
    private Set<ItemForSale> itemsForSale = new HashSet<>();

    public void addItemForSale(final ItemForSale itemForSale) {
        itemsForSale.add(itemForSale);
    }

    public boolean meetsMinimumQuantityWanted() {
        int totalQuantity = getTotalQuantity();
        boolean meetsMinimumQuantityWanted = wantedItem.getWantedQty() <= totalQuantity;
        if (!meetsMinimumQuantityWanted) {
            log.debug("Store {} total quantity {} {} wanted item minimum quantity {} for item {}", store.getStoreName(), totalQuantity, ((meetsMinimumQuantityWanted) ? "meets" : "doesn't meet"), wantedItem.getWantedQty(), wantedItem.getItemNo());
        }
        return meetsMinimumQuantityWanted;
    }

    public double getWantedItemCost() {
        AtomicDouble wantedItemCost = new AtomicDouble(0.0);
        AtomicInteger wantedQuantityRemaining = new AtomicInteger(wantedItem.getWantedQty());
        itemsForSale.stream()
                    .sorted(Comparator.comparing(ItemForSale::getN4Qty))
                    .forEach(ifs -> {
                        int quantityUsedFromThisItemForSale = Math.min(wantedQuantityRemaining.get(), ifs.getN4Qty());
                        wantedItemCost.addAndGet(ifs.getSalePrice() * quantityUsedFromThisItemForSale);
                        wantedQuantityRemaining.addAndGet(-1 * quantityUsedFromThisItemForSale);
                    });
        return Math.round(wantedItemCost.get() * 100.0) / 100.00;
    }

    public int getTotalQuantity() {
        return itemsForSale.stream()
                           .map(ItemForSale::getN4Qty)
                           .reduce(0, Integer::sum);
    }

    public double getSalePrice() {
        AtomicDouble totalCost = new AtomicDouble(0.0);
        AtomicInteger totalQuantity = new AtomicInteger(0);
        itemsForSale.forEach(ifs -> {
            totalQuantity.addAndGet(ifs.getN4Qty());
            totalCost.addAndGet(ifs.getSalePrice() * ifs.getN4Qty());
        });
        double avgPrice = totalCost.get() / totalQuantity.get();
        return Math.round(avgPrice * 10000.0) / 10000.0;
    }
}
