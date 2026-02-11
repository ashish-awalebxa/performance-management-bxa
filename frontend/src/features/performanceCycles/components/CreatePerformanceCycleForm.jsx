import { useState } from "react";
import { createCycle } from "../performanceCycles.store";

const CreatePerformanceCycleForm = () => {
  const [form, setForm] = useState({
    name: "",
    cycleType: "",
    startDate: "",
    endDate: ""
  });
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await createCycle(form);
      alert("Performance cycle created successfully.");
      setForm({ name: "", cycleType: "", startDate: "", endDate: "" });
    } catch (error) {
      const message = error?.response?.data?.message || "Unable to create performance cycle.";
      alert(message);
    } finally {
      setLoading(false);
    }
  };

  const labelClass = "block text-xs font-medium text-slate-700 mb-1";
  const inputClass = "w-full rounded-lg border border-slate-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500 transition shadow-sm";

  return (
    <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-4 gap-4 items-end">
      <div className="md:col-span-1">
        <label className={labelClass}>Cycle Name</label>
        <input
          placeholder="e.g. FY2026 Q1"
          value={form.name}
          onChange={(e) => setForm({ ...form, name: e.target.value })}
          className={inputClass}
          required
        />
      </div>

      <div className="md:col-span-1">
        <label className={labelClass}>Cycle Type</label>
        <select
          value={form.cycleType}
          onChange={(e) => setForm({ ...form, cycleType: e.target.value })}
          className={inputClass}
          required
        >
          <option value="">Select Type</option>
          <option value="ANNUAL">Annual Review</option>
          <option value="QUARTERLY">Quarterly Review</option>
          <option value="PROBATION">Probation</option>
        </select>
      </div>

      <div className="md:col-span-1">
        <label className={labelClass}>Start Date</label>
        <input
          type="date"
          value={form.startDate}
          onChange={(e) => setForm({ ...form, startDate: e.target.value })}
          className={inputClass}
          required
        />
      </div>

      <div className="md:col-span-1">
        <label className={labelClass}>End Date</label>
        <input
          type="date"
          value={form.endDate}
          onChange={(e) => setForm({ ...form, endDate: e.target.value })}
          className={inputClass}
          required
        />
      </div>

      <div className="md:col-span-4 flex justify-end mt-2">
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

export default CreatePerformanceCycleForm;
