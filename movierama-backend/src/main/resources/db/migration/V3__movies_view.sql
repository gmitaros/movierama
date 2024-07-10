CREATE VIEW movies_view AS
SELECT m.id,
       m.title,
       m.description,
       m.publication_date,
       m.user_id,
       m.created_date,
       COUNT(CASE WHEN v.type = 'LIKE' THEN 1 END) AS likes_count,
       COUNT(CASE WHEN v.type = 'HATE' THEN 1 END) AS hates_count
FROM movies m
         LEFT JOIN
     votes v ON m.id = v.movie_id
GROUP BY m.id, m.title, m.description, m.publication_date, m.user_id;
