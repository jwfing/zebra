CREATE TABLE `zb_news` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `url` varchar(256) NOT NULL,
  `downloadTime` varchar(256) DEFAULT NULL,
  `publishTime` varchar(256) DEFAULT NULL,
  `title` varchar(256) DEFAULT NULL,
  `downloadSource` varchar(256) DEFAULT NULL,
  `publisher` varchar(256) DEFAULT NULL,
  `tags` varchar(256) DEFAULT NULL,
  `mainText` text,
  `attachmentPath` varchar(1024) DEFAULT NULL,
  `dupFlag` int(11) DEFAULT '0',
  `signature` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `url` (`url`)
) ENGINE=MyISAM AUTO_INCREMENT=3297 DEFAULT CHARSET=utf8;