package com.haihaycode.techvibesservice.service;

import com.haihaycode.techvibesservice.entity.AddressEntity;
import com.haihaycode.techvibesservice.entity.UserEntity;
import com.haihaycode.techvibesservice.repository.AddressRepository;
import com.haihaycode.techvibesservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressService {


    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressEntity saveAddress(AddressEntity address) {
        return addressRepository.save(address);
    }

    // Lấy tất cả địa chỉ của người dùng đang đăng nhập
    public List<AddressEntity> getAddressesByUser(UserEntity user) {
        return addressRepository.findByUser(user);
    }

    // Xóa địa chỉ theo ID và người dùng
    public void deleteAddressByIdAndUser(Long id, UserEntity user) {
        Optional<AddressEntity> addressOpt = addressRepository.findByIdAndUser(id, user);
        addressOpt.ifPresent(addressRepository::delete);
    }

    // Cập nhật địa chỉ theo ID và người dùng
    public Optional<AddressEntity> updateAddressByIdAndUser(Long id, UserEntity user, AddressEntity newAddressData) {
        return addressRepository.findByIdAndUser(id, user).map(address -> {
            address.setAddress(newAddressData.getAddress());
            address.setPhone(newAddressData.getPhone());
            address.setEmail(newAddressData.getEmail());
            address.setName(newAddressData.getName());
            address.setDefaultAddress(true);
            return addressRepository.save(address);
        });
    }
    // Cập nhật địa chỉ mặc định
    public Optional<AddressEntity> setDefaultAddress(Long id, UserEntity user) {
        List<AddressEntity> addresses = addressRepository.findByUser(user);
        addresses.forEach(address -> {
            address.setDefaultAddress(address.getId().equals(id));
            addressRepository.save(address);
        });

        return addressRepository.findById(id);
    }
}
