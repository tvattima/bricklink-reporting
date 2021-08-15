package com.vattima.bricklink.reporting.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
public class PartItem implements Item {
    private String itemId;

    @Override
    public ItemType getItemType() {
        return ItemType.PART;
    }

    @Override
    public String getItemId() {
        return itemId;
    }
}
