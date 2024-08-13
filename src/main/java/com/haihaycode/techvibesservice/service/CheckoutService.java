package com.haihaycode.techvibesservice.service;

import ch.qos.logback.core.model.Model;
import com.haihaycode.techvibesservice.entity.*;
import com.haihaycode.techvibesservice.exception.InvalidInputException;
import com.haihaycode.techvibesservice.repository.*;
import com.haihaycode.techvibesservice.service.vnpay.VNPAYService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CheckoutService {

    @Autowired
    private VNPAYService vnpayService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private  AddressService addressService;
    @Autowired
    private OrderStatusRepository orderStatusRepository ;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private  CartService cartService;

    @Autowired
    private ProductRepository productRepository;

    public OrderEntity placeOrder(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("Không tìm thấy ngời dùng"));
        CartEntity cart = cartRepository.findByUser(user);

        List<AddressEntity> addresses = addressService.getAddressesByUser(user);

        AddressEntity defaultAddress = addresses.stream()
                .filter(AddressEntity::getDefaultAddress)
                .findFirst()
                .orElseThrow(() -> new InvalidInputException("Địa chỉ mặt định không có !"));

       OrderStatusEntity status = orderStatusRepository.findById(5L)
               .orElseThrow(() -> new InvalidInputException("Không tìm thấy trạng thái đơn hàng"));

        // Kiểm tra và trừ số lượng sản phẩm trong kho
        for (CartItemEntity item : cart.getItems()) {
            ProductEntity product = item.getProduct();
            if (product.getQuantity() < item.getQuantity()) {
                throw new InvalidInputException("Không đủ số lượng sản phẩm: " + product.getName());
            }
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);
        }
        // Tạo đơn hàng mới
        OrderEntity order = new OrderEntity();
        order.setEmail(defaultAddress.getEmail());
        order.setAddress(defaultAddress.getAddress());
        order.setFullName(defaultAddress.getName());
        order.setPhone(Integer.parseInt(defaultAddress.getPhone()));
        order.setTotalPrice(calculateTotalPrice(cart));
        order.setNotes("");
        order.setCreateDate(new Date());
        order.setUpdateDate(new Date());
        order.setAccount(user);
        order.setOrderStatus(status);
        order.setOrderCode(generateOrderCode());

        OrderEntity finalOrder = order;
        List<OrderDetailEntity> orderDetails = cart.getItems().stream().map(item -> {
            OrderDetailEntity detail = new OrderDetailEntity();
            detail.setOrder(finalOrder);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity().intValue());
            detail.setPrice(item.getPrice());
            return detail;
        }).collect(Collectors.toList());

        order.setOrderDetails(orderDetails);

        order = orderRepository.save(order);
        user.setVnpTxnRef("");
        userRepository.save(user);
        cartRepository.save(cart);
        cartService.clearCart(user.getUserId());
        return order;
    }

    private Long calculateTotalPrice(CartEntity cart) {
        return cart.getItems().stream()
                .mapToLong(item -> item.getPrice()  * item.getQuantity())
                .sum();
    }

    private String generateOrderCode() {
        return "ORDER-" + System.currentTimeMillis();
    }


    public String placeOrderVNPAY(Long id, HttpServletRequest request){
       UserEntity user = userRepository.findById(id)
               .orElseThrow(() -> new InvalidInputException("Không tìm thấy ngời dùng"));
       CartEntity cart = cartRepository.findByUser(user);
       if(cart==null){
           throw new InvalidInputException("Không có người dùng này trong giỏ hàng");
       }
       Long totalPrice = calculateTotalPrice(cart);
       String OderInfo = "Techvibes - Thanh toán hóa đơn "+ totalPrice;
       String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
       String vnpayUrl = vnpayService.createOrder(totalPrice, OderInfo, baseUrl,id);
       return vnpayUrl;
    }
}
