
-- ============================================================================================
-- Последовательность для установки айди пользователей
CREATE SEQUENCE seq_users_id INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1;
COMMENT ON SEQUENCE seq_users_id IS 'Последовательность для установки айди пользователей';

-- ============================================================================================
-- Таблица пользователей
CREATE TABLE "users" (
-- fields
    "id"           INTEGER      NOT NULL DEFAULT nextval('seq_users_id'::regclass),
    "groupId"      SMALLINT     NOT NULL,
    "login"        VARCHAR(128) NOT NULL,
    "email"        VARCHAR(128)          DEFAULT NULL,
    "passwordHash" CHAR(52)     NOT NULL,
    "dateRegister" TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "lastActivity" TIMESTAMP             DEFAULT NULL,
    "lastLogin"    TIMESTAMP             DEFAULT NULL,
-- constraints
    CONSTRAINT "usersPk" PRIMARY KEY ("id"),
    CONSTRAINT "usersFkGroupId" FOREIGN KEY ("groupId")
        REFERENCES "userGroups" ("id") MATCH SIMPLE
        ON UPDATE RESTRICT ON DELETE RESTRICT
)
WITH (
    OIDS=FALSE
);

-- --------------------------------------------------------------------------------------------
-- Комментарии к таблице пользователей

COMMENT ON TABLE "users" IS 'Таблица пользователей';

COMMENT ON COLUMN "users"."id" IS 'Айди пользователя';
COMMENT ON COLUMN "users"."groupId" IS 'Айди группы, в которой находится пользователь';
COMMENT ON COLUMN "users"."login" IS 'Логин пользователя';
COMMENT ON COLUMN "users"."passwordHash" IS 'Хеш пароля пользователя вместе с солью';
COMMENT ON COLUMN "users"."dateRegister" IS 'Дата регистрации пользователя';
COMMENT ON COLUMN "users"."lastActivity" IS 'Дата последней активности пользователя';
COMMENT ON COLUMN "users"."lastLogin" IS 'Дата последней авторизации пользователя';

COMMENT ON CONSTRAINT "usersPk" ON "users" IS 'Первичный ключ таблицы пользователей';
COMMENT ON CONSTRAINT "usersFkGroupId" ON "users" IS 'Внешний ключ, ссылающийся на группу пользователя';

-- ===========================================================================
-- Индексы для таблицы пользователей

-- ---------------------------------------------------------------------------
-- Покрывающий индекс по айди групп пользователей
CREATE INDEX "usersIdxGroupId" ON "users" ("groupId");
COMMENT ON INDEX "usersIdxGroupId" IS 'Покрывающий индекс по айди групп пользователей';

-- ---------------------------------------------------------------------------
-- Логин пользователя должен быть уникальным
CREATE UNIQUE INDEX "usersIdxUnqLogin" ON "users" (LOWER("login"));
COMMENT ON INDEX "usersIdxUnqLogin" IS 'Логин пользователя должен быть уникальным';

-- ---------------------------------------------------------------------------
-- Email пользователя должен быть уникальным
CREATE UNIQUE INDEX "usersIdxUnqEmail" ON "users" (LOWER("email"));
COMMENT ON INDEX "usersIdxUnqEmail" IS 'Email пользователя должен быть уникальным';

-- ===========================================================================
-- Триггерные функции и триггеры таблицы пользователей

-- ---------------------------------------------------------------------------
-- Триггерная функция, обновляющая счётчики в таблице групп пользователей при изменении инорфмации в таблице пользователей
CREATE OR REPLACE FUNCTION "tf_updateUserGroupsCounters" () RETURNS TRIGGER AS $BODY$
BEGIN
    IF (TG_OP = 'DELETE') THEN
        UPDATE "userGroups" SET "usersCount" = "usersCount" - 1 WHERE "id" = OLD."groupId";
        RETURN OLD;
    ELSIF (TG_OP = 'UPDATE' AND NEW."groupId" <> OLD."groupId") THEN
        UPDATE "userGroups" SET "usersCount" = "usersCount" - 1 WHERE "id" = OLD."groupId";
        UPDATE "userGroups" SET "usersCount" = "usersCount" + 1 WHERE "id" = NEW."groupId";
    ELSIF (TG_OP = 'INSERT') THEN
        UPDATE "userGroups" SET "usersCount" = "usersCount" + 1 WHERE "id" = NEW."groupId";
    END IF;
    RETURN NEW;
END;
$BODY$
LANGUAGE plpgsql VOLATILE COST 100;
COMMENT ON FUNCTION "tf_updateUserGroupsCounters"()
    IS 'Триггерная функция, обновляющая счётчики в таблице групп пользователей при изменении инорфмации в таблице пользователей';

-- ---------------------------------------------------------------------------
-- Триггер для обновления счетчика пользователей у групп пользователей
CREATE TRIGGER "t_users_updateUserGroupsCounters" AFTER INSERT OR UPDATE OR DELETE ON "users"
    FOR EACH ROW EXECUTE PROCEDURE "tf_updateUserGroupsCounters"();
COMMENT ON TRIGGER "t_users_updateUserGroupsCounters" ON "users"
    IS 'Триггер для обновления счетчика пользователей у групп пользователей';

-- ============================================================================================
-- Данные, необходимые для вставки в таблицу пользователей
INSERT INTO "users" ("id", "groupId", "login", "passwordHash") VALUES
    (-1, -1, 'system', ''),
    ( 0,  1, 'guest', '');