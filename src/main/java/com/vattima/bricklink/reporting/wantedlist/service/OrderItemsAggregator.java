package com.vattima.bricklink.reporting.wantedlist.service;

import com.bricklink.api.rest.model.v1.Order;
import com.bricklink.api.rest.model.v1.OrderItem;
import lombok.Data;

import java.util.List;

@Data
public class OrderItemsAggregator {
    private List<Order> orders;
    private List<OrderItem> orderItems;
}
