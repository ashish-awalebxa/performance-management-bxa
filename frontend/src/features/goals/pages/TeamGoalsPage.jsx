import { useEffect, useMemo, useState } from "react";
import { goalsStore, fetchTeamGoals } from "../goals.store";
import GoalsTable from "../components/GoalsTable";
import { GOAL_STATUS } from "../goals.constants";
import Pagination from "../../../components/common/Pagination";

const TeamGoalsPage = () => {
  const [state, setState] = useState(goalsStore.getState());
  const [query, setQuery] = useState("");

  useEffect(() => {
    const unsub = goalsStore.subscribe(setState);
    fetchTeamGoals();
    return unsub;
  }, []);

  const pendingGoals = useMemo(() => {
    const normalizedQuery = query.trim().toLowerCase();

    return (state.teamGoals || []).filter((g) => {
      const title = g?.title?.toLowerCase?.() || "";
      const description = g?.description?.toLowerCase?.() || "";
      const isSubmitted = g.status === GOAL_STATUS.SUBMITTED;
      const matchesSearch =
        normalizedQuery.length === 0 ||
        title.includes(normalizedQuery) ||
        description.includes(normalizedQuery);

      return isSubmitted && matchesSearch;
    });
  }, [state.teamGoals, query]);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-slate-900">Team Goals</h1>
        <p className="text-sm text-slate-500 mt-1">
          Scalable approval inbox for high-volume teams — review submitted goals faster.
        </p>
      </div>

      <div className="bg-white rounded-xl border border-slate-200 p-4 sm:p-5 space-y-4">
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search submitted goals by title or description"
          className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-300"
        />

        <div className="flex flex-wrap items-center gap-4 text-xs text-slate-600">
          <span className="rounded-full bg-yellow-100 text-yellow-800 px-3 py-1 font-semibold">
            Pending approvals: {pendingGoals.length}
          </span>
          {state.loading && <span className="text-slate-500">Loading goals…</span>}
          {state.error && <span className="text-red-600">{state.error}</span>}
        </div>
      </div>

      <GoalsTable goals={pendingGoals} />

      <Pagination
        page={state.page || 0}
        totalPages={state.totalPages || 0}
        onPageChange={(nextPage) => fetchTeamGoals(nextPage)}
      />
    </div>
  );
};

export default TeamGoalsPage;
