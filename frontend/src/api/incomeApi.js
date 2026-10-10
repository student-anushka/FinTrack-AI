import axiosInstance from "./axiosInstance";

const incomeApi = {
  getIncomes: async (params = {}, config = {}) => {
    const response = await axiosInstance.get("/incomes", {
      params,
      ...config,
    });
    return response.data;
  },

  getIncomeById: async (incomeId, config = {}) => {
    const response = await axiosInstance.get(`/incomes/${incomeId}`, config);
    return response.data;
  },

  createIncome: async (incomeData) => {
    const response = await axiosInstance.post("/incomes", incomeData);
    return response.data;
  },

  updateIncome: async (incomeId, incomeData) => {
    const response = await axiosInstance.put(
      `/incomes/${incomeId}`,
      incomeData,
    );
    return response.data;
  },

  deleteIncome: async (incomeId) => {
    await axiosInstance.delete(`/incomes/${incomeId}`);
  },
};

export default incomeApi;
