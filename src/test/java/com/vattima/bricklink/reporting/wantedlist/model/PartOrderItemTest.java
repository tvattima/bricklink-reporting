package com.vattima.bricklink.reporting.wantedlist.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PartOrderItemTest {
    @Test
    void apply() {
        PartOrderItem partOrderItem = PartOrderItem.of("3026b", 11, "N", 10);
        assertThat(partOrderItem.getQuantity()).isEqualTo(10);
        assertThat(partOrderItem.getQuantityLeft()).isEqualTo(10);
        partOrderItem.applyQuantity(6);
        System.out.println(partOrderItem);
        assertThat(partOrderItem.getQuantity()).isEqualTo(10);
        assertThat(partOrderItem.getQuantityLeft()).isEqualTo(4);
        partOrderItem.applyQuantity(2);
        System.out.println(partOrderItem);
        assertThat(partOrderItem.getQuantity()).isEqualTo(10);
        assertThat(partOrderItem.getQuantityLeft()).isEqualTo(2);
        partOrderItem.applyQuantity(5);
        System.out.println(partOrderItem);
        assertThat(partOrderItem.getQuantity()).isEqualTo(10);
        assertThat(partOrderItem.getQuantityLeft()).isEqualTo(0);
    }

}