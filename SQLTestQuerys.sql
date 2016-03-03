// Given a part of a title of a book, print all book data for books that match this title.
SELECT b.*
FROM Book b
WHERE (b.title LIKE '%Potter%')

// Given a year, print all book data for books that are published before that year.
SELECT b.*
FROM Book b
WHERE (pubYear < '2015-01-01')

// Given a date, print all patron data for patrons that are younger than that date.
SELECT p.*
FROM Patron p
WHERE (dateOfBirth > '2000-01-01')

// Given a zip, print all patron data for patrons that live at that zip.
SELECT p.*
FROM Patron p
WHERE (zipcode = '14626')

// Given a bookId and dateOfTrans, show me all transaction data related to that bookId and date.
SELECT t.*
FROM Transaction t
WHERE ((bookId = '132') AND (dateOFTrans = '2015-10-12'))
