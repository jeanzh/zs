# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

小说追踪与推荐平台 (Novel Tracking & Recommendation Platform) — a multi-platform novel information aggregation platform targeting Web, iOS, Android, Harmony, and WeChat Mini Programs.

## Tech Stack

- **Backend**: Spring Boot 4.x + Spring Security + Spring Data JPA + Spring Cache (Redis)
- **Database**: PostgreSQL 18.4 (primary) + Redis 7 (cache/queue) + optional Elasticsearch (search)
- **Message Queue**: RabbitMQ / Kafka (decouple update notifications; Spring Event as initial fallback)
- **Frontend (Web)**: Nuxt 3 (SSR) + Vue 3
- **Frontend (Mini Program / App)**: uni-app (Vue 3)
- **Storage**: MinIO or Alibaba Cloud OSS (images)
- **Reverse Proxy**: Nginx
- **CI/CD**: GitHub Actions
- **API Docs**: Swagger / OpenAPI

## Architecture

- Front-end / back-end separation; RESTful API with JWT stateless authentication.
- Business services, recommendation engine, and crawler services are separate deployable units.
- API responses follow standard REST conventions with JWT tokens in `Authorization` header.

## Core Domain Entities

| Entity | Key Fields |
|--------|------------|
| User | id, phone (encrypted), wechat_openid, nickname, avatar_url, created_at, status |
| Book | id, title, author, cover_url, tags (text[]), summary, world_background, highlights, serial_status, total_words, avg_rating, review_count, created_at, updated_at |
| Chapter | id, book_id, seq, title, source_url, word_count, published_at |
| Follow | id, user_id, book_id, created_at |
| Review | id, user_id, book_id, rating (1-5), content, likes, created_at, updated_at |
| Feed | id, user_id (recipient), type, book_id, chapter_id, created_at |

## Key Business Flows

1. **Update Tracking → Feed**: When a new chapter is added to a book, the system generates feed entries for all followers. Initially implemented with Spring Events, later decoupled via message queue.
2. **Review**: One review per user per book; submitting a new review overwrites the old one. Deleting a review cascades to remove associated likes.
3. **Auth**: Supports phone + SMS code login, WeChat OAuth, and guest browsing. Authenticated actions (follow, review) require JWT.

## Project Status

This project is at the planning/initiation stage (as of 2026-05-26). The repository currently contains only the requirements document (README.md). No code has been written yet.

Development is planned across 5 milestones over ~12 weeks:
- M1 (2 weeks): Project scaffolding, DB design, user auth, static book detail page
- M2 (3 weeks): Admin panel, follow feature, review system
- M3 (3 weeks): Update tracking logic, feed generation, push notifications
- M4 (2 weeks): Search, multi-platform adaptation, performance/caching
- M5 (2 weeks): Testing, bug fixes, admin polish, deployment
