# Jimple

Jimple은 자바로 작성된 독립 실행형 CLI(명령줄 인터페이스) 애플리케이션입니다. Java 런타임 없이도 실행 가능한 네이티브 실행 파일로 제공됩니다.

## 기능
Jimple은 다음과 같은 주요 기능을 제공합니다:
- 간결한 명령어 기반 인터페이스
- 빠른 시작 시간
- GraalVM 네이티브 이미지를 통한 경량화된 실행 파일
- JDK 21 기반의 최신 자바 기능 활용
## 설치 방법
### 바이너리 다운로드

최신 릴리스에서 운영체제에 맞는 바이너리를 다운로드하세요:
- [최신 릴리스](https://github.com/yourusername/jimple/releases)다운로드 후 실행 권한을 부여하세요 (macOS/Linux):

```bash
chmod +x jimple
```
### 소스에서 빌드

소스에서 직접 빌드하려면:
1. 필수 조건:
    - JDK 21 이상
    - Maven
    - GraalVM (네이티브 이미지 빌드용)

2. 빌드 명령:
```bash
# 프로젝트 클론
git clone [https://github.com/yourusername/jimple.git](https://github.com/yourusername/jimple.git) cd jimple

# Maven으로 JAR 빌드
mvn clean package

# 네이티브 이미지 빌드
mvn -Pnative package
```

## 사용 방법
기본 사용법:
```bash 
jimple [options]
```

### 기본 명령어

| 명령어       | 설명       |
| --------- | -------- |
| `help`    | 도움말 표시   |
| `version` | 버전 정보 표시 |

### 예제

도움말 표시:
```bash
jimple help
```

버전 확인:
```bash
jimple version
```

## 프로젝트 구조
```
jimple/
 ├── src/ # 소스 코드 디렉토리
 ├── pom.xml # Maven 빌드 설정
 ├── .gitignore # Git 무시 파일 목록 
 └── README.MD # 프로젝트 문서
````

## 개발 환경 설정

개발 환경을 설정하려면:
1. 이 저장소를 클론합니다:
```bash
git clone https://github.com/yourusername/jimple.git
```
2. IntelliJ IDEA나 Eclipse와 같은 IDE로 프로젝트를 엽니다.
3. JDK 21이 설정되어 있는지 확인합니다.
4. Maven을 사용하여 프로젝트를 빌드합니다:
```
mvn clean install
```
## 기여 방법

1. 이 저장소를 포크합니다.
2. 새 기능 브랜치를 생성합니다 (`git checkout -b feature/amazing-feature`).
3. 변경 사항을 커밋합니다 (`git commit -m 'Add some amazing feature'`).
4. 브랜치를 푸시합니다 (`git push origin feature/amazing-feature`).
5. Pull Request를 생성합니다.
## 라이선스

이 프로젝트는 [MIT 라이선스](LICENSE)에 따라 라이선스가 부여됩니다.

## 연락처

질문이나 피드백이 있으시면 [이슈 트래커](https://github.com/yourusername/jimple/issues)를 통해 문의하세요.
