package com.haihaycode.techvibesservice.controller;

import com.haihaycode.techvibesservice.entity.FavoriteEntity;
import com.haihaycode.techvibesservice.entity.ProductEntity;
import com.haihaycode.techvibesservice.model.ResponseWrapper;
import com.haihaycode.techvibesservice.security.UserPrincipal;
import com.haihaycode.techvibesservice.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/public")
public class favoriteController {
    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/favorite")// public  -> THÊM VÀO MỤC YÊU THÍCH SA PHẨM
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<ResponseWrapper<Void>> addOrRemoveFavorite(@AuthenticationPrincipal UserPrincipal principal, @RequestParam Long productId) {
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, favoriteService.favorite(principal.getUserId(), productId), null));
    }

    @GetMapping("/favorite")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<ResponseWrapper<Page<ProductEntity>>> getFavorite(@AuthenticationPrincipal UserPrincipal principal,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int limit,
                                                                             @RequestParam(required = false) Optional<String> sort) {
        String sortField = "id";
        Sort.Direction sortDirection = Sort.Direction.DESC;
        if (sort.isPresent()) {
            String[] sortParams = sort.get().split(",");
            sortField = sortParams[0];
            if (sortParams.length > 1) {
                sortDirection = Sort.Direction.fromString(sortParams[1]);
            }
        }
        Page<ProductEntity> favorites = favoriteService.getFavorite(principal.getUserId(), page, limit, sortDirection, sortField);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Fetch favorites successful", favorites));
    }

}
