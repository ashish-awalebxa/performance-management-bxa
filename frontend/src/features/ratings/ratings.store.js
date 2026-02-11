import httpClient from "../../services/httpClient";
import { authStore } from "../../auth/auth.store";

import {
  createRatingApi,
  submitRatingApi,
  calibrateRatingApi,
  finalizeRatingApi,
  getRatingsForCycleApi,
  fetchMyRatingApi
} from "./ratings.api";

const initialState = {
  ratings: [],
  loading: false,
  error: null
};

let state = { ...initialState };
let listeners = [];

export const ratingsStore = {
  getState: () => state,
  subscribe(listener) {
    listeners.push(listener);
    return () => {
      listeners = listeners.filter(l => l !== listener);
    };
  }
};

function setState(newState) {
  state = { ...state, ...newState };
  listeners.forEach(l => l(state));
}

/* ================= FETCH ================= */

export async function fetchRatingsForActiveCycle() {
  try {
    setState({ loading: true, error: null });
    const cycleRes = await httpClient.get("/api/performance-cycles/active-cycle");

    if (!cycleRes.data) {
      setState({ ratings: [], loading: false });
      return;
    }

    const cycleId = cycleRes.data.id;
    const ratingsRes = await getRatingsForCycleApi(cycleId);
    const role = authStore.getState().user?.role;

    let ratings = ratingsRes.data.content || [];
    if (role === "HR") {
      ratings = ratings.filter((rating) => rating.status === "MANAGER_SUBMITTED");
    }
    if (role === "LEADERSHIP") {
      ratings = ratings.filter((rating) => rating.status === "HR_CALIBRATED");
    }

    setState({ ratings, loading: false });
  } catch (e) {
    setState({
      ratings: [],
      loading: false,
      error: e?.response?.data?.message || "Failed to load ratings"
    });
  }
}

/* ================= ACTIONS ================= */

export async function createRating(payload) {
  await createRatingApi(payload);
}

export async function submitRating(id) {
  await submitRatingApi(id);
  await fetchRatingsForActiveCycle();
}

export async function calibrateRating(id, payload) {
  await calibrateRatingApi(id, payload);
  await fetchRatingsForActiveCycle();
}

export async function finalizeRating(id) {
  await finalizeRatingApi(id);
  await fetchRatingsForActiveCycle();
}

export async function fetchMyRating() {
  setState({ loading: true });

  try {
    const res = await fetchMyRatingApi();
    setState({
      ratings: [res.data],
      loading: false
    });
  } catch {
    setState({
      ratings: [],
      loading: false
    });
  }
}

export async function updateManagerRating(id, payload) {
  await httpClient.put(`/api/ratings/${id}/manager`, payload);
  await fetchRatingsForActiveCycle();
}
