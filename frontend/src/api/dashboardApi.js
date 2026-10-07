import axiosInstance from "./axiosInstance";

const dashboardApi = {
  getSummary: async () => {
    const response = await axiosInstance.get("/dashboard/summary");
    return response.data;
  },

  getCategorySpending: async () => {
    const response = await axiosInstance.get("/dashboard/category-spending");
    return response.data;
  },

  getMonthlyTrends: async () => {
    const response = await axiosInstance.get("/dashboard/monthly-trends");
    return response.data;
  },

  getBudgetUtilization: async () => {
    const response = await axiosInstance.get("/dashboard/budget-utilization");
    return response.data;
  },

  getGoalProgress: async () => {
    const response = await axiosInstance.get("/dashboard/goal-progress");
    return response.data;
  },

  getFinancialHealth: async () => {
    const response = await axiosInstance.get("/dashboard/financial-health");
    return response.data;
  },

  getRecentActivity: async () => {
    const response = await axiosInstance.get("/dashboard/recent-activity");
    return response.data;
  },
};

export default dashboardApi;
