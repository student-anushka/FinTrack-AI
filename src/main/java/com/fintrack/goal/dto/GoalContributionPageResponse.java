package com.fintrack.goal.dto;

import java.util.List;

public class GoalContributionPageResponse {

    private List<GoalContributionResponse> contributions;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int pageSize;

    public GoalContributionPageResponse() {
    }

    public GoalContributionPageResponse(
            List<GoalContributionResponse> contributions,
            int currentPage,
            int totalPages,
            long totalElements,
            int pageSize) {

        this.contributions = contributions;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.pageSize = pageSize;
    }

    public List<GoalContributionResponse> getContributions() {
        return contributions;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setContributions(
            List<GoalContributionResponse> contributions) {
        this.contributions = contributions;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}