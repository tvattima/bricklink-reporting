package com.vattima.bricklink.reporting.model;

import com.bricklink.api.ajax.model.v1.ItemForSale;
import com.bricklink.api.html.model.v2.WantedItem;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public class WantedItemForSaleAggregator {
    private final Set<StoreLotsForSale> storeLotsForSaleSet = new HashSet<>();

    public void addItemForSale(final WantedItem wantedItem, final ItemForSale itemForSale) {
        Store store = Store.fromItemForSale(itemForSale);
        storeLotsForSaleSet.stream()
                           .filter(slfs -> wantedItem.equals(slfs.getWantedItem()))
                           .filter(slfs -> slfs.getStore()
                                               .equals(store))
                           .findFirst()
                           .ifPresentOrElse(slfs -> {
                                       log.debug("Adding Item {} Color {} Condition {} Quantity {} to store {} [{}]", wantedItem.getItemNo(), itemForSale.getStrColor(), itemForSale.getCodeNew(), itemForSale.getN4Qty(), store.getSellerUsername(), store.getStoreName());
                                       slfs.addItemForSale(itemForSale);
                                   },
                                   () -> {
                                       StoreLotsForSale storeLotsForSale = new StoreLotsForSale(wantedItem, store);
                                       storeLotsForSale.addItemForSale(itemForSale);
                                       storeLotsForSale.getItemsForSale()
                                                       .add(itemForSale);
                                       storeLotsForSaleSet.add(storeLotsForSale);
                                   });
    }

    public Map<Store, Set<StoreLotsForSale>> getStores() {
        return storeLotsForSaleSet.stream()
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

    public Map<WantedItem, Set<StoreLotsForSale>> getWantedItemsForSale() {
        return storeLotsForSaleSet.stream()
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
    }

    public void filterStores() {
        log.info("Filtering stores...");
        filterStoresNotMeetingMinimumBuy();
    }

    public void filterStoresNotMeetingMinimumBuy() {
        Set<StoreLotsForSale> keepStoreLotsForSale = new HashSet<>();

        storeLotsForSaleSet.stream()
                           .map(StoreLotsForSale::getWantedItem)
                           .distinct()
                           .forEach(wi -> {
                               keepStoreLotsForSale.addAll(storeLotsForSaleSet.stream()
                                                                              .filter(slfs -> slfs.getWantedItem()
                                                                                                  .equals(wi))
                                                                              .filter(StoreLotsForSale::meetsMinimumQuantityWanted)
                                                                              .toList());
                           });
        int storeLotsForSaleOriginalSize = storeLotsForSaleSet.size();
        storeLotsForSaleSet.retainAll(keepStoreLotsForSale);
        log.debug("Retaining {} StoreLotsForSale, Removed {} StoreLotsForSale", keepStoreLotsForSale.size(), (storeLotsForSaleOriginalSize - keepStoreLotsForSale.size()));
    }

    public boolean meetsStoreMinimumBuy(final Store store, final Map<Store, Set<StoreLotsForSale>> storeMap) {
        final Set<StoreLotsForSale> storeLotsForSale = storeMap.get(store);
        if (null == storeLotsForSale) {
            return false;
        } else {
            Double storePurchaseAmount = storePurchaseAmount(store, storeMap);
            return store.getMinBuy() <= storePurchaseAmount;
        }
    }

    public double storePurchaseAmount(final Store store, final Map<Store, Set<StoreLotsForSale>> storeMap) {
        final Set<StoreLotsForSale> storeLotsForSale = storeMap.get(store);
        if (null == storeLotsForSale) {
            return 0.0;
        } else {
            double storePurchaseAmount = storeLotsForSale.stream()
                                                         .map(StoreLotsForSale::getWantedItemCost)
                                                         .reduce(0.0, Double::sum);
            return (storePurchaseAmount * 100.0) / 100.0;
        }
    }
}
