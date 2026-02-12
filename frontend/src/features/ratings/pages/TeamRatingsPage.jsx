import { useEffect, useMemo, useState } from "react";
import { getTeamGoalsApi } from "../../goals/goals.api";
import {
  ratingsStore,
  fetchRatingsForActiveCycle,
  submitRating,
  updateManagerRating
} from "../ratings.store";

const TeamRatingsPage = () => {
  const [state, setState] = useState(ratingsStore.getState());
  const [teamGoals, setTeamGoals] = useState([]);

  useEffect(() => {
    const unsub = ratingsStore.subscribe(setState);

    const loadData = async () => {
      await fetchRatingsForActiveCycle();
      const teamGoalsRes = await getTeamGoalsApi(0, 200);
      setTeamGoals(teamGoalsRes.data?.content || []);
    };

    loadData();
    return unsub;
  }, []);

  const goalsByEmployee = useMemo(() => {
    return teamGoals.reduce((acc, goal) => {
      const employeeGoals = acc[goal.employeeId] || [];
      employeeGoals.push(goal);
      acc[goal.employeeId] = employeeGoals;
      return acc;
    }, {});
  }, [teamGoals]);

  if (state.error) {
    return (
      <div>
        <h1 className="text-2xl font-bold">Team Ratings</h1>
        <p className="text-red-600 mt-4">{state.error}</p>
      </div>
    );
  }

  if (!state.ratings.length) {
    return (
      <div>
        <h1 className="text-2xl font-bold">Team Ratings</h1>
        <p className="text-slate-500 mt-4">No ratings available yet.</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold">Team Ratings</h1>

      {state.ratings.map((r) => (
        <div
          key={r.id}
          className="bg-white p-5 rounded-xl border shadow-sm space-y-3"
        >
          <p className="font-semibold">
            Employee: <span className="font-normal">{r.employeeName || "Unknown"}</span>
          </p>

          <p className="text-sm text-slate-600">
            Employee ID: <span className="font-normal">{r.employeeId}</span>
          </p>

          <p>
            Calculated Score: <b>{r.score}</b>
          </p>

          <p className="text-sm text-slate-600">
            Rating Status: <b>{r.status}</b>
          </p>

          <div className="bg-slate-50 border rounded p-3 space-y-2">
            <p className="text-sm font-semibold">Goals used for score calculation</p>
            {(goalsByEmployee[r.employeeId] || []).length === 0 ? (
              <p className="text-sm text-slate-500">No goals found in active cycle.</p>
            ) : (
              <div className="space-y-3">
                {(goalsByEmployee[r.employeeId] || []).map((goal) => (
                  <div key={goal.id} className="border rounded p-2 bg-white">
                    <p className="text-sm font-medium">{goal.title}</p>
                    <p className="text-xs text-slate-600">Goal Status: <b>{goal.status}</b></p>
                    <div className="mt-1 space-y-1">
                      {(goal.keyResults || []).map((kr) => (
                        <p key={kr.id} className="text-xs text-slate-700">
                          {kr.metric}: <b>{kr.currentValue}</b> / {kr.targetValue}
                        </p>
                      ))}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          {r.status === "DRAFT" && (
            <div className="space-y-2 mt-2">
              <textarea
                placeholder="Manager justification"
                className="border p-2 w-full"
                onBlur={(e) =>
                  updateManagerRating(r.id, {
                    score: r.score,
                    justification: e.target.value
                  })
                }
              />

              <button
                onClick={async () => {
                  await submitRating(r.id);
                  await fetchRatingsForActiveCycle();
                }}
                className="px-3 py-1 bg-blue-600 text-white rounded"
              >
                Submit Rating
              </button>
            </div>
          )}

          {r.status === "MANAGER_SUBMITTED" && (
            <p className="text-green-600 font-medium mt-2">
              Submitted ✔ Waiting for HR calibration
            </p>
          )}
        </div>
      ))}
    </div>
  );
};

export default TeamRatingsPage;
