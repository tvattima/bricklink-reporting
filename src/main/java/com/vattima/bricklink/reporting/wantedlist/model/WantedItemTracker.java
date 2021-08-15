package com.vattima.bricklink.reporting.wantedlist.model;

import lombok.Data;

import java.util.List;

@Data
public class WantedItemTracker {
    private WantedItem wantedItem;
    private List<PartOrderItem> partOrderItems;
}
