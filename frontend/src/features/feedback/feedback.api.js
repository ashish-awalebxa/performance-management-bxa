import httpClient from "../../services/httpClient";

export const getFeedbackRecipientsApi = () =>
  httpClient.get("/api/feedback/recipients");

export const createFeedbackApi = (payload) =>
  httpClient.post("/api/feedback", payload);

export const getReceivedFeedbackApi = (page = 0, size = 10) =>
  httpClient.get(`/api/feedback/received?page=${page}&size=${size}`);

export const getGivenFeedbackApi = (page = 0, size = 10) =>
  httpClient.get(`/api/feedback/given?page=${page}&size=${size}`);

export const getTeamFeedbackApi = (page = 0, size = 10) =>
  httpClient.get(`/api/feedback/team?page=${page}&size=${size}`);

export const acknowledgeFeedbackApi = (id) =>
  httpClient.patch(`/api/feedback/${id}/acknowledge`);
