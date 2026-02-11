import {
  submitRating,
  finalizeRating
} from "../ratings.store";
import CalibrateRatingForm from "./CalibrateRatingForm";

const RatingCard = ({ rating, role }) => {
  const onSubmit = async () => {
    try {
      await submitRating(rating.id);
      alert("Rating submitted successfully.");
    } catch (e) {
      alert(e?.response?.data?.message || "Unable to submit rating.");
    }
  };

  const onFinalize = async () => {
    try {
      await finalizeRating(rating.id);
      alert("Rating finalized successfully.");
    } catch (e) {
      alert(e?.response?.data?.message || "Unable to finalize rating.");
    }
  };

  return (
    <div className="border p-4 rounded space-y-2">
      <p>Employee: {rating.employeeName || "Unknown"}</p>
      <p>Employee ID: {rating.employeeId}</p>
      <p>Status: {rating.status}</p>
      <p>Score: {rating.score}</p>

      {role === "MANAGER" && rating.status === "DRAFT" && (
        <button
          onClick={onSubmit}
          className="px-3 py-1 bg-green-600 text-white rounded"
        >
          Submit
        </button>
      )}

      {role === "HR" && rating.status === "MANAGER_SUBMITTED" && (
        <CalibrateRatingForm rating={rating} />
      )}

      {role === "LEADERSHIP" && rating.status === "HR_CALIBRATED" && (
        <button
          onClick={onFinalize}
          className="px-3 py-1 bg-black text-white rounded"
        >
          Finalize
        </button>
      )}
    </div>
  );
};

export default RatingCard;
