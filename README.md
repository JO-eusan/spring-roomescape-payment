# 🚪방탕출 예약 서비스

방탈출 테마와 시간에 대해 날짜 별로 예약할 수 있는 웹 애플리케이션입니다.
현재 사용자와 관리자를 위한 다음 기능을 제공하고 있습니다.

### 사용자(User)
사용자 계정: `user`, 비밀번호: `1234` (회원가입 기능 추가 예정)

* 인기 테마 목록: http://localhost:8080/
* 예약 페이지: http://localhost:8080/reservation
* 나의 예약 페이지: http://localhost:8080/reservation-mine

### 관리자(Admin)
관리자 계정: `admin`, 비밀번호: `1234`

* 어드민 페이지: http://localhost:8080/admin
* 테마 관리 페이지: http://localhost:8080/admin/theme
* 시간 관리 페이지: http://localhost:8080/admin/time
* 예약 관리 페이지: http://localhost:8080/admin/reservation

## API 명세
Swagger를 이용하여 문서 자동화

* Swagger UI: http://localhost:8080/docs
* OpenAPI 명세 경로: `/v0/api-docs`
