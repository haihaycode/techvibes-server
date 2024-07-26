package com.haihaycode.techvibesservice.service;

import com.haihaycode.techvibesservice.entity.RoleEntity;
import com.haihaycode.techvibesservice.exception.RoleNotFoundException;
import com.haihaycode.techvibesservice.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class RoleService {
    private final RoleRepository roleRepository;
    public List<RoleEntity> getRoles() {
        return roleRepository.findAll();
    }
    public RoleEntity updateRoleDescription(Long roleId, String description) {
        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + roleId));
        role.setDescription(description);
        return roleRepository.save(role);
    }
}
