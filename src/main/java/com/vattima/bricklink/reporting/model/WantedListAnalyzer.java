package com.vattima.bricklink.reporting.model;

import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class WantedListAnalyzer {
    private final Set<WantedItemTracker> wantedItemTrackers = new HashSet<>();

    public void analyze(final Set<StoreWantedItemLotsForSale> storeWantedItemLotsForSaleSet) {

    }
}
