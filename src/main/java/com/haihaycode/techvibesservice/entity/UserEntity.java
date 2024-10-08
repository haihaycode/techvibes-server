package com.haihaycode.techvibesservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class UserEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String email;

    @JsonIgnore
    private String password;

    private String address;
    private Integer phone;
    private String fullName;
    private String photo;
    private Boolean available;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles;

    private String extractInfo;

    @Column(name = "vnp_txn_ref", unique = true, nullable = false)
    private String vnpTxnRef;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<FavoriteEntity> favorites;

    @OneToMany(mappedBy = "account")
    @JsonManagedReference
    private List<OrderEntity> orders;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<AddressEntity> addresses; // Danh sách địa chỉ của người dùng

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private CartEntity cart; // Giỏ hàng của người dùng
}
