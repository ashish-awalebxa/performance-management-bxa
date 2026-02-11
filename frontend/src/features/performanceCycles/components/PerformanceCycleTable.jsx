import { useEffect, useState } from "react";
import { startCycle, closeCycle } from "../performanceCycles.store";
import { authStore } from "../../../auth/auth.store";

const PerformanceCycleTable = ({ cycles }) => {
  const [authState, setAuthState] = useState(authStore.getState());

  useEffect(() => {
    return authStore.subscribe(setAuthState);
  }, []);

  const isHR = authState.user?.role === "HR" || authState.user?.role === "ADMIN";

  const handleStart = async (id) => {
    try {
      await startCycle(id);
      alert("Performance cycle started.");
    } catch (e) {
      alert(e?.response?.data?.message || "Unable to start cycle.");
    }
  };

  const handleClose = async (id) => {
    try {
      await closeCycle(id);
      alert("Performance cycle closed.");
    } catch (e) {
      alert(e?.response?.data?.message || "Unable to close cycle.");
    }
  };

  if (!cycles || cycles.length === 0) {
    return (
      <div className="bg-white p-8 rounded-xl border border-dashed border-slate-300 text-center text-slate-500">
        No performance cycles found.
      </div>
    );
  }

  return (
    <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-slate-200 text-sm">
          <thead className="bg-slate-50">
            <tr>
              <th className="px-6 py-3 text-left font-semibold text-slate-900">Name</th>
              <th className="px-6 py-3 text-left font-semibold text-slate-900">Type</th>
              <th className="px-6 py-3 text-left font-semibold text-slate-900">Timeline</th>
              <th className="px-6 py-3 text-left font-semibold text-slate-900">Status</th>
              <th className="px-6 py-3 text-right font-semibold text-slate-900">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-200 bg-white">
            {cycles.map((c) => (
              <tr key={c.id} className="hover:bg-slate-50 transition-colors">
                <td className="px-6 py-4 font-medium text-slate-900">{c.name}</td>
                <td className="px-6 py-4">
                  <span className="inline-block px-2 py-1 text-xs font-medium bg-slate-100 text-slate-600 rounded border border-slate-200">
                    {c.cycleType}
                  </span>
                </td>
                <td className="px-6 py-4 text-slate-500">
                  {c.startDate} <span className="text-slate-300 mx-1">→</span> {c.endDate}
                </td>
                <td className="px-6 py-4">
                  {/* Status Badge */}
                  <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium border ${
                    c.status === 'ACTIVE' ? 'bg-green-50 text-green-700 border-green-200' :
                    c.status === 'DRAFT' ? 'bg-yellow-50 text-yellow-700 border-yellow-200' :
                    'bg-slate-100 text-slate-500 border-slate-200'
                  }`}>
                    <span className={`w-1.5 h-1.5 rounded-full ${
                      c.status === 'ACTIVE' ? 'bg-green-600' :
                      c.status === 'DRAFT' ? 'bg-yellow-500' :
                      'bg-slate-400'
                    }`}></span>
                    {c.status}
                  </span>
                </td>
                <td className="px-6 py-4 text-right">
                  {isHR && c.status === "DRAFT" && (
                    <button
                      onClick={() => handleStart(c.id)}
                      className="text-xs font-medium px-3 py-1.5 bg-green-600 text-white rounded hover:bg-green-700 transition shadow-sm"
                    >
                      Start Cycle
                    </button>
                  )}
                  {isHR && c.status === "ACTIVE" && (
                    <button
                      onClick={() => handleClose(c.id)}
                      className="text-xs font-medium px-3 py-1.5 bg-white border border-slate-300 text-slate-700 rounded hover:bg-slate-50 transition"
                    >
                      Close Cycle
                    </button>
                  )}
                  {(!isHR || c.status === "CLOSED") && (
                    <span className="text-slate-400 text-xs italic">Read only</span>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default PerformanceCycleTable;