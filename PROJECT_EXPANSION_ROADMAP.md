# PMS Full-Fledged Expansion Roadmap

This repository already had core capability for:
- user/role/department management
- performance and review cycles
- goals, key results, reviews, ratings
- role-based security and audit foundations

To move this prototype toward a production-grade PMS without replacing the project, the roadmap below expands feature-by-feature **inside the same codebase**.

## ✅ Added in this iteration

### Continuous Feedback Module
- Real-time feedback API (`/api/feedback`) for employees, managers and HR.
- Feedback composition with:
  - recipient
  - feedback type (appreciation/development/peer/manager)
  - visibility controls
  - message and action items
- Inbox/outbox views:
  - received feedback
  - given feedback
  - manager team feedback
- Recipient acknowledgement workflow.
- Frontend page integrated into existing app routes and sidebar.

## High-priority next modules

1. **1:1 Check-in Management**
   - recurring meeting schedules
   - agenda templates
   - notes + private/shared actions

2. **Competency Framework & Skill Matrix**
   - job-family competency library
   - proficiency levels
   - gap analysis and recommendations

3. **Career Development Plans (IDP)**
   - employee development goals
   - mentor assignment
   - learning-path tracking

4. **Succession Planning / Talent Pools**
   - critical role mapping
   - readiness scoring
   - replacement charts

5. **Calibration Workshops**
   - cross-manager rating normalization
   - comments and final decision audit trail

6. **Rewards & Compensation Integration**
   - merit and bonus recommendation workflows
   - HR approval chain

7. **Objectives Alignment Map**
   - org goal cascade (company → department → team → individual)
   - dependency graph

8. **Notifications & Nudges**
   - in-app and email reminders
   - SLA breach and overdue alerts

9. **Analytics & BI dashboards**
   - trend analytics by cycle/department/manager
   - engagement and completion KPIs

10. **Policy & Workflow Engine**
   - configurable stages by business unit
   - dynamic routing and approvals

11. **360 Feedback**
   - peer nomination
   - anonymized feedback options
   - weighted scoring model

12. **Attachments & Evidence**
   - upload achievements/artifacts per goal/review

13. **Compliance & Governance Enhancements**
   - stronger PII masking
   - retention policies
   - legal hold

14. **Integrations**
   - HRIS sync
   - SSO (SAML/OIDC)
   - calendar integration

15. **Admin configurability**
   - editable rating scales
   - custom templates
   - cycle blueprints

## Production hardening checklist

- Add Flyway/Liquibase migrations.
- Add integration and security tests for all APIs.
- Add API versioning strategy.
- Add observability stack: metrics, tracing, centralized logs.
- Add secret management and environment isolation.
- Add queue resilience patterns for Kafka events.
- Add frontend test suites (unit + e2e).
- Add CI/CD with quality gates.
