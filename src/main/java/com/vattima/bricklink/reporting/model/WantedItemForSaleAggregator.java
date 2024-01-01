package com.vattima.bricklink.reporting.model;

import com.bricklink.api.ajax.model.v1.ItemForSale;
import com.bricklink.api.html.model.v2.WantedItem;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class WantedItemForSaleAggregator {
    private final Map<String, Store> stores = new HashMap<>();
    private final Set<WantedItem> wantedItems = new HashSet<>();
    private final Set<StoreWantedItemLotsForSale> storeWantedItemLotsForSaleSet = new HashSet<>();

    public void addItemForSale(final WantedItem wantedItem, final ItemForSale itemForSale) {
        wantedItems.add(wantedItem);
        Store store = stores.computeIfAbsent(itemForSale.getStrStorename(), storeName ->Store.fromItemForSale(itemForSale));
        storeWantedItemLotsForSaleSet.stream()
                                     .filter(slfs -> wantedItem.equals(slfs.getWantedItem()))
                                     .filter(slfs -> slfs.getStore()
                                                         .equals(store))
                                     .findFirst()
                                     .ifPresentOrElse(slfs -> {
                                                 log.debug("Adding Item {} Color {} Condition {} Quantity {} to store {} [{}]", wantedItem.getItemNo(), itemForSale.getStrColor(), itemForSale.getCodeNew(), itemForSale.getN4Qty(), store.getSellerUsername(), store.getStoreName());
                                                 slfs.addItemForSale(itemForSale);
                                             },
                                             () -> {
                                                 StoreWantedItemLotsForSale storeWantedItemLotsForSale = new StoreWantedItemLotsForSale(wantedItem, store);
                                                 storeWantedItemLotsForSale.addItemForSale(itemForSale);
                                                 storeWantedItemLotsForSale.getItemsForSale()
                                                                           .add(itemForSale);
                                                 storeWantedItemLotsForSaleSet.add(storeWantedItemLotsForSale);
                                             });
    }

    public Map<Store, Set<StoreWantedItemLotsForSale>> getStores() {
        return storeWantedItemLotsForSaleSet.stream()
                                            .reduce(new HashMap<>(),
                                                    (storeMap, storeLotsForSale) -> {
                                                        storeMap.computeIfAbsent(storeLotsForSale.getStore(), k -> new HashSet<>())
                                                                .add(storeLotsForSale);
                                                        return storeMap;
                                                    },
                                                    (storeMap1, storeMap2) -> {
                                                        storeMap2.forEach((store, storeLotsForSaleSet) -> storeMap1.merge(store, storeLotsForSaleSet, (storeLotsForSaleSet1, storeLotsForSaleSet2) -> {
                                                            storeLotsForSaleSet1.addAll(storeLotsForSaleSet2);
                                                            return storeLotsForSaleSet1;
                                                        }));
                                                        return storeMap1;
                                                    });
    }

    public Map<WantedItem, Set<StoreWantedItemLotsForSale>> getWantedItemsForSale() {
        return storeWantedItemLotsForSaleSet.stream()
                                            .reduce(new HashMap<>(),
                                                    (wantedItemMap, storeLotsForSale) -> {
                                                        wantedItemMap.computeIfAbsent(storeLotsForSale.getWantedItem(), k -> new HashSet<>())
                                                                     .add(storeLotsForSale);
                                                        return wantedItemMap;
                                                    },
                                                    (wantedItemMap1, wantedItemMap2) -> {
                                                        wantedItemMap2.forEach((wantedItem, storeLotsForSaleSet) -> wantedItemMap1.merge(wantedItem, storeLotsForSaleSet, (storeLotsForSaleSet1, storeLotsForSaleSet2) -> {
                                                            storeLotsForSaleSet1.addAll(storeLotsForSaleSet2);
                                                            return storeLotsForSaleSet1;
                                                        }));
                                                        return wantedItemMap1;
                                                    });
    }

    public void analyze() {
        log.info("Analyzing stores...");
        computeStoreTotalCost();
    }

    private void computeStoreTotalCost() {
        final Map<Store, Set<StoreWantedItemLotsForSale>> storeMap = getStores();
        storeMap.forEach((store, storeLotsForSale) -> {
            double amount = storePurchaseAmount(store, storeMap);
            store.setTotalOrderCost(amount);
        });
    }

    public void filterStores() {
        log.info("Filtering stores...");
        filterStoresNotMeetingMinimumBuy();
        //filterStoresAboveLowestPrices();
    }

    public void filterStoresAboveLowestPrices() {
        Set<StoreWantedItemLotsForSale> storeWantedItemLotsForSaleToRetain = new HashSet<>();
        getWantedItemsForSale()
                .forEach((wantedItem, wantedItemLotsForSale) -> {
                    double wantedItemMaxPrice = wantedItemLotsForSale.stream()
                                                                     .map(StoreWantedItemLotsForSale::getSalePrice)
                                                                     .sorted(Double::compare)
                                                                     .limit(10)
                                                                     .max(Double::compare)
                                                                     .orElse(0.0);
                    Set<StoreWantedItemLotsForSale> keepStoreWantedItemLotsForSale = wantedItemLotsForSale
                            .stream()
                            .filter(wilfs -> wilfs.getSalePrice() <= wantedItemMaxPrice)
                            .collect(Collectors.toSet());
                    log.info("Part [{}]::[{}] Color {} Condition {} has [{}] stores under the max price of [{}]", wantedItem.getItemName(), wantedItem.getItemNo(), wantedItem.getColorName(), wantedItem.getWantedNew(), keepStoreWantedItemLotsForSale.stream()
                                                                                                                                                                                                                                                        .map(StoreWantedItemLotsForSale::getStore)
                                                                                                                                                                                                                                                        .distinct()
                                                                                                                                                                                                                                                        .count(), wantedItemMaxPrice);
                    storeWantedItemLotsForSaleToRetain.addAll(keepStoreWantedItemLotsForSale);
                });
        int storeLotsForSaleOriginalSize = storeWantedItemLotsForSaleSet.size();
        storeWantedItemLotsForSaleSet.retainAll(storeWantedItemLotsForSaleToRetain);
        log.debug("Retaining {} StoreLotsForSale, Removed {} StoreLotsForSale", storeWantedItemLotsForSaleToRetain.size(), (storeLotsForSaleOriginalSize - storeWantedItemLotsForSaleToRetain.size()));
    }

    public void filterStoresNotMeetingMinimumBuy() {
        Set<StoreWantedItemLotsForSale> keepStoreWantedItemLotsForSale = new HashSet<>();

        storeWantedItemLotsForSaleSet.stream()
                                     .map(StoreWantedItemLotsForSale::getWantedItem)
                                     .distinct()
                                     .forEach(wi -> {
                                         keepStoreWantedItemLotsForSale.addAll(storeWantedItemLotsForSaleSet.stream()
                                                                                                            .filter(slfs -> slfs.getWantedItem()
                                                                                                                                .equals(wi))
                                                                                                            .filter(StoreWantedItemLotsForSale::meetsQuantityWanted)
                                                                                                            .toList());
                                     });
        int storeLotsForSaleOriginalSize = storeWantedItemLotsForSaleSet.size();
        storeWantedItemLotsForSaleSet.retainAll(keepStoreWantedItemLotsForSale);
        log.debug("Retaining {} StoreLotsForSale, Removed {} StoreLotsForSale", keepStoreWantedItemLotsForSale.size(), (storeLotsForSaleOriginalSize - keepStoreWantedItemLotsForSale.size()));
    }

    public boolean meetsStoreMinimumBuy(final Store store, final Map<Store, Set<StoreWantedItemLotsForSale>> storeMap) {
        final Set<StoreWantedItemLotsForSale> storeWantedItemLotsForSale = storeMap.get(store);
        if (null == storeWantedItemLotsForSale) {
            return false;
        } else {
            Double storePurchaseAmount = storePurchaseAmount(store, storeMap);
            return store.getMinBuy() <= storePurchaseAmount;
        }
    }

    public double storePurchaseAmount(final Store store, final Map<Store, Set<StoreWantedItemLotsForSale>> storeMap) {
        final Set<StoreWantedItemLotsForSale> storeWantedItemLotsForSale = storeMap.get(store);
        if (null == storeWantedItemLotsForSale) {
            return 0.0;
        } else {
            double storePurchaseAmount = storeWantedItemLotsForSale.stream()
                                                                   .map(StoreWantedItemLotsForSale::getWantedItemCost)
                                                                   .reduce(0.0, Double::sum);
            return (storePurchaseAmount * 100.0) / 100.0;
        }
    }
}
