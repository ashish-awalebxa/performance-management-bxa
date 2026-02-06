import { useEffect, useState } from "react";
import {
  ratingsStore,
  fetchRatingsForActiveCycle,
  submitRating,
  updateManagerRating
} from "../ratings.store";

const TeamRatingsPage = () => {
  const [state, setState] = useState(ratingsStore.getState());

  useEffect(() => {
    const unsub = ratingsStore.subscribe(setState);
    fetchRatingsForActiveCycle(); // fetch ratings for manager’s team
    return unsub;
  }, []);

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
          className="bg-white p-5 rounded-xl border shadow-sm space-y-2"
        >
          <p className="font-semibold">
            Employee ID: <span className="font-normal">{r.employeeId}</span>
          </p>

          <p>
            Score: <b>{r.score}</b>
          </p>

          <p className="text-sm text-slate-600">
            Status: <b>{r.status}</b>
          </p>

          {/* 🔥 MANAGER ACTIONS */}

          {r.status === "DRAFT" && (
            <div className="space-y-2 mt-2">
              <select
                value={r.score}
                onChange={(e) =>
                  updateManagerRating(r.id, {
                    score: Number(e.target.value),
                    justification: r.managerJustification || ""
                  })
                }
                className="border p-2"
              >
                {[1,2,3,4,5].map(v => (
                  <option key={v} value={v}>{v}</option>
                ))}
              </select>

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
                onClick={() => submitRating(r.id)}
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
