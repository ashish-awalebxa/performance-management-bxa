import httpClient from "../../services/httpClient";


import {
  createRatingApi,
  submitRatingApi,
  calibrateRatingApi,
  finalizeRatingApi,
  getRatingsForCycleApi,
  getMyRatingApi
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
  const cycleRes = await httpClient.get(
    "/api/performance-cycles/active-cycle"
  );

  if (!cycleRes.data) return;

  const cycleId = cycleRes.data.id;

  const ratingsRes = await getRatingsForCycleApi(cycleId);
  setState({ ratings: ratingsRes.data.content });
}


/* ================= ACTIONS ================= */

export async function createRating(payload) {
  await createRatingApi(payload);
}

export async function submitRating(id) {
  await submitRatingApi(id);
}

export async function calibrateRating(id, payload) {
  await calibrateRatingApi(id, payload);
}

export async function finalizeRating(id) {
  await finalizeRatingApi(id);
}

export async function fetchMyRating() {
  setState({ loading: true });

  try {
    const res = await getMyRatingApi();
    setState({
      ratings: [res.data], // single rating
      loading: false
    });
  } catch (e) {
    setState({
      ratings: [],
      loading: false
    });
  }
}

export async function updateManagerRating(id, payload) {
  await httpClient.put(`/api/ratings/${id}/manager`, payload);
  fetchRatingsForActiveCycle();
}


