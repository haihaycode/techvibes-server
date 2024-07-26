package com.haihaycode.techvibesservice.service;

import com.haihaycode.techvibesservice.entity.FavoriteEntity;
import com.haihaycode.techvibesservice.entity.ProductEntity;
import com.haihaycode.techvibesservice.entity.UserEntity;
import com.haihaycode.techvibesservice.exception.ResourceNotFoundException;
import com.haihaycode.techvibesservice.repository.FavoriteRepository;
import com.haihaycode.techvibesservice.repository.ProductRepository;
import com.haihaycode.techvibesservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public Page<FavoriteEntity> getFavoritesByCriteria(Long userId, Long productId, Date startDate, Date endDate, Pageable pageable) {
        return favoriteRepository.findFavoritesByCriteria(userId, productId, startDate, endDate, pageable);
    }

    @Transactional
    public void favorite(Long userId, Long productId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        Optional<ProductEntity> productOptional = productRepository.findById(productId);

        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        if (productOptional.isEmpty()) {
            throw new ResourceNotFoundException("Product not found with ID: " + productId);
        }
        Optional<FavoriteEntity> existingFavorite = favoriteRepository.findByUserIdAndProductId(userId, productId);
        if (existingFavorite.isPresent()) {
            favoriteRepository.delete(existingFavorite.get());
        }else{
            FavoriteEntity favorite = new FavoriteEntity();
            favorite.setUser(userOptional.get());
            favorite.setProduct(productOptional.get());
            favorite.setCreateDate(new Date());
            favoriteRepository.save(favorite);
        }
    }


}
