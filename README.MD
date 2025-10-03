# Bookstore API

### To build and run

execute `./gradlew -i clean build` - cleans and builds the project with executing all the tests

execute `docker compose -f docker/docker-compose.yml up -d` - starts docker compose with postgres db and keycloak (with user and realm
setup)

execute `./gradlew -i bootRun` - start the application creating the tables and seeding the data

### To call the API

There is a Postman collection in the postman folder.
The API is protected with Keycloak, so you need to get a token first. By either logging in with user or admin

## Todo

- [ ] Add more tests - there are only one unit test class and one integration test class to demonstrate setup
- [ ] Improve on logging - there is only a logging aspect that will log only on debug level and only service classes
- [ ] Add swagger documentation
- [ ] Improve on validations - there is only few basic validations on DTOs and service classes
- [ ] Search is generaly very slow if we use like %searchTerm% - we could use some full text search solution like elasticsearch or
  postgresql full text search
- [ ] Improve on exception handling - there is only a few exception handlers
- [ ] Improve on error messages and their meanings 