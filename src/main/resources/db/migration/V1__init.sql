CREATE TABLE bookstore.genre (
    created_on timestamptz(6) NULL,
    updated_on timestamptz(6) NULL,
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    "name" varchar(255) NOT NULL,
    CONSTRAINT uq_genre_id PRIMARY KEY (id)
);

CREATE TABLE bookstore.author (
    created_on timestamptz(6) NULL,
    updated_on timestamptz(6) NULL,
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    "name" varchar(255) NOT NULL,
    CONSTRAINT uq_author_id PRIMARY KEY (id)
);

CREATE TABLE bookstore.book (
    price numeric(38, 2) NOT NULL,
    created_on timestamptz(6) NULL,
    updated_on timestamptz(6) NULL,
    genre_id uuid DEFAULT gen_random_uuid() NULL,
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    title varchar(255) NOT NULL,
    CONSTRAINT uq_book_id PRIMARY KEY (id)
);

CREATE TABLE bookstore.book_authors (
    author_id uuid DEFAULT gen_random_uuid() NOT NULL,
    book_id uuid DEFAULT gen_random_uuid() NOT NULL
);

ALTER TABLE bookstore.book ADD CONSTRAINT uq_book_genre_id FOREIGN KEY (genre_id) REFERENCES bookstore.genre(id);
ALTER TABLE bookstore.book_authors ADD CONSTRAINT uq_book_authors_author_id FOREIGN KEY (author_id) REFERENCES bookstore.author(id);
ALTER TABLE bookstore.book_authors ADD CONSTRAINT uq_book_authors_book_id FOREIGN KEY (book_id) REFERENCES bookstore.book(id);