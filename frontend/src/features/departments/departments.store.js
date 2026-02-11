import {
  getDepartmentsApi,
  createDepartmentApi,
  updateDepartmentApi,
  deleteDepartmentApi
} from "./departments.api";

const initialState = {
  departments: [],
  loading: false,
  error: null,
  page: 0,
  size: 6,
  totalPages: 0,
  totalElements: 0
};

let state = { ...initialState };
let listeners = [];

export const departmentsStore = {
  getState() {
    return state;
  },
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

export async function fetchDepartments(page = 0, size = state.size) {
  try {
    setState({ loading: true, error: null });

    const res = await getDepartmentsApi({ page, size });
    setState({
      departments: (res.data.content || []).filter((d) => d.active !== false),
      page: res.data.number,
      size: res.data.size,
      totalPages: res.data.totalPages,
      totalElements: res.data.totalElements,
      loading: false
    });
  } catch {
    setState({ error: "Failed to load departments", loading: false });
  }
}

export async function createDepartment(payload) {
  await createDepartmentApi(payload);
  await fetchDepartments(0, state.size);
}

export async function updateDepartment(id, payload) {
  await updateDepartmentApi(id, payload);
  await fetchDepartments(state.page, state.size);
}

export async function deleteDepartment(id) {
  await deleteDepartmentApi(id);
  const targetPage = state.departments.length === 1 && state.page > 0 ? state.page - 1 : state.page;
  await fetchDepartments(targetPage, state.size);
}
