import { useState } from "react";
import { createReviewCycleApi } from "../reviewCycles.api";

const CreateReviewCycleForm = ({ onSuccess }) => {
  const [form, setForm] = useState({
    name: "",
    selfReviewEnabled: true,
    managerReviewEnabled: true,
    startDate: "",
    endDate: ""
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const submit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      await createReviewCycleApi(form);
      setForm({
        name: "",
        selfReviewEnabled: true,
        managerReviewEnabled: true,
        startDate: "",
        endDate: ""
      });
      if (onSuccess) onSuccess();
    } catch (err) {
      const message = err?.response?.data?.message || "Unable to create review cycle. Please create and activate a performance cycle first.";
      setError(message);
      alert(message);
    } finally {
      setLoading(false);
    }
  };

  const labelClass = "block text-xs font-medium text-slate-700 mb-1";
  const inputClass = "w-full rounded-lg border border-slate-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500 transition shadow-sm";

  return (
    <form onSubmit={submit} className="grid grid-cols-1 md:grid-cols-2 gap-6">
      {error ? (
        <div className="md:col-span-2 text-sm text-red-600 bg-red-50 border border-red-200 rounded-lg p-3">
          {error}
        </div>
      ) : null}

      {/* Name Input - Full Width */}
      <div className="md:col-span-2">
        <label className={labelClass}>Cycle Name</label>
        <input
          placeholder="e.g. 2026 Mid-Year Review"
          value={form.name}
          onChange={(e) => setForm({ ...form, name: e.target.value })}
          className={inputClass}
          required
        />
      </div>

      {/* Date Inputs */}
      <div>
        <label className={labelClass}>Start Date</label>
        <input
          type="date"
          value={form.startDate}
          onChange={(e) => setForm({ ...form, startDate: e.target.value })}
          className={inputClass}
          required
        />
      </div>
      <div>
        <label className={labelClass}>End Date</label>
        <input
          type="date"
          value={form.endDate}
          onChange={(e) => setForm({ ...form, endDate: e.target.value })}
          className={inputClass}
          required
        />
      </div>

      {/* Toggles / Checkboxes */}
      <div className="md:col-span-2 flex gap-4 pt-2">
        <label className={`flex items-center gap-3 p-3 border rounded-lg cursor-pointer transition w-full ${form.selfReviewEnabled ? 'bg-blue-50 border-blue-200' : 'bg-white border-slate-200 hover:bg-slate-50'}`}>
          <input
            type="checkbox"
            checked={form.selfReviewEnabled}
            onChange={(e) => setForm({ ...form, selfReviewEnabled: e.target.checked })}
            className="w-4 h-4 text-blue-600 rounded focus:ring-blue-500"
          />
          <span className={`text-sm font-medium ${form.selfReviewEnabled ? 'text-blue-700' : 'text-slate-700'}`}>Enable Self Review</span>
        </label>

        <label className={`flex items-center gap-3 p-3 border rounded-lg cursor-pointer transition w-full ${form.managerReviewEnabled ? 'bg-blue-50 border-blue-200' : 'bg-white border-slate-200 hover:bg-slate-50'}`}>
          <input
            type="checkbox"
            checked={form.managerReviewEnabled}
            onChange={(e) => setForm({ ...form, managerReviewEnabled: e.target.checked })}
            className="w-4 h-4 text-blue-600 rounded focus:ring-blue-500"
          />
          <span className={`text-sm font-medium ${form.managerReviewEnabled ? 'text-blue-700' : 'text-slate-700'}`}>Enable Manager Review</span>
        </label>
      </div>

      {/* Submit Button */}
      <div className="md:col-span-2 flex justify-end mt-2">
        <button
          type="submit"
          disabled={loading}
          className="px-6 py-2.5 rounded-lg bg-blue-600 text-white font-medium hover:bg-blue-700 transition shadow-sm disabled:opacity-70 flex items-center gap-2"
        >
          {loading ? "Creating..." : "Create Cycle"}
        </button>
      </div>
    </form>
  );
};

export default CreateReviewCycleForm;