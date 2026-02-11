import { useState } from "react";
import { createGoal } from "../goals.store";
import { authStore } from "../../../auth/auth.store";

const CreateGoalForm = ({ onSuccess }) => {
  const { user } = authStore.getState();

  const [form, setForm] = useState({
    title: "",
    description: "",
    keyResults: [{ metric: "", targetValue: "" }]
  });

  const [loading, setLoading] = useState(false);

  /* ---------- Handlers ---------- */

  const handleAddKR = () => {
    setForm((prev) => ({
      ...prev,
      keyResults: [...prev.keyResults, { metric: "", targetValue: "" }]
    }));
  };

  const handleKRChange = (index, field, value) => {
    const updated = [...form.keyResults];
    updated[index][field] = value;
    setForm({ ...form, keyResults: updated });
  };

  const handleRemoveKR = (index) => {
    if (form.keyResults.length === 1) return; // 🔐 must have at least 1 KR
    setForm({
      ...form,
      keyResults: form.keyResults.filter((_, i) => i !== index)
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    // 🔥 BACKEND-SAFE PAYLOAD
    const payload = {
      title: form.title,
      description: form.description,
      employeeId: user.id,
      keyResults: form.keyResults.map((kr) => ({
        metric: kr.metric,
        targetValue: Number(kr.targetValue)
      }))
    };

    const result = await createGoal(payload);

    setLoading(false);

    if (!result?.ok) {
      alert(result?.message || "Unable to create goal");
      return;
    }

    setForm({
      title: "",
      description: "",
      keyResults: [{ metric: "", targetValue: "" }]
    });

    if (onSuccess) onSuccess();
  };

  /* ---------- UI ---------- */

  const inputClass =
    "w-full rounded-lg border border-slate-300 px-3 py-2.5 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500 transition shadow-sm";

  const labelClass =
    "block text-xs font-medium text-slate-700 mb-1";

  return (
    <form onSubmit={handleSubmit} className="space-y-6">

      {/* Title & Description */}
      <div className="grid grid-cols-1 gap-6">
        <div>
          <label className={labelClass}>Objective Title</label>
          <input
            required
            value={form.title}
            onChange={(e) =>
              setForm({ ...form, title: e.target.value })
            }
            className={inputClass}
            placeholder="Increase Customer Retention by 15%"
          />
        </div>

        <div>
          <label className={labelClass}>Description</label>
          <textarea
            rows={3}
            value={form.description}
            onChange={(e) =>
              setForm({ ...form, description: e.target.value })
            }
            className={inputClass}
          />
        </div>
      </div>

      {/* Key Results */}
      <div className="bg-slate-50 rounded-xl p-5 border border-slate-200">
        <h3 className="text-sm font-bold text-slate-700 mb-4">
          Key Results
        </h3>

        <div className="space-y-3">
          {form.keyResults.map((kr, index) => (
            <div
              key={index}
              className="flex gap-3 items-end bg-white p-3 rounded-lg border"
            >
              <div className="flex-1">
                <label className="text-[10px] uppercase font-bold text-slate-400">
                  Metric
                </label>
                <input
                  required
                  value={kr.metric}
                  onChange={(e) =>
                    handleKRChange(index, "metric", e.target.value)
                  }
                  className="w-full border-b text-sm"
                />
              </div>

              <div className="w-24">
                <label className="text-[10px] uppercase font-bold text-slate-400">
                  Target
                </label>
                <input
                  required
                  type="number"
                  min="1"
                  value={kr.targetValue}
                  onChange={(e) =>
                    handleKRChange(index, "targetValue", e.target.value)
                  }
                  className="w-full border-b text-sm"
                />
              </div>

              {form.keyResults.length > 1 && (
                <button
                  type="button"
                  onClick={() => handleRemoveKR(index)}
                  className="text-red-500 text-xs"
                >
                  ✕
                </button>
              )}
            </div>
          ))}
        </div>

        <button
          type="button"
          onClick={handleAddKR}
          className="mt-4 text-xs font-semibold text-blue-600"
        >
          + Add Key Result
        </button>
      </div>

      {/* Submit */}
      <div className="flex justify-end">
        <button
          type="submit"
          disabled={loading}
          className="px-6 py-2.5 rounded-lg bg-blue-600 text-white font-medium hover:bg-blue-700 disabled:opacity-70"
        >
          {loading ? "Saving..." : "Save Goal"}
        </button>
      </div>
    </form>
  );
};

export default CreateGoalForm;
