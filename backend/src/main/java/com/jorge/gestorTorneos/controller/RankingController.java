package com.jorge.gestorTorneos.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jorge.gestorTorneos.dto.response.RankingGlobalResponse;
import com.jorge.gestorTorneos.service.RankingService;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping("/global")
    public List<RankingGlobalResponse> rankingGlobal() {
        return rankingService.rankingGlobal();
    }
}
