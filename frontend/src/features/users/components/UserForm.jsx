import { useState } from "react";
import { createUser } from "../users.store";
import { useNavigate } from "react-router-dom";
import ManagerSelect from "./ManagerSelect";
import DepartmentSelect from "../../departments/components/DepartmentSelect";

const UserForm = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const [form, setForm] = useState({
    username: "",
    email: "",
    password: "",
    role: "",
    departmentType: "",
    departmentDisplayName: "",
    managerId: ""
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    const payload = {
      username: form.username,
      email: form.email,
      password: form.password,
      role: form.role,
      departmentType: form.departmentType,
      departmentDisplayName: form.departmentDisplayName || null,
      managerId: form.managerId ? Number(form.managerId) : null
    };

    await createUser(payload);
    setLoading(false);
    navigate("/users");
  };

  // Reusable styling classes
  const labelClass = "block text-sm font-medium text-slate-700 mb-1";
  const inputClass = "w-full rounded-lg border border-slate-300 px-3 py-2.5 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500 transition shadow-sm";

  return (
    <div className="max-w-3xl mx-auto">
      {/* Form Card */}
      <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">

        <div className="px-6 py-4 border-b border-slate-100 bg-slate-50 flex justify-between items-center">
            <h2 className="font-semibold text-slate-800">Create New User</h2>
        </div>

        <div className="p-6 md:p-8">
          <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-6">

            {/* --- Account Information Section --- */}
            <div className="md:col-span-2">
                <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-4 border-b border-slate-100 pb-2">Account Details</h3>
            </div>

            {/* Username */}
            <div>
              <label className={labelClass}>Full Name / Username</label>
              <input
                name="username"
                value={form.username}
                onChange={handleChange}
                className={inputClass}
                placeholder="e.g. John Doe"
                autoComplete="off"
                required
              />
            </div>

            {/* Email */}
            <div>
              <label className={labelClass}>Email Address</label>
              <input
                type="email"
                name="email"
                value={form.email}
                onChange={handleChange}
                className={inputClass}
                placeholder="john@company.com"
                autoComplete="new-email"
                required
              />
            </div>

            {/* Password */}
            <div>
              <label className={labelClass}>Password</label>
              <input
                type="password"
                name="password"
                value={form.password}
                onChange={handleChange}
                className={inputClass}
                placeholder="••••••••"
                autoComplete="new-password"
                required
              />
            </div>

            {/* Role */}
            <div>
              <label className={labelClass}>System Role</label>
              <select
                name="role"
                value={form.role}
                onChange={handleChange}
                className={inputClass}
                required
              >
                <option value="">Select a role...</option>
                <option value="EMPLOYEE">Employee (Standard)</option>
                <option value="MANAGER">Manager</option>
                <option value="HR">HR Administrator</option>
                <option value="ADMIN">System Admin</option>
                <option value="LEADERSHIP">Leadership</option>
              </select>
            </div>

            {/* --- Organization Section --- */}
            <div className="md:col-span-2 mt-2">
                <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-4 border-b border-slate-100 pb-2">Organization</h3>
            </div>

            {/* Department Type */}
            <div>
              <label className={labelClass}>Department</label>
              {/* Ensure DepartmentSelect accepts className or wrap it in a styled div if needed.
                  Assuming it renders a standard select, you might need to pass styling down or wrap it.
                  For now, we wrap it to enforce width. */}
              <div className="[&>select]:w-full [&>select]:rounded-lg [&>select]:border-slate-300 [&>select]:px-3 [&>select]:py-2.5 [&>select]:text-sm">
                <DepartmentSelect
                  value={form.departmentType}
                  onChange={(value) => setForm({ ...form, departmentType: value })}
                />
              </div>
            </div>

            {/* Department Display Name */}
            <div>
              <label className={labelClass}>Display Name <span className="text-slate-400 font-normal">(Optional)</span></label>
              <input
                name="departmentDisplayName"
                value={form.departmentDisplayName}
                onChange={handleChange}
                className={inputClass}
                placeholder="e.g. Platform Engineering"
              />
            </div>

            {/* Manager Select */}
            <div className="md:col-span-2">
              <label className={labelClass}>Direct Manager</label>
              <ManagerSelect
                value={form.managerId}
                onChange={(managerId) => setForm({ ...form, managerId })}
                className={inputClass}
              />
              <p className="text-xs text-slate-500 mt-1">Select the person this user reports to.</p>
            </div>

            {/* Submit Button */}
            <div className="md:col-span-2 pt-4 flex justify-end gap-3 border-t border-slate-100 mt-2">
               <button
                 type="button"
                 onClick={() => navigate("/users")}
                 className="px-5 py-2.5 rounded-lg border border-slate-300 text-slate-700 font-medium hover:bg-slate-50 transition"
               >
                 Cancel
               </button>
               <button
                 type="submit"
                 disabled={loading}
                 className="px-6 py-2.5 rounded-lg bg-slate-900 text-white font-medium hover:bg-slate-800 transition shadow-md disabled:opacity-70 flex items-center gap-2"
               >
                 {loading && <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>}
                 Create User
               </button>
            </div>

          </form>
        </div>
      </div>
    </div>
  );
};

export default UserForm;