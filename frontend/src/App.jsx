import LivePresence from "./components/LivePresence.jsx";
import TodayTable from "./components/TodayTable.jsx";

export default function App() {
  return (
    <div className="min-h-screen bg-gray-50 text-gray-900">
      <header className="px-6 py-4 border-b bg-white">
        <h1 className="text-2xl font-bold">RFID Attendance</h1>
        <p className="text-sm text-gray-600">ESP32 → Firebase → Spring Boot → MongoDB</p>
      </header>

      <main className="p-6 grid gap-6 md:grid-cols-2">
        <section className="bg-white rounded-lg shadow p-4">
          <h2 className="text-lg font-semibold mb-2">Live Presence</h2>
          <LivePresence />
        </section>

        <section className="bg-white rounded-lg shadow p-4 md:col-span-2">
          <h2 className="text-lg font-semibold mb-2">Today</h2>
          <TodayTable />
        </section>
      </main>
    </div>
  );
}
