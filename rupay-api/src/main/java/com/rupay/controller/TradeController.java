package com.rupay.controller;

import com.rupay.dto.CommonDTO;
import com.rupay.dto.TradeDTO;
import com.rupay.model.User;
import com.rupay.service.TradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trade")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @PostMapping("/buy")
    public ResponseEntity<CommonDTO.ApiResponse<TradeDTO.TradeResponse>> buy(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TradeDTO.BuyRequest request) {
        try {
            TradeDTO.TradeResponse response = tradeService.buy(user, request);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success("Purchase successful", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/sell")
    public ResponseEntity<CommonDTO.ApiResponse<TradeDTO.TradeResponse>> sell(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TradeDTO.SellRequest request) {
        try {
            TradeDTO.TradeResponse response = tradeService.sell(user, request);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success("Sale successful", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<CommonDTO.ApiResponse<TradeDTO.TradeResponse>> transfer(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TradeDTO.TransferRequest request) {
        try {
            TradeDTO.TradeResponse response = tradeService.transfer(user, request);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success("Transfer successful", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
}
