## 가구 판매 API 개발 프로젝트
가구 판매 API 개발  

---
### ERD
https://www.erdcloud.com/d/2LT45g7ncsDdZYS7J

---
### USE
- Java 17
- Spring Boot
- Spring Data Jpa
- Spring Security
- JWT (로그인, 인증인가)
- MySQL
- Redis (JWT RefreshToken)
- Swagger (API Document)
- Docker (docker image)
- GitHub Action (build and push docker)
- Iamport API (결제)

---
### 기능
 - 회원(등록, 조회, 수정)
 - 로그인, 로그아웃(JWT, Redis 이용)
 - 상품(등록, 조회, 수정)
 - 장바구니(등록, 수정, 조회)
 - 주문(등록, 조회, 수정)
 - 결제, 환불(Iamport API 호출)