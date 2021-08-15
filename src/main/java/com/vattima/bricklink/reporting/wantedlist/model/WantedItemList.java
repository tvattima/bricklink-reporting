package com.vattima.bricklink.reporting.wantedlist.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class WantedItemList {
    private final int id;
    private final String name;
    private List<WantedItem> items = new ArrayList<>();

    public void addWantedItem(WantedItem wantedItem) {
        wantedItem.setWantedItemListId(this.id);
        items.add(wantedItem);
    }
}
