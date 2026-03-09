create table if not exists users (
   id       uuid primary key,
   email    varchar(255) not null,
   names    varchar(255) not null,
   password varchar(255) not null,
   level    varchar(50) not null
);