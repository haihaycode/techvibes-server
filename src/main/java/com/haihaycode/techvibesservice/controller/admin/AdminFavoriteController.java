package com.haihaycode.techvibesservice.controller.admin;

import com.haihaycode.techvibesservice.entity.FavoriteEntity;
import com.haihaycode.techvibesservice.entity.ProductEntity;
import com.haihaycode.techvibesservice.model.ResponseWrapper;
import com.haihaycode.techvibesservice.security.UserPrincipal;
import com.haihaycode.techvibesservice.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminFavoriteController {
    @Autowired
    private FavoriteService favoriteService;

    @GetMapping("/favorite")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<Page<FavoriteEntity>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Optional<String> sort,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long productId) {

        String sortField = "id";
        Sort.Direction sortDirection = Sort.Direction.DESC;

        if (sort.isPresent()) {
            String[] sortParams = sort.get().split(",");
            sortField = sortParams[0];
            if (sortParams.length > 1) {
                sortDirection = Sort.Direction.fromString(sortParams[1]);
            }
        }
        Pageable pageable = PageRequest.of(page, limit, Sort.by(new Sort.Order(sortDirection, sortField)));
        ResponseWrapper<Page<FavoriteEntity>> response = new ResponseWrapper<>(HttpStatus.OK, "Favorite retrieved successfully", favoriteService.getFavoritesByCriteria(userId, productId, startDate, endDate, pageable));
        return ResponseEntity.ok(response);
    }


    @PostMapping("/favorite")//để bên public
    @PreAuthorize("hasAnyRole('ADMIN','USER','STAFF')")
    public ResponseEntity<ResponseWrapper<Void>> addOrRemoveFavorite(@AuthenticationPrincipal UserPrincipal principal, @RequestParam Long productId) {
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK,  favoriteService.favorite(principal.getUserId(), productId), null));
    }

    @GetMapping("/favorite/export/excel")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<InputStreamResource> exportProductsToExcel() {
        ByteArrayInputStream inputStream = favoriteService.exportFavoritesToExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=favorites.xlsx");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(inputStream));
    }


}
