import axiosInstance from "./axiosInstance";

const expenseApi = {
  getExpenses: async (params = {}, config = {}) => {
    const response = await axiosInstance.get("/expenses", {
      params,
      ...config,
    });
    return response.data;
  },

  getExpenseById: async (expenseId, config = {}) => {
    const response = await axiosInstance.get(`/expenses/${expenseId}`, config);
    return response.data;
  },

  createExpense: async (expenseData) => {
    const response = await axiosInstance.post("/expenses", expenseData);
    return response.data;
  },

  updateExpense: async (expenseId, expenseData) => {
    const response = await axiosInstance.put(
      `/expenses/${expenseId}`,
      expenseData,
    );
    return response.data;
  },

  deleteExpense: async (expenseId) => {
    await axiosInstance.delete(`/expenses/${expenseId}`);
  },
};

export default expenseApi;
