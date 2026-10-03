package com.fintrack.income.dto;

import java.util.List;

public class IncomePageResponse {

    private List<IncomeResponse> incomes;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int pageSize;

    public IncomePageResponse() {
    }

    public IncomePageResponse(
            List<IncomeResponse> incomes,
            int currentPage,
            int totalPages,
            long totalElements,
            int pageSize) {

        this.incomes = incomes;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.pageSize = pageSize;
    }

    public List<IncomeResponse> getIncomes() {
        return incomes;
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

    public void setIncomes(List<IncomeResponse> incomes) {
        this.incomes = incomes;
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