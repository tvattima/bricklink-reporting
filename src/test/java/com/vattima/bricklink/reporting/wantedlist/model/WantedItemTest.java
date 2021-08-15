package com.vattima.bricklink.reporting.wantedlist.model;

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

    @Test
    void equalsAndHashCode_differentPartAndColorAndCondition_notEqual() {
        WantedItem wantedItem1 = WantedItem.builder().id("1234a").color(123).condition("N").quantity(7).build();
        WantedItem wantedItem2 = WantedItem.builder().id("3062b").color(456).condition("U").quantity(7).build();
        assertThat(wantedItem1).isNotEqualTo(wantedItem2);
    }

    @Test
    void equalsAndHashCode_samePart_differentColorAndCondition_notEqual() {
        WantedItem wantedItem1 = WantedItem.builder().id("3062b").color(123).condition("N").quantity(7).build();
        WantedItem wantedItem2 = WantedItem.builder().id("3062b").color(456).condition("U").quantity(7).build();
        assertThat(wantedItem1).isNotEqualTo(wantedItem2);
    }

    @Test
    void equalsAndHashCode_samePartAndColor_differentCondition_notEqual() {
        WantedItem wantedItem1 = WantedItem.builder().id("3062b").color(123).condition("N").quantity(7).build();
        WantedItem wantedItem2 = WantedItem.builder().id("3062b").color(123).condition("U").quantity(7).build();
        assertThat(wantedItem1).isNotEqualTo(wantedItem2);
    }

    @Test
    void equalsAndHashCode_samePartAndColorAndCondition_equal() {
        WantedItem wantedItem1 = WantedItem.builder().id("3062b").color(123).condition("N").quantity(7).build();
        WantedItem wantedItem2 = WantedItem.builder().id("3062b").color(123).condition("N").quantity(7).build();
        assertThat(wantedItem1).isEqualTo(wantedItem2);
    }
}