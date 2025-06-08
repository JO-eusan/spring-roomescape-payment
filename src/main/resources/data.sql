-- 테마 등록 (id 자동 증가)
INSERT INTO theme (name, description, thumbnail)
VALUES ('공포의 저택', '무서운 이야기', 'https://picsum.photos/200/300'),
       ('미스터리 학교', '괴담과 미스터리', 'https://picsum.photos/200/300'),
       ('마법사의 방', '판타지 테마', 'https://picsum.photos/200/300');

-- 예약 시간 등록 (id 자동 증가)
INSERT INTO reservation_time (start_at)
VALUES ('10:00'),
       ('14:00'),
       ('18:00');

INSERT INTO member (name, email, password, role)
VALUES ('관리자', 'admin', '1234', 'ADMIN'),
       ('사용자', 'user', '1234', 'USER');
