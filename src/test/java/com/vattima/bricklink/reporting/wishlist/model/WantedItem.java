package com.vattima.bricklink.reporting.wishlist.model;

import com.vattima.bricklink.reporting.model.Color;
import com.vattima.bricklink.reporting.model.Condition;
import com.vattima.bricklink.reporting.model.Item;
import com.vattima.bricklink.reporting.model.ItemType;
import com.vattima.bricklink.reporting.model.PartItem;
import lombok.Data;

@Data
public class WantedItem implements Item {
    private PartItem item;
    private Color color;
    private Condition condition;
    private int quantity;

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

    public static class Builder {
        private WantedItem wantedItem = new WantedItem();

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
