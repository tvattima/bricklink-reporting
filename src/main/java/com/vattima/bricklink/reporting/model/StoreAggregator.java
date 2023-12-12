package com.vattima.bricklink.reporting.model;

import com.bricklink.api.ajax.model.v1.ItemForSale;
import com.google.common.util.concurrent.AtomicDouble;
import com.vattima.bricklink.reporting.wantedlist.model.WantedItem;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class StoreAggregator {
    private final Map<Store, Set<StoreLotsForSale>> stores = new HashMap<>();

    public void addItemForSale(final WantedItem wantedItem, final StoreLotsForSale storeLotsForSale) {
        Store store = storeLotsForSale.getStore();
        Set<StoreLotsForSale> storeLotsForSaleSet = stores.computeIfAbsent(store, (s) -> new HashSet<>());
        storeLotsForSaleSet.add(storeLotsForSale);
        stores.put(store, storeLotsForSaleSet);
    }

    public Map<Store, Set<StoreLotsForSale>> getStores() {
        return stores;
    }

    public Set<StoreLotsForSale> getStoreLotsForSale(final Store store) {
        return getStores().computeIfAbsent(store, s -> Collections.emptySet());
    }

    public Map<Store, Set<StoreLotsForSale>> getStores(final Set<Store> storesToExclude) {
        return storesToExclude.stream()
                              .filter(stores::containsKey)
                              .collect(Collectors.toMap(Function.identity(), stores::get));
    }

    public void filterStores() {
        Set<Store> storesNotMeetingMinimumBuy = new HashSet<>();
        stores.forEach((store, storeLotsForSale) -> {
            final AtomicDouble storeTotalOrderCost = new AtomicDouble(0.0);
            storeLotsForSale.forEach(slfs -> {
                WantedItem wantedItem = slfs.getWantedItem();
                final AtomicInteger wantedQuantityRemaining = new AtomicInteger(wantedItem.getQuantity());
                slfs.getItemsForSale().stream()
                    .sorted(Comparator.comparing(ItemForSale::getN4Qty))
                    .forEach(ifs -> {
                        int quantityUsedFromThisItemForSale = Math.min(wantedQuantityRemaining.get(), ifs.getN4Qty());
                        storeTotalOrderCost.addAndGet(ifs.getSalePrice() * quantityUsedFromThisItemForSale);
                        wantedQuantityRemaining.addAndGet(-1 * quantityUsedFromThisItemForSale);
                    });
            });
            store.setTotalOrderCost(storeTotalOrderCost.get());
            if (storeTotalOrderCost.get() < store.getMinBuy()) {
                log.info("Store {} total purchase of {} does not meet minimum buy of {}", store.getStoreName(), storeTotalOrderCost, store.getMinBuy());
                storesNotMeetingMinimumBuy.add(store);
            }
        });
        storesNotMeetingMinimumBuy.forEach(stores::remove);
    }
}