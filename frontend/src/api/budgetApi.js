import axiosInstance from "./axiosInstance";

const budgetApi = {
  getBudgets: async (params = {}, config = {}) => {
    const response = await axiosInstance.get("/budgets", {
      ...config,
      params,
    });
    return response.data;
  },

  getBudgetById: async (budgetId, config = {}) => {
    const response = await axiosInstance.get(`/budgets/${budgetId}`, config);
    return response.data;
  },

  createBudget: async (budgetData) => {
    const response = await axiosInstance.post("/budgets", budgetData);
    return response.data;
  },

  updateBudget: async (budgetId, budgetData) => {
    const response = await axiosInstance.put(
      `/budgets/${budgetId}`,
      budgetData,
    );
    return response.data;
  },

  deleteBudget: async (budgetId) => {
    await axiosInstance.delete(`/budgets/${budgetId}`);
  },
};

export default budgetApi;
