-- create database reserve;
-- use reserve;
-- 상영관
create table room(
                     id bigint primary key auto_increment,
                     name varchar(50) not null
);

-- 좌석
create table seat(
                     id bigint primary key auto_increment,
                     room_id bigint not null,
                     seat_number int not null,
                     is_available boolean default true,

                     foreign key (room_id) references room(id),

                     unique(room_id, seat_number)
);

-- 영화
create table movies(
                       id bigint primary key auto_increment,
                       title varchar(100) not null,
                       grade varchar(10) default '일반',
                       price decimal(10,2) not null,
                       view_count int default 0
);

-- 고객
create table customer(
                         id bigint primary key auto_increment,
                         email varchar(100) not null unique,
                         name varchar(50) not null,
                         age int not null,
                         create_at datetime default current_timestamp,
                         is_available boolean default true
);

-- 예약
create table reservation(
                            id bigint primary key auto_increment,
                            customer_id bigint not null,
                            movie_id bigint not null,
                            room_id bigint not null,
                            seat_id bigint not null,

                            foreign key (customer_id) references customer(id),
                            foreign key (movie_id) references movies(id),
                            foreign key (room_id) references room(id),
                            foreign key (seat_id) references seat(id)
);