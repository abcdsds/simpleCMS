게시판 포트폴리오
=============
___

http://3.34.177.89:9765/
---


간단한 게시판 관리 포트폴리오 입니다.
글쓰기, 댓글, 대댓글, 페이징, 게시판 관리, 글 관리, 댓글 관리, 메뉴 관리, 대쉬보드 같은 기능이 있습니다. 

test 계정 : ice3152 
test pass : 12341234

기술 및 환경
---
___

* Java 8
* Spring Boot
* Spring Data Jpa
* Maven
* Docker
* PostgreSql
* jenkins
* Aws
* Srping Security
* Bootstrap 4
* Jquery
* Junit 5

CI
---
___

* jenkins를 이용하여 배포
* 서버는 aws2 를 사용
* git에 푸쉬할때마다 jenkins에서 자동으로 test와 build를 진행

CD
---
___

* CI서버에서 test와 build가 성공적으로 종료되면
  ssh로 docker 명령어를 전달
  컨테이너 종료후 삭제 및 docker-compose로 컨테이너 다시 생성하는 방식으로 배포
  

서버 & DB
---
___

boot app 서버와 postgresql 서버, CI서버 모두 docker 사용


구조
---
___

<img src="https://user-images.githubusercontent.com/50533198/84874937-453c2380-b0c0-11ea-9087-eed697dbf2a7.png" alt="구조도" style="max-width:100%;">


화면
---
___

![사이트 3](https://user-images.githubusercontent.com/50533198/84889817-7cb4cb00-b0d4-11ea-8ce9-13fcd15fae12.png)
![사이트1](https://user-images.githubusercontent.com/50533198/84889821-7de5f800-b0d4-11ea-81ff-4ce3b8e61d94.png)
![사이트2](https://user-images.githubusercontent.com/50533198/84889823-7e7e8e80-b0d4-11ea-9ae8-e17b80180888.png)
![사이트 4](https://user-images.githubusercontent.com/50533198/84895580-7c6cfd80-b0dd-11ea-9a1b-a44cb07b0e90.png)

DB스키마
---
___

![relation](https://user-images.githubusercontent.com/50533198/84895572-7b3bd080-b0dd-11ea-95b1-2b053ae48b1e.PNG)


