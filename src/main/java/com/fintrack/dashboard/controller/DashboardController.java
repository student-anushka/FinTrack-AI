package com.fintrack.dashboard.controller;

import com.fintrack.dashboard.dto.DashboardSummaryResponse;
import com.fintrack.dashboard.service.DashboardService;
import com.fintrack.dashboard.dto.CategorySpendingResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService) {

        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary(Authentication authentication) {

        return ResponseEntity.ok(
                dashboardService.getSummary(
                        authentication));
    }

    @GetMapping("/category-spending")
    public ResponseEntity<List<CategorySpendingResponse>> getCategoryWiseSpending(
            Authentication authentication) {

        return ResponseEntity.ok(
                dashboardService.getCategoryWiseSpending(
                        authentication));
    }
}