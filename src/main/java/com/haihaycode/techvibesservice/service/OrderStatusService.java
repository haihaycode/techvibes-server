package com.haihaycode.techvibesservice.service;

import com.haihaycode.techvibesservice.entity.OrderStatusEntity;
import com.haihaycode.techvibesservice.exception.ResourceNotFoundException;
import com.haihaycode.techvibesservice.repository.OrderStatusRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderStatusService {
    private final OrderStatusRepository orderStatusRepository;

    public List<OrderStatusEntity> getAll(){
        return orderStatusRepository.findAll();
    }
    public OrderStatusEntity getById(Long id){
        return orderStatusRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("OrderStatus not found for id :" + id)
        );
    }
}
