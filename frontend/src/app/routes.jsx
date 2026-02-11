import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import AppLayout from "../layout/AppLayout";
import Login from "../auth/pages/Login";
import UserList from "../features/users/pages/UserList";
import CreateUser from "../features/users/pages/CreateUser";
import RoleGuard from "./RoleGuard";
import AuthGuard from "./AuthGuard";
import EditUser from "../features/users/pages/EditUser";
import DepartmentList from "../features/departments/pages/DepartmentList";
import EmployeeDashboard from "../features/dashboard/pages/EmployeeDashboard";
import MyGoalsPage from "../features/goals/pages/MyGoalsPage";
import TeamGoalsPage from "../features/goals/pages/TeamGoalsPage";
import PerformanceCycleList from "../features/performanceCycles/pages/PerformanceCycleList";
import ReviewCyclePage from "../features/reviewCycles/pages/ReviewCyclePage";
import RequireRole from "../components/common/RequireRole";
import ManagerRatingsPage from "../features/ratings/pages/ManagerRatingsPage";
import HrCalibrationPage from "../features/ratings/pages/HrCalibrationPage";
import FinalRatingsPage from "../features/ratings/pages/FinalRatingsPage";
import MyReviewPage from "../features/reviews/pages/MyReviewPage";
import TeamReviewsPage from "../features/reviews/pages/TeamReviewsPage";
import TeamRatingsPage from "../features/ratings/pages/TeamRatingsPage";
import MyRatingPage from "../features/ratings/pages/MyRatingPage";
import ProfilePage from "../auth/pages/profile";
import ContinuousFeedbackPage from "../features/feedback/pages/ContinuousFeedbackPage";

const AppRoutes = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="/login" element={<Login />} />

        <Route element={<AuthGuard />}>
          <Route element={<AppLayout />}>
            <Route path="/dashboard" element={<EmployeeDashboard />} />
            <Route path="/goals" element={<MyGoalsPage />} />
            <Route path="/goals/team" element={<TeamGoalsPage />} />
            <Route path="/reviews/my" element={<MyReviewPage />} />
            <Route path="/reviews/team" element={<RequireRole roles={["MANAGER"]}><TeamReviewsPage /></RequireRole>} />

            <Route path="/ratings/team" element={<RequireRole roles={["MANAGER"]}><TeamRatingsPage /></RequireRole>} />
            <Route path="/ratings/my" element={<RequireRole roles={["EMPLOYEE"]}><MyRatingPage /></RequireRole>} />
            <Route path="/ratings/manager" element={<RequireRole roles={["MANAGER"]}><ManagerRatingsPage /></RequireRole>} />
            <Route path="/ratings/hr" element={<RequireRole roles={["HR"]}><HrCalibrationPage /></RequireRole>} />
            <Route path="/ratings/final" element={<RequireRole roles={["LEADERSHIP"]}><FinalRatingsPage /></RequireRole>} />
            <Route path="/ratings/finalize" element={<RequireRole roles={["HR", "LEADERSHIP"]}><FinalRatingsPage /></RequireRole>} />

            <Route path="/profile" element={<ProfilePage />} />
            <Route path="/feedback" element={<ContinuousFeedbackPage />} />
            <Route path="/users" element={<UserList />} />

            <Route element={<RoleGuard allowedRoles={["HR", "ADMIN"]} />}>
              <Route path="/users/create" element={<CreateUser />} />
              <Route path="/users/:id/edit" element={<EditUser />} />
              <Route path="/departments" element={<DepartmentList />} />
              <Route path="/performance-cycles" element={<PerformanceCycleList />} />
              <Route path="/review-cycles" element={<ReviewCyclePage />} />
            </Route>
          </Route>
        </Route>
      </Routes>
    </BrowserRouter>
  );
};

export default AppRoutes;
