import { useEffect, useState } from "react";
import {
  departmentsStore,
  fetchDepartments,
  deleteDepartment
} from "../departments.store";
import DepartmentForm from "../components/DepartmentForm";
import Pagination from "../../../components/common/Pagination";

const DepartmentList = () => {
  const [state, setState] = useState(departmentsStore.getState());
  const [editingDepartment, setEditingDepartment] = useState(null);

  useEffect(() => {
    const unsub = departmentsStore.subscribe(setState);
    fetchDepartments(0);
    return unsub;
  }, []);

  const handleDelete = async (id) => {
    if (!window.confirm("Deactivate this department?")) return;
    await deleteDepartment(id);
  };

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-2xl font-bold text-slate-900">Departments</h1>
        <p className="text-sm text-slate-500 mt-1">
          Organize your company structure and teams.
        </p>
      </div>

      <div className="bg-white p-6 rounded-xl shadow-sm border border-slate-200">
        <h2 className="text-base font-semibold text-slate-800 mb-4 flex items-center gap-2">
          <svg className="w-5 h-5 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v3m0 0v3m0-3h3m-3 0H9m12 0a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          {editingDepartment ? "Edit Department" : "Add New Department"}
        </h2>
        <DepartmentForm
          initialData={editingDepartment}
          onDone={() => setEditingDepartment(null)}
        />
      </div>

      <div>
        <h3 className="text-sm font-bold text-slate-400 uppercase tracking-wider mb-4">
          Active Departments ({state.totalElements})
        </h3>

        {state.departments.length === 0 ? (
          <div className="text-center p-12 bg-slate-50 rounded-xl border border-dashed border-slate-300 text-slate-500">
            No departments found. Create one above.
          </div>
        ) : (
          <>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {state.departments.map((dept) => (
                <div
                  key={dept.id}
                  className="bg-white p-5 rounded-lg border border-slate-200 shadow-sm hover:shadow-md transition-shadow"
                >
                  <div className="flex items-start justify-between">
                    <div>
                      <h4 className="font-bold text-slate-900">{dept.displayName}</h4>
                      <span className="inline-block mt-2 px-2.5 py-1 rounded text-xs font-medium bg-slate-100 text-slate-600 border border-slate-200">
                        {dept.type}
                      </span>
                      {dept.headId ? (
                        <p className="text-xs text-slate-500 mt-2">Head ID: {dept.headId}</p>
                      ) : null}
                    </div>
                  </div>

                  <div className="flex gap-2 mt-4">
                    <button
                      onClick={() => setEditingDepartment(dept)}
                      className="px-3 py-1.5 text-sm rounded bg-amber-100 text-amber-800 hover:bg-amber-200"
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => handleDelete(dept.id)}
                      className="px-3 py-1.5 text-sm rounded bg-red-100 text-red-700 hover:bg-red-200"
                    >
                      Deactivate
                    </button>
                  </div>
                </div>
              ))}
            </div>

            <div className="flex justify-center pt-6">
              <Pagination
                page={state.page}
                totalPages={state.totalPages}
                onPageChange={(newPage) => fetchDepartments(newPage, state.size)}
              />
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default DepartmentList;
