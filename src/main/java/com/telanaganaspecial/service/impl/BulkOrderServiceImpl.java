package com.telanaganaspecial.service.impl;

import com.telanaganaspecial.dto.BulkOrderQuoteResponse;
import com.telanaganaspecial.dto.BulkOrderRequest;
import com.telanaganaspecial.entity.Product;
import com.telanaganaspecial.repository.ProductRepository;
import com.telanaganaspecial.service.BulkOrderService;
import com.telanaganaspecial.service.DeliveryChargeCalculator;
import com.telanaganaspecial.service.DistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BulkOrderServiceImpl implements BulkOrderService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DeliveryChargeCalculator deliveryChargeCalculator;

    @Autowired
    private DistanceService distanceService;

    @Override
    public BulkOrderQuoteResponse generateQuote(BulkOrderRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        int quantityAccountedFor = request.getDeliverySplits().stream()
                .mapToInt(BulkOrderRequest.DeliverySplit::getQuantity).sum();

        if (quantityAccountedFor != request.getTotalQuantity()) {
            throw new IllegalArgumentException(
                    "Split quantities (" + quantityAccountedFor +
                            ") don't add up to total quantity (" + request.getTotalQuantity() + ")");
        }

        List<BulkOrderQuoteResponse.SplitQuote> splitQuotes = new ArrayList<>();

        for (BulkOrderRequest.DeliverySplit split : request.getDeliverySplits()) {
            double distanceKm = distanceService.calculateDistanceKm(split.getAddress());
            double charge = deliveryChargeCalculator.calculateCharge(distanceKm);
            splitQuotes.add(new BulkOrderQuoteResponse.SplitQuote(
                    split.getAddress(), split.getQuantity(), distanceKm, charge));
        }

        return new BulkOrderQuoteResponse(
                product.getName(), product.getPrice(), request.getTotalQuantity(), splitQuotes);
    }
}