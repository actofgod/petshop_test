
-- ===========================================================================
-- Последовательность для установки айди групп пользователей
CREATE SEQUENCE seq_user_groups_id INCREMENT 1 MINVALUE 1 MAXVALUE 32767 START 1 CACHE 1;
COMMENT ON SEQUENCE seq_user_groups_id IS 'Последовательность для установки айди групп пользователей';

-- ===========================================================================
-- Таблица групп пользователей
CREATE TABLE "userGroups" (
-- columns
    "id"           SMALLINT    NOT NULL DEFAULT nextval('seq_user_groups_id'::regclass),
    "parentId"     SMALLINT    NOT NULL,
    "usersCount"   INTEGER     NOT NULL DEFAULT 0,
    "name"         VARCHAR(64) NOT NULL,
    "description"  TEXT                 DEFAULT NULL,
-- constraints
    CONSTRAINT "userGroupsPk" PRIMARY KEY ("id")
)
WITH (
    OIDS = FALSE
);

-- ---------------------------------------------------------------------------
-- Комментарии к таблице групп пользователей

COMMENT ON TABLE "userGroups" IS 'Таблица групп пользователей';

COMMENT ON COLUMN "userGroups"."id" IS 'Айди группы пользователей';
COMMENT ON COLUMN "userGroups"."parentId" IS 'Айди родительской группы пользователей';
COMMENT ON COLUMN "userGroups"."usersCount" IS 'Количество пользователей в группе, устанавливается автоматически триггером';
COMMENT ON COLUMN "userGroups"."name" IS 'Имя группы пользователей';
COMMENT ON COLUMN "userGroups"."description" IS 'Описание группы пользователей';

COMMENT ON CONSTRAINT "userGroupsPk" ON "userGroups" IS 'Первичный ключ таблицы групп пользователей';

-- ===========================================================================
-- Данные, необходимые для вставки в таблицу групп пользователей

INSERT INTO "userGroups" ("id", "parentId", "name", "description") VALUES
    (-1, 0, 'system', 'Группа системных пользователей'),
    ( 0, 0, 'root', 'Корневая группа пользователей, является родителем всех групп');

INSERT INTO "userGroups" ("parentId", "name", "description") VALUES
    (0, 'guests', 'Группа гостей, все незарегестрированные/неавторизированные пользователи находятся в ней'),
    (0, 'registered', 'Базовая группа зарегестрированных пользователей, предоставляет возможность авторизации');

INSERT INTO "userGroups" ("parentId", "name", "description") VALUES
    (2, 'administrators', 'Группа администраторов, предоставляет возможность авторизации в админке');

INSERT INTO "userGroups" ("parentId", "name", "description") VALUES
    (3, 'superAdmins', 'Группа суперадминистраторов, только для настроек сайта');

-- ===========================================================================
-- Дополнительные ограничения для таблиц пользователей и групп

-- ---------------------------------------------------------------------------
-- Внешний ключ, ссылающийся на родительскую группу
ALTER TABLE "userGroups" ADD CONSTRAINT "userGroupsFkParentId" FOREIGN KEY ("parentId")
    REFERENCES "userGroups" ("id") MATCH SIMPLE ON UPDATE RESTRICT ON DELETE RESTRICT;
COMMENT ON CONSTRAINT "userGroupsFkParentId" ON "userGroups" IS 'Внешний ключ, ссылающийся на родительскую группу';

-- ===========================================================================
-- Индексы для таблицы групп пользователей

-- ---------------------------------------------------------------------------
-- Покрывающий индекс для айди родительской группы
CREATE INDEX "userGroupsIdxParentId" ON "userGroups" ("parentId");
COMMENT ON INDEX "userGroupsIdxParentId" IS 'Покрывающий индекс для айди родительской группы';

-- ---------------------------------------------------------------------------
-- Имя группы внутри родительской группы должно быть уникальным
CREATE UNIQUE INDEX "userGroupsIdxUnqName" ON "userGroups" ("parentId", LOWER("name"));
COMMENT ON INDEX "userGroupsIdxUnqName" IS 'Имя группы внутри родительской группы должно быть уникальным';
