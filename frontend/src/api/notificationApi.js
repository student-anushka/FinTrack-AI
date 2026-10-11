import axiosInstance from "./axiosInstance";

const notificationApi = {
  getNotifications: async (config = {}) => {
    const response = await axiosInstance.get("/notifications", config);
    return response.data;
  },

  getUnreadCount: async (config = {}) => {
    const response = await axiosInstance.get(
      "/notifications/unread-count",
      config,
    );
    return response.data;
  },

  markAsRead: async (notificationId) => {
    const response = await axiosInstance.patch(
      `/notifications/${notificationId}/read`,
    );
    return response.data;
  },

  markAllAsRead: async () => {
    await axiosInstance.patch("/notifications/read-all");
  },

  deleteNotification: async (notificationId) => {
    await axiosInstance.delete(`/notifications/${notificationId}`);
  },
};

export default notificationApi;
