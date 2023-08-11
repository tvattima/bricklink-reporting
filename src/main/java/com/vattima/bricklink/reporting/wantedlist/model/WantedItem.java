package com.vattima.bricklink.reporting.wantedlist.model;

import com.vattima.bricklink.reporting.model.Color;
import com.vattima.bricklink.reporting.model.Condition;
import com.vattima.bricklink.reporting.model.Item;
import com.vattima.bricklink.reporting.model.ItemType;
import com.vattima.bricklink.reporting.model.PartItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class WantedItem implements Item {
    @EqualsAndHashCode.Include
    private PartItem item;

    @EqualsAndHashCode.Include
    private Color color;

    @EqualsAndHashCode.Include
    private Condition condition;

    private int wantedItemListId;

    private int quantity;

    private int quantityFilled;

    public boolean filled() {
        return quantity == quantityFilled;
    }

    public int applyQuantity(int quantityToApply) {
        int quantityApplied = Math.min((quantity - quantityFilled), quantityToApply);
        quantityFilled = quantityFilled + quantityApplied;
        return quantityApplied;
    }

    public boolean matches(String itemid, int colorId, String conditionCode) {
        return this.equals(WantedItem.builder()
                                     .id(itemid)
                                     .color(colorId)
                                     .condition(conditionCode)
                                     .build()) || this.equals(WantedItem.builder()
                                                                        .id(itemid)
                                                                        .color(colorId)
                                                                        .condition(Condition.UNSPECIFIED.getConditionCode())
                                                                        .build());
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public ItemType getItemType() {
        return item.getItemType();
    }

    @Override
    public String getItemId() {
        return item.getItemId();
    }

    public int getColorId() {
        return color.getColorId();
    }

    public String getConditionCode() {
        return condition.getConditionCode();
    }

    public static WantedItem of(String itemId, int colorId, String conditionCode, int quantity) {
        return WantedItem.builder()
                         .id(itemId)
                         .color(colorId)
                         .condition(conditionCode)
                         .quantity(quantity)
                         .build();
    }

    public static class Builder {
        private final WantedItem wantedItem = new WantedItem();

        public Builder id(String itemid) {
            PartItem partItem = new PartItem();
            partItem.setItemId(itemid);
            wantedItem.setItem(partItem);
            return this;
        }

        public Builder color(int colorId) {
            wantedItem.setColor(new Color(colorId));
            return this;
        }

        public Builder condition(String conditionCode) {
            wantedItem.setCondition(Condition.of(conditionCode));
            return this;
        }

        public Builder quantity(int quantity) {
            wantedItem.setQuantity(quantity);
            return this;
        }

        public WantedItem build() {
            return wantedItem;
        }
    }
}
