You have been provided with the following files:

1. small_db.txt: Contains 1000 records for a small test database.

2. createdb.jar: A runnable jar file that connects to a running database server on localhost, creates a table, an index on the timestamp column, and inserts 1K records from small_db.txt into the table. Start your server and run this file using the command: "java -jar createdb.jar small_db.txt". Make sure you have small_db.txt in the same directory as this jar file.

3. checkdb.jar: A runnable jar file that generates 10 random intervals, runs between queries for each and verifies the results against the contents of small_db.txt. Start your server and run this file using the command: "java -jar checkdb.jar small_db.txt". Make sure you have small_db.txt in the same directory as this jar file.

4. checkdbe.jar: A runnable jar file that runs 3 erroneous (on various aspects) queries. Use this to check that your code reports proper exceptions on each. Start your server and run this file using the command: "java -jar checkdbe.jar"
