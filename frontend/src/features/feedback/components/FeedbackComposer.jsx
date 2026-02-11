import { useEffect, useState } from "react";
import { createFeedbackApi, getFeedbackRecipientsApi } from "../feedback.api";

const initialPayload = {
  recipientId: "",
  type: "APPRECIATION",
  visibility: "PRIVATE",
  title: "",
  message: "",
  actionItems: ""
};

const FeedbackComposer = ({ onCreated }) => {
  const [payload, setPayload] = useState(initialPayload);
  const [recipients, setRecipients] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    getFeedbackRecipientsApi().then((res) => setRecipients(res.data || []));
  }, []);

  const submit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await createFeedbackApi({
        ...payload,
        recipientId: Number(payload.recipientId)
      });
      setPayload(initialPayload);
      onCreated?.();
    } catch (err) {
      alert(err?.response?.data?.message || "Unable to submit feedback");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={submit} className="bg-white rounded-xl border border-slate-200 p-5 space-y-4 shadow-sm">
      <h2 className="text-lg font-semibold text-slate-900">Share Continuous Feedback</h2>

      <div className="grid md:grid-cols-3 gap-3">
        <select
          required
          value={payload.recipientId}
          onChange={(e) => setPayload((prev) => ({ ...prev, recipientId: e.target.value }))}
          className="border border-slate-300 rounded-lg px-3 py-2 text-sm"
        >
          <option value="">Select recipient</option>
          {recipients.map((user) => (
            <option key={user.id} value={user.id}>
              {user.name} ({user.role})
            </option>
          ))}
        </select>

        <select
          value={payload.type}
          onChange={(e) => setPayload((prev) => ({ ...prev, type: e.target.value }))}
          className="border border-slate-300 rounded-lg px-3 py-2 text-sm"
        >
          <option value="APPRECIATION">Appreciation</option>
          <option value="DEVELOPMENT">Development</option>
          <option value="PEER">Peer</option>
          <option value="MANAGER">Manager</option>
        </select>

        <select
          value={payload.visibility}
          onChange={(e) => setPayload((prev) => ({ ...prev, visibility: e.target.value }))}
          className="border border-slate-300 rounded-lg px-3 py-2 text-sm"
        >
          <option value="PRIVATE">Private</option>
          <option value="MANAGER_AND_EMPLOYEE">Manager & Employee</option>
          <option value="PUBLIC">Public</option>
        </select>
      </div>

      <input
        required
        placeholder="Title"
        value={payload.title}
        onChange={(e) => setPayload((prev) => ({ ...prev, title: e.target.value }))}
        className="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm"
      />

      <textarea
        required
        rows={4}
        placeholder="What went well? What can improve?"
        value={payload.message}
        onChange={(e) => setPayload((prev) => ({ ...prev, message: e.target.value }))}
        className="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm"
      />

      <textarea
        rows={2}
        placeholder="Optional action items"
        value={payload.actionItems}
        onChange={(e) => setPayload((prev) => ({ ...prev, actionItems: e.target.value }))}
        className="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm"
      />

      <button
        type="submit"
        disabled={loading}
        className="px-4 py-2 bg-slate-900 hover:bg-slate-700 text-white rounded-lg text-sm"
      >
        {loading ? "Submitting..." : "Send Feedback"}
      </button>
    </form>
  );
};

export default FeedbackComposer;
