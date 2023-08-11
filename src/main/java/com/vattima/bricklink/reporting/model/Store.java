package com.vattima.bricklink.reporting.model;

import com.bricklink.api.ajax.model.v1.ItemForSale;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.function.Function;

@Data
@Builder
public class Store {
    @EqualsAndHashCode.Include
    private String storeName;
    @EqualsAndHashCode.Include
    private String sellerUsername;

    private Integer currencyId;
    private Double minBuy;
    private Integer sellerFeedbackScore;
    private String sellerCountryName;
    private String sellerCountryCode;

    public static Store fromItemForSale(final ItemForSale itemForSale) {
        return storeFromItemForSale.apply(itemForSale);
    }

    private static Function<ItemForSale, Store> storeFromItemForSale = ifs -> Store.builder()
                                                                                   .storeName(ifs.getStrStorename())
                                                                                   .sellerUsername(ifs.getStrSellerUsername())
                                                                                   .currencyId(ifs.getIdCurrencyStore())
                                                                                   .minBuy(ifs.getMinBuy())
                                                                                   .sellerFeedbackScore(ifs.getN4SellerFeedbackScore())
                                                                                   .sellerCountryName(ifs.getStrSellerCountryName())
                                                                                   .sellerCountryCode(ifs.getStrSellerCountryCode())
                                                                                   .build();
}
