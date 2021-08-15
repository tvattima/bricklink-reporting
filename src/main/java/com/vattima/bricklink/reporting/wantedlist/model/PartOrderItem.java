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
public class PartOrderItem implements Item {
    @EqualsAndHashCode.Include
    private PartItem item;

    @EqualsAndHashCode.Include
    private Color color;

    @EqualsAndHashCode.Include
    private Condition condition;

    private int orderId;

    private int quantity;

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        quantityLeft = quantity;
    }

    private int quantityLeft;

    public void applyQuantity(int quantityToApply) {
        quantityLeft = quantityLeft - Math.min(quantityToApply, quantityLeft);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static PartOrderItem of(String itemId, int colorId, String conditionCode, int quantity) {
        return PartOrderItem.builder()
                            .id(itemId)
                            .color(colorId)
                            .condition(conditionCode)
                            .quantity(quantity)
                            .build();
    }

    public boolean matches(String itemid, int colorId, String conditionCode) {
        return this.equals(PartOrderItem.builder()
                                        .id(itemid)
                                        .color(colorId)
                                        .condition(conditionCode)
                                        .build());
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
        private final PartOrderItem partOrderItem = new PartOrderItem();

        public Builder id(String itemid) {
            PartItem partItem = new PartItem();
            partItem.setItemId(itemid);
            partOrderItem.setItem(partItem);
            return this;
        }

        public Builder color(int colorId) {
            partOrderItem.setColor(new Color(colorId));
            return this;
        }

        public Builder condition(String conditionCode) {
            partOrderItem.setCondition(Condition.of(conditionCode));
            return this;
        }

        public Builder quantity(int quantity) {
            partOrderItem.setQuantity(quantity);
            return this;
        }

        public PartOrderItem build() {
            return partOrderItem;
        }
    }
}
