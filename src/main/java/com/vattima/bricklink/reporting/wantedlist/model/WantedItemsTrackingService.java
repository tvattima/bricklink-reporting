package com.vattima.bricklink.reporting.wantedlist.model;

import com.vattima.bricklink.reporting.model.Condition;
import lombok.Setter;

import java.util.List;

public class WantedItemsTrackingService {
    @Setter
    private WantedItemListAggregator wantedItemListAggregator;

    public void applyOrderItems(List<PartOrderItem> partOrderItems) {
        partOrderItems.forEach(poi -> {
            wantedItemListAggregator.getAllPartWantedItems(poi.getItemId(), poi.getColorId(), poi.getConditionCode())
                                    .forEach(wi -> {
                                        if (Condition.UNSPECIFIED.equals(wi.getCondition()) || !Condition.NEW.equals(wi.getCondition())) {
                                            applyPartOrderItemToWantedItem(poi, wi);
                                        }
                                    });
        });
    }

    void applyPartOrderItemToWantedItem(PartOrderItem partOrderItem, WantedItem wantedItem) {
        int quantityToApply = partOrderItem.getQuantityLeft();
        int quantityApplied = wantedItem.applyQuantity(quantityToApply);
        partOrderItem.applyQuantity(quantityApplied);
    }
}
