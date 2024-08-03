package com.haihaycode.techvibesservice.controller.admin;

import com.haihaycode.techvibesservice.entity.RoleEntity;
import com.haihaycode.techvibesservice.model.ResponseWrapper;
import com.haihaycode.techvibesservice.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AdminRoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<List<RoleEntity>>> getRoles() {
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Roles fetched successfully", roleService.getRoles()));
    }
    @PutMapping("/roles/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<RoleEntity>> updateRoleDescription(
            @PathVariable Long roleId,
            @RequestBody Map<String, String> request) {
        String description = request.get("description");
        RoleEntity updatedRole = roleService.updateRoleDescription(roleId, description);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Role description updated successfully", updatedRole));
    }
}
