import {
  getMyGoalsApi,
  createGoalApi,
  updateGoalApi,
  deleteGoalApi,
  submitGoalApi,
  approveGoalApi,
  rejectGoalApi,
  getTeamGoalsApi,
  updateKeyResultProgressApi
} from "./goals.api";

const PAGE_SIZE = 25;

const initialState = {
  goals: [],
  teamGoals: [],
  page: 0,
  totalPages: 0,
  loading: false,
  error: null
};

let state = { ...initialState };
let listeners = [];
let myGoalsRequestId = 0;
let teamGoalsRequestId = 0;

export const goalsStore = {
  getState: () => state,
  subscribe(listener) {
    listeners.push(listener);
    return () => {
      listeners = listeners.filter((l) => l !== listener);
    };
  }
};

function setState(newState) {
  state = { ...state, ...newState };
  listeners.forEach((l) => l(state));
}

function normalizePagedResponse(response) {
  const data = response?.data ?? {};
  return {
    content: Array.isArray(data.content) ? data.content : [],
    page: Number.isInteger(data.number) ? data.number : 0,
    totalPages: Number.isInteger(data.totalPages) ? data.totalPages : 0
  };
}

export async function fetchMyGoals(page = 0) {
  const requestId = ++myGoalsRequestId;
  try {
    setState({ loading: true, error: null });
    const res = await getMyGoalsApi(page, PAGE_SIZE);

    if (requestId !== myGoalsRequestId) {
      return;
    }

    const normalized = normalizePagedResponse(res);
    setState({
      goals: normalized.content,
      page: normalized.page,
      totalPages: normalized.totalPages,
      loading: false
    });
  } catch (e) {
    if (requestId !== myGoalsRequestId) {
      return;
    }

    setState({
      goals: [],
      loading: false,
      error: e?.response?.data?.message || "Unable to load goals right now."
    });
  }
}

export async function createGoal(payload) {
  try {
    setState({ error: null });
    await createGoalApi(payload);
    await fetchMyGoals(0);
    return { ok: true };
  } catch (e) {
    const message = getErrorMessage(e, "Unable to create goal.");
    setState({ error: message });
    return { ok: false, message };
  }
}

export async function updateGoal(id, payload) {
  try {
    setState({ error: null });
    await updateGoalApi(id, payload);
    await fetchMyGoals(state.page);
    return { ok: true };
  } catch (e) {
    const message = getErrorMessage(e, "Unable to update goal.");
    setState({ error: message });
    return { ok: false, message };
  }
}

export async function deleteGoal(id) {
  try {
    setState({ error: null });
    await deleteGoalApi(id);
    await fetchMyGoals(state.page);
    return { ok: true };
  } catch (e) {
    const message = getErrorMessage(e, "Unable to delete goal.");
    setState({ error: message });
    return { ok: false, message };
  }
}

export async function submitGoal(id) {
  await submitGoalApi(id);
  fetchMyGoals(state.page);
}

export async function approveGoal(id) {
  await approveGoalApi(id);
  fetchTeamGoals(state.page);
}

export async function rejectGoal(id, reason) {
  await rejectGoalApi(id, reason);
  fetchTeamGoals(state.page);
}

export async function fetchTeamGoals(page = 0) {
  const requestId = ++teamGoalsRequestId;
  try {
    setState({ loading: true, error: null });
    const res = await getTeamGoalsApi(page, PAGE_SIZE);

    if (requestId !== teamGoalsRequestId) {
      return;
    }

    const normalized = normalizePagedResponse(res);
    setState({
      teamGoals: normalized.content,
      page: normalized.page,
      totalPages: normalized.totalPages,
      loading: false
    });
  } catch (e) {
    if (requestId !== teamGoalsRequestId) {
      return;
    }

    setState({
      teamGoals: [],
      loading: false,
      error: e?.response?.data?.message || "Unable to load team goals right now."
    });
  }

  return goals.map((goal) => ({
    ...goal,
    keyResults: Array.isArray(goal.keyResults)
      ? goal.keyResults.map((kr) =>
          kr.id === keyResultId ? { ...kr, currentValue: value } : kr
        )
      : []
  }));
}

function updateKeyResultInGoals(goals, keyResultId, value) {
  if (!Array.isArray(goals)) {
    return [];
  }

  return goals.map((goal) => ({
    ...goal,
    keyResults: Array.isArray(goal.keyResults)
      ? goal.keyResults.map((kr) =>
          kr.id === keyResultId ? { ...kr, currentValue: value } : kr
        )
      : []
  }));
}

export async function updateKeyResultProgress(keyResultId, value) {
  try {
    setState({ error: null });
    await updateKeyResultProgressApi(keyResultId, value);

    setState({
      goals: updateKeyResultInGoals(state.goals, keyResultId, value),
      teamGoals: updateKeyResultInGoals(state.teamGoals, keyResultId, value)
    });
  } catch {
    alert("Failed to update progress");
  }
}
