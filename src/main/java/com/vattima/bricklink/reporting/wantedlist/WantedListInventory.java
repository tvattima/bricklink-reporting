package com.vattima.bricklink.reporting.wantedlist;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JacksonXmlRootElement(localName="INVENTORY")
public class WantedListInventory {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonProperty("ITEM")
    private List<WantedListItem> wantedListItems = new ArrayList<>();
}
