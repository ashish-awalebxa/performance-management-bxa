import { useEffect, useState } from "react";
import { ratingsStore, fetchRatingsForActiveCycle } from "../ratings.store";
import RatingCard from "../components/RatingCard";
import { authStore } from "../../../auth/auth.store";

const FinalRatingsPage = () => {
  const [state, setState] = useState(ratingsStore.getState());
  const role = authStore.getState().user.role;

  useEffect(() => {
    const unsub = ratingsStore.subscribe(setState);
    fetchRatingsForActiveCycle();
    return unsub;
  }, []);

  return (
    <div className="space-y-4">
      <h2 className="text-xl font-bold">Final Ratings</h2>
      {state.ratings.map(r => (
        <RatingCard key={r.id} rating={r} role={role} />
      ))}
    </div>
  );
};

export default FinalRatingsPage;
