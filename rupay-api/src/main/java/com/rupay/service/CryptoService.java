package com.rupay.service;

import com.rupay.dto.CommonDTO;
import com.rupay.model.Crypto;
import com.rupay.repository.CryptoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CryptoService {

    private final CryptoRepository cryptoRepository;

    public List<CommonDTO.CryptoResponse> getAllCryptos() {
        return cryptoRepository.findAllByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Crypto getCryptoBySymbol(String symbol) {
        return cryptoRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Crypto not found: " + symbol));
    }

    public CommonDTO.CryptoResponse getCryptoResponse(String symbol) {
        return mapToResponse(getCryptoBySymbol(symbol));
    }

    private CommonDTO.CryptoResponse mapToResponse(Crypto crypto) {
        return CommonDTO.CryptoResponse.builder()
                .id(crypto.getId())
                .symbol(crypto.getSymbol())
                .name(crypto.getName())
                .priceInPkr(crypto.getPriceInPkr())
                .change24h(crypto.getChange24h())
                .icon(crypto.getIcon())
                .build();
    }
}
