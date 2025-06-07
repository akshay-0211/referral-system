package com.referral.service;

import com.referral.dto.ProfileUpdateRequest;
import com.referral.dto.SignupRequest;
import com.referral.model.User;
import com.referral.model.Counter;
import com.referral.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing user operations in the referral system.
 * Handles user registration, profile completion, and referral tracking.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // @Autowired
    // private CounterRepository counterRepository;

    @Autowired
    private MongoOperations mongoOperations;

    /**
     * Registers a new user in the system.
     * If a referral code is provided, links the new user with the referrer.
     *
     * @param request SignupRequest containing user details
     * @return The created User object
     * @throws RuntimeException if email already exists or referral code is invalid
     */
    @Transactional
    public User signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setReferralCode(generateUniqueReferralCode());
        user.setUserNumber(getNextUserNumber());

        if (request.getReferralCode() != null && !request.getReferralCode().isEmpty()) {
            Optional<User> referrer = userRepository.findByReferralCode(request.getReferralCode());
            if (referrer.isPresent()) {
                user.setReferredBy(referrer.get().getId());
                User ref = referrer.get();
                ref.getReferrals().add(user.getId());
                userRepository.save(ref);
            } else {
                throw new RuntimeException("Invalid referral code");
            }
        }

        User savedUser = userRepository.save(user);
        if (savedUser.getReferredBy() != null) {
            Optional<User> referrer = userRepository.findById(savedUser.getReferredBy());
            referrer.ifPresent(ref -> {
                if (!ref.getReferrals().contains(savedUser.getId())) {
                    ref.getReferrals().add(savedUser.getId());
                    userRepository.save(ref);
                }
            });
        }
        return savedUser;
    }

    /**
     * Completes a user's profile with additional information.
     *
     * @param userId  ID of the user to update
     * @param request ProfileUpdateRequest containing profile details
     * @return Updated User object
     * @throws RuntimeException if user is not found
     */
    @Transactional
    public User completeProfile(String userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setProfileCompleted(true);

        return userRepository.save(user);
    }

    /**
     * Retrieves all successful referrals for a given user.
     * A successful referral is one where the referred user has completed their
     * profile.
     *
     * @param userId ID of the referrer
     * @return List of successfully referred users
     */
    public List<User> getSuccessfulReferrals(String userId) {
        return userRepository.findByReferredByAndProfileCompletedTrue(userId);
    }

    /**
     * Generates a unique referral code for new users.
     * Uses UUID to ensure uniqueness.
     *
     * @return A unique 8-character referral code
     */
    private String generateUniqueReferralCode() {
        String referralCode;
        do {
            referralCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (userRepository.existsByReferralCode(referralCode));
        return referralCode;
    }

    /**
     * Gets the next available user number using MongoDB's atomic operations.
     * Uses a counter collection to ensure thread-safe incrementing.
     *
     * @return The next available user number
     */
    @SuppressWarnings("null")
    private int getNextUserNumber() {
        Query query = new Query(Criteria.where("_id").is("userNumber"));
        Update update = new Update().inc("seq", 1);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true).upsert(true);
        Counter counter = mongoOperations.findAndModify(query, update, options, Counter.class);
        return counter.getSeq();
    }

    /**
     * Retrieves a user by their user number.
     *
     * @param userNumber The user number to search for
     * @return User object
     * @throws RuntimeException if user is not found
     */
    public User getUserByUserNumber(Integer userNumber) {
        return userRepository.findAll().stream()
                .filter(u -> userNumber.equals(u.getUserNumber()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Retrieves all users in the system.
     *
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Completes a user's profile using their user number.
     *
     * @param userNumber User number of the user to update
     * @param request    ProfileUpdateRequest containing profile details
     * @return Updated User object
     */
    public User completeProfileByUserNumber(Integer userNumber, ProfileUpdateRequest request) {
        User user = getUserByUserNumber(userNumber);
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setProfileCompleted(true);
        return userRepository.save(user);
    }

    /**
     * Gets successful referrals for a user by their user number.
     *
     * @param userNumber User number of the referrer
     * @return List of successfully referred users
     */
    public List<User> getSuccessfulReferralsByUserNumber(Integer userNumber) {
        User user = getUserByUserNumber(userNumber);
        return user.getReferrals().stream()
                .map(id -> userRepository.findById(id).orElse(null))
                .filter(u -> u != null && u.isProfileCompleted())
                .collect(Collectors.toList());
    }

    /**
     * Generates a referral report for a user by their user number.
     * The report includes total, successful, and pending referrals.
     *
     * @param userNumber User number to generate report for
     * @return ReferralReport containing referral statistics
     */
    public ReferralReport getReferralReportByUserNumber(Integer userNumber) {
        User user = getUserByUserNumber(userNumber);
        List<String> allReferrals = user.getReferrals();
        int total = allReferrals.size();
        int successful = 0;
        for (String refId : allReferrals) {
            Optional<User> refUser = userRepository.findById(refId);
            if (refUser.isPresent() && refUser.get().isProfileCompleted()) {
                successful++;
            }
        }
        int pending = total - successful;
        return new ReferralReport(total, successful, pending);
    }

    /**
     * Data class representing a user's referral statistics.
     */
    public static class ReferralReport {
        private int totalReferrals;
        private int successfulReferrals;
        private int pendingReferrals;

        public ReferralReport(int totalReferrals, int successfulReferrals, int pendingReferrals) {
            this.totalReferrals = totalReferrals;
            this.successfulReferrals = successfulReferrals;
            this.pendingReferrals = pendingReferrals;
        }

        public int getTotalReferrals() {
            return totalReferrals;
        }

        public int getSuccessfulReferrals() {
            return successfulReferrals;
        }

        public int getPendingReferrals() {
            return pendingReferrals;
        }
    }
}