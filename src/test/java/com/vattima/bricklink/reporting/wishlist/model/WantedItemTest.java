package com.vattima.bricklink.reporting.wishlist.model;

import com.vattima.bricklink.reporting.model.Condition;
import com.vattima.bricklink.reporting.model.ItemType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WantedItemTest {
    @Test
    void builder() {
        WantedItem wantedItem = WantedItem.builder().id("3062b").color(123).condition("N").quantity(7).build();
        assertThat(wantedItem.getItemId()).isEqualTo("3062b");
        assertThat(wantedItem.getItemType()).isEqualTo(ItemType.PART);
        assertThat(wantedItem.getColorId()).isEqualTo(123);
        assertThat(wantedItem.getCondition()).isEqualTo(Condition.NEW);
        assertThat(wantedItem.getConditionCode()).isEqualTo("N");
        assertThat(wantedItem.getQuantity()).isEqualTo(7);
    }

}