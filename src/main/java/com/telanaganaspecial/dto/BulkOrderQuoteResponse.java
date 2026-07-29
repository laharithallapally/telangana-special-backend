package com.telanaganaspecial.dto;

import java.util.List;

public class BulkOrderQuoteResponse {
    private String productName;
    private Double pricePerUnit;
    private Double itemTotal;
    private List<SplitQuote> splitQuotes;
    private Double totalDeliveryCharge;
    private Double grandTotal;

    public static class SplitQuote {
        private String address;
        private Integer quantity;
        private Double distanceKm;
        private Double deliveryCharge;

        public SplitQuote(String address, Integer quantity, Double distanceKm, Double deliveryCharge) {
            this.address = address;
            this.quantity = quantity;
            this.distanceKm = distanceKm;
            this.deliveryCharge = deliveryCharge;
        }

        public String getAddress() { return address; }
        public Integer getQuantity() { return quantity; }
        public Double getDistanceKm() { return distanceKm; }
        public Double getDeliveryCharge() { return deliveryCharge; }
    }

    public BulkOrderQuoteResponse(String productName, Double pricePerUnit, Integer totalQuantity,
                                  List<SplitQuote> splitQuotes) {
        this.productName = productName;
        this.pricePerUnit = pricePerUnit;
        this.itemTotal = pricePerUnit * totalQuantity;
        this.splitQuotes = splitQuotes;
        this.totalDeliveryCharge = splitQuotes.stream()
                .mapToDouble(SplitQuote::getDeliveryCharge).sum();
        this.grandTotal = this.itemTotal + this.totalDeliveryCharge;
    }

    public String getProductName() { return productName; }
    public Double getPricePerUnit() { return pricePerUnit; }
    public Double getItemTotal() { return itemTotal; }
    public List<SplitQuote> getSplitQuotes() { return splitQuotes; }
    public Double getTotalDeliveryCharge() { return totalDeliveryCharge; }
    public Double getGrandTotal() { return grandTotal; }
}