package com.vattima.bricklink.reporting.model;

import com.vattima.bricklink.reporting.wantedlist.model.WantedItem;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class Order {
    private final Store store;
    private Set<StoreWantedItemLotsForSale> itemsForSale = new HashSet<>();

    private boolean containsWantedItem(final WantedItem wanteditem) {
        return itemsForSale.stream().anyMatch(ifs -> ifs.getWantedItem().equals(wanteditem));
    }

    private boolean containsAllWantedItem(final Set<WantedItem> wanteditems) {
        return itemsForSale.stream().map(StoreWantedItemLotsForSale::getWantedItem).collect(Collectors.toSet()).containsAll(wanteditems);
    }
}
