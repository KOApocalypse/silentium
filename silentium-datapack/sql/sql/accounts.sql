CREATE TABLE IF NOT EXISTS `accounts` (
  `login` VARCHAR(45) NOT NULL DEFAULT '',
  `password` VARCHAR(45),
  `lastactive` DECIMAL(20),
  `access_level` INT(3) NOT NULL DEFAULT 0,
  `lastServer` INT(4) DEFAULT 1,
  `newbie` INT UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`login`)
);
