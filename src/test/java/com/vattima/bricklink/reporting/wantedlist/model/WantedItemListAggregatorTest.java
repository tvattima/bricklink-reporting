package com.vattima.bricklink.reporting.wantedlist.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WantedItemListAggregatorTest {

    @Test
    void getAllPartWantedItems() {
        WantedItemListAggregator wantedItemListAggregator = new WantedItemListAggregator(0, "Aggregate Wanted Item List");
        WantedItemList wantedItemList1 = new WantedItemList(1, "Wanted Item List 1");
        WantedItemList wantedItemList2 = new WantedItemList(2, "Wanted Item List 2");
        WantedItemList wantedItemList3 = new WantedItemList(2, "Wanted Item List 3");
        wantedItemList1.addWantedItem(WantedItem.of("3062b", 1, "X", 7));
        wantedItemList1.addWantedItem(WantedItem.of("3062b", 2, "X", 5));
        wantedItemList1.addWantedItem(WantedItem.of("1111", 3, "N", 2));

        wantedItemList2.addWantedItem(WantedItem.of("3062b", 1, "X", 6));
        wantedItemList2.addWantedItem(WantedItem.of("3062b", 2, "X", 4));
        wantedItemList2.addWantedItem(WantedItem.of("1111", 3, "X", 10));
        wantedItemList2.addWantedItem(WantedItem.of("2222", 1, "N", 20));

        wantedItemList3.addWantedItem(WantedItem.of("3062b", 1, "X", 20));
        wantedItemList3.addWantedItem(WantedItem.of("2222", 1, "N", 5));
        wantedItemList3.addWantedItem(WantedItem.of("3333", 2, "U", 107));

        wantedItemListAggregator.appendWantedItemList(wantedItemList1);
        wantedItemListAggregator.appendWantedItemList(wantedItemList2);
        wantedItemListAggregator.appendWantedItemList(wantedItemList3);

        List<WantedItem> wantedItemList = wantedItemListAggregator.getAllPartWantedItems("3062b", 1, "X");
        wantedItemList.forEach(System.out::println);
    }
}