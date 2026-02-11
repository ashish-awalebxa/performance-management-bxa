import httpClient from "../../services/httpClient";

export const getMyGoalsApi = (page = 0, size = 10) =>
  httpClient.get(`/api/goals?page=${page}&size=${size}`);

export const getGoalByIdApi = (id) =>
  httpClient.get(`/api/goals/${id}`);

export const createGoalApi = (payload) =>
  httpClient.post("/api/goals", payload);

export const updateGoalApi = (id, payload) =>
  httpClient.put(`/api/goals/${id}`, payload);

export const deleteGoalApi = (id) =>
  httpClient.delete(`/api/goals/${id}`);

export const submitGoalApi = (id) =>
  httpClient.post(`/api/goals/${id}/submit`);

export const approveGoalApi = (id) =>
  httpClient.post(`/api/goals/${id}/approve`);

export const getTeamGoalsApi = (page = 0, size = 10) =>
  httpClient.get(`/api/goals/team?page=${page}&size=${size}`);

export const rejectGoalApi = (id, reason) =>
  httpClient.post(`/api/goals/${id}/reject`, { reason });

export const updateKeyResultProgressApi = (id, currentValue) =>
  httpClient.patch(`/api/key-results/${id}/progress`, {
    currentValue
  });
