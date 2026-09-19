import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";
import { getDatabase } from "firebase/database";
import { getStorage } from "firebase/storage";

const firebaseConfig = {
  apiKey: "AIzaSyCaOHDZscmUUKiOktNmoLUrCZ_zC9TDhlg",
  authDomain: "dvote-system.firebaseapp.com",
  databaseURL: "https://dvote-system-default-rtdb.firebaseio.com",
  projectId: "dvote-system",
  storageBucket: "dvote-system.firebasestorage.app",
  messagingSenderId: "277074315002",
  appId: "1:277074315002:android:a2d382f181f9869fc4ab43"
};

const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);
export const db = getDatabase(app);
export const storage = getStorage(app);
