import { useEffect, useState } from "react";
import { db } from "../firebaseClient";
import { ref, onValue } from "firebase/database";

export default function LivePresence() {
  const [presence, setPresence] = useState({});

  useEffect(() => {
    const r = ref(db, "live/presence");
    return onValue(r, (snap) => setPresence(snap.val() || {}));
  }, []);

  const entries = Object.entries(presence);

  return (
    <div className="space-y-2">
      {entries.length === 0 && <p className="text-sm text-gray-500">No live events yet.</p>}
      <ul className="divide-y">
        {entries.map(([rfidId, p]) => (
          <li key={rfidId} className="py-2 flex items-center justify-between">
            <div>
              <div className="font-medium">{p.name || rfidId}</div>
              <div className="text-xs text-gray-500">{rfidId}</div>
            </div>
            <div className="text-sm">{p.lastEvent} @ {new Date(p.time).toLocaleTimeString()}</div>
          </li>
        ))}
      </ul>
    </div>
  );
}
