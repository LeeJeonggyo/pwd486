

# 🛠 Capstone Design 프로젝트 - 졸업작품 - 백엔드<br>

## 💻 프로젝트 소개

---

<h3>주제 <br><br>🔑 SHD:Smart Hidden Door-lock using biometric authentication and IoT technology</h3><br>


### 프로젝트 특징
> - 히든 도어 방식의 스마트 도어락
> - 비밀번호, 지문, 카드키, NFC, 비콘을 이용해 잠금을 해제
> - 모바일 앱에서 해제 기록 조회 및 키 관리 기능 지원
> - 해제 기록 분석을 통한 주 출입시간 계산 및 이를 기반으로 자동 해제 기능 제공

<br>

### 구성

- 도어락 기기 - 아두이노 / 라즈베리파이 / Flask (파이썬)
- 프론트 - 리액트네이티브 / 안드로이드
- 백엔드 - 스프링부트 / MariaDB / firebase
- 서버 - AWS EC2

<br>

## ⏳ 개발 기간

---
<h3>백엔드 - 24.03.01 - 24.05.24 (85일) - 담당자:이정교</h3>
<details>
<summary>전체 일정 상세</summary>
<div>

| 구분    | 기간                         | 담당자                         | 보조  |
|-------|----------------------------|-----------------------------|-----|
| 전체 | 23.09.01 - 24.05.31 (274일) | 전원                          | -   |
| 기획 & 설계 | 23.09.01 - 24.02.29 (182일) | 전원                          | -   |
| 프로토타입 제작 | 23.11.01 - 24.12.08 (38일)  | 전원                          | -   |
| 기기 | 24.03.01 - 24.05.24 (85일)  | 류승준                         | 이정교 |
| 프론트 | 24.03.01 - 24.05.24 (85일) | 이은빈                         | 이정교 |
| 백엔드 | 24.03.01 - 24.05.24 (85일) | 이정교                         | -   |
| 발표 & 시연 | 24.05.24 - 24.05.31 (8일) | 발표 : 류승준 <br>시연 : 이은빈 & 이정교 | -   |

</div>
</details>

### 👨‍👧‍👧 멤버 구성

- 팀장 : 류승준 -
- 팀원 : 이은비 -
- 팀원 : 이정교 -



### ⚙ 개발 환경

- Java 11
- JDK 11.0.22
- *IDE** : IntelliJ IDEA Community Edition
- **Framework** : Spring Boot 2.7.17
- **Database** : Maria DB

<br>

## 📌 주요 기능

---
### 📁 폴더 구조

📦 root <br>
├─ common &emsp;&emsp;&emsp;&emsp;&emsp;&nbsp;&nbsp; // 엔티티/파이어베이스 등의 공통 폴더<br>
├─ machine &emsp;&emsp;&emsp;&emsp;&emsp;&nbsp;&nbsp; // 기기와의 통신 폴더<br>&emsp;&emsp;
├─ openLock &emsp;&emsp;&emsp;// 도어락 해제 신호 발송 폴더<br>&emsp;&emsp;
├─ saveKey &emsp;&emsp;&emsp;&nbsp; // 도어락 해제 키 등록 폴더<br>
├─ phone &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;// 어플과의 통신 폴더<br>&emsp;&emsp;
├─ login &emsp;&emsp;&emsp;&emsp;&emsp;&nbsp; // 어플 로그인 관리 <br>&emsp;&emsp;
├─ main &emsp;&emsp;&emsp;&emsp;&emsp;&nbsp; // 어플 메인 화면 <br>&emsp;&emsp;
├─ registDoorLock &emsp;// 어플에 도어락 등록 화면 <br>&emsp;&emsp;
├─ settings &emsp;&emsp;&emsp;&emsp; // 어플 설정 화면 필요 기능 <br>
├─ README.md
<br><br>


### ✨ 구현 기능
### 📡 machine (기기) - [폴더](https://github.com/LeeJeonggyo/pwd486/blob/9d870d23c876a2cfa00cbf9a3de8e0ae96396b03/src/main/java/univ/inu/Capstone/machine) ###
### └─ 🔐 openLock (기기) - [폴더](https://github.com/LeeJeonggyo/pwd486/blob/9d870d23c876a2cfa00cbf9a3de8e0ae96396b03/src/main/java/univ/inu/Capstone/machine/openLock) ###
#### &emsp;&emsp;[ 도어락 해제 유효성 검증 ] ####
&emsp;&emsp;• 기기 or 모바일 어플 로부터 도어락 해제 시도 데이터를 전달받음.
<br>&emsp;&emsp;• 기기 & 해제키 유효성 검사
<br>&emsp;&emsp;• 해제 시도에 대한 가능(해제) & 불가능(해제 실패) 로그 기록
<br>&emsp;&emsp;• 해제 가능 시, 도어락 기기로 도어락 해제 신호(flask Rest API) 전송
<br>&emsp;&emsp;• 해제 가능 시, 모바일 어플로 해제 알림(firebase) 전송

### &emsp;&emsp;└─ 기능 ###
&emsp;&emsp;&emsp;&emsp;**• 비밀번호 해제** : openBySecretNo()
<br>&emsp;&emsp;&emsp;&emsp;**• RFID & NFC 해제** : openByRfidAndNfc()
<br>&emsp;&emsp;&emsp;&emsp;**• 지문 해제** : openByFingerPrint()
<br>&emsp;&emsp;&emsp;&emsp;**• 태그리스 해제** : openByTagless()

### └─ 🔑 saveKey (기기) - [폴더](https://github.com/LeeJeonggyo/pwd486/blob/94d85494eaa297672e6a6d576612e7abeb30f50e/src/main/java/univ/inu/Capstone/machine/saveKey) ###
#### &emsp;&emsp;[ 해제키 등록 & 변경 & 삭제 ] ####
&emsp;&emsp;• 기기 or 모바일 어플 로부터 도어락과 해제키 데이터를 전달받음.
<br>&emsp;&emsp;• 기기 & 해제키 유효성 검사
<br>&emsp;&emsp;• 기존 등록이 없는 경우, 등록 or 변경 or 삭제

### &emsp;&emsp;└─ 기능 ###
&emsp;&emsp;&emsp;&emsp;**• 카드키 등록** : saveKeyCard()
<br>&emsp;&emsp;&emsp;&emsp;**• 지문 등록** : saveKeyBio()
<br>&emsp;&emsp;&emsp;&emsp;**• 비밀번호 변경** : changePwd()
<br>&emsp;&emsp;&emsp;&emsp;**• 카드키 삭제** : delKeyCard()
<br>&emsp;&emsp;&emsp;&emsp;**• 지문 정보 삭제** : delKeyBio()

### **📱 phone (어플)** - [폴더](https://github.com/LeeJeonggyo/pwd486/blob/9d870d23c876a2cfa00cbf9a3de8e0ae96396b03/src/main/java/univ/inu/Capstone/phone) ###
### └─ login (기기) - [폴더](https://github.com/LeeJeonggyo/pwd486/blob/b75ccf3944e3ed2d1b6823a61179df86c42357fa/src/main/java/univ/inu/Capstone/phone/login) ###
#### &emsp;&emsp;[ 도어락 관리 어플 로그인 ] ####
&emsp;&emsp;• 어플로부터 카카오 로그인시 발급되는 아이디 및 로그인 필요 정보 전달
<br>&emsp;&emsp;• JWT 토큰으로 로그인 상태를 관리한다.

### &emsp;&emsp;└─ 기능 ###
&emsp;&emsp;&emsp;&emsp;**• 카카오 로그인 (최초)** : firstLogin()
<br>&emsp;&emsp;&emsp;&emsp;**• 지문 로그인** : accessLogin()
<br>&emsp;&emsp;&emsp;&emsp;**• accessToken 재발급** : refreshLogin()

### └─ main (기기) - [폴더](https://github.com/LeeJeonggyo/pwd486/blob/7a37810209c505f25f7efbd2f2e9fdc72ad3695d/src/main/java/univ/inu/Capstone/phone/main) ###
#### &emsp;&emsp;[ 도어락 관리 어플 메인 페이지 필요 기능 모음 ] ####

### &emsp;&emsp;└─ 기능 ###
&emsp;&emsp;&emsp;&emsp;**• 사용자별 등록된 nfc 데이터 리스트 출력** : getMyNfcList()
<br>&emsp;&emsp;&emsp;&emsp;**• owner 권한 이외, 등록된 NFC 삭제 API** : delNfcOther()
<br>&emsp;&emsp;&emsp;&emsp;**• nfc 활성화 로그 실행** : activateNfc()

### └─ registDoorLock (기기) - [폴더](https://github.com/LeeJeonggyo/pwd486/blob/8be42bdbc799b7fa1672103a7f63c74303137963/src/main/java/univ/inu/Capstone/phone/registDoorLock) ###
#### &emsp;&emsp;[ 도어락 관리 어플 - 도어락 기기 등록 및 NFC 등록 페이지 기능 모음 ] ####
### &emsp;&emsp;└─ 기능 ###
#### &emsp;&emsp;&emsp;&emsp;&nbsp;[ 도어락 기기 등록 ] ####
&emsp;&emsp;&emsp;&emsp;**• 도어락 기기 정보 등록** : registMachine()
<br>&emsp;&emsp;&emsp;&emsp;**• 시리얼 넘버를 통한 도어락 검색** : searchSerialNo()
<br><br>
#### &emsp;&emsp;&emsp;&emsp;&nbsp;[ NFC 등록 - owner:도어락 주인 ] ####
&emsp;&emsp;&emsp;&emsp;**• 사용자 도어락 NFC 등록 (owner:도어락 주인)** : registNfc()
<br><br>
#### &emsp;&emsp;&emsp;&emsp;&nbsp;[ 설정 화면 - 초대코드 생성 ] ####
&emsp;&emsp;&emsp;&emsp;**• member, guest 초대 코드 생성** : inviteCode()
<br><br>
#### &emsp;&emsp;&emsp;&emsp;&nbsp;[ NFC 등록 - member, guest 권한으로 도어락 해제키(NFC) 사용자 초대 및 등록 ] ####
<br>&emsp;&emsp;&emsp;&emsp;**• 초대 코드 조회** : searchInviteCode()
<br>&emsp;&emsp;&emsp;&emsp;**• owner 권한 이외, NFC 등록 API** : registNfcOther()

### └─ settings (기기) - [폴더](https://github.com/LeeJeonggyo/pwd486/blob/9d870d23c876a2cfa00cbf9a3de8e0ae96396b03/src/main/java/univ/inu/Capstone/phone/settings) ###
#### &emsp;&emsp;[ 도어락 관리 어플 - 설정 페이지 기능 모음 ] ####
### &emsp;&emsp;└─ 기능 ###
#### &emsp;&emsp;&emsp;&emsp;&nbsp;[ 도어락 비밀번호 설정 화면 ] ####
&emsp;&emsp;&emsp;&emsp;**• 마지막 출입시간 기록 조회** : lastLog()
<br>&emsp;&emsp;&emsp;&emsp;**• 도어락 비밀번호 변경** : changePw()
<br><br>
#### &emsp;&emsp;&emsp;&emsp;&nbsp;[ 출입기록 보기 화면 ] ####
&emsp;&emsp;&emsp;&emsp;**• 출입로그 조회** : viewLog()
<br><br>
#### &emsp;&emsp;&emsp;&emsp;&nbsp;[ 멤버관리 화면 ] ####
&emsp;&emsp;&emsp;&emsp;**• 등록된 nfc, 지문, 카드키 전체 조회** : viewRegistKey()
<br>&emsp;&emsp;&emsp;&emsp;**• 승인된 nfc 해제키 리스트 조회** : viewApproveNfcList()
<br>&emsp;&emsp;&emsp;&emsp;**• member & guest 사용허가** : usePermit()
<br>&emsp;&emsp;&emsp;&emsp;**• member & guest 삭제** : delNfcOther()
<br>&emsp;&emsp;&emsp;&emsp;**• 카드키 삭제** : delKeyCard()
<br>&emsp;&emsp;&emsp;&emsp;**• 지문 삭제** : delKeyBio()
<br>&emsp;&emsp;&emsp;&emsp;**• owner 권한 양도** : tossOwnerAuth()
<br><br>
#### &emsp;&emsp;&emsp;&emsp;&nbsp;[ AI 설정 (태그리스 설정) 화면 ] ####
&emsp;&emsp;&emsp;&emsp;**• 데이터 수집, AI 서비스 동의 여부 조회 로직** : selectPrivateYn()
<br>&emsp;&emsp;&emsp;&emsp;**• 토글-데이터 수집여부 update** : dataToggle()
<br>&emsp;&emsp;&emsp;&emsp;**• 토글-AI 서비스 동의 여부 update** : aiServiceToggle()
<br><br>
#### &emsp;&emsp;&emsp;&emsp;&nbsp;[ 카드키 제거 화면 ] ####
&emsp;&emsp;&emsp;&emsp;**• owner의 양도 없는 삭제** : deleteOwner()