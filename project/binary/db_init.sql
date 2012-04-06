CREATE TABLE IF NOT EXISTS CommonDocument (
   id int not null auto_increment,
   urlMd5 varchar(64) not null unique,
   sourceUrl varchar(256) not null,
   url varchar(256) not null,
   title varchar(256),
   channel varchar(256),
   downloadTime bigint not null,
   articleText varchar(2048),
   description varchar(1024),
   PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
