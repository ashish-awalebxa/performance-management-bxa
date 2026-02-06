import httpClient from "../../services/httpClient";

export const createRatingApi = (payload) =>
  httpClient.post("/api/ratings", payload);

export const getMyRatingApi = () =>
    httpClient.get("/api/ratings/my");

export const submitRatingApi = (id) =>
  httpClient.post(`/api/ratings/${id}/submit`);

export const calibrateRatingApi = (id, payload) =>
  httpClient.post(`/api/ratings/${id}/calibrate`, payload);

export const finalizeRatingApi = (id) =>
  httpClient.post(`/api/ratings/${id}/finalize`);

export const getRatingsForCycleApi = (cycleId, page = 0, size = 10) =>
  httpClient.get(`/api/ratings?cycleId=${cycleId}&page=${page}&size=${size}`);

export const updateManagerRating = (id, payload) =>
  httpClient.put(`/api/ratings/${id}/manager`, payload);
