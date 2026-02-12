import { useState } from "react";
import { createRating } from "../ratings.store";

const CreateRatingForm = () => {
  const [form, setForm] = useState({
    employeeId: "",
    managerJustification: ""
  });
  const [loading, setLoading] = useState(false);

  const onCreate = async () => {
    if (!form.employeeId || !form.managerJustification) {
      alert("Please fill all rating fields before creating.");
      return;
    }

    try {
      setLoading(true);
      await createRating({
        ...form,
        employeeId: Number(form.employeeId)
      });
      alert("Rating created successfully. Score was auto-calculated from goals.");
      setForm({ employeeId: "", managerJustification: "" });
    } catch (e) {
      alert(e?.response?.data?.message || "Failed to create rating.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="border p-4 rounded space-y-3">
      <input
        placeholder="Employee ID"
        className="border p-2 w-full"
        value={form.employeeId}
        onChange={(e) => setForm({ ...form, employeeId: e.target.value })}
      />

      <textarea
        rows={3}
        className="border p-2 w-full"
        placeholder="Manager justification"
        value={form.managerJustification}
        onChange={(e) =>
          setForm({ ...form, managerJustification: e.target.value })
        }
      />

      <button
        disabled={loading}
        onClick={onCreate}
        className="px-4 py-2 bg-blue-600 text-white rounded disabled:opacity-70"
      >
        {loading ? "Creating..." : "Create Rating"}
      </button>
    </div>
  );
};

export default CreateRatingForm;
