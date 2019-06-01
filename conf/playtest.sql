------mysql----------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', '张');
INSERT INTO `user` VALUES ('3', '王五');
INSERT INTO `user` VALUES ('4', 'sss');

------postgresql--------
---建库
CREATE DATABASE playtest WITH OWNER = postgres
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'Chinese (Simplified)_China.936'
       LC_CTYPE = 'Chinese (Simplified)_China.936'
       CONNECTION LIMIT = -1;
---建表
CREATE TABLE public."user"
(
  id integer NOT NULL DEFAULT nextval('user_id_seq'::regclass),---自增id
  user_name character varying(255),
  CONSTRAINT user_pkey PRIMARY KEY (id)
) WITH ( OIDS=FALSE );

ALTER TABLE public."user" OWNER TO postgres;

---插入数据
INSERT INTO public."user"(id, user_name) VALUES (?, ?);
