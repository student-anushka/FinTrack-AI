import axiosInstance from "./axiosInstance";

const categoryApi = {
  getCategories: async (config = {}) => {
    const response = await axiosInstance.get("/categories", config);
    return response.data;
  },
};

export default categoryApi;
