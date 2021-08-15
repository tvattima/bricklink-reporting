package com.vattima.bricklink.reporting.wantedlist.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@RequiredArgsConstructor
public class WantedItemListAggregator {
    private final int id;
    private final String name;
    private List<WantedItemList> items = new ArrayList<>();

    public void appendWantedItemList(WantedItemList wantedItemList) {
        items.add(wantedItemList);
    }

    public List<WantedItem> getAllPartWantedItems(String itemId, int colorId, String conditionCode) {
        return items.stream()
                    .flatMap(wil -> wil.getItems()
                                       .stream())
                    .filter(wi -> wi.matches(itemId, colorId, conditionCode))
                    .collect(Collectors.toList());
    }

    public void report() {
        items.stream()
             .flatMap(wil -> wil.getItems()
                                .stream())
             .forEach(System.out::println);
    }
}
