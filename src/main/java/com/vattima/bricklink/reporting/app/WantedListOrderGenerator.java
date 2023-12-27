package com.vattima.bricklink.reporting.app;

import com.bricklink.api.ajax.PagingBricklinkAjaxClient;
import com.bricklink.api.ajax.support.CatalogItemsForSaleResult;
import com.bricklink.api.html.BricklinkHtmlClient;
import com.bricklink.api.html.model.v2.CatalogItem;
import com.bricklink.api.html.model.v2.WantedItem;
import com.bricklink.api.rest.client.ParamsBuilder;
import com.bricklink.web.api.BricklinkWebService;
import com.vattima.bricklink.reporting.model.Store;
import com.vattima.bricklink.reporting.model.StoreLotsForSale;
import com.vattima.bricklink.reporting.model.WantedItemForSaleAggregator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Set;

@SpringBootApplication(scanBasePackages = {"com"})
@EnableConfigurationProperties
public class WantedListOrderGenerator {

    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public static void main(String[] args) {
        SpringApplication.run(WantedListOrderGenerator.class, args);
    }

    @Component
    @RequiredArgsConstructor
    @Slf4j
    public static class WantedListTest implements CommandLineRunner {
        private final BricklinkWebService bricklinkWebService;
        private final BricklinkHtmlClient bricklinkHtmlClient;
        private final PagingBricklinkAjaxClient pagingBricklinkAjaxClient;

        @Override
        public void run(String... args) throws Exception {
            Set<WantedItem> wantedItems = bricklinkWebService.getWantedListItems(7152827L);

            log.info("------------------------------------------------------------------------------------------------");
            WantedItemForSaleAggregator wantedItemForSaleAggregator = new WantedItemForSaleAggregator();
            wantedItems.forEach(wi -> {
                log.info("Part {} Color {} Condition {} Quantity {}", wi.getItemNo(), wi.getColorName(), wi.getWantedNew(), wi.getWantedQty());

                CatalogItem catalogitem = bricklinkHtmlClient.getCatalogPartItemId(wi.getItemID());

                Map<String, Object> params = new ParamsBuilder()
                        .of("itemid", catalogitem.getItemId())
                        .of("rpp", 500)
                        .of("loc", "US")
                        .of("minqty", 1 /*wi.getQuantity()*/) // Use MinQty of 1 so that the Quantity of multiple lots from same seller can be combined to determine if MinQty wanted is met by the Seller
                        .of("cond", wi.getWantedNew().equals("N") ? "N" : "*")
                        .of("st", 1)
                        .of("color", wi.getColorID())
                        .get();

                CatalogItemsForSaleResult catalogNewItemsForSaleResult = pagingBricklinkAjaxClient.catalogItemsForSale(params);
                catalogNewItemsForSaleResult.getList()
                                            .forEach(fsr -> wantedItemForSaleAggregator.addItemForSale(wi, fsr));
            });

            wantedItemForSaleAggregator.filterStores();
            wantedItemForSaleAggregator.analyze();

            Map<Store, Set<StoreLotsForSale>> storeMap = wantedItemForSaleAggregator.getStores();
            storeMap.entrySet()
                    .stream()
                    .filter(e -> wantedItemForSaleAggregator.meetsStoreMinimumBuy(e.getKey(), storeMap))
                    //.sorted(e -> {})
                    .forEach(e -> {
                        log.info("Store : {} Min Buy {} - Fulfills {} out of {} Wanted Items -  Total Purchase Amount {}",
                                e.getKey()
                                 .getStoreName(),
                                e.getKey()
                                 .getMinBuy(),
                                storeMap.get(e.getKey())
                                        .size(),
                                wantedItems.size(),
                                format(wantedItemForSaleAggregator.storePurchaseAmount(e.getKey(), storeMap)));
                        storeMap.get(e.getKey())
                                .forEach(slfs -> {
                                    log.info("\t Item {} Color {} Condition {} Quantity on Hand {} Wanted Quantity {} Cost {} Wanted Item Cost {}",
                                            slfs.getWantedItem().getItemNo(),
                                            slfs.getWantedItem().getColorName(),
                                            slfs.getWantedItem().getWantedNew(),
                                            slfs.getTotalQuantity(),
                                            slfs.getWantedItem().getWantedQty(),
                                            slfs.getSalePrice(),
                                            slfs.getWantedItemCost());
                                });
                    });
        }
    }

    private static String format(final double value) {
        return decimalFormat.format(value);
    }
}
