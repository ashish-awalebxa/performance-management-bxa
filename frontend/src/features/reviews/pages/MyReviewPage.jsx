import { useEffect, useState } from "react";
import { getMyReviewApi, submitSelfReviewApi } from "../reviews.api";

const MyReviewPage = () => {
  const [review, setReview] = useState(null);
  const [comments, setComments] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const loadReview = async () => {
    setError("");
    try {
      const res = await getMyReviewApi();
      setReview(res.data);
      setComments(res.data.selfReviewComments || "");
    } catch (e) {
      setError(e?.response?.data?.message || "Failed to load review.");
    }
  };

  useEffect(() => {
    loadReview();
  }, []);

  if (!review) return <p>{error || "No active review cycle."}</p>;

  const submit = async () => {
    setLoading(true);
    setError("");
    try {
      await submitSelfReviewApi(review.id, comments);
      alert("Self review submitted");
      await loadReview();
    } catch (e) {
      const message = e?.response?.data?.message || "Failed to submit self review.";
      setError(message);
      alert(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-3xl space-y-6">
      <h1 className="text-2xl font-bold">My Review</h1>

      <div className="bg-white p-6 rounded-xl border">
        <h2 className="font-semibold mb-2">Self Review</h2>

        {error ? <p className="text-sm text-red-600 mb-3">{error}</p> : null}

        <textarea
          rows={6}
          value={comments}
          onChange={(e) => setComments(e.target.value)}
          className="w-full border rounded p-3"
        />

        <button
          disabled={loading}
          onClick={submit}
          className="mt-4 bg-blue-600 text-white px-6 py-2 rounded"
        >
          Submit Self Review
        </button>
      </div>
    </div>
  );
};

export default MyReviewPage;
