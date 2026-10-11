import axiosInstance from "./axiosInstance";

const goalApi = {
  // Fetch all goals belonging to the authenticated user.
  getGoals: async (config = {}) => {
    const response = await axiosInstance.get("/goals", config);
    return response.data;
  },

  getGoalById: async (goalId, config = {}) => {
    const response = await axiosInstance.get(`/goals/${goalId}`, config);
    return response.data;
  },

  createGoal: async (goalData) => {
    const response = await axiosInstance.post("/goals", goalData);
    return response.data;
  },

  updateGoal: async (goalId, goalData) => {
    const response = await axiosInstance.put(`/goals/${goalId}`, goalData);
    return response.data;
  },

  deleteGoal: async (goalId) => {
    await axiosInstance.delete(`/goals/${goalId}`);
  },

  addContribution: async (goalId, contributionData) => {
    const response = await axiosInstance.post(
      `/goals/${goalId}/contributions`,
      contributionData,
    );
    return response.data;
  },

  getContributions: async (goalId, params = {}, config = {}) => {
    const response = await axiosInstance.get(`/goals/${goalId}/contributions`, {
      ...config,
      params,
    });
    return response.data;
  },
};

export default goalApi;
