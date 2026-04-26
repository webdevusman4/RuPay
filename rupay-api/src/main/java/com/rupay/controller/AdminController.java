package com.rupay.controller;

import com.rupay.dto.AdminDTO;
import com.rupay.dto.CommonDTO;
import com.rupay.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<CommonDTO.ApiResponse<AdminDTO.AdminDashboard>> getDashboard() {
        AdminDTO.AdminDashboard dashboard = adminService.getDashboard();
        return ResponseEntity.ok(CommonDTO.ApiResponse.success(dashboard));
    }

    @GetMapping("/users")
    public ResponseEntity<CommonDTO.ApiResponse<List<AdminDTO.UserSummary>>> getAllUsers() {
        List<AdminDTO.UserSummary> users = adminService.getAllUsers();
        return ResponseEntity.ok(CommonDTO.ApiResponse.success(users));
    }

    @GetMapping("/withdrawals/pending")
    public ResponseEntity<CommonDTO.ApiResponse<List<AdminDTO.WithdrawRequestSummary>>> getPendingWithdrawals() {
        List<AdminDTO.WithdrawRequestSummary> requests = adminService.getPendingWithdrawals();
        return ResponseEntity.ok(CommonDTO.ApiResponse.success(requests));
    }

    @PostMapping("/withdrawals/{id}/process")
    public ResponseEntity<CommonDTO.ApiResponse<String>> processWithdrawal(
            @PathVariable Long id,
            @RequestBody AdminDTO.ProcessWithdrawRequest request) {
        try {
            adminService.processWithdrawRequest(id, request.isApprove());
            String message = request.isApprove() 
                    ? "Withdrawal approved successfully" 
                    : "Withdrawal rejected";
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(message, message));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
}
