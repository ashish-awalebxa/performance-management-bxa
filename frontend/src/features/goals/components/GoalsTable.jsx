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
      {goals.map((goal, index) => (
        <div
          key={goal?.id ?? `${goal?.title || "goal"}-${index}`}
          className="bg-white rounded-xl shadow-sm border border-slate-200 p-6"
        >
          {/* -------- GOAL HEADER -------- */}
          <div className="flex justify-between items-start mb-4">
            <div>
              <h3 className="text-lg font-bold text-slate-800">
                {goal?.title || "Untitled goal"}
              </h3>
              {goal?.description && (
                <p className="text-slate-500 text-sm mt-1">
                  {goal?.description}
                </p>
              )}
            </div>

            <span
              className={`px-2.5 py-0.5 rounded text-xs font-bold uppercase
                ${
                  goal?.status === "COMPLETED"
                    ? "bg-green-100 text-green-700"
                    : goal?.status === "REJECTED"
                    ? "bg-red-100 text-red-700"
                    : goal?.status === "SUBMITTED"
                    ? "bg-yellow-100 text-yellow-700"
                    : "bg-blue-100 text-blue-700"
                }`}
            >
              {goal?.status || "UNKNOWN"}
            </span>
          </div>

          {/* -------- KEY RESULTS -------- */}
          <div className="space-y-3">
            {Array.isArray(goal?.keyResults) &&
              goal.keyResults.map((kr) => (
                <KeyResultProgress key={kr.id} kr={kr} />
              ))}
          </div>

          {/* -------- ACTIONS -------- */}
          <div className="flex gap-3 mt-4">
            {/* EMPLOYEE: Submit */}
            {role === "EMPLOYEE" &&
              goal?.status === "DRAFT" && (
                <button
                  onClick={() => submitGoal(goal?.id)}
                  className="px-4 py-1.5 text-sm font-semibold rounded bg-blue-600 text-white hover:bg-blue-700"
                >
                  + Add key result
                </button>

            {/* MANAGER: Approve / Reject */}
            {role === "MANAGER" &&
              goal?.status === "SUBMITTED" && (
                <>
                  <button
                    onClick={() => approveGoal(goal?.id)}
                    className="px-4 py-1.5 text-sm font-semibold rounded bg-green-600 text-white hover:bg-green-700"
                  >
                    Save Update
                  </button>
                  <button
                    onClick={() => {
                      const reason = prompt("Rejection reason:");
                      if (reason) rejectGoal(goal?.id, reason);
                    }}
                    className="px-4 py-1.5 text-sm font-semibold rounded bg-red-600 text-white hover:bg-red-700"
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
