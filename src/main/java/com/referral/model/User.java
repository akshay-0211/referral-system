package com.referral.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;

    private String email;
    private String password;
    private String referralCode;
    private String referredBy; // store referrer user id as String
    private List<String> referrals = new ArrayList<>(); // store referred user ids as Strings
    private boolean profileCompleted = false;
    private String name;
    private String phoneNumber;
    private String address;
    private Integer userNumber;
}