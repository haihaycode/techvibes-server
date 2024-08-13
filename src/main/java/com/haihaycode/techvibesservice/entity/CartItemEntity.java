    package com.haihaycode.techvibesservice.entity;

    import com.fasterxml.jackson.annotation.JsonBackReference;
    import jakarta.persistence.*;
    import lombok.Getter;
    import lombok.Setter;

    @Getter
    @Setter
    @Entity
    public class CartItemEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "cart_id")
        @JsonBackReference
        private CartEntity cart; // Liên kết với giỏ hàng

        @ManyToOne
        @JoinColumn(name = "product_id")
        private ProductEntity product; // Liên kết với sản phẩm

        private Long quantity; // Số lượng sản phẩm
        private Long price; // Giá sản phẩm

        @PrePersist
        @PreUpdate
        public void updatePrice() {
            if (product != null) {
                this.price = product.getPrice() - (product.getPrice() * product.getDiscount() / 100);
            }
        }
    }
