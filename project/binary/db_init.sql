CREATE TABLE IF NOT EXISTS `zb_followlink` (
      `id` bigint(20) NOT NULL AUTO_INCREMENT,
      `comments` varchar(255) DEFAULT NULL,
      `seedUrl` text NOT NULL,
      `tags` varchar(255) DEFAULT NULL,
      `timeCreated` bigint(20) DEFAULT NULL,
      `url` text NOT NULL,
      `urlMd5` varchar(64) NOT NULL,
      PRIMARY KEY (`id`),
      UNIQUE KEY `urlMd5` (`urlMd5`),
      KEY `tags` (`tags`,`timeCreated`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `zb_category` (
      `id` bigint(20) NOT NULL AUTO_INCREMENT,
      `tag` varchar(64) NOT NULL,
      `samples` text,
      `time_created` bigint(20) DEFAULT NULL,
      PRIMARY KEY (`id`),
      UNIQUE KEY `tag` (`tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS`zb_seed` (
      `id` bigint(20) NOT NULL AUTO_INCREMENT,
      `next_fetch` bigint(20) DEFAULT NULL,
      `strict` text,
      `tags` varchar(255) DEFAULT NULL,
      `time_created` bigint(20) DEFAULT NULL,
      `update_period` bigint(20) DEFAULT NULL,
      `url` text NOT NULL,
      `urlMd5` varchar(64) NOT NULL,
      PRIMARY KEY (`id`),
      UNIQUE KEY `urlMd5` (`urlMd5`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

