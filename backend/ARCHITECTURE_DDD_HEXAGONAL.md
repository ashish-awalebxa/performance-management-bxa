# Proposed DDD + Hexagonal Architecture Layout

```text
backend/src/main/java/com/example/performance_management_system/performance
├── domain
│   ├── model
│   │   ├── entity
│   │   │   ├── Employee.java
│   │   │   ├── ReviewCycle.java
│   │   │   ├── PerformanceReview.java
│   │   │   └── PerformanceReviewStatus.java
│   │   └── valueobject
│   │       ├── EmployeeId.java
│   │       ├── ReviewCycleId.java
│   │       ├── PerformanceScore.java
│   │       ├── RatingCriteria.java
│   │       ├── GoalStatusSnapshot.java
│   │       ├── GoalProgress.java
│   │       └── KeyResultProgress.java
│   └── service
│       └── PerformanceScoreCalculator.java
├── application
│   ├── dto
│   │   ├── CalculatePerformanceScoreCommand.java
│   │   └── PerformanceScoreResult.java
│   ├── port
│   │   ├── in
│   │   │   └── CalculatePerformanceScoreUseCase.java
│   │   └── out
│   │       ├── EmployeePerformanceMetricsRepository.java
│   │       └── PerformanceReviewRepository.java
│   └── usecase
│       └── CalculatePerformanceScoreService.java
└── infrastructure
    └── adapters
        ├── in
        │   └── web
        │       ├── CalculatePerformanceScoreHttpRequest.java
        │       └── PerformanceScoringController.java
        └── out
            └── persistence
                ├── JpaEmployeePerformanceMetricsRepositoryAdapter.java
                └── JpaPerformanceReviewRepositoryAdapter.java
```

## Aggregate Root Choice

`PerformanceReview` is the aggregate root because score transitions (draft → manager submitted → HR calibrated → finalized), score recalculation, and justifications must stay consistent within one transactional boundary.

## Isolation of Performance Logic

Performance calculation logic is moved to the domain service `PerformanceScoreCalculator`. It only accepts domain value objects (`GoalProgress`, `RatingCriteria`) and returns a domain value object (`PerformanceScore`).
No framework, database, controller, or JPA types are referenced in this domain service.
