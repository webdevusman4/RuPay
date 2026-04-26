package com.rupay.controller;

import com.rupay.dto.CommonDTO;
import com.rupay.service.CryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
public class CryptoController {

    private final CryptoService cryptoService;

    @GetMapping
    public ResponseEntity<CommonDTO.ApiResponse<List<CommonDTO.CryptoResponse>>> getAllCryptos() {
        List<CommonDTO.CryptoResponse> cryptos = cryptoService.getAllCryptos();
        return ResponseEntity.ok(CommonDTO.ApiResponse.success(cryptos));
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<CommonDTO.ApiResponse<CommonDTO.CryptoResponse>> getCrypto(
            @PathVariable String symbol) {
        try {
            CommonDTO.CryptoResponse crypto = cryptoService.getCryptoResponse(symbol);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(crypto));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
}
