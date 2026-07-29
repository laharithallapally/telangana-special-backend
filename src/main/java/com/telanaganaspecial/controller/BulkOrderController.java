package com.telanaganaspecial.controller;

import com.telanaganaspecial.dto.BulkOrderQuoteResponse;
import com.telanaganaspecial.dto.BulkOrderRequest;
import com.telanaganaspecial.service.BulkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bulk-orders")
public class BulkOrderController {

    @Autowired
    private BulkOrderService bulkOrderService;

    @PostMapping("/quote")
    public BulkOrderQuoteResponse getQuote(@RequestBody BulkOrderRequest request) {
        return bulkOrderService.generateQuote(request);
    }
}