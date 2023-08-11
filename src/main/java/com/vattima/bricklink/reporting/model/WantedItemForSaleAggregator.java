package com.vattima.bricklink.reporting.model;

import com.bricklink.api.ajax.model.v1.ItemForSale;
import com.vattima.bricklink.reporting.wantedlist.model.WantedItem;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class WantedItemForSaleAggregator {
    private final Map<WantedItem, Set<StoreLotsForSale>> wantedItemsForSale = new HashMap<>();

    public void addItemForSale(WantedItem wantedItem, ItemForSale itemForSale) {
        Set<StoreLotsForSale> storeLotsForSaleSet = wantedItemsForSale.computeIfAbsent(wantedItem, (s) -> new HashSet<>());

        Store store = Store.fromItemForSale(itemForSale);
        storeLotsForSaleSet.stream().filter(storeLotForSale -> storeLotForSale.getStore().equals(store)).findFirst().ifPresentOrElse(storeLotForSale -> {
            storeLotForSale.addItemForSale(itemForSale);
        }, () -> {
            StoreLotsForSale storeLotsForSale = new StoreLotsForSale(wantedItem, Store.fromItemForSale(itemForSale));
            storeLotsForSale.addItemForSale(itemForSale);
            storeLotsForSaleSet.add(storeLotsForSale);
        });
        wantedItemsForSale.put(wantedItem, storeLotsForSaleSet);
    }

    public Map<WantedItem, Set<StoreLotsForSale>> getWantedItemsForSale() {
        return wantedItemsForSale;
    }

    public Set<StoreLotsForSale> getWantedItemsForSale(final WantedItem wanteditem) {
        return getWantedItemsForSale().computeIfAbsent(wanteditem, wi -> Collections.emptySet());
    }

    public Set<StoreLotsForSale> getWantedStoreLotsForSale(final WantedItem wanteditem, final int bottomNFilter) {
        Set<StoreLotsForSale> storeLotsForSale = Optional.ofNullable(getWantedItemsForSale())
                                                         .map(m -> m.get(wanteditem))
                                                         .orElseGet(Collections::emptySet);
        List<Double> bottomNPrices = storeLotsForSale.stream()
                                                     .map(StoreLotsForSale::getSalePrice)
                                                     .distinct()
                                                     .sorted()
                                                     .toList();
        Double maxPrice = bottomNPrices.subList(0, Math.min(bottomNFilter, bottomNPrices.size()))
                                       .stream()
                                       .max(Double::compareTo)
                                       .orElse(0.0D);
        return storeLotsForSale.stream()
                               .filter(ifs -> ifs.getSalePrice() <= maxPrice)
                               .collect(Collectors.toSet());
    }

    public void filterStores() {
        wantedItemsForSale.entrySet().forEach(e -> {
            Set<StoreLotsForSale> storeLotsForSale = e.getValue();
            Set<StoreLotsForSale> updatedStoreLotsForSale = storeLotsForSale.stream()
                                                                            .filter(StoreLotsForSale::meetsMinimumQuantityWanted)
                                                                            .collect(Collectors.toSet());
            log.info("Before filter StoreLotsForSale size {} / After filter StoreLotsForSale size {} for Item {}", storeLotsForSale.size(), updatedStoreLotsForSale.size(), e.getKey().getItemId());
            storeLotsForSale.retainAll(updatedStoreLotsForSale);
        });
    }
}
