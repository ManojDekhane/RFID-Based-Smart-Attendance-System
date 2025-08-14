import { useEffect, useState } from "react";
import api from "../api/axios";

export default function TodayTable() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get("/api/attendance/today")
      .then(r => setRows(r.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="text-sm text-gray-500">Loading...</div>;

  return (
    <div className="overflow-x-auto">
      <table className="min-w-full text-sm">
        <thead className="bg-gray-100">
          <tr>
            <th className="px-3 py-2 text-left">Name</th>
            <th className="px-3 py-2 text-left">RFID</th>
            <th className="px-3 py-2 text-left">Check In</th>
            <th className="px-3 py-2 text-left">Check Out</th>
            <th className="px-3 py-2 text-left">Reader</th>
          </tr>
        </thead>
        <tbody className="divide-y">
          {rows.map(r => (
            <tr key={r.id}>
              <td className="px-3 py-2">{r.name}</td>
              <td className="px-3 py-2">{r.rfidId}</td>
              <td className="px-3 py-2">{r.checkInTime || "-"}</td>
              <td className="px-3 py-2">{r.checkOutTime || "-"}</td>
              <td className="px-3 py-2">{r.readerId || "-"}</td>
            </tr>
          ))}
        </tbody>
      </table>
      {rows.length === 0 && <p className="text-sm text-gray-500 mt-3">No attendance yet today.</p>}
    </div>
  );
}
