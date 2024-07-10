-- Adding an index on publication_date
CREATE INDEX idx_publication_date ON movies (publication_date);

-- Adding an index on user_id
CREATE INDEX idx_user_id ON movies (user_id);

-- Adding an index on created_date
CREATE INDEX idx_created_date ON movies (created_date);

-- Adding an index on title
CREATE INDEX idx_title ON movies (title);
