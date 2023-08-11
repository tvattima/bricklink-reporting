package com.vattima.bricklink.reporting.model;


import com.bricklink.api.ajax.model.v1.ItemForSale;
import com.google.common.util.concurrent.AtomicDouble;
import com.vattima.bricklink.reporting.wantedlist.model.WantedItem;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Slf4j
public class StoreLotsForSale {
    private final WantedItem wantedItem;
    private final Store store;
    private double storeSalePrice = 0.0;
    private Set<ItemForSale> itemsForSale = new HashSet<>();

    public void addItemForSale(final ItemForSale itemForSale) {
          itemsForSale.add(itemForSale);
//        double price = Precision.round(itemForSale.getSalePrice(), 2);
//        double lowestPrice = itemsForSale.stream().map(ifs -> Precision.round(ifs.getSalePrice(),2)).min(Double::compare).orElse(0.0);
//        if (storeSalePrice == 0.0 || price <= lowestPrice) {
//            if (price < lowestPrice) {
//                log.info("Replacing price of {} with lower price {} for item {} for Store {}", price, lowestPrice, wantedItem, store);
//                itemsForSale = new HashSet<>();
//            } else if (Double.compare(price, lowestPrice) == 0) {
//                log.info("Price of {} is equal to lower price {} for item {} for Store {}", price, lowestPrice, wantedItem, store);
//            }
//            itemsForSale.add(itemForSale);
//            storeSalePrice = price;
//        }
    }

    public boolean meetsMinimumQuantityWanted() {
        int totalQuantity = getTotalQuantity();
        boolean meetsMinimumQuantityWanted = wantedItem.getQuantity() <= totalQuantity;
        if (!meetsMinimumQuantityWanted) {
            log.info("Store {} total quantity {} {} wanted item minimum quantity {} for item {}", store.getStoreName(), totalQuantity, ((meetsMinimumQuantityWanted) ? "meets" : "doesn't meet"), wantedItem.getQuantity(), wantedItem.getItem());
        }
        return meetsMinimumQuantityWanted;
    }

    public boolean meetsStoreMinimumBuy() {
        double wantedItemCost = getWantedItemCost();
        boolean meetsStoreMinimumBuy = store.getMinBuy() <= wantedItemCost;
        if (!meetsStoreMinimumBuy) {
            log.info("Wanted Item cost {} {} Store {} minimum buy of {} for item {}", wantedItemCost, ((meetsStoreMinimumBuy)?"meets":" doesn't meet"), store.getStoreName(), store.getMinBuy(), wantedItem.getItem());
        }
        return meetsStoreMinimumBuy;
    }

    public double getWantedItemCost() {
        AtomicDouble wantedItemCost = new AtomicDouble(0.0);
        AtomicInteger wantedQuantityRemaining = new AtomicInteger(wantedItem.getQuantity());
        itemsForSale.stream()
                    .sorted(Comparator.comparing(ItemForSale::getN4Qty))
                    .forEach(ifs -> {
                        int quantityUsedFromThisItemForSale = Math.min(wantedQuantityRemaining.get(), ifs.getN4Qty());
                        wantedItemCost.addAndGet(ifs.getSalePrice() * quantityUsedFromThisItemForSale);
                        wantedQuantityRemaining.addAndGet(-1 * quantityUsedFromThisItemForSale);
        });
        return wantedItemCost.get();
    }

    public int getTotalQuantity() {
        AtomicInteger totalQuantity = new AtomicInteger(0);
        itemsForSale.forEach(ifs -> {
            totalQuantity.addAndGet(ifs.getN4Qty());
        });
        return totalQuantity.get();
    }

    public double getSalePrice() {
        AtomicDouble totalCost = new AtomicDouble(0.0);
        AtomicInteger totalQuantity = new AtomicInteger(0);
        itemsForSale.forEach(ifs -> {
            totalQuantity.addAndGet(ifs.getN4Qty());
            totalCost.addAndGet(ifs.getSalePrice() * ifs.getN4Qty());
        });
        double avgPrice =  totalCost.get()/totalQuantity.get();
        return avgPrice;
    }
}
