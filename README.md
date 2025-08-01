# Jimple

정적 사이트 생성기 - 마크다운 파일을 HTML로 변환하여 개인 블로그를 생성하는 Java 기반 CLI 도구

## 개요

Jimple은 마크다운 파일을 읽어 정적 HTML 블로그를 생성하는 Java 21 기반의 정적 사이트 생성기입니다. 개인 블로그나 문서 사이트를 빠르게 구축할 수 있도록 설계되었습니다.

## 주요 기능

- 📝 **마크다운 → HTML 변환**: CommonMark 기반의 마크다운을 HTML로 변환
- 🎨 **테마 커스터마이징**: YAML 설정을 통한 색상, 레이아웃, 프로필 정보 커스터마이징
- 📱 **반응형 디자인**: 모바일과 데스크톱 환경에 최적화된 반응형 레이아웃
- 🚀 **고성능**: GraalVM 네이티브 이미지 지원으로 빠른 실행 속도
- 📊 **목록 페이지**: 자동으로 생성되는 포스트 목록 및 페이지네이션
- 🔧 **CLI 인터페이스**: 간단한 명령어로 사이트 생성

## 기술 스택

- **Java 21**: 최신 Java 기능 활용 (record, pattern matching, switch expressions)
- **Maven**: 빌드 및 의존성 관리
- **CommonMark**: 마크다운 파싱 및 HTML 변환
- **Jackson**: YAML 설정 파일 파싱
- **JSoup**: HTML 조작 및 처리
- **JUnit 5 + Mockito**: 단위 테스트

## 설치 및 실행

### 요구사항

- Java 21 이상
- Maven 3.6 이상

### 빌드

```bash
# 프로젝트 클론
git clone <repository-url>
cd jimple

# 의존성을 포함한 JAR 빌드
mvn clean package

# 네이티브 이미지 빌드 (GraalVM 필요)
mvn clean package -Pnative
```

### 사용법

```bash
# 도움말 확인
java -jar target/jimple-0.1.0.jar help

# 마크다운 파일이 있는 디렉터리를 HTML로 변환
java -jar target/jimple-0.1.0.jar run <소스디렉터리> [결과디렉터리]

# 설정 파일을 사용하여 변환
java -jar target/jimple-0.1.0.jar run <소스디렉터리> <결과디렉터리> --config config.yml
```

### 설정 파일 예시

```yaml
# config.yml
title: "내 블로그"
description: "개발 이야기를 공유하는 블로그입니다."

layout:
  title: "My <span style=\"color:#10b981\">Blog</span>"
  welcome:
    show: true
    title: "<h1>안녕하세요!</h1>"
    comment: "<p>블로그에 오신 것을 환영합니다.</p>"

profile:
  name: "홍길동"
  role: "개발자"
  bio: "Java 개발을 좋아하는 개발자입니다."
```

## 프로젝트 구조

```
src/
├── main/java/com/jimple/
│   ├── Main.java                    # 메인 진입점
│   ├── collector/                   # 마크다운 파일 수집 및 매핑
│   ├── finder/                      # 파일 검색
│   ├── generator/                   # HTML 생성
│   │   └── converter/               # 마크다운 → HTML 변환
│   ├── manager/                     # 전체 프로세스 관리
│   ├── model/                       # 데이터 모델
│   │   ├── config/                  # 설정 관련 모델
│   │   ├── generator/               # 생성 관련 모델
│   │   ├── list/                    # 목록 페이지 모델
│   │   └── md/                      # 마크다운 관련 모델
│   └── parser/                      # 파싱 관련
│       ├── extractor/               # 메타데이터 추출
│       ├── template/                # 템플릿 엔진
│       └── yml/                     # YAML 파싱
└── test/                            # 단위 테스트
```

## 개발 원칙

이 프로젝트는 다음 원칙을 따릅니다:

- **SOLID 원칙** 준수
- **TDD (Test-Driven Development)** 적용
- **Java 21** 최신 기능 활용
- **계층형 아키텍처** (Controller → Service → Repository)
- **불변 객체** 우선 사용 (record 클래스)

## 라이선스

이 프로젝트는 [LICENSE](LICENSE) 파일에 명시된 라이선스를 따릅니다.

## 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 문의

프로젝트에 대한 문의사항이 있으시면 Issue를 통해 연락주세요.