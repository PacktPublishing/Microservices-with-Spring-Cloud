

CREATE TABLE users (
  username VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  enabled  TINYINT      NOT NULL DEFAULT 1,
  PRIMARY KEY (username)
);

CREATE TABLE user_roles (
  user_role_id INT(11)      NOT NULL AUTO_INCREMENT,
  username     VARCHAR(255) NOT NULL,
  role         VARCHAR(255) NOT NULL,
  PRIMARY KEY (user_role_id),
  CONSTRAINT uni_username_role UNIQUE (role, username),
  CONSTRAINT fk_username FOREIGN KEY (username) REFERENCES users (username)
  ON DELETE CASCADE ON UPDATE CASCADE
);
