import { useEffect, useState } from "react";
import { ratingsStore, fetchMyRating } from "../ratings.store";

const MyRatingPage = () => {
  const [state, setState] = useState(ratingsStore.getState());

  useEffect(() => {
    const unsub = ratingsStore.subscribe(setState);
    fetchMyRating();
    return unsub;
  }, []);

  const rating = state.ratings[0];

  if (!rating) {
    return (
      <div className="bg-white p-8 rounded-xl border text-center">
        <p className="text-slate-500">
          Your rating is not available yet.
        </p>
      </div>
    );
  }

  return (
    <div className="max-w-2xl space-y-6">
      <h1 className="text-2xl font-bold text-slate-900">
        My Performance Rating
      </h1>

      <div className="bg-white p-6 rounded-xl border space-y-4">
        <div className="flex justify-between">
          <span className="text-sm text-slate-500">Score</span>
          <span className="text-lg font-bold text-blue-600">
            {rating.score} / 5
          </span>
        </div>

        <div>
          <p className="text-sm font-semibold text-slate-600">
            Manager Feedback
          </p>
          <p className="text-slate-700 mt-1">
            {rating.managerJustification || "Pending"}
          </p>
        </div>

        {rating.hrJustification && (
          <div>
            <p className="text-sm font-semibold text-slate-600">
              HR Calibration
            </p>
            <p className="text-slate-700 mt-1">
              {rating.hrJustification}
            </p>
          </div>
        )}

        <div className="pt-2">
          <span
            className={`text-xs font-bold px-3 py-1 rounded-full ${
              rating.status === "FINALIZED"
                ? "bg-green-100 text-green-700"
                : "bg-yellow-100 text-yellow-700"
            }`}
          >
            {rating.status}
          </span>
        </div>
      </div>
    </div>
  );
};

export default MyRatingPage;
