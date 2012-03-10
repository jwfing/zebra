CREATE TABLE IF NOT EXISTS CommonDocument (
   id int not null auto_increment,
   articleText varchar(2048),
   description varchar(1024),
   downloadTime bigint not null,
   title varchar(256),
   url varchar(256) not null unique,
   PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
ALTER TABLE CommonDocument convert to CHARSET utf8;
