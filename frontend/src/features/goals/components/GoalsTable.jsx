import { useMemo, useState } from "react";
import {
  submitGoal,
  approveGoal,
  rejectGoal,
  updateGoal,
  deleteGoal
} from "../goals.store";
import { authStore } from "../../../auth/auth.store";
import KeyResultProgress from "./KeyResultProgress";

const EDITABLE_STATUSES = new Set(["DRAFT", "REJECTED"]);

const GoalsTable = ({ goals }) => {
  const { user } = authStore.getState();
  const role = user?.role;

  const [editGoalId, setEditGoalId] = useState(null);
  const [form, setForm] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const goalsById = useMemo(() => {
    const map = new Map();
    (goals || []).forEach((goal) => {
      if (goal?.id != null) {
        map.set(goal.id, goal);
      }
    });
    return map;
  }, [goals]);

  if (!Array.isArray(goals) || goals.length === 0) {
    return (
      <div className="bg-white p-12 rounded-xl border border-dashed border-slate-300 text-center text-slate-500">
        <p>No goals found.</p>
      </div>
    );
  }

  const startEditing = (goal) => {
    setEditGoalId(goal.id);
    setForm({
      title: goal?.title || "",
      description: goal?.description || "",
      keyResults: (goal?.keyResults || []).map((kr) => ({
        id: kr.id,
        metric: kr.metric || "",
        targetValue: String(kr.targetValue ?? "")
      }))
    });
  };

  const cancelEdit = () => {
    setEditGoalId(null);
    setForm(null);
  };

  const onKrChange = (index, field, value) => {
    setForm((prev) => ({
      ...prev,
      keyResults: prev.keyResults.map((kr, idx) =>
        idx === index ? { ...kr, [field]: value } : kr
      )
    }));
  };

  const addKr = () => {
    setForm((prev) => ({
      ...prev,
      keyResults: [...prev.keyResults, { metric: "", targetValue: "" }]
    }));
  };

  const removeKr = (index) => {
    setForm((prev) => ({
      ...prev,
      keyResults: prev.keyResults.filter((_, idx) => idx !== index)
    }));
  };

  const handleUpdate = async (goalId) => {
    if (!form?.title?.trim() || !form.keyResults?.length) {
      alert("Title and at least one key result are required.");
      return;
    }

    const payload = {
      title: form.title.trim(),
      description: form.description?.trim() || "",
      keyResults: form.keyResults.map((kr) => ({
        ...(kr.id ? { id: kr.id } : {}),
        metric: kr.metric.trim(),
        targetValue: Number(kr.targetValue)
      }))
    };

    if (payload.keyResults.some((kr) => !kr.metric || !kr.targetValue || kr.targetValue <= 0)) {
      alert("Each key result needs a metric and a target greater than 0.");
      return;
    }

    setSubmitting(true);
    const result = await updateGoal(goalId, payload);
    setSubmitting(false);

    if (!result.ok) {
      alert(result.message || "Failed to update goal");
      return;
    }

    cancelEdit();
  };

  const handleDelete = async (goalId) => {
    if (!window.confirm("Delete this goal? This action cannot be undone.")) {
      return;
    }

    const result = await deleteGoal(goalId);
    if (!result.ok) {
      alert(result.message || "Failed to delete goal");
    }
  };

  const handleSubmitGoal = async (goalId) => {
    const result = await submitGoal(goalId);
    if (!result.ok) {
      alert(result.message || "Failed to submit goal");
    }
  };

  const handleApprove = async (goalId) => {
    const result = await approveGoal(goalId);
    if (!result.ok) {
      alert(result.message || "Failed to approve goal");
    }
  };

  const handleReject = async (goalId) => {
    const reason = prompt("Rejection reason:");
    if (!reason) {
      return;
    }

    const result = await rejectGoal(goalId, reason);
    if (!result.ok) {
      alert(result.message || "Failed to reject goal");
    }
  };

  return (
    <div className="grid grid-cols-1 gap-4">
      {goals.map((goal, index) => {
        const isEditing = editGoalId === goal?.id;
        const latestGoal = goalsById.get(goal?.id) || goal;
        const isEditable = role === "EMPLOYEE" && EDITABLE_STATUSES.has(latestGoal?.status);

        return (
          <div
            key={goal?.id ?? `${goal?.title || "goal"}-${index}`}
            className="bg-white rounded-xl shadow-sm border border-slate-200 p-6"
          >
            <div className="flex justify-between items-start mb-4 gap-3">
              <div className="min-w-0">
                <h3 className="text-lg font-bold text-slate-800">
                  {latestGoal?.title || "Untitled goal"}
                </h3>
                {latestGoal?.description && (
                  <p className="text-slate-500 text-sm mt-1">{latestGoal.description}</p>
                )}
              </div>

              <span
                className={`px-2.5 py-0.5 rounded text-xs font-bold uppercase whitespace-nowrap
                  ${
                    latestGoal?.status === "COMPLETED"
                      ? "bg-green-100 text-green-700"
                      : latestGoal?.status === "REJECTED"
                      ? "bg-red-100 text-red-700"
                      : latestGoal?.status === "SUBMITTED"
                      ? "bg-yellow-100 text-yellow-700"
                      : "bg-blue-100 text-blue-700"
                  }`}
              >
                {latestGoal?.status || "UNKNOWN"}
              </span>
            </div>

            {isEditing ? (
              <div className="space-y-3 border border-slate-200 rounded-lg p-4 bg-slate-50">
                <input
                  value={form?.title || ""}
                  onChange={(e) => setForm((prev) => ({ ...prev, title: e.target.value }))}
                  className="w-full rounded border border-slate-300 px-3 py-2 text-sm"
                  placeholder="Goal title"
                />
                <textarea
                  value={form?.description || ""}
                  onChange={(e) =>
                    setForm((prev) => ({ ...prev, description: e.target.value }))
                  }
                  rows={3}
                  className="w-full rounded border border-slate-300 px-3 py-2 text-sm"
                  placeholder="Goal description"
                />

                <div className="space-y-2">
                  {form?.keyResults?.map((kr, krIndex) => (
                    <div key={`${kr.id || "new"}-${krIndex}`} className="grid grid-cols-12 gap-2">
                      <input
                        value={kr.metric}
                        onChange={(e) => onKrChange(krIndex, "metric", e.target.value)}
                        className="col-span-8 rounded border border-slate-300 px-2 py-1.5 text-sm"
                        placeholder="Metric"
                      />
                      <input
                        value={kr.targetValue}
                        type="number"
                        min="1"
                        onChange={(e) => onKrChange(krIndex, "targetValue", e.target.value)}
                        className="col-span-3 rounded border border-slate-300 px-2 py-1.5 text-sm"
                        placeholder="Target"
                      />
                      <button
                        type="button"
                        onClick={() => removeKr(krIndex)}
                        disabled={(form?.keyResults?.length || 0) <= 1}
                        className="col-span-1 text-xs text-red-600 disabled:text-slate-300"
                      >
                        ✕
                      </button>
                    </div>
                  ))}
                </div>

                <button
                  type="button"
                  onClick={addKr}
                  className="text-xs font-semibold text-blue-600"
                >
                  + Add key result
                </button>

                <div className="flex gap-2 pt-2">
                  <button
                    disabled={submitting}
                    onClick={() => handleUpdate(latestGoal?.id)}
                    className="px-3 py-1.5 text-sm font-semibold rounded bg-blue-600 text-white hover:bg-blue-700 disabled:opacity-70"
                  >
                    Save Update
                  </button>
                  <button
                    disabled={submitting}
                    onClick={cancelEdit}
                    className="px-3 py-1.5 text-sm font-semibold rounded bg-slate-200 text-slate-800 hover:bg-slate-300"
                  >
                    Cancel
                  </button>
                </div>
              </div>
            ) : (
              <>
                <div className="space-y-3">
                  {Array.isArray(latestGoal?.keyResults) &&
                    latestGoal.keyResults.map((kr) => <KeyResultProgress key={kr.id} kr={kr} />)}
                </div>

                <div className="flex gap-2 mt-4 flex-wrap">
                  {role === "EMPLOYEE" && latestGoal?.status === "DRAFT" && (
                    <button
                      onClick={() => handleSubmitGoal(latestGoal?.id)}
                      className="px-4 py-1.5 text-sm font-semibold rounded bg-blue-600 text-white hover:bg-blue-700"
                    >
                      Submit
                    </button>
                  )}

                  {isEditable && (
                    <>
                      <button
                        onClick={() => startEditing(latestGoal)}
                        className="px-4 py-1.5 text-sm font-semibold rounded bg-slate-700 text-white hover:bg-slate-800"
                      >
                        Update
                      </button>
                      <button
                        onClick={() => handleDelete(latestGoal?.id)}
                        className="px-4 py-1.5 text-sm font-semibold rounded bg-red-600 text-white hover:bg-red-700"
                      >
                        Delete
                      </button>
                    </>
                  )}

                  {role === "MANAGER" && latestGoal?.status === "SUBMITTED" && (
                    <>
                      <button
                        onClick={() => handleApprove(latestGoal?.id)}
                        className="px-4 py-1.5 text-sm font-semibold rounded bg-green-600 text-white hover:bg-green-700"
                      >
                        Approve
                      </button>

                      <button
                        onClick={() => handleReject(latestGoal?.id)}
                        className="px-4 py-1.5 text-sm font-semibold rounded bg-red-600 text-white hover:bg-red-700"
                      >
                        Reject
                      </button>
                    </>
                  )}
                </div>
              </>
            )}
          </div>
        );
      })}
    </div>
  );
};

export default GoalsTable;
