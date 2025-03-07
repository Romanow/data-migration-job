-- 30 Insert data into source tables
SELECT *
INTO names
FROM (VALUES ('Alex'),
             ('Mike'),
             ('Jack'),
             ('Harry'),
             ('Jacob'),
             ('Charlie'),
             ('Thomas'),
             ('George'),
             ('Oscar'),
             ('James'),
             ('William')) AS names(name);

SELECT *
INTO countries
FROM (VALUES ('Russia'),
             ('USA'),
             ('Great Britain'),
             ('Mexico'),
             ('Canada'),
             ('Germany'),
             ('France'),
             ('Brazil'),
             ('China'),
             ('Japan'),
             ('Denmark')) AS countries(name);

INSERT INTO users (uid, name, age, location)
SELECT gen_random_uuid(),
       (SELECT name FROM names ORDER BY RANDOM() LIMIT 1),
       FLOOR(81 * RANDOM() + 18),
       (SELECT name FROM countries ORDER BY RANDOM() LIMIT 1)
FROM GENERATE_SERIES(1, 20000) AS s(id);
