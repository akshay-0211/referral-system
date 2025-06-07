package com.referral.repository;

import com.referral.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findByReferralCode(String referralCode);

    List<User> findByReferredByAndProfileCompletedTrue(String referrerId);

    boolean existsByEmail(String email);

    boolean existsByReferralCode(String referralCode);
}