# Kafka 기초 개념 및 실습 보고서

## 1. Kafka란?

Apache Kafka는 **대용량의 데이터를 빠르게 처리하기 위한 분산 스트리밍 플랫폼**입니다. 주요 특징은 다음과 같습니다:

- Publisher-Subscriber 모델 기반 메시지 브로커
- 확장성과 내결함성을 고려한 분산 구조
- 메시지 내구성과 정확한 전달 보장
- 실시간 데이터 파이프라인 구축에 적합

---

## 2. Kafka 핵심 개념 정리

| 개념 | 설명 |
|------|------|
| **Producer** | Kafka에 데이터를 발행하는 주체 |
| **Consumer** | Kafka로부터 데이터를 수신하는 주체 |
| **Broker** | 메시지를 저장하고 전달하는 Kafka 서버 |
| **Topic** | 메시지를 구분하기 위한 논리적 채널 |
| **Partition** | Topic을 분할한 단위로 병렬 소비를 가능하게 함 |
| **Offset** | 메시지의 위치를 나타내는 인덱스 |
| **Consumer Group** | 동일 그룹 내 Consumer는 메시지를 나눠 받고, 그룹 간에는 모두 수신 가능 |

---

## 3. Kafka docker compose 설치 및 실습

### 3.1 설치 및 실행

```yml
  kafka:
    image: bitnami/kafka:latest
    ports:
      - "9094:9094"
    volumes:
      - ./data/kafka:/bitnami/kafka
    environment:
      KAFKA_CFG_NODE_ID: 0
      KAFKA_CFG_PROCESS_ROLES: controller,broker
      KAFKA_CFG_LISTENERS: PLAINTEXT://:9092,CONTROLLER://:9093,EXTERNAL://:9094
      KAFKA_CFG_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,EXTERNAL://localhost:9094
      KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP: CONTROLLER:PLAINTEXT,EXTERNAL:PLAINTEXT,PLAINTEXT:PLAINTEXT
      KAFKA_CFG_CONTROLLER_QUORUM_VOTERS: 0@kafka:9093
      KAFKA_CFG_CONTROLLER_LISTENER_NAMES: CONTROLLER
```

### 3.2 기본 명령어

#### 토픽 생성
```shell
$ kafka-topics.sh --create --topic test-topic \
--bootstrap-server localhost:9094 \
--partitions 1 --replication-factor 1
```

#### 메시지 발행
```shell
$ kafka-console-producer.sh --topic test-topic --bootstrap-server localhost:9094
```

#### 메시지 소비
```shell
$ kafka-console-consumer.sh --topic test-topic --from-beginning --bootstrap-server localhost:9092
```
---

## 4. Kafka 연동 실습 (결제 시스템 기반)
### 4.1 흐름 요약

```text
[PaymentService]
  └─ 결제 처리
     └─ PaymentEventPublisher → SpringPaymentPublisher
         └─ ApplicationEvent 발행
             └─ KafkaPaymentListener (@TransactionalEventListener)
                 └─ Kafka 메시지 전송 (Topic: payment-completed)

[KafkaListener]
  ├─ ConcertEventListener → 랭킹 갱신
  └─ ReservationEventListener → 데이터 플랫폼 전달
```

Kafka를 활용해 결제 완료 이벤트를 다른 서비스에 비동기적으로 전달하도록 구성했습니다. 핵심 흐름은 다음과 같습니다:

1. 사용자가 결제를 완료하면 `PaymentService`에서 트랜잭션 처리를 수행합니다.
2. 트랜잭션이 성공적으로 커밋되면, `PaymentEventPublisher`를 통해 도메인 이벤트 `PaymentCompletedEvent`가 발행됩니다.
3. 이 이벤트는 Spring의 `@TransactionalEventListener`를 통해 비동기 리스너인 `KafkaPaymentListener`로 전달되며, 해당 리스너가 Kafka로 메시지를 전송합니다.
4. Kafka에 전송된 메시지는 `payment-completed` 토픽에 저장되며, 여러 개의 Consumer가 독립적으로 이를 구독합니다.
5. `ConcertEventListener`와 `ReservationEventListener`는 각자의 목적에 맞게 메시지를 소비하여 랭킹 갱신, 데이터 전송 등의 후속 처리를 수행합니다.

### 4.2 구성 요소

#### ✅ Producer: KafkaPaymentListener
```java
@Async
@TransactionalEventListener(phase = AFTER_COMMIT)
public void send(PaymentCompletedEvent event) {
    kafkaTemplate.send("payment-completed", event);
}
```
- 트랜잭션 커밋 이후 메시지 발행 → 데이터 정합성 보장
- 비동기 처리로 응답 지연 최소화

#### ✅ Consumer: ConcertEventListener
```java
@KafkaListener(topics = "payment-completed", groupId = "concert-ranking-group")
public void paymentUpdateRankingIfSoldOut(PaymentCompletedEvent event) {
	concertService.addRanking(...);
}
```
- 콘서트 랭킹 갱신 로직을 수행하는 Consumer입니다.
- groupId를 concert-ranking-group으로 지정하여 독립적인 소비 그룹으로 동작합니다.
- 메시지를 수신하면 concertService를 호출하여 해당 콘서트의 매진 속도 기반 랭킹을 갱신합니다.

#### ✅ Consumer: ReservationEventListener
```java
@KafkaListener(topics = "payment-completed", groupId = "reservation-data-platform-group")
public void sendDataPlatform(PaymentCompletedEvent event) {
	log.info("데이터 플랫폼 전송 성공");
}
```
- 예약 데이터 관련 정보를 외부 데이터 플랫폼으로 전달하는 역할입니다.
- groupId를 다르게 설정하여 동일 메시지를 병렬로 각각 소비할 수 있게 했습니다.
