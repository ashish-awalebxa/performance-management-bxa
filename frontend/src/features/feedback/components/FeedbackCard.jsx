const FeedbackCard = ({ item, canAcknowledge = false, onAcknowledge }) => {
  return (
    <div className="border border-slate-200 rounded-xl bg-white p-4 space-y-2 shadow-sm">
      <div className="flex items-center justify-between gap-3">
        <h3 className="font-semibold text-slate-800">{item.title}</h3>
        <span className="text-xs bg-slate-100 text-slate-700 px-2 py-1 rounded-full">
          {item.type}
        </span>
      </div>

      <p className="text-sm text-slate-600">{item.message}</p>

      {item.actionItems && (
        <p className="text-xs text-blue-700 bg-blue-50 p-2 rounded-md">
          <span className="font-medium">Action Items:</span> {item.actionItems}
        </p>
      )}

      <div className="flex items-center justify-between text-xs text-slate-500 pt-1">
        <span>{new Date(item.createdAt).toLocaleString()}</span>
        <span>{item.visibility}</span>
      </div>

      {canAcknowledge && !item.acknowledged && (
        <button
          onClick={() => onAcknowledge(item.id)}
          className="text-sm mt-2 px-3 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-lg"
        >
          Acknowledge
        </button>
      )}

      {item.acknowledged && (
        <p className="text-xs font-medium text-emerald-700">Acknowledged</p>
      )}
    </div>
  );
};

export default FeedbackCard;
