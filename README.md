# 🚀 Backend Repository

이 레포지토리는 **베타랩 백엔드 개발**을 위한 저장소입니다.  
Java & Spring Boot 기반으로 구축되었으며, Docker + AWS 인프라 환경에서 운영됩니다

---

## 📦  Stack

### Backend
![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

### Infrastructure / Deployment
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![AWS EC2](https://img.shields.io/badge/AWS%20EC2-FF9900?style=for-the-badge&logo=amazon-ec2&logoColor=white)
![AWS RDS](https://img.shields.io/badge/AWS%20RDS-527FFF?style=for-the-badge&logo=amazon-rds&logoColor=white)
![AWS ECR](https://img.shields.io/badge/AWS%20ECR-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white)

### CI/CD
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white)

### Version Control & Collaboration
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)

---

## 🌿 Git 브랜치 전략

1. **각자 브랜치에서 작업** 후, `main` 브랜치로 **Pull Request**.
2. **2명 이상의 BE 개발자** 코드 리뷰 시 `main` 브랜치로 머지 가능.
3. **간단한 기능**: 1명 승인 후 머지.
4. **주요 기능**: 2명 이상 승인 후 머지.

---

## 📝 커밋 메시지 컨벤션

| 타입 | 설명 |
|------|------|
| `Feat(/#이슈번호)` | 새로운 기능 추가 |
| `Fix(/#이슈번호)` | 버그 수정 |
| `Chore(/#이슈번호)` | 빌드 작업, 환경 설정 |
| `Refactor(/#이슈번호)` | 코드 리팩토링 (기능 변경 없음) |
| `Docs(/#이슈번호)` | 문서 수정 |

---

## 🔍 메서드 네이밍 컨벤션

- **생성** : `create`
- **수정** : `update`
- **삭제** : `delete`
- **조회** : `find`

---

## 💡 코드 컨벤션

- 클래스 선언부 아래 **필드** 작성 시 한 칸 띄우기
- **메서드 길이**는 15줄 이하 (SRP 원칙 준수)
- 의미 없는 개행 제거, 개행 규칙 준수
- **블록 들여쓰기**는 1단계로 제한
- 블록 띄어쓰기는 4칸, LF(Line Feed) 사용
- 블록 아래 한 칸 띄우고 작성
- `else` 사용 지양
- `stream` 사용 시 `.stream()` 뒤에 줄바꿈

---
## 📂 프로젝트 구조

~~~
src/main/java/com/example/nexus/
├── app/
│   ├── category/          # 카테고리 관리
│   ├── dashboard/         # 대시보드 기능
│   ├── global/            # 전역 설정 (Security, OAuth, S3 등)
│   ├── message/           # 메시지 기능
│   ├── mypage/            # 마이페이지
│   ├── post/              # 게시글 관리
│   ├── ranking/           # 랭킹 시스템
│   ├── recruitment/       # 모집 프로필
│   ├── review/            # 리뷰 시스템
│   └── user/              # 사용자 관리
├── notification/          # 알림 시스템
└── NexusApplication.java  # 메인 애플리케이션
~~~

### 📁 각 모듈 구조
```
모듈명/
├── controller/    # API 엔드포인트
├── service/       # 비즈니스 로직
├── repository/    # 데이터 액세스
├── domain/        # 엔티티
└── dto/           # 데이터 전송 객체
```
