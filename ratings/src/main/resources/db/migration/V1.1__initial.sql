create table ratings (
  type       varchar(255)            not null,
  entity_id  varchar(255)            not null,
  rating     double                  not null,
  created_on timestamp default now() not null
);
create index ratings_idx
  on ratings (type, entity_id);