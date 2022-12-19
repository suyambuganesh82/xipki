DROP TABLE IF EXISTS DBSCHEMA;
DROP TABLE IF EXISTS KEYSPEC;
DROP TABLE IF EXISTS KEYPOOL;

-- changeset xipki:1
CREATE TABLE DBSCHEMA (
    NAME VARCHAR(45) NOT NULL,
    VALUE2 VARCHAR(100) NOT NULL,
    CONSTRAINT PK_DBSCHEMA PRIMARY KEY (NAME)
)
COMMENT='database schema information';

INSERT INTO DBSCHEMA (NAME, VALUE2) VALUES ('VENDOR', 'XIPKI');
INSERT INTO DBSCHEMA (NAME, VALUE2) VALUES ('VERSION', '7');

CREATE TABLE KEYSPEC (
    ID SMALLINT NOT NULL,
    KEYSPEC VARCHAR(100) NOT NULL,
    CONSTRAINT PK_KEYSPEC PRIMARY KEY (ID)
);

CREATE TABLE KEYPOOL (
    ID BIGINT NOT NULL,
    SHARD_ID SMALLINT NOT NULL COMMENT 'Shard id, match the shard id of the CA software instance',
    KID SMALLINT NOT NULL COMMENT 'KEYSPEC ID',
    ENC_ALG SMALLINT NOT NULL COMMENT 'Encryption algorithm: 1 for AES128/GCM, 2 for AES192/GCM, 3 for AES256/GCM',
    ENC_META VARCHAR(100) NULL COMMENT 'For ENC_ALG 1, 2, 3: base64(nonce)',
    DATA VARCHAR(3300) NOT NULL COMMENT 'base64(encrypted PrivateKeyInfo)',
    CONSTRAINT PK_KEYPOOL PRIMARY KEY (ID)
);

