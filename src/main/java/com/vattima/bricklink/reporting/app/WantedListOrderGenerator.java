package com.vattima.bricklink.reporting.app;

import com.bricklink.api.ajax.BricklinkAjaxClient;
import com.bricklink.api.ajax.support.CatalogItemsForSaleResult;
import com.bricklink.api.html.BricklinkHtmlClient;
import com.bricklink.api.html.model.v2.CatalogItem;
import com.bricklink.api.rest.client.ParamsBuilder;
import com.bricklink.web.support.BricklinkWebService;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.vattima.bricklink.reporting.model.Store;
import com.vattima.bricklink.reporting.model.StoreAggregator;
import com.vattima.bricklink.reporting.model.StoreLotsForSale;
import com.vattima.bricklink.reporting.model.WantedItemForSaleAggregator;
import com.vattima.bricklink.reporting.wantedlist.WantedListInventory;
import com.vattima.bricklink.reporting.wantedlist.model.WantedItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootApplication(scanBasePackages = {"com"})
@EnableConfigurationProperties
public class WantedListOrderGenerator {

    private static final DecimalFormat decimalFormat = new DecimalFormat("#.00");

    public static void main(String[] args) {
        SpringApplication.run(WantedListOrderGenerator.class, args);
    }

    @Component
    @RequiredArgsConstructor
    @Slf4j
    public static class WantedListTest implements CommandLineRunner {
        private final BricklinkWebService bricklinkWebService;
        private final BricklinkHtmlClient bricklinkHtmlClient;
        private final BricklinkAjaxClient bricklinkAjaxClient;

        @Override
        public void run(String... args) throws Exception {
            byte[] bytes = bricklinkWebService.dowloadWantedList(5441708L, "MISSING_(11)_BTD_Union_Pacific_Heritage_Water_Tender");
            XmlMapper xmlMapper = new XmlMapper();
            String xml = IOUtils.toString(bytes, CharEncoding.UTF_8);
            WantedListInventory inventory = xmlMapper.readValue(xml, WantedListInventory.class);

            Set<WantedItem> wantedItems = inventory.getWantedListItems()
                                                    .stream()
                                                    .map(wli -> WantedItem.builder()
                                                                          .id(wli.getItemId())
                                                                          .color(wli.getColor())
                                                                          .condition(wli.getCondition())
                                                                          .quantity(wli.getMinQty())
                                                                          .build())
                                                    .collect(Collectors.toSet());
            log.info("------------------------------------------------------------------------------------------------");
            StoreAggregator storeAggregator = new StoreAggregator();
            WantedItemForSaleAggregator wantedItemForSaleAggregator = new WantedItemForSaleAggregator();
            wantedItems.forEach(wi -> {
                log.info("Part {} Color {} Condition {} Quantity {}", wi.getItem().getItemId(), wi.getColor().getColorId(), wi.getCondition().getConditionCode(), wi.getQuantity());

                CatalogItem catalogitem = bricklinkHtmlClient.getCatalogPartItemId(wi.getItemId());

                Map<String, Object> params = new ParamsBuilder()
                        .of("itemid",catalogitem.getItemId())
                        .of("rpp", 500)
                        .of("loc", "US")
                        .of("minqty", 1 /*wi.getQuantity()*/) // Use MinQty of 1 so that the Quantity of multiple lots from same seller can be combined to determine if MinQty wanted is met by the Seller
                        .of("cond", wi.getCondition().getConditionCode().equals("N")?"N":"*")
                        .of("st", 1)
                        .of("color", wi.getColorId())
                        .get();

                CatalogItemsForSaleResult catalogNewItemsForSaleResult = bricklinkAjaxClient.catalogItemsForSale(params);
                // log.info("\t {} total count {}, rpp {} -------------------------------------------------------------------------------", params, catalogNewItemsForSaleResult.getTotal_count(), catalogNewItemsForSaleResult.getRpp());
                catalogNewItemsForSaleResult.getList().forEach(fsr -> {
                    // log.info("\t{}", fsr);
                    wantedItemForSaleAggregator.addItemForSale(wi, fsr);
                });
                Set<StoreLotsForSale> storeLotsForSale = wantedItemForSaleAggregator.getWantedStoreLotsForSale(wi, 30);
                storeLotsForSale.forEach(slfs -> storeAggregator.addItemForSale(wi, slfs));

//                Store store = slfs.getStore();
//                log.info(String.format("\t\t\t Store %1$30s, Price %2$7s, Min Buy %3$7s Total Price %4$7s Total Lots %5$d", store.getStoreName(), decimalFormat.format(slfs.getSalePrice()), decimalFormat.format(store.getMinBuy()), decimalFormat.format(slfs.getSalePrice() * wi.getQuantity()), storeAggregator.getStoreLotsForSale(store).size()));
//                log.info("\t-------------------------------------------------------------------------------");

            });
            wantedItemForSaleAggregator.filterStores();
            storeAggregator.filterStores();
            log.info("Stores Report ----------------------------------------------------------------------------------------------------");
            storeAggregator.getStores().entrySet().stream().sorted(Comparator.comparingInt(e -> e.getValue().size())).forEach(e -> {
                Store store = e.getKey();
                Set<StoreLotsForSale> storeLotsForSale = e.getValue();
                log.info("Store {} Lots {} Total Price {} Parts {}", store.getStoreName(), storeLotsForSale.size(), decimalFormat.format(storeLotsForSale.stream().mapToDouble(StoreLotsForSale::getSalePrice).sum()), storeLotsForSale.stream().map(ifs -> ifs.getWantedItem().getItemId()).collect(Collectors.joining(",")));
            });
//            log.info("Wanted Items {}", wantedItemForSaleAggregator.getWantedItemsForSale());
        }
    }
}
