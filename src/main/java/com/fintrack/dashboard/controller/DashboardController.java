package com.fintrack.dashboard.controller;

import com.fintrack.dashboard.dto.CategorySpendingResponse;
import com.fintrack.dashboard.dto.DashboardSummaryResponse;
import com.fintrack.dashboard.dto.MonthlyTrendResponse;
import com.fintrack.dashboard.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService) {

        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary(
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getName());

        DashboardSummaryResponse response = dashboardService.getSummary(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/category-spending")
    public ResponseEntity<List<CategorySpendingResponse>> getCategoryWiseSpending(
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getName());

        List<CategorySpendingResponse> response = dashboardService.getCategoryWiseSpending(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/monthly-trends")
    public ResponseEntity<List<MonthlyTrendResponse>> getMonthlyTrends(
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getName());

        List<MonthlyTrendResponse> response = dashboardService.getMonthlyTrends(userId);

        return ResponseEntity.ok(response);
    }
}