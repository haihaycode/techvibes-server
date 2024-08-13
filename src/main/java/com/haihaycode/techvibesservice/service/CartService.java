package com.haihaycode.techvibesservice.service;

import com.haihaycode.techvibesservice.entity.CartEntity;
import com.haihaycode.techvibesservice.entity.CartItemEntity;
import com.haihaycode.techvibesservice.entity.ProductEntity;
import com.haihaycode.techvibesservice.entity.UserEntity;
import com.haihaycode.techvibesservice.exception.ResourceNotFoundException;
import com.haihaycode.techvibesservice.repository.CartRepository;
import com.haihaycode.techvibesservice.repository.CartItemRepository;
import com.haihaycode.techvibesservice.repository.ProductRepository;
import com.haihaycode.techvibesservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;


    public CartEntity getCartByUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return cartRepository.findByUser(user);
    }

    public List<CartItemEntity> getCartItems(Long cartId) {
        return cartItemRepository.findByCartId(cartId);
    }

    @Transactional
    public void addOrUpdateCartItem(Long userId, Long productId, Long quantity) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        ProductEntity product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        if(!product.getAvailable()){
            throw new ResourceNotFoundException("Product not found with ID: " + productId);
        }
        CartEntity cart = cartRepository.findByUser(user);
        if (cart == null) {
            cart = new CartEntity();
            cart.setUser(user);
            cartRepository.save(cart);
        }

        Optional<CartItemEntity> existingItem = cartItemRepository.findByCartAndProduct(cart, product);

        if(quantity==0){
            return;
        }
        if (existingItem.isPresent()) {
            CartItemEntity item = existingItem.get();

            item.setQuantity(quantity);
            cartItemRepository.save(item);
        } else {
            CartItemEntity newItem = new CartItemEntity();
            newItem.setCart(cart);

            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }
    }

    @Transactional
    public void removeCartItem(Long userId, Long productId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        ProductEntity product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        CartEntity cart = cartRepository.findByUser(user);
        if (cart != null) {
            Optional<CartItemEntity> existingItem = cartItemRepository.findByCartAndProduct(cart, product);
            existingItem.ifPresent(cartItemRepository::delete);
        }
    }
    public void clearCart(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        CartEntity cart = cartRepository.findByUser(user);
        if (cart != null) {
            cartItemRepository.deleteAll(cart.getItems());
            cart.getItems().clear();
            cartRepository.save(cart);
        }
    }
}
