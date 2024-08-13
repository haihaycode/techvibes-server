package com.haihaycode.techvibesservice.controller;

import com.haihaycode.techvibesservice.entity.CartEntity;
import com.haihaycode.techvibesservice.entity.CartItemEntity;
import com.haihaycode.techvibesservice.model.ResponseWrapper;
import com.haihaycode.techvibesservice.security.UserPrincipal;
import com.haihaycode.techvibesservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@CrossOrigin("*")
public class CartController {
    @Autowired
    private CartService cartService;
    @PreAuthorize("hasAnyRole('USER', 'ADMIN','STAFF')")
    @GetMapping("/cart/view")
    public ResponseEntity<ResponseWrapper<CartEntity>> viewCart(@AuthenticationPrincipal UserPrincipal principal) {
        CartEntity cart = cartService.getCartByUser(principal.getUserId());
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Cart retrieved successfully",cart));
    }


    @GetMapping("/cart/items")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<List<CartItemEntity>>> viewCartItems(@AuthenticationPrincipal UserPrincipal principal) {
        CartEntity cart = cartService.getCartByUser(principal.getUserId());
        List<CartItemEntity> items = cartService.getCartItems(cart.getId());
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Cart items retrieved successfully" ,items));
    }
    @PostMapping("/cart/add")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<Void>> addItemToCart(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam Long productId,
            @RequestParam Long quantity) {
        cartService.addOrUpdateCartItem(principal.getUserId(), productId, quantity);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Thêm vào giỏ hàng thành công", null));
    }
    @PostMapping("/cart/remove")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<Void>> removeItemFromCart(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam Long productId) {
        cartService.removeCartItem(principal.getUserId(), productId);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK,  "Xóa thành công", null));
    }
    @DeleteMapping("/cart/clear")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<Void>> clearCart(@AuthenticationPrincipal UserPrincipal principal) {
        cartService.clearCart(principal.getUserId());
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Giỏ hàng đã được xóa hết", null));
    }
}
