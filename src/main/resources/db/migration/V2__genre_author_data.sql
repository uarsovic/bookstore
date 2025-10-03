insert into genre (id, name, created_on)
values (gen_random_uuid(), 'Science Fiction', now()),
       (gen_random_uuid(), 'Fantasy', now()),
       (gen_random_uuid(), 'Mystery', now()),
       (gen_random_uuid(), 'Romance', now()),
       (gen_random_uuid(), 'Horror', now());

insert into author (id, name, created_on)
values (gen_random_uuid(), 'Isaac Asimov', now()),
       (gen_random_uuid(), 'J.K. Rowling', now()),
       (gen_random_uuid(), 'Agatha Christie', now()),
       (gen_random_uuid(), 'Jane Austen', now()),
       (gen_random_uuid(), 'Stephen King', now());