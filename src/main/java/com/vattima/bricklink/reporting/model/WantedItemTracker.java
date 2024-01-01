package com.vattima.bricklink.reporting.model;

import com.bricklink.api.html.model.v2.WantedItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WantedItemTracker {
    @Getter
    private final WantedItem wantedItem;
}
