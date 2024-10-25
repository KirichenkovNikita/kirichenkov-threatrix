# Kirichenkov-Threatrix Service

This service provides a GraphQL API for managing users with ScyllaDB as the database backend. To get started, follow the setup instructions below.

## Setup Instructions

1. **Run Docker Compose**:  
   In the `/docker` directory, there is a `docker-compose.yml` file that defines the ScyllaDB container. Make sure you are in the `/docker` directory and run:

   ```bash
   docker-compose up -d

This will start ScyllaDB on port `9042` as specified in the compose file.

## Create Keyspace in ScyllaDB
After the container is up, access the ScyllaDB instance and create the necessary keyspace with the desired replication settings. Connect to the ScyllaDB instance (for example, using `cqlsh`) and create the keyspace as shown below:

```sql
CREATE KEYSPACE my_keyspace WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'};
```
Replace `my_keyspace` with your actual keyspace name.

## GraphQL API Usage
The GraphQL API is accessible via the `/graphql` endpoint. Below are the available mutations and queries with example calls.

### 1. Create User (`createUser`)
Creates a new user with specified details.

**Mutation Example:**

```graphql
mutation {
  createUser(user: {
    email: "johndoe@example.com",
    firstName: "John",
    lastName: "Doe",
    password: "password123",
    organization: "MyOrg",
    permissions: "ADMIN"
  }) {
    email
    firstName
    lastName
  }
}
```
### 2. Find All Users (`getAllUsers`)
Find a list of all users.

**Query Example:**

```graphql
{
   getAllUsers(startAfterEmail: "johndoe@example.com", limit: 2) {
      email
      firstName
      lastName
      organization
      permissions
   }
}
```

### 3. Find User by Email (`getUserByEmail`)
Find a single user by his email.

**Query Example:**

```graphql
{
  getUserByEmail(email: "johndoe@example.com") {
    email
    firstName
    lastName
    organization
    permissions
  }
}
```

### 4. Create or Update User (`createOrUpdateUser`)
Creates a new user if the email does not exist or updates an existing user's information.

**Mutation Example:**

```graphql
mutation {
  createOrUpdateUser(user: {
    email: "johndoe@example.com",
    firstName: "John",
    lastName: "Doe",
    password: "newpassword123",
    organization: "MyOrg",
    permissions: "USER"
  }) {
    email
    firstName
    lastName
  }
}
```

### 5. Delete User (`deleteUser`)
Deletes a user by email.

**Mutation Example:**

```graphql
mutation {
  deleteUser(email: "johndoe@example.com")
}
```

### 6. Retrieve Users by Organization (`getUsersByOrganization`)
Finds and retrieves all users associated with a specified organization.

**Query Example:**

```graphql
{
   getUsersByOrganization(organization: "MyOrg", startAfterEmail: "johndoe@example.com", limit: 2) {
      email
      firstName
      lastName
      organization
      permissions
   }
}
```



