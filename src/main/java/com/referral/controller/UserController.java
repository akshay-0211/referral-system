package com.referral.controller;

import com.referral.dto.ProfileUpdateRequest;
import com.referral.dto.SignupRequest;
import com.referral.model.User;
import com.referral.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@Valid @RequestBody SignupRequest request) {
        User user = userService.signup(request);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userNumber}/profile")
    public ResponseEntity<User> completeProfile(
            @PathVariable Integer userNumber,
            @Valid @RequestBody ProfileUpdateRequest request) {
        User user = userService.completeProfileByUserNumber(userNumber, request);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{userNumber}/referrals")
    public ResponseEntity<List<User>> getSuccessfulReferrals(@PathVariable Integer userNumber) {
        List<User> referrals = userService.getSuccessfulReferralsByUserNumber(userNumber);
        return ResponseEntity.ok(referrals);
    }

    @GetMapping("/{userNumber}/referral-report")
    public ResponseEntity<UserService.ReferralReport> getReferralReport(@PathVariable Integer userNumber) {
        return ResponseEntity.ok(userService.getReferralReportByUserNumber(userNumber));
    }

    @GetMapping("")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/referral-report-csv")
    public ResponseEntity<byte[]> getReferralReportCsv() {
        List<User> users = userService.getAllUsers();
        StringBuilder csv = new StringBuilder();
        csv.append("UserNumber,Name,Email,ReferralCode,ReferredBy,ProfileCompleted,Referrals\n");
        for (User user : users) {
            csv.append(user.getUserNumber() != null ? user.getUserNumber() : "").append(",")
                    .append(user.getName() != null ? user.getName() : "").append(",")
                    .append(user.getEmail() != null ? user.getEmail() : "").append(",")
                    .append(user.getReferralCode() != null ? user.getReferralCode() : "").append(",")
                    .append(user.getReferredBy() != null ? user.getReferredBy() : "").append(",")
                    .append(user.isProfileCompleted()).append(",")
                    .append(user.getReferrals() != null ? String.join("|", user.getReferrals()) : "")
                    .append("\n");
        }
        byte[] csvBytes = csv.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=referral_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvBytes);
    }
}