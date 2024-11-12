## CommunityHub - 커뮤니티 플랫폼 프로젝트

### 기본 정보

**프로젝트 개요**

CommunityHub는 회원 관리, 게시물 관리, 댓글 관리, 채팅 기능을 제공하는 커뮤니티 플랫폼입니다. 본 프로젝트의 백엔드는 Spring Boot와 JPA를 기반으로 설계되었으며, **RESTful API**와 **JWT 기반 인증/인가를 통해 안정적인 데이터 처리를 구현**하였습니다. **CI/CD 파이프라인 구축을 통해 자동 배포 환경을 설정**하고, QueryDSL과 Fetch Join을 사용하여 **데이터 조회 성능을 최적화**하였습니다.

**프로젝트 진행 기간**

- 2024.9.3 ~ 진행 중

---

**Swagger API Documentation**: https://app.swaggerhub.com/apis/Hidea/CommunityPlatform/1.0.0

---

### **기술 스택**

- **Backend**: Java, Spring Boot, Spring Security, JPA, QueryDSL
- **Database**: MySQL(H2 임시 사용 중), Redis
- **DevOps**: Docker, Jenkins, AWS EC2
- **API Documentation**: Swagger
- **Version Control**: GitHub

---

### **주요 기능**

1. **회원 관리**
    - 회원 가입, 로그인, JWT 기반 인증 및 토큰 갱신.
    - 비밀번호 암호화 및 프로필 정보 관리.
    - 로그인 유효성 검사 및 로그인 이력 관리.
2. **게시물 관리**
    - 게시물 작성, 수정, 삭제 및 조회 기능.
    - 키워드 기반 게시물 검색, 조회수 관리.
    - 이전/다음 게시물 조회 및 페이징 처리.
3. **댓글 관리**
    - 각 게시물에 대한 댓글 작성, 수정, 삭제 및 조회.
    - 작성자 및 댓글 별 알림 기능 제공.
4. **지연 로딩 문제 해결 및 성능 최적화**
    - QueryDSL과 Fetch Join을 사용해 N+1 문제를 해결하고, 지연 로딩으로 인한 프록시 객체 직렬화 문제 방지.
    - DTO를 통해 필요한 데이터만 조회하여 성능 최적화.
5. **JWT 리프레시 토큰 최적화 및 보안 강화**
    - Redis를 이용하여 리프레시 토큰을 저장 및 관리.
    - 탈취된 토큰의 제어 및 로그아웃 시 토큰 무효화 처리.

---

### **ERD 설계**

<img src="https://github.com/user-attachments/assets/3d78dd0b-9401-4d12-ba5d-c7c3de38e26a" width="800" />


---

### **트러블슈팅 및 최적화**

### **1. 직렬화 문제 해결 및 DB 조회 성능 최적화**

---

**문제 상황**

- **기존 문제: 즉시 로딩 문제**
    - 개발 공부 중 ‘즉시 로딩’과 ‘지연 로딩’ 개념에 대해 배우면서 현 프로젝트의 문제점에 대해 알게 됨.
    - 현재 프로젝트는 모든 연관 관계가 **즉시 로딩**으로 설정되어 있어서 매번 연관 객체까지 함께 조회하며 조회 성능 약화.
- **지연 로딩 적용 후: 직렬화 문제**
    - Jackson이 지연 로딩된 프록시 객체를 **직렬화**하려고 시도할 때 `InvalidDefinitionException` 예외 발생.
        
        ```java
        com.fasterxml.jackson.databind.exc.InvalidDefinitionException:
        No serializer found for class org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor and no properties discovered to create BeanSerializer
        ```
        
- **추가 문제: N+1 문제**
    - 위 문제 해결을 위해 인터넷을 뒤지던 중 JPA의 N+1 문제에 대해 추가적으로 배우게 됨.
    - 현재 연관 객체까지 조회 시 이에 해당하는 **N개 쿼리가 추가**로 발생해 비효율적 조회 실행.
        - 현재는 소량의 데이터로 테스트 중이지만, 추후 연관 객체의 데이터가 많아지면 문제가 더욱 악화될 수 있음.

**원인 탐색**

- Hibernate가 엔티티 간의 연관 관계를 **지연 로딩** 방식으로 처리하여 **프록시 객체**를 반환.
- Jackson이 실제 객체가 아닌 프록시 객체를  JSON으로 직렬화하려고 하여 직렬화 오류 발생.

**해결 방법**

**직렬화 문제 해결: DTO(Data Transfer Object) 적용**

- 클라이언트에 프록시 객체가 아닌 실제 객체 타입을 반환해 **직렬화 문제를 방지**.
- 레이어 간 명확한 **경계 설정** 및 주요 필드 노출을 방지해 **보안 강화**.

**추가 최적화: QueryDSL 적용**

- DTO만 사용 시 엔티티 전체를 조회해야 하므로 QueryDSL을 이용해 필요한 필드만 조회해 조회 성능 최적화.
- Q클래스를 통해 **Type Safe**하게 엔티티 간의 조인을 수행.

**N+1 문제 해결: Fetch Join 적용**

- 연관된 모든 엔티티를 한 번의 조회로 가져오기 위해 `Fetch Join`을 사용하여 즉시 로딩(Immediate Loading)으로 처리.
- 지연 로딩 시 발생할 수 있는 다수의 추가 쿼리 실행을 방지하여 성능 최적화.

**추가 최적화: 읽기 전용 Transactional 설정**

- `@Transactional(readOnly = true)` 어노테이션을 사용하여 읽기 전용 트랜잭션 설정.
- JPA가 **변경 감지(Dirty Checking)** 기능을 수행하지 않아 데이터베이스 **쓰기 작업 최소화**.
    - 변경 감지: 엔티티의 필드 값이 변경되었는지 추적하여 트랜잭션 종료 시점에 수정된 내용을 반영하기 위해 update 쿼리를 실행하는 것.
- 읽기 작업에 대한 **DB Lock 회피** 가능.
    - 일반적으로 트랜잭션 내에서 데이터를 조회하면 데이터베이스의 락이 발생하여 다른 트랜잭션이 해당 데이터에 접근하는 것이 제한됨.
    - 하지만 읽기 전용 트랜잭션 적용 시 `SELECT` 쿼리가 **shared lock**을 사용하지 않게 하여, 쓰기 트랜잭션이 데이터 변경을 시도해도 서로 간섭하지 않고 안전하게 데이터를 읽을 수 있음(MySQL 기준).

**참고 자료**

- QueryDSL 이점 및 사용법
    - https://github.com/querydsl/querydsl/blob/master/README.md
- 지연 로딩 관련 정보 및 해결법
    - https://docs.jboss.org/hibernate/orm/7.0/introduction/html_single/Hibernate_Introduction.html
- MySQL에서 읽기 전용 트랜잭션 사용 이점
    - https://dev.mysql.com/doc/refman//8.0/en/innodb-performance-ro-txn.html

**결과**

- 직렬화 문제 해결 및 조회 성능 개선.
- 직렬화 오류 없이 데이터 전달 가능, 필요한 데이터만 조회함으로써 전체 응답 속도 향상.
- 데이터베이스 쓰기 작업 최소화 및 읽기 작업에 대한 DB Lock 회피 가능.

### **2. Jenkins & AWS EC2 서버 무한 빌드 및 다운 현상 해결**

---

**문제 상황**

- Jenkins 빌드가 끝없이 지속되다가 AWS EC2 서버가 다운됨.

**원인 탐색**

- CPU 사용률을 모니터링한 결과, 빌드 중 **CPU와 RAM의 사용량이 최대치**에 도달하여 서버가 응답하지 않음.
    
    <img src="https://github.com/user-attachments/assets/3f8ba86b-0cc8-4b78-aa15-25c87f107670" width="550" />


    
- EC2 인스턴스(t2.micro)의 제한된 성능(CPU, 1GB RAM)으로 인해 Jenkins 빌드 중 **메모리 부족 문제** 발생.

**해결 방법**

**인스턴스 성능 업그레이드**

- 더 성능이 좋은 인스턴스 모델을 사용.
- 그러나 해당 방법은 **비용 문제**로 적용이 어려움.

**Swap 메모리 설정**

- **Swap 메모리 개념**
    - RAM 부족 시, 디스크를 임시 메모리로 사용하여 시스템의 안정성을 높임. (운영체제 수업에서 수강한 내용)
- **설정 절차**
    - AWS 공식 문서를 통해 해당 서버에 **가장 적합한 스왑 메모리 할당량** 설정.
        - ununtu 공식 문서에 따르면 RAM이 1GB 이상인 경우 **최대치의 두 배** 권장
    - AWS EC2 인스턴스에 접속하여 2GB의 Swap 파일 생성 및 설정.
        
        <img src="https://github.com/user-attachments/assets/f505ff20-05a4-450a-bb60-bf426863020d" width="550" />
        

**참고 자료**

- **메모리 부족 시 해결법**
    - https://repost.aws/ko/knowledge-center/ec2-memory-swap-file
- **스왑 메모리 할당량 설정**
    - https://help.ubuntu.com/community/SwapFaq#How_much_swap_do_I_need.3F

**결과**

- Swap 메모리 설정으로 메모리 부족 문제 방지, 안정적인 빌드 및 배포 환경 확보.
- Jenkins 빌드 도중 발생하던 무한 빌드 및 서버 다운 문제 해결.
- **빌드 시 CPU 사용률** **100% 초과 → 평균 50% 미만**으로 감소.
    
    <img src="https://github.com/user-attachments/assets/48f3476b-b5d7-4f2d-8d5d-27d8850d981d" width="550" />
    

---

### 빌드 및 배포

**CI/CD 파이프라인 설정**

1. **Jenkins Pipeline 설정**:
    - Jenkinsfile을 통해 소스 코드 빌드, 테스트, Docker 이미지 생성 및 AWS EC2 서버에 배포 자동화.
2. **Docker 설정**:
    - Dockerfile을 사용하여 애플리케이션 환경을 컨테이너화하여 테스트 및 배포 효율성 증대.
3. **AWS 배포**:
    - AWS EC2 인스턴스를 통해 애플리케이션 호스팅.
    
    <img src="https://github.com/user-attachments/assets/85449cca-cfec-43e5-9f2f-429468c39290" width="550" />


    

---

### 향후 계획

**채팅 기능 추가**

- **Spring WebSocket**
    - 실시간 양방향 통신을 위해 Spring WebSocket을 사용하여 안정적이고 확장 가능한 구조를 구현.
- **STOMP 프로토콜**
    - STOMP(Simple Text Oriented Messaging Protocol)를 사용하여 메시징을 관리하고, 사용자의 메시지를 특정 주제(topic)로 구분하여 효율적인 전송 지원.
- **Redis Pub/Sub**
    - 여러 서버 간의 실시간 채팅 데이터 공유를 위해 Redis Pub/Sub를 적용하여 세션 관리를 효율적으로 처리.
    - 이를 통해 서버 간 데이터를 빠르게 전파하여 확장성을 높이고, 사용자 경험을 개선 가능.
- **JWT 기반 인증**
    - WebSocket 연결 시 JWT 토큰을 활용하여 사용자 인증을 수행하며, 채팅 메시지 발신 및 수신 시에도 인증된 사용자만이 참여할 수 있도록 보안성 강화.

**CSRF 보호 활성화**

- Jenkins 사용 시 HTTP 요청 보내면 Jenkins의 CSRF 보호에 의해 아래 403 응답 반환.
    - `Error 403 No valid crumb was included in the request`
- 위 문제 해결을 위해 현재 Jenkins에서 **CSRF 보호 비활성화 스크립트**를 실행해 CSRF 보호 비활성화 ****수행.
    - 그러나 해당 방법은 **보안 위험**이 있어 권장되지 않음.
- 스프링 서버도 현재 **CSRF 보호 비활성화 상태**
- 추후 보안 강화를 위해 **스프링, Jenkins CSRF 보호 활성화로 변경**할 예정

**대용량 트래픽 처리 테스트 및 DB 최적화**

- SSAFY(Samsung Software Academy) 멘토링을 통해 **DB Lock 관리, 모니터링(detect), 로그관리 등 DB 최적화**가 모든 프로젝트에서 굉장히 중요하다는 것을 배움.
    - 현업에서 DB Lock이 걸리는 경우가 빈번하다고 말씀하심.
    - DB 최적화 방식이 굉장히 다양하며 중요한 작업임을 강조. (인덱스 이용, 타입 변경 등)
- **대용량 트래픽 처리 테스트**를 통해 DB Lock 발생 및 성능 저하 문제에 대한 해결책을 모색하고, 안정적인 데이터 처리를 위한 최적화 전략을 수립할 예정.
- **CRUD 작업이 빈번한 환경**에서 delete, insert, update 작업 시 **DB Lock이 발생하지 않도록 트랜잭션 관리** 및 **데이터 접근 방식을 개선**할 방안을 연구하고, 이를 통해 효율적인 데이터 처리 방안을 마련.
- **모니터링 및 시스템 안정화 도구 활용**
    - Prometheus, cAdvisor, Node Exporter, Grafana 등 **모니터링 도구**를 활용하여 서버 및 DB 성능을 실시간으로 모니터링하고, **이상 징후를 탐지**할 수 있는 시스템 구축.
    - 이를 통해 **트래픽 증가**에 따른 시스템 병목 현상을 신속하게 발견하고 대응 가능.
- **DB 최적화 및 성능 개선**
    - 데이터베이스 **쿼리 최적화**를 통해 응답 속도를 높이고, DB Lock 문제 최소화.
    - **인덱스 최적화**, **데이터 타입 변경(예: Date → int)**, **쿼리 구조 개선** 등의 방법을 적용하여, 대용량 데이터 조회 및 조작 시 성능 저하를 방지하고 최적의 성능 유지.
- **로그 관리 및 분석 시스템 구축**
    - DB 및 애플리케이션 로그를 체계적으로 관리하고, **로그 분석 시스템을 구축**하여 트랜잭션 오류나 비정상적인 접근을 신속하게 탐지하고 대응할 수 있는 체계 마련.
