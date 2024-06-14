CREATE DATABASE IF NOT EXISTS VendingMachine;

CREATE USER 'admin'@'%' IDENTIFIED BY '4313sch';
GRANT ALL PRIVILEGES ON *.* TO 'admin'@'%';
FLUSH PRIVILEGES;

USE VendingMachine;

CREATE TABLE `ConstantMoneyTbl` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `price` int(11) DEFAULT NULL,
    `qty` int(11) DEFAULT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `ConstantVarTbl` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `price` int(10) NOT NULL DEFAULT 0,
    `qty` int(10) NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `name` (`name`)
);

CREATE TABLE `CredentialTbl` (
    `idx` int(11) NOT NULL AUTO_INCREMENT,
    `id` varchar(100) NOT NULL,
    `password` varchar(100) NOT NULL,
    PRIMARY KEY (`idx`),
    UNIQUE KEY `id` (`id`)
);

CREATE TABLE `MachineCertainMoneyTbl` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `vendingid` varchar(5) DEFAULT '',
    `price` int(11) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `vendingid` (`vendingid`)
);

CREATE TABLE `MachineHistoryTbl` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `vendingid` varchar(5) DEFAULT NULL COMMENT 'vending ID',
    `time` timestamp NULL DEFAULT NULL COMMENT 'executeTime',
    `opType` varchar(1) DEFAULT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `MachineItemTbl` (
    `id` varchar(5) NOT NULL,
    `productId` int(11) NOT NULL,
    `name` varchar(255) NOT NULL DEFAULT '',
    `price` int(11) NOT NULL DEFAULT 0,
    `qty` int(11) NOT NULL DEFAULT 0,
    `qty_limit` int(11) NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`, `productId`),
    UNIQUE KEY `id` (`id`, `name`)
);

CREATE TABLE `MachineTbl` (
    `id` varchar(5) NOT NULL COMMENT 'Primary Key',
    `name` varchar(255) NOT NULL DEFAULT '' COMMENT '자판기 이름',
    PRIMARY KEY (`id`)
);

CREATE TABLE `MachineMoneyTbl` (
    `id` varchar(5) NOT NULL COMMENT 'Primary Key',
    `priceid` int(11) NOT NULL,
    `price` int(11) NOT NULL COMMENT 'price',
    `qty` int(11) NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`, `priceid`),
    CONSTRAINT `MachineMoneyTbl_ibfk_1` FOREIGN KEY (`id`) REFERENCES `MachineTbl` (`id`) ON DELETE CASCADE
);

-- Procedure

DELIMITER //
CREATE PROCEDURE `DELIMIT_VALUES`(
    IN tableName VARCHAR(255),
    IN valData TEXT
)
BEGIN
    START TRANSACTION;

    set @tableName = 'MachineItemTbl';
    set @valData = '0000|2|1|2|3|4=0000|1|커피|2|3|4';


    -- 열 개수 확인
    set @maxNumber = (SELECT count(*) FROM information_schema.COLUMNS WHERE TABLE_NAME = 'MachineItemTbl');

    set @lineLoop = 1;
    set @lineCount = 0;
    call `FUNCTION_SPLIT_DELIMITER`(valData,'=',@lineCount);

    WHILE @lineLoop <= @lineCount
    DO
        set @lineData = '';
        set @valLoop = 1;
        set @valCount = 0;

        call `FUNCTION_SPLIT_GET_INDEX`(valData,@lineLoop,'=',@lineData);
        call `FUNCTION_SPLIT_DELIMITER`(@lineData,'|',@valCount);

        IF @maxNumber != @valCount
        THEN
            select '데이터의 길이가 일치하지 않습니다.',@lineLoop;
            ROLLBACK;
        END IF;

        set @values = '';
        set @value = '';
        WHILE @valLoop <= @valCount
        DO
            call `FUNCTION_SPLIT_GET_INDEX`(@lineData,@valLoop,'|',@value);
            set @values = CONCAT(@values,"'",@value,"'");
            IF @valLoop != @valCount
            THEN
                set @values = CONCAT(@values,',');
            END IF;
            set @valLoop = @valLoop + 1;
        END WHILE;
        set @lineLoop = @lineLoop + 1;
        set @replace_query = CONCAT('REPLACE INTO ',tableName,' VALUES (',@values,');');
        PREPARE replace_query from @replace_query;
        EXECUTE replace_query;    
    END WHILE;

    COMMIT;

END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE `FUNCTION_DELIMIT2_GET_INDEX`(
    IN valData Text,
    IN delimit1 varchar(3),
    IN delimit2 varchar(3),
    IN idx1 INT,
    IN idx2 INT,
    OUT outputValue TEXT
)
BEGIN
    SET @value1 = '';
    SET @value2 = '';
    CALL `FUNCTION_SPLIT_GET_INDEX`(valData,  idx1,delimit1, @value1);
    CALL `FUNCTION_SPLIT_GET_INDEX`(@value1,  idx2,delimit2, @value2);
    set outputValue = @value2;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE `FUNCTION_SPLIT_DELIMITER`(
    IN valData TEXT,
    IN delimit VARCHAR(5),
    OUT count int
)
BEGIN
    set @length = 0;
    set @pos = 1;
    set @find = 1;
    set count = 0;

    WHILE @find != 0
    DO
        set @find = locate(delimit,valData,@pos);
        IF (@find != 0)
        THEN
        set @length = @length + 1;
        set @pos = @find + 1;
        END IF;
    END WHILE;
    set @length = @length + 1;
    set count = @length;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE `FUNCTION_SPLIT_GET_INDEX`(
    IN valData TEXT,
    IN idx INT,
    IN delimit varchar(3),
    out outputValue TEXT
)
BEGIN
    set outputValue = SUBSTRING_INDEX(SUBSTRING_INDEX(valData, delimit, idx), delimit, -1); 
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE `MACHINE_HIST_ADD`(
  IN vendingid varchar(5),
  IN executeTime VARCHAR(100),
  IN opType VARCHAR(3),
  IN valData TEXT
)
BEGIN
    set @lineCount = 0;
    set @lineLoop = 1;
    set @valData = '0000|2|콜라|5|300|5=0000|1|fucking|2|3|4';
    set @vendingid = '';

    CALL `FUNCTION_DELIMIT2_GET_INDEX`(valData,'=','|',1,1,vendingid);
    INSERT INTO `MachineHistoryTbl`(vendingid,time,optype) VALUES (vendingid,executeTime,opType);
    set @lastNumber = (select id from `MachineHistoryTbl` ORDER BY id DESC LIMIT 1);
    
    CALL `FUNCTION_SPLIT_DELIMITER`(valData,'=',@lineCount);
    select @lineCount;
    WHILE @lineLoop <= @lineCount
    DO
        set @prdid = '';
        CALL `FUNCTION_DELIMIT2_GET_INDEX`(valData,'=','|',@lineLoop,2,@prdid);
        set @qty = '';
        CALL `FUNCTION_DELIMIT2_GET_INDEX`(valData,'=','|',@lineLoop,5,@qty);

        set @diffQty = (select qty - @qty from `MachineItemTbl` where `productId` = @prdid AND id = vendingid);


        INSERT INTO `MachineItemHistoryTbl`(id,vendingid,time,productid,opType,qty) VALUES (@lastNumber,vendingid,executeTime,@prdid,opType,ABS(@diffQty));
        IF(@qty = 0)
        THEN
          INSERT INTO `MachineItemHistoryTbl`(id,vendingid,time,productid,opType,qty) VALUES (@lastNumber,vendingid,executeTime,@prdid,3,0);
        END IF;

        set @lineLoop = @lineLoop + 1;
    END WHILE;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE `MACHINE_HIST_GET`(
  IN searchvendingid varchar(5),
  IN searchyear varchar(5),
  IN searchmonth varchar(2),
  IN searchdate varchar(2),
  IN limitcount int
)
BEGIN
  select vendingid,time,`opType`,A.productid,qty,name
  FROM
  (
    select A.vendingid,A.time,B.productid,B.qty,B.`opType` from `MachineHistoryTbl` A
    RIGHT OUTER JOIN `MachineItemHistoryTbl` B
    ON A.id = B.id
    WHERE A.vendingid = searchvendingid
  ) A,
  (
    select `productId`,name from `MachineItemTbl` 
    WHERE `id` = searchvendingid
  ) B
  WHERE A.`productId` = B.`productId` AND qty > 0
  HAVING MONTH(time) LIKE searchmonth AND YEAR(time) LIKE searchyear AND DAY(time) LIKE searchdate
  ORDER BY time DESC
  LIMIT limitcount;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE `MACHINE_INIT`(
    IN machineId VARCHAR(5),
    IN machineComment VARCHAR(255)
)
BEGIN
DECLARE changedName VARCHAR(255);
SET changedName = IFNULL(machineComment,'');
INSERT INTO `MachineTbl`(id, name) VALUES (machineId, changedName);

SET @ROWNUM=0;
insert into `MachineItemTbl`(id, productId,name, price, qty,qty_limit)
select machineId as id,@ROWNUM:=@ROWNUM+1 as productId, name,price,qty,qty as qty_limit
from `ConstantVarTbl`;

SET @ROWNUM=0;
insert into `MachineMoneyTbl`(id, priceid, price,qty)
select machineId as id,@ROWNUM:=@ROWNUM+1 as priceid,price,qty
from `ConstantMoneyTbl`;

insert into `MachineCertainMoneyTbl`(vendingid,price)
VALUES (machineId,0);

SELECT * FROM `MachineItemTbl`;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE `MACHINE_MONEY`(
    IN cmd varchar(5),
    IN inputvendingId varchar(5),
    IN time TIMESTAMP,
    IN priceId varchar(5),
    IN valData TEXT
)
BEGIN
START TRANSACTION;
set @cmd = upper(cmd);

IF (@cmd = 'GET')
THEN
    SELECT * FROM `MachineMoneyTbl` where id = inputvendingId HAVING `priceid` LIKE priceid;
END IF;

IF (@cmd = 'SPE')
THEN
    SELECT * FROM `MachineCertainMoneyTbl` WHERE vendingid = inputvendingId;
END IF;

IF (@cmd = 'SET')
THEN
    CALL `DELIMIT_VALUES`('MachineCertainMoneyTbl',valData);
END IF;

IF (@cmd = 'ADD' OR @cmd = "SUB")
THEN
    CALL `DELIMIT_VALUES`('MachineMoneyTbl',valData);
END IF;
COMMIT;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE `MACHINE_MONEY_INSERT`(
  IN vendingId varchar(5),
  IN valdata TEXT
)
BEGIN
  -- 임시 테이블 생성
  IF (EXISTS( SELECT * FROM information_schema.TABLES WHERE TABLE_NAME = 'temp_dataTbl')) THEN 
    DROP TEMPORARY TABLE temp_dataTbl;
  END IF;

  CREATE TEMPORARY TABLE temp_dataTbl (idx INT AUTO_INCREMENT PRIMARY KEY, val Int);


  -- 매개변수 값 예제
  set @vendingId = '0000';
  set @valdata = '1|1|||';

  -- 필수 값
  set @valDataLength = LENGTH(valdata);
  set @length = 0;
  set @pos = 1;
  set @find = 1;

  WHILE @find != 0
  DO
    set @find = locate('|',valdata,@pos);
    IF (@find != 0)
    THEN
      set @length = @length + 1;
      set @pos = @find + 1;
    END IF;
  END WHILE;
  set @length = @length + 1;

  set @pos = 1;
  WHILE @pos <= @length
  DO
    set @value= SUBSTRING_INDEX(SUBSTRING_INDEX(valdata, '|', @pos), '|', -1);
    INSERT INTO temp_dataTbl(val) VALUES(IF (@value != '',@value,0));
    set @pos = @pos + 1;
  END WHILE;

  UPDATE 
    `MachineMoneyTbl` a,
    `temp_dataTbl` b
  SET a.qty = a.qty + b.val
  where a.priceid = b.idx AND id = vendingId;

  update 
    `MachineCertainMoneyTbl` a,
    (
      select SUM(a.price * b.val) as sum from `MachineMoneyTbl` a 
      INNER JOIN temp_dataTbl b
      ON a.priceid = b.idx
      where id = vendingId
    ) b
  set a.price = a.price + b.sum
  where a.vendingid = vendingId;

  DROP TEMPORARY TABLE temp_dataTbl;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE `MACHINE_PRODUCT`(
    IN cmd varchar(5),
    IN inputvendingId varchar(5),
    IN time TIMESTAMP,
    IN prodcutId varchar(5),
    IN valData TEXT
)
BEGIN
START TRANSACTION;
set @cmd = upper(cmd);

IF (@cmd = 'GET')
THEN
    SELECT * FROM `MachineItemTbl` where id = inputvendingId HAVING `productId` LIKE prodcutId;
END IF;

IF (@cmd = 'ADD' OR @cmd = "SUB")
THEN
    IF (@cmd = 'ADD')
    THEN
        CALL `MACHINE_HIST_ADD`(inputvendingId, time, 1, valData);
    ELSEIF (@cmd = 'SUB')
    THEN
        CALL `MACHINE_HIST_ADD`(inputvendingId, time, 2, valData);
    END IF;
    CALL `DELIMIT_VALUES`('MachineItemTbl',valData);
END IF;
COMMIT;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE `MACHINE_REMOVE`(
    IN machineID varchar(5)
)
BEGIN
delete from `MachineItemTbl` where id = machineID;
delete from `MachineTbl` WHERE id = machineID;
delete from `MachineCertainMoneyTbl` WHERE vendingid = machineID;
delete from `MachineMoneyTbl` where id = machineID;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE `MACHINE_REMOVE_ALL`()
BEGIN
DELETE FROM `MachineMoneyTbl` WHERE 1 = 1;
DELETE FROM `MachineItemTbl` WHERE 1 = 1;
DELETE FROm `MachineTbl` WHERE 1 = 1;
DELETE FROm `MachineCertainMoneyTbl` WHERE 1 = 1;
SELECT * FROM `MachineTbl`;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE `USER_LOGININFO_GET`(
    IN userid VARCHAR(100)
)
BEGIN
SELECT * from `CredentialTbl` where id = userid;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE `USER_PASSWORD_CHANGE`(
    IN `username` VARCHAR(100),
    IN `passwordchange` VARCHAR(255)
)
BEGIN
UPDATE `CredentialTbl` set password = passwordchange WHERE id = username;
END //
DELIMITER ;

-- permissions

CREATE USER 'vending'@'%' identified by 'vending1234';
FLUSH PRIVILEGES;

GRANT EXECUTE ON PROCEDURE MACHINE_HIST_ADD TO 'vending'@'%';
GRANT EXECUTE ON PROCEDURE MACHINE_HIST_GET TO 'vending'@'%';
GRANT EXECUTE ON PROCEDURE MACHINE_INIT TO 'vending'@'%';
GRANT EXECUTE ON PROCEDURE MACHINE_MONEY TO 'vending'@'%';
GRANT EXECUTE ON PROCEDURE MACHINE_MONEY_INSERT TO 'vending'@'%';
GRANT EXECUTE ON PROCEDURE MACHINE_PRODUCT TO 'vending'@'%';
GRANT EXECUTE ON PROCEDURE MACHINE_REMOVE TO 'vending'@'%';
GRANT EXECUTE ON PROCEDURE MACHINE_REMOVE_ALL TO 'vending'@'%';
GRANT EXECUTE ON PROCEDURE USER_LOGININFO_GET TO 'vending'@'%';
GRANT EXECUTE ON PROCEDURE USER_PASSWORD_CHANGE TO 'vending'@'%';

-- item data
INSERT INTO `ConstantVarTbl`(`name`,`price`,`qty`) VALUES('물',450,6);
INSERT INTO `ConstantVarTbl`(`name`,`price`,`qty`) VALUES('커피',500,6);
INSERT INTO `ConstantVarTbl`(`name`,`price`,`qty`) VALUES('이온 음료',550,6);
INSERT INTO `ConstantVarTbl`(`name`,`price`,`qty`) VALUES('고급 커피',700,6);
INSERT INTO `ConstantVarTbl`(`name`,`price`,`qty`) VALUES('탄산 음료',750,6);
INSERT INTO `ConstantVarTbl`(`name`,`price`,`qty`) VALUES('특화 음료',800,6);

-- money data
INSERT INTO `ConstantMoneyTbl`(`price`,`qty`) VALUES(10,10);
INSERT INTO `ConstantMoneyTbl`(`price`,`qty`) VALUES(50,10);
INSERT INTO `ConstantMoneyTbl`(`price`,`qty`) VALUES(100,10);
INSERT INTO `ConstantMoneyTbl`(`price`,`qty`) VALUES(500,10);
INSERT INTO `ConstantMoneyTbl`(`price`,`qty`) VALUES(1000,10);