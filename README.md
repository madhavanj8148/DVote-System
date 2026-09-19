# DVote – Secure Digital Voting & Election Management System

DVote is a comprehensive, multi-platform digital voting solution designed to modernize the electoral process by providing a secure, transparent, and user-friendly environment. The system ensures "One Person, One Vote" through multi-layered verification, including biometric authentication and QR-code validation.

---

## 📌 Overview
Traditional voting systems often face challenges such as logistical inefficiencies, potential for identity fraud, and lack of real-time transparency. **DVote** addresses these issues by offering a dual-platform ecosystem:
1.  **Android Mobile Application**: A secure client for citizens to register, verify their identity, and cast ballots remotely or at polling stations.
2.  **Admin Web Portal**: An enterprise-grade dashboard for election authorities to manage candidates, constituencies, and monitor live results.

---

## 🎯 Objectives
*   **Enhance Security**: Implement hardware-level biometric verification and unique QR-based ballot activation.
*   **Ensure Integrity**: Use real-time database locks to prevent multiple voting per citizen.
*   **Improve Accessibility**: Provide a multilingual interface supporting 22+ Indian languages.
*   **Real-time Monitoring**: Offer election administrators a live counting and analytics dashboard.

---

## ✨ Features

### 🔐 Security & Verification
*   **Biometric MFA**: Mandatory Fingerprint/Face ID verification via the Android Biometric API.
*   **QR-Code Validation**: Unique Voter ID QR scanning to unlock the digital ballot.
*   **Session Security**: Secure login and persistent session management via Firebase Authentication.
*   **Identity Locking**: Real-time server-side flags (`hasVoted`) to permanently lock Voter IDs after a single submission.

### 📱 Voter Experience
*   **Multilingual Support**: Fully localized interface in **22 Indian languages** (Hindi, Telugu, Bengali, Tamil, etc.).
*   **Regional Filtering**: Automatic filtering of candidates and elections based on the user's registered State and Constituency.
*   **Voter Profile**: Personal dashboard showing registration details, location, and voting status.
*   **Modern UI**: Built with **Material Design 3**, featuring smooth Lottie animations and glassmorphism cards.

### 📊 Administrative Management
*   **Live Results**: Real-time vote distribution visualization using interactive Bar and Pie charts.
*   **Candidate Management**: Full CRUD operations (Add, Edit, Delete) for candidates with photo and party symbol uploads.
*   **Election Scheduling**: Centralized control to launch, manage, and end National or State-level elections.
*   **User Roll**: Searchable database of all registered citizens and their voting status.

---

## 🏗️ System Architecture
The application follows a **Serverless 3-Tier Architecture**:

1.  **Presentation Tier**: 
    *   **Android App** (Java) for Voter interactions.
    *   **React Portal** (JS/MUI) for Admin governance.
2.  **Logic Tier**: 
    *   Firebase Security Rules.
    *   Client-side validation (Biometric API, Location Services).
3.  **Data Tier**: 
    *   **Firebase Realtime Database** (JSON Ledger).
    *   **Firebase Cloud Storage** (Asset hosting).

---

## 📱 Application Modules

*   **Splash Screen**: Initial session check and branding animation.
*   **Registration**: Data capture with regional selection (State/Constituency).
*   **Biometric Activity**: Mandatory hardware-level security gateway.
*   **Home Dashboard**: Central navigation for Elections, Profile, and Results.
*   **QR Scanner**: ZXing-powered module for Voter ID verification.
*   **Voting Ballot**: Digital ballot paper with party symbols and confirmation dialogs.
*   **Results Dashboard**: Real-time analytics view for authorized personnel.

---

## 🛠️ Technology Stack

| Category | Technology Used |
| :--- | :--- |
| **Language** | Java (Android), JavaScript (React), Kotlin (Theme/Config) |
| **Android SDK** | Android 10+ (API Level 29+ recommended) |
| **Frontend (Web)** | React.js, Material UI (MUI) 5, Framer Motion |
| **Database** | Firebase Realtime Database |
| **Authentication** | Firebase Auth, Android Biometric API |
| **Storage** | Firebase Cloud Storage |
| **Libraries** | Glide (Image), Lottie (Anim), ZXing (QR), Recharts (Analytics) |
| **APIs** | Google Play Services Location |

---

## 🔥 Firebase Integration

*   **Authentication**: Manages secure voter registration, login, and token-based sessions.
*   **Realtime Database**: Acts as the "Live Ballot Box," ensuring every vote is synced instantly and safely.
*   **Cloud Storage**: Securely stores candidate profile pictures and political party logos.
*   **Hosting**: Deploys the Administrative Dashboard to a secure SSL-certified URL.

---

## 🌐 Multilingual Support
DVote is designed for inclusivity, supporting:
*   **English & Hindi**
*   **Regional Languages**: Telugu, Tamil, Marathi, Bengali, Gujarati, Kannada, Malayalam, Odia, Punjabi, Sanskrit, and more (22+ total).

---

## 🔄 Voting Workflow
1.  **Auth**: User logs in and passes **Biometric Verification**.
2.  **Identify**: User scans their **Voter ID QR Code**.
3.  **Verify**: System checks the `hasVoted` flag in the Realtime Database.
4.  **Select**: If eligible, the user selects a candidate from their specific constituency.
5.  **Submit**: Atomic transaction increments vote count and sets `hasVoted: true`.
6.  **Done**: User is redirected to Home with a success confirmation.

---

## 📂 Project Structure

```text
DVoteSystem/
├── app/                          # Android Application
│   ├── src/main/java/            # Java Logic (Activities, Adapters)
│   ├── src/main/res/             # Layouts, Anims, Strings, Drawables
│   └── AndroidManifest.xml       # App Configuration
├── dvote-admin-portal/           # React Administrative Dashboard
│   ├── src/pages/                # Dashboard Pages (Candidates, Results)
│   ├── src/theme/                # MUI Custom Theme (M3)
│   └── package.json              # Web Dependencies
├── build.gradle.kts              # Project-level Gradle config
└── README.md                     # Documentation
```

---

## 🚀 Installation & Setup

### Mobile Application
1.  Clone the repository.
2.  Add your `google-services.json` to the `/app` directory.
3.  Open the project in **Android Studio**.
4.  Sync Gradle and build the project.
5.  Run on an emulator or physical device with biometric support.

### Admin Portal
1.  Navigate to `/dvote-admin-portal`.
2.  Run `npm install` to install dependencies.
3.  Run `npm start` to launch the local development server.

---

## 🔮 Future Enhancements
*   **Blockchain Integration**: Transition to a decentralized ledger for immutable vote records.
*   **AI Face Match**: Secondary identity verification using live camera feeds.
*   **Geofencing**: GPS-based constituency validation to ensure voters are in the correct region.
*   **Offline Mode**: Secure local vote caching for rural areas with poor connectivity.

---

## 👨‍💻 Development Team

| Name | Role |
|------|------|
| Developer 1 | Android Development / Firebase Integration |
| Developer 2 | React Admin Portal / UI-UX Design |

---

## 🎓 Academic Project
This project was developed as part of the B.Tech Final Year curriculum.
*   **College**: [Insert College Name]
*   **Department**: [Insert Department]
*   **Project Guide**: [Insert Guide Name]

---

## 📄 License
License information can be added here.
