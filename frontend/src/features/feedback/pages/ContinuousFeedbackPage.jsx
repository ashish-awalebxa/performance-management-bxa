import { useCallback, useEffect, useState } from "react";
import FeedbackComposer from "../components/FeedbackComposer";
import FeedbackCard from "../components/FeedbackCard";
import {
  acknowledgeFeedbackApi,
  getGivenFeedbackApi,
  getReceivedFeedbackApi,
  getTeamFeedbackApi
} from "../feedback.api";
import { authStore } from "../../../auth/auth.store";

const tabs = ["received", "given", "team"];

const ContinuousFeedbackPage = () => {
  const [activeTab, setActiveTab] = useState("received");
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const role = authStore.getState()?.user?.role;

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      const api = activeTab === "received"
        ? getReceivedFeedbackApi
        : activeTab === "given"
          ? getGivenFeedbackApi
          : getTeamFeedbackApi;

      const res = await api();
      setItems(res.data?.content || []);
    } finally {
      setLoading(false);
    }
  }, [activeTab]);

  useEffect(() => {
    if (activeTab === "team" && role !== "MANAGER") {
      setActiveTab("received");
      return;
    }
    fetchData();
  }, [activeTab]);

  const acknowledge = async (id) => {
    await acknowledgeFeedbackApi(id);
    fetchData();
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-slate-900">Continuous Feedback</h1>
        <p className="text-sm text-slate-500">Capture real-time recognition, coaching and growth feedback.</p>
      </div>

      <FeedbackComposer onCreated={fetchData} />

      <div className="flex gap-2">
        {tabs.map((tab) => {
          if (tab === "team" && role !== "MANAGER") return null;
          return (
            <button
              key={tab}
              onClick={() => setActiveTab(tab)}
              className={`px-4 py-2 rounded-lg text-sm capitalize ${
                activeTab === tab ? "bg-blue-600 text-white" : "bg-slate-100 text-slate-700"
              }`}
            >
              {tab}
            </button>
          );
        })}
      </div>

      {loading ? (
        <p className="text-sm text-slate-500">Loading feedback...</p>
      ) : items.length === 0 ? (
        <p className="text-sm text-slate-500">No feedback records found.</p>
      ) : (
        <div className="grid md:grid-cols-2 gap-4">
          {items.map((item) => (
            <FeedbackCard
              key={item.id}
              item={item}
              canAcknowledge={activeTab === "received"}
              onAcknowledge={acknowledge}
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default ContinuousFeedbackPage;
