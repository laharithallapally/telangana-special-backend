package com.telanaganaspecial.service;

import com.telanaganaspecial.dto.BulkOrderQuoteResponse;
import com.telanaganaspecial.dto.BulkOrderRequest;

public interface BulkOrderService {
    BulkOrderQuoteResponse generateQuote(BulkOrderRequest request);
}