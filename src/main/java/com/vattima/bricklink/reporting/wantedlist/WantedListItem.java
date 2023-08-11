package com.vattima.bricklink.reporting.wantedlist;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;
import java.util.function.Predicate;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JacksonXmlRootElement(localName = "ITEM")
public class WantedListItem {
    @EqualsAndHashCode.Include
    @JsonProperty("ITEMTYPE")
    private String itemType;

    @EqualsAndHashCode.Include
    @JsonProperty("ITEMID")
    private String itemId;

    @EqualsAndHashCode.Include
    @JsonProperty("COLOR")
    private Integer color;

    @EqualsAndHashCode.Include
    @JsonProperty("CONDITION")
    private String condition;

    @JsonProperty("MAXPRICE")
    private Double maxPrice;

    @JsonProperty("MINQTY")
    private Integer minQty;

    @JsonProperty("QTYFILLED")
    private Integer qtyFilled;

    @JsonProperty("REMARKS")
    private String remarks;

    @JsonProperty("NOTIFY")
    private String notify;

    Predicate<WantedListItem> matches = wli -> {
        boolean match = (wli.getItemType().equals(getItemType()));
        match = match && (wli.getItemId().equals(getItemId()));
        match = match && (Optional.ofNullable(getColor()).map(i -> i.equals(getColor())).orElse(true));
        match = match && (Optional.ofNullable(getCondition()).map(i -> i.equals(getCondition())).orElse(true));
//        System.out.printf("this itemType=[%s], searching for itemType [%s]", getItemType(), wli.getItemType());
//        System.out.printf("this itemId=[%s], searching for itemId [%s]", getItemId(), wli.getItemId());
//        System.out.printf("this color=[%s], searching for color [%s]", getColor(), wli.getColor());
//        System.out.printf("this condition=[%s], searching for condition [%s]", getCondition(), wli.getCondition());
//        System.out.printf("matches [%s]%n", match);
//        System.out.println("==========================================");
        return match;
    };
}

