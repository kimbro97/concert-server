# 🎟️ Redis 기반 대기열 토큰 관리 시스템

## 📌 개요

실시간 트래픽이 몰리는 콘서트 예매 시스템에서 사용자 대기열을 효율적으로 관리하기 위해  
Redis를 활용한 **PENDING → ACTIVE 전환 기반의 토큰 대기열 시스템**을 설계 및 구현했습니다.

---

## 🧱 시스템 구조

### ✅ Redis 자료구조 활용
| 목적          | Redis 자료구조 | Key 형식 예시                       |
| ----------- | ---------- | ------------------------------- |
| 대기열 순서 관리   | Sorted Set | `pending:schedule:{scheduleId}` |
| 활성 사용자 관리   | Set        | `active:schedule:{scheduleId}`  |
| 토큰 상세 정보 조회 | String     | `token:data:{uuid}`             |

---

## 🔄 토큰 흐름

### 1. 토큰 생성 
- 사용자와 스케줄 정보를 기반으로 UUID 토큰 생성
- 상태: `PENDING`
- Redis Sorted Set에 timestamp를 score로 사용하여 순번 저장
- 상세 정보는 `String(JSON)`으로 저장

### 2. 토큰 위치 조회
- Sorted Set의 `ZRANK` 명령어를 통해 순번 조회
- ACTIVE 토큰이면 바로 `location=1` 반환

### 3. ACTIVE 전환 (스케줄러 기반)
- 스케줄별로 PENDING 토큰 중 가장 앞에 있는 토큰 조회
- ACTIVE 토큰 수가 1000개 미만일 경우, 상태 전환
  - Sorted Set에서 제거
  - Set에 추가
  - 상세 JSON 업데이트

### 4. 토큰 만료 처리
- ACTIVE 토큰 중 `expireAt` 기준 시간이 지난 토큰 제거
  - Set에서 삭제
  - JSON 데이터 삭제

---

## ⚙️ 스케줄러 동작

| 스케줄러 메서드          | 설명                     | 주기  |
| ----------------- | ---------------------- | --- |
| `activateToken()` | PENDING → ACTIVE 상태 전환 | 10초 |
| `expireToken()`   | 만료된 ACTIVE 토큰 삭제       | 10초 |

---

## 🧪 테스트 전략

- 유닛 테스트: `TokenServiceTest`
- 통합 테스트: `TokenRedisRepositoryTest`, `TokenServiceIntegrationTest`
- Redis 초기화: `@AfterEach`에서 `flushDb` 처리

---

## ✅ 결론

- DB 부하 없이 Redis만으로 대기열 상태 관리 가능
- 구조가 단순하고 성능이 우수함
- 유지보수 및 확장이 용이한 구조
