# CryptoWalletManager

To run the application, you must have Docker Desktop installed. Then, execute the following command to create the database:
docker run --name postgres-db -e POSTGRES_DB=cryptowallet -e POSTGRES_USER=test -e POSTGRES_PASSWORD=test -p 5432:5432 -d postgres:15
