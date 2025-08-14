import { initializeApp } from "firebase/app";
import { getDatabase } from "firebase/database";

const config = {
  apiKey: import.meta.env.VITE_FB_API_KEY,
  databaseURL: import.meta.env.VITE_FB_DATABASE_URL,
  authDomain: import.meta.env.VITE_FB_AUTH_DOMAIN,
};

const app = initializeApp(config);
export const db = getDatabase(app);
