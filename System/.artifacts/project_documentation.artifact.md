# Project Documentation: TransitGo (Smart Bus Transit & Commuter System)

## 1. Project Overview & Title
- **Project Name:** TransitGo
- **System Type:** Object-Oriented Android Mobile Application (Smart Bus Transit, Ticketing, Live GPS Tracking, and AI Support System)
- **Target Institution / Domain:** University and Public Commuter Transit System (e.g., BatStateU / Manila Transit routes)

---

## 2. Introduction & Background
Navigating daily public transit can often be stressful and inefficient due to unpredictable bus schedules, lack of real-time location tracking, manual ticketing friction, and difficulties reaching customer support. **TransitGo** is designed to modernize commuter transit by bridging passengers, drivers, conductors, and administrators onto a unified real-time platform.

The application streamlines bus schedule discovery, seat booking with digital payment integration (GCash/PayMaya), live GPS bus tracking, driver rating feedback, and an integrated 24/7 AI-powered support chatbot.

---

## 3. Objectives
### General Objective
To design and develop a robust, secure, and user-friendly object-oriented Android mobile application that digitizes bus transit operations, enhances commuter convenience through real-time tracking and digital ticketing, and provides administrative oversight.

### Specific Objectives
1. Implement secure role-based authentication (Commuter, Conductor, Driver, Administrator) with institutional email validation (`@g.batstate-u.edu.ph`).
2. Enable commuters to search routes, view schedules, select seats, and book tickets with digital payment verification.
3. Provide real-time GPS tracking of buses on interactive maps and terminals.
4. Provide conductors and drivers with dedicated mobile tools for schedule management, headcount tracking, and live navigation.
5. Provide administrators with comprehensive dashboard controls for buses, drivers, conductors, routes, schedules, and terminals.
6. Integrate an intelligent AI Customer Support Chatbot to assist commuters with frequently asked questions, tracking guides, and support hotline details.

---

## 4. Scope and Limitations
### Scope
- **Passenger Module:** Profile management, route search, bus schedule viewing, interactive seat selection, ticket booking, live bus tracking, trip & driver rating, and AI assistant chatbot.
- **Conductor Module:** Conductor ticketing, headcount monitoring, and trip management.
- **Driver Module:** Driver schedule viewing and live GPS navigation.
- **Admin Module:** Complete CRUD management for buses, drivers, conductors, routes, schedules, and terminal locations.
- **Backend Integration:** Firebase Authentication, Cloud Firestore real-time synchronization, and Supabase image storage.

### Limitations
- Real-time GPS coordinates rely on device location services and simulated transit telemetry for demonstration routes.
- Payment processing simulates digital wallet verification (GCash / PayMaya QR scanning) rather than direct production banking SDK callbacks.

---

## 5. System Architecture & Tech Stack
- **Platform:** Android (Min SDK 27, Target SDK 37)
- **UI Framework:** Jetpack Compose (Material 3 Design System)
- **Language:** Kotlin
- **Database & Cloud:** Firebase Authentication & Cloud Firestore (Real-time NoSQL database)
- **Asynchronous Processing:** Kotlin Coroutines & Flows
- **AI Integration:** Google Gemini Generative AI REST API (`v1beta` / `v1`) with offline fallback support
- **Mapping & Location:** Google Play Services Location & Custom Leaflet Map views

---

## 6. User Roles & Features
### 1. Commuter / Passenger
- **Authentication:** Secure registration & login with institutional email validation.
- **Route & Schedule Search:** Browse active bus schedules, origins, destinations, and fares.
- **Seat Booking & Payment:** Select bus seats interactively and pay via GCash/PayMaya QR code.
- **Live Bus Tracking:** Track assigned buses in real-time on the map view.
- **Trip & Driver Rating:** Rate completed trips and drivers from 1 to 5 stars.
- **AI Support Chatbot:** Access 24/7 automated assistance for FAQs, tracking, and support hotline information.

### 2. Conductor
- **Ticketing & Headcount:** Issue tickets and manage passenger headcounts per trip.
- **Trip Management:** Monitor active bus trips and passenger manifests.

### 3. Driver
- **Schedule Viewer:** View assigned daily trips and departure times.
- **Live Navigation:** Use turn-by-turn navigation and telemetry reporting.

### 4. Administrator
- **Dashboard & Management:** Full control over Fleet Management (Buses, Drivers, Conductors, Routes, Schedules, Terminals).
- **System Monitoring:** Real-time visibility into active bus locations and schedules.

---

## 7. Database Design & Data Models
The application utilizes object-oriented data models synchronized with Firebase Firestore:
- **`user` (`user.kt`):** Stores `userId`, `fullname`, `email`, `password`, `role`, and `imageUrl`.
- **`Bus` (`Bus.kt`):** Stores `busId`, `busNumber`, `capacity`, `assignedDriver`, and `status`.
- **`BusRoute` (`BusRoute.kt`):** Stores `routeId`, `routeName`, `origin`, `destination`, and `fare`.
- **`BusSchedule` (`BusSchedule.kt`):** Stores `scheduleId`, `bus` object, `route` object, `departureTime`, `arrivalTime`, and `status`.
- **`BusStop` (`BusStop.kt`):** Stores `stopId`, `stopName`, and location coordinates.

---

## 8. Key Modules & Implementation Highlights
- **Jetpack Compose UI:** Built entirely with declarative UI components, custom cards, bottom navigation bars, and dynamic dialogs.
- **Database Manager (`DatabaseManager.kt`):** Centralized repository handling Firestore queries, real-time listeners (`snapshotListener`), and user/schedule persistence.
- **AI Chatbot (`CommuterChatbot.kt`):** Integrated AI virtual assistant supporting natural language questions, quick suggestion chips, and robust local rule-based fallback responses.

---

## 9. Testing & Verification
- **Build Verification:** Successfully compiled and built debug APK (`app-debug.apk`) using Android Gradle Plugin and Kotlin Compose compiler.
- **Functional Testing:** Verified smooth navigation between Home, Bookings, Tracking, and Profile screens, as well as AI chatbot response handling.

---

## 10. Conclusion & Future Enhancements
### Conclusion
TransitGo successfully demonstrates a modern, object-oriented, cloud-connected mobile transit application that addresses real-world commuting challenges through intuitive UI design, real-time synchronization, and AI assistance.

### Future Enhancements
- Integration with official banking APIs (real GCash/Maya payment gateway webhooks).
- Push notifications for bus arrival alerts and schedule updates.
- Advanced analytics dashboard for transit operators.
