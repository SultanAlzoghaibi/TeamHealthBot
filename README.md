# 🧠 TeamHealthBot — Anonymous Slack gRPC Health Check System (Work in Progress 🚧)

TeamHealthBot is an anonymous Slack-integrated health check system designed to make it easier for users to report dysfunctional or unhealthy teams — without pressure, judgment, or retaliation. Each week, users are prompted privately to rate their team from **1–100**, with scoring logic designed to encourage **honest responses** and detect outliers. The scores are processed via a high-performance **C++ gRPC microservice**, with calculations that factor in deviation, inflation limits, and alignment bonuses to detect anomalies and ensure fairness.

---

## 🔧 System Design Overview

### 🎯 Core Flow:
1. Slack bot sends `/teamhealth` prompt to all users (via slash command or weekly schedule).
2. Users respond with a score from 1–100 within 5 minutes.
3. Scores are:
   - Cached in **Redis** (for speed and deduplication)
   - Aggregated by **Spring Boot**
   - Sent to a **C++ gRPC microservice** for score normalization.
4. Final team score is computed using:
   - Team average
   - Variance/deviation penalties
   - Inflation caps (to prevent constant 100s)
   - Alignment bonuses (tight clustering gets bonus)
5. Result is logged, stored in **PostgreSQL**, and can be shown in Slack.

📐 **Figma System Diagram:**  
[View the full architecture & data flow on FigJam →](https://www.figma.com/board/OBpqjJ9DRBn3OYBe9oYGIR/Slack-gRPC?node-id=1-30&t=xftvKoJvGdytPzfn-1)

---

## 🛠 Tech Stack

| Layer         | Tech Used                          |
|---------------|------------------------------------|
| Messaging     | Slack API (bot, slash commands)     |
| Backend       | Java 24, Spring Boot 3.4.4          |
| Caching       | Redis                               |
| Database      | PostgreSQL + Spring Data JPA        |
| Microservice  | C++ gRPC (score calculator)         |
| Deployment    | Docker (Kubernetes-ready)           |
| Messaging     | gRPC (on port `50051`)              |

---

## ⚙️ How to Run Locally

### 🧵 1. Start the gRPC C++ Microservice
```bash
cd cpp_microservice-gRPC/build
cmake ..
make
./microservice


## 🧮 Score Calculation (via gRPC)

```proto
message CalculateScoreRequest {
  string team_id = 1;
  map<string, int32> scores = 2;
}

message CalculateScoreResponse {
  int32 final_score = 1;
}
