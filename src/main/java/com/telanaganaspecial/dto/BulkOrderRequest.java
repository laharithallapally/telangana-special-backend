package com.telanaganaspecial.dto;

import java.util.List;

public class BulkOrderRequest {
    private Long productId;
    private Integer totalQuantity;
    private List<DeliverySplit> deliverySplits;

    public static class DeliverySplit {
        private Integer quantity;
        private String address;

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(Integer totalQuantity) { this.totalQuantity = totalQuantity; }

    public List<DeliverySplit> getDeliverySplits() { return deliverySplits; }
    public void setDeliverySplits(List<DeliverySplit> deliverySplits) { this.deliverySplits = deliverySplits; }
}