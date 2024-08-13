package com.haihaycode.techvibesservice.controller;

import com.haihaycode.techvibesservice.entity.UserEntity;
import com.haihaycode.techvibesservice.service.CheckoutService;
import com.haihaycode.techvibesservice.service.UserService;
import com.haihaycode.techvibesservice.service.vnpay.VNPAYService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/api/public/checkout")
public class vnpayPaymentController {

    @Autowired
    private VNPAYService vnpayService;
    @Autowired
    private UserService userService;
    @Autowired
    private CheckoutService checkoutService;

    @GetMapping("/vnpay-payment")
    public String handlePayment(HttpServletRequest request, Model model){
        int paymentStatus =vnpayService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");
        String vnpTxnRef = request.getParameter("vnp_TxnRef");

        model.addAttribute("orderId", orderInfo);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("paymentTime", paymentTime);
        model.addAttribute("transactionId", transactionId);
        if (paymentStatus == 1) {
            UserEntity user = userService.getUserByVnpTxnRef(vnpTxnRef);
            checkoutService.placeOrder(user.getUserId());
            return "vnpay/orderSuccessFul";
        } else {
            return "vnpay/orderFail";
        }
    }
}
