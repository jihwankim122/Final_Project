package com.top.entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "wishlist")
@Data

public class Wishlist {
    @Id
    @Column(name = "wishlist_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public static Wishlist createWishlist(Member member){
        Wishlist wishlist = new Wishlist();
        wishlist.setMember(member);
        return wishlist;
    }

}
