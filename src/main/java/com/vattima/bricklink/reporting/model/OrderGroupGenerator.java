package com.vattima.bricklink.reporting.model;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class OrderGroupGenerator {
    private final StoreAggregator storeAggregator;
    private List<OrderGroup> orderGroups = new ArrayList();

    public void computeOrderGroups(final int maxOrderGroups) {
        while (orderGroups.size() <= maxOrderGroups) {

        }
    }
}
