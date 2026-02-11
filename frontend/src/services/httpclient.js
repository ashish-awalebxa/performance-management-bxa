import axios from "axios";
import { authStore } from "../auth/auth.store";

const httpClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: {
    "Content-Type": "application/json"
  }
});

httpClient.interceptors.request.use((config) => {
  const token = authStore.getState().token;

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

httpClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const method = error?.config?.method?.toLowerCase();
    const shouldAlert = method && method !== "get";

    if (shouldAlert) {
      const message = error?.response?.data?.message || "Operation failed. Please try again.";
      if (typeof window !== "undefined") {
        window.alert(message);
      }
    }

    return Promise.reject(error);
  }
);

export default httpClient;
