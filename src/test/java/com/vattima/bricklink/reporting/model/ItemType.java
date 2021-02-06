package com.vattima.bricklink.reporting.model;

public enum ItemType {
    SET("S"),
    PART("P");

    private String itemType;

    ItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemType() {
        return itemType;
    }
}
