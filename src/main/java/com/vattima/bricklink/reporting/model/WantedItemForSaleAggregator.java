package com.vattima.bricklink.reporting.model;

import com.bricklink.api.ajax.model.v1.ItemForSale;
import com.vattima.bricklink.reporting.wantedlist.model.WantedItem;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class WantedItemForSaleAggregator {
    private final Set<StoreLotsForSale> storeLotsForSaleSet = new HashSet<>();
//    private final Map<WantedItem, Set<StoreLotsForSale>> wantedItemsForSale = new HashMap<>();
//    private final Map<Store, Set<StoreLotsForSale>> stores = new HashMap<>();

    public void addItemForSale(final WantedItem wantedItem, final ItemForSale itemForSale) {
        Store store = Store.fromItemForSale(itemForSale);
        storeLotsForSaleSet.stream()
                           .filter(slfs -> wantedItem.equals(slfs.getWantedItem()))
                           .filter(slfs -> slfs.getStore()
                                               .equals(store))
                           .findFirst()
                           .ifPresentOrElse(slfs ->
                                           slfs.addItemForSale(itemForSale),
                                   () -> {
                                       StoreLotsForSale storeLotsForSale = new StoreLotsForSale(wantedItem, store);
                                       storeLotsForSale.addItemForSale(itemForSale);
                                       storeLotsForSale.getItemsForSale()
                                                       .add(itemForSale);
                                       storeLotsForSaleSet.add(storeLotsForSale);
                                   });
    }

//    public void addItemForSale(WantedItem wantedItem, ItemForSale itemForSale) {
//        Set<StoreLotsForSale> storeLotsForSaleSet = wantedItemsForSale.computeIfAbsent(wantedItem, (s) -> new HashSet<>());
//        Store store = Store.fromItemForSale(itemForSale);
//        storeLotsForSaleSet.stream()
//                           .filter(storeLotForSale -> storeLotForSale.getStore()
//                                                                     .equals(store))
//                           .findFirst()
//                           .ifPresentOrElse(storeLotForSale -> {
//                               storeLotForSale.addItemForSale(itemForSale);
//                           }, () -> {
//                               StoreLotsForSale storeLotsForSale = new StoreLotsForSale(wantedItem, Store.fromItemForSale(itemForSale));
//                               storeLotsForSale.addItemForSale(itemForSale);
//                               storeLotsForSaleSet.add(storeLotsForSale);
//                           });
//        wantedItemsForSale.put(wantedItem, storeLotsForSaleSet);
//        storeLotsForSaleSet.forEach(slfs -> {
//            addStoreLotsForSale(store, slfs);
//        });
//    }

//    public void addStoreLotsForSale(final Store store, final StoreLotsForSale storeLotsForSale) {
//        stores.putIfAbsent(store, new HashSet<>());
//        stores.computeIfPresent(store, (s, slfs) -> {
//            slfs.add(storeLotsForSale);
//            return slfs;
//        });
//    }

    //    public Map<Store, Set<StoreLotsForSale>> getStores() {
//        return stores;
//    }
//
    public Map<Store, Set<StoreLotsForSale>> getStores() {
        storeLotsForSaleSet.stream()
                           .collect(Collectors.toMap(StoreLotsForSale::getStore,
                                   StoreLotsForSale::getItemsForSale,
                                   (oldItemsForSale, newItemsForSale) -> {
                                       newItemsForSale.addAll(oldItemsForSale);
                                       return newItemsForSale;
                                   }))
                .entrySet()
                .stream()
                .;
    }

//    public Map<WantedItem, Set<StoreLotsForSale>> getWantedItemsForSale() {
//        return wantedItemsForSale;
//    }

    public void filterStores() {
        Set<StoreLotsForSale> keepStoreLotsForSale = new HashSet<>();
        storeLotsForSaleSet.stream()
                           .map(StoreLotsForSale::getWantedItem)
                           .distinct()
                           .forEach(wi -> {
                               keepStoreLotsForSale.addAll(storeLotsForSaleSet.stream()
                                                                              .filter(slfs -> slfs.getWantedItem()
                                                                                                  .equals(wi))
                                                                              .filter(StoreLotsForSale::meetsMinimumQuantityWanted)
                                                                              .filter(StoreLotsForSale::meetsStoreMinimumBuy)
                                                                              .toList());
                           });
        storeLotsForSaleSet.retainAll(keepStoreLotsForSale);
    }

//    public Set<StoreLotsForSale> getWantedItemsForSale(final WantedItem wanteditem) {
//        return getWantedItemsForSale().computeIfAbsent(wanteditem, wi -> Collections.emptySet());
//    }

//    public Set<StoreLotsForSale> getWantedStoreLotsForSale(final WantedItem wanteditem, final int bottomNFilter) {
//        Set<StoreLotsForSale> storeLotsForSale = Optional.ofNullable(getWantedItemsForSale())
//                                                         .map(m -> m.get(wanteditem))
//                                                         .orElseGet(Collections::emptySet);
//        log.info("Wanted Item [{}] has [{}] store lots for sale", wanteditem, storeLotsForSale.size());
//        List<Double> bottomNPrices = storeLotsForSale.stream()
//                                                     .map(StoreLotsForSale::getSalePrice)
//                                                     .distinct()
//                                                     .sorted()
//                                                     .toList();
//        log.info("Wanted Item [{}] has [{}] bottomNPrices", wanteditem, bottomNPrices.size());
//        Double maxPrice = bottomNPrices.subList(0, Math.min(bottomNFilter, bottomNPrices.size()))
//                                       .stream()
//                                       .max(Double::compareTo)
//                                       .orElse(0.0D);
//        Set<StoreLotsForSale> wantedItemsForSaleCostTooHigh = storeLotsForSale.stream()
//                                                                              .filter(ifs -> ifs.getSalePrice() > maxPrice)
//                                                                              .collect(Collectors.toSet());
//        wantedItemsForSaleCostTooHigh.forEach(slfs -> {
//            log.info("Wanted Item [{}] has StoreLotsForSale from Store [{}] with sale price [{}] is greater than maximum price of [{}]", wanteditem, slfs.getStore()
//                                                                                                                                                         .getStoreName(), slfs.getSalePrice(), maxPrice);
//        });
//        Set<StoreLotsForSale> wantedItemsForSale = storeLotsForSale.stream()
//                                                                   .filter(ifs -> ifs.getSalePrice() <= maxPrice)
//                                                                   .collect(Collectors.toSet());
//        log.info("Wanted Item [{}] has [{}] StoreLotsForSale with sale price <= [{}]", wanteditem, wantedItemsForSale.size(), maxPrice);
//        return wantedItemsForSale;
//    }

//    public void filterStores() {
//        wantedItemsForSale.entrySet()
//                          .forEach(wantedItem -> {
//                              Set<StoreLotsForSale> storeLotsForSale = wantedItem.getValue();
//                              Set<StoreLotsForSale> updatedStoreLotsForSale = storeLotsForSale.stream()
//                                                                                              .filter(StoreLotsForSale::meetsMinimumQuantityWanted)
//                                                                                              .collect(Collectors.toSet());
//                              log.info("Before filter StoreLotsForSale size {} / After filter StoreLotsForSale size {} for Item {}", storeLotsForSale.size(), updatedStoreLotsForSale.size(), wantedItem.getKey()
//                                                                                                                                                                                                        .getItemId());
//                              storeLotsForSale.retainAll(updatedStoreLotsForSale);
//                          });
//
//        Set<Store> storesNotMeetingMinimumBuy = new HashSet<>();
//        stores.forEach((store, storeLotsForSale) -> {
//            final AtomicDouble storeTotalOrderCost = new AtomicDouble(0.0);
//            storeLotsForSale.forEach(slfs -> {
//                WantedItem wantedItem = slfs.getWantedItem();
//                final AtomicInteger wantedQuantityRemaining = new AtomicInteger(wantedItem.getQuantity());
//                slfs.getItemsForSale()
//                    .stream()
//                    .sorted(Comparator.comparing(ItemForSale::getN4Qty))
//                    .forEach(ifs -> {
//                        int quantityUsedFromThisItemForSale = Math.min(wantedQuantityRemaining.get(), ifs.getN4Qty());
//                        storeTotalOrderCost.addAndGet(ifs.getSalePrice() * quantityUsedFromThisItemForSale);
//                        wantedQuantityRemaining.addAndGet(-1 * quantityUsedFromThisItemForSale);
//                    });
//            });
//            store.setTotalOrderCost(storeTotalOrderCost.get());
//            if (storeTotalOrderCost.get() < store.getMinBuy()) {
//                log.info("Store {} total purchase of {} does not meet minimum buy of {}", store.getStoreName(), storeTotalOrderCost, store.getMinBuy());
//                storesNotMeetingMinimumBuy.add(store);
//            }
//        });
//        storesNotMeetingMinimumBuy.forEach(stores::remove);
//    }
}
