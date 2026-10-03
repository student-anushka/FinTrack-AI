package com.fintrack.budget.dto;

import java.util.List;

public class BudgetPageResponse {

    private List<BudgetResponse> budgets;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int pageSize;

    public BudgetPageResponse() {
    }

    public BudgetPageResponse(
            List<BudgetResponse> budgets,
            int currentPage,
            int totalPages,
            long totalElements,
            int pageSize) {

        this.budgets = budgets;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.pageSize = pageSize;
    }

    public List<BudgetResponse> getBudgets() {
        return budgets;
    }

    public void setBudgets(List<BudgetResponse> budgets) {
        this.budgets = budgets;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}