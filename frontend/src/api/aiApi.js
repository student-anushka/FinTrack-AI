import axiosInstance from "./axiosInstance";

const aiApi = {
  askQuestion: async (question) => {
    const response = await axiosInstance.post("/ai/chat", {
      question,
    });

    return response.data;
  },
};

export default aiApi;
