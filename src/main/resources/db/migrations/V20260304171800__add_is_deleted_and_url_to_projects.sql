-- Add missing fields to projects table that were introduced in the entity
alter table projects
  add column if not exists is_deleted boolean not null default false;

-- If current DB uses link from legacy schema, keep compatibility by ensuring URL column exists.
-- If you want persistent migration from link -> url, remove this part and adjust code to name = "link".
alter table projects
  add column if not exists url varchar(255);

-- Optional: backfill url from existing data if the app previously used link.
update projects set url = link where url is null and link is not null;
