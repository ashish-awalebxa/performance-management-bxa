import { useEffect, useMemo, useState } from "react";
import { goalsStore, fetchMyGoals } from "../goals.store";
import CreateGoalForm from "../components/CreateGoalForm";
import GoalsTable from "../components/GoalsTable";
import Pagination from "../../../components/common/Pagination";

const STATUS_FILTERS = ["ALL", "DRAFT", "SUBMITTED", "APPROVED", "COMPLETED", "REJECTED"];

const MyGoalsPage = () => {
  const [state, setState] = useState(goalsStore.getState());
  const [showCreate, setShowCreate] = useState(false);
  const [query, setQuery] = useState("");
  const [status, setStatus] = useState("ALL");

  useEffect(() => {
    const unsub = goalsStore.subscribe(setState);
    fetchMyGoals();
    return unsub;
  }, []);

  const filteredGoals = useMemo(() => {
    const normalizedQuery = query.trim().toLowerCase();

    return (state.goals || []).filter((goal) => {
      const goalStatus = goal?.status || "";
      const title = goal?.title?.toLowerCase?.() || "";
      const description = goal?.description?.toLowerCase?.() || "";

      const matchesStatus = status === "ALL" || goalStatus === status;
      const matchesQuery =
        normalizedQuery.length === 0 ||
        title.includes(normalizedQuery) ||
        description.includes(normalizedQuery);

      return matchesStatus && matchesQuery;
    });
  }, [state.goals, query, status]);

  return (
    <div className="space-y-8">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">My Goals</h1>
          <p className="text-sm text-slate-500 mt-1">
            Built for scale: quickly search, filter, and review objectives across large teams.
          </p>
        </div>
        <button
          onClick={() => setShowCreate(!showCreate)}
          className="inline-flex items-center justify-center gap-2 bg-slate-900 hover:bg-slate-800 text-white px-5 py-2.5 rounded-lg text-sm font-medium transition-all shadow-sm"
        >
          {showCreate ? "Cancel Goal" : "+ Set New Goal"}
        </button>
      </div>

      {showCreate && (
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden animate-in fade-in slide-in-from-top-4 duration-300">
          <div className="px-6 py-4 border-b border-slate-100 bg-slate-50">
            <h2 className="font-semibold text-slate-800">Define New Objective</h2>
          </div>
          <div className="p-6">
            <CreateGoalForm onSuccess={() => setShowCreate(false)} />
          </div>
        </div>
      )}

      <div className="bg-white rounded-xl border border-slate-200 p-4 sm:p-5 space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
          <input
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search by title or description"
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-300"
          />

          <select
            value={status}
            onChange={(e) => setStatus(e.target.value)}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-slate-300"
          >
            {STATUS_FILTERS.map((filter) => (
              <option key={filter} value={filter}>
                {filter}
              </option>
            ))}
          </select>
        </div>

        <div className="flex flex-wrap items-center gap-4 text-xs text-slate-600">
          <span className="rounded-full bg-slate-100 px-3 py-1 font-semibold">
            Showing {filteredGoals.length} / {state.goals?.length || 0} goals on this page
          </span>
          {state.loading && <span className="text-slate-500">Loading goals…</span>}
          {state.error && <span className="text-red-600">{state.error}</span>}
        </div>
      </div>

      <div className="space-y-4">
        <h3 className="text-sm font-bold text-slate-400 uppercase tracking-wider">
          Active Objectives
        </h3>
        <GoalsTable goals={filteredGoals} />
      </div>

      <Pagination
        page={state.page || 0}
        totalPages={state.totalPages || 0}
        onPageChange={(nextPage) => fetchMyGoals(nextPage)}
      />
    </div>
  );
};

export default MyGoalsPage;
