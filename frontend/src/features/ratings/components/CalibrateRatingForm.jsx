import { useState } from "react";
import { calibrateRating } from "../ratings.store";

const CalibrateRatingForm = ({ rating, onDone }) => {
  if (rating.status !== "MANAGER_SUBMITTED") return null;

  const [newScore, setNewScore] = useState(rating.score);
  const [justification, setJustification] = useState("");

  const onCalibrate = async () => {
    try {
      await calibrateRating(rating.id, {
        newScore,
        justification
      });
      alert("Rating calibrated successfully.");
      if (onDone) onDone();
    } catch (e) {
      alert(e?.response?.data?.message || "Unable to calibrate rating.");
    }
  };

  return (
    <div className="space-y-2 mt-3 border-t pt-3">
      <select
        className="border p-2 w-full"
        value={newScore}
        onChange={(e) => setNewScore(Number(e.target.value))}
      >
        {[1, 2, 3, 4, 5].map((v) => (
          <option key={v} value={v}>{v}</option>
        ))}
      </select>

      <textarea
        rows={3}
        className="border p-2 w-full"
        placeholder="Calibration justification"
        value={justification}
        onChange={(e) => setJustification(e.target.value)}
      />

      <button
        disabled={!justification}
        onClick={onCalibrate}
        className="px-3 py-1 bg-purple-600 text-white rounded disabled:opacity-50"
      >
        Calibrate
      </button>
    </div>
  );
};

export default CalibrateRatingForm;
