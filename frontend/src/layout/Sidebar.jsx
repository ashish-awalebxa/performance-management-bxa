import { NavLink, Link } from "react-router-dom"; // <--- Added 'Link' import
import { authStore } from "../auth/auth.store";

const NavItem = ({ to, children, icon, end = false }) => (
  <NavLink
    to={to}
    end={end}
    className={({ isActive }) =>
      `flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-medium transition-all duration-200 group ${
        isActive
          ? "bg-blue-600 text-white shadow-md"
          : "text-slate-400 hover:bg-slate-800 hover:text-white"
      }`
    }
  >
    {icon}
    <span>{children}</span>
  </NavLink>
);

const Icons = {
  Dashboard: <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" /></svg>,
  Users: <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" /></svg>,
  Dept: <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" /></svg>,
  Cycle: <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" /></svg>,
  Goal: <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>,
  Review: <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" /></svg>,
  Star: <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l2.01 6.186a1 1 0 00.95.69h6.505c.969 0 1.371 1.24.588 1.81l-5.263 3.823a1 1 0 00-.364 1.118l2.01 6.186c.3.922-.755 1.688-1.539 1.118l-5.262-3.822a1 1 0 00-1.176 0L6.148 22.93c-.783.57-1.838-.196-1.539-1.118l2.01-6.186a1 1 0 00-.364-1.118L.992 11.613c-.783-.57-.38-1.81.588-1.81h6.505a1 1 0 00.95-.69l2.014-6.186z" /></svg>,
  Feedback: <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 10h.01M12 10h.01M16 10h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4-.8L3 20l1.19-3.57A7.31 7.31 0 013 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" /></svg>,
};

const Sidebar = () => {
  const { user } = authStore.getState();

  return (
    <aside className="w-64 bg-slate-900 text-white flex flex-col border-r border-slate-800 shadow-xl z-20 hidden md:flex">
      <div className="h-16 flex items-center px-6 border-b border-slate-800">
        <div className="flex items-center gap-2 text-blue-400">
          <svg className="w-8 h-8" fill="currentColor" viewBox="0 0 24 24"><path d="M12 2L2 7l10 5 10-5-10-5zm0 9l2.5-1.25L12 8.5l-2.5 1.25L12 11zm0 2.5l-5-2.5-5 2.5L12 22l10-8.5-5-2.5-5 2.5z"/></svg>
          <h1 className="text-xl font-bold tracking-wider text-white">PMS<span className="text-blue-500">.io</span></h1>
        </div>
      </div>

      <nav className="flex-1 px-4 py-6 space-y-2 overflow-y-auto">
        <p className="px-4 text-xs font-semibold text-slate-500 uppercase tracking-wider mb-2">Main Menu</p>
        <NavItem to="/dashboard" icon={Icons.Dashboard}>Dashboard</NavItem>

        {(user?.role === "HR" || user?.role === "ADMIN") && (
          <>
            <p className="px-4 text-xs font-semibold text-slate-500 uppercase tracking-wider mt-6 mb-2">Administration</p>
            <NavItem to="/users" icon={Icons.Users}>Users</NavItem>
            <NavItem to="/departments" icon={Icons.Dept}>Departments</NavItem>
            <NavItem to="/performance-cycles" icon={Icons.Cycle}>Performance Cycles</NavItem>
            <NavItem to="/review-cycles" icon={Icons.Review}>Review Cycles</NavItem>
            <NavItem to="/feedback" icon={Icons.Feedback}>Feedback</NavItem>
          </>
        )}

        {(user?.role === "EMPLOYEE" || user?.role === "MANAGER") && (
           <>
             <p className="px-4 text-xs font-semibold text-slate-500 uppercase tracking-wider mt-6 mb-2">Performance</p>
             <NavItem to="/goals" icon={Icons.Goal} end={true}>My Goals</NavItem>
             <NavItem to="/reviews/my">My Review</NavItem>
             <NavItem to="/feedback" icon={Icons.Feedback}>Feedback</NavItem>
           </>
        )}

        {user?.role === "MANAGER" && (
            <>
            <NavItem to="/goals/team" icon={Icons.Users}>Team Goals</NavItem>
            <NavItem to="/reviews/team">Team Reviews</NavItem>
            </>
        )}

        {(user?.role === "HR" || user?.role === "LEADERSHIP") && (
          <NavItem to="/ratings/finalize" icon={Icons.Goal}>
            Finalize Ratings
          </NavItem>
        )}

    {user?.role === "MANAGER" && (
      <NavItem to="/ratings/team" icon={Icons.Star}>
        Team Ratings
      </NavItem>
    )}

    {user?.role === "EMPLOYEE" && (
      <NavItem to="/ratings/my" icon={Icons.Star}>
        My Rating
      </NavItem>
    )}


      </nav>

      <div className="p-4 border-t border-slate-800 bg-slate-900">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 rounded-full bg-blue-500 flex items-center justify-center text-xs font-bold">
            {user?.loginId?.charAt(0).toUpperCase()}
          </div>
          <div className="flex flex-col">
            <span className="text-sm font-medium">{user?.role}</span>
            {/* 👇 LINKED PROFILE HERE 👇 */}
            <Link to="/profile" className="text-xs text-slate-400 hover:text-blue-400 transition-colors cursor-pointer">
              View Profile
            </Link>
          </div>
        </div>
      </div>
    </aside>
  );
};

export default Sidebar;