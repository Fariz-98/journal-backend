# Journal Backend
Spring Boot REST API backend for a journal-writing social media. This application is built to learn the Spring framework
and showcase what I have learnt.

# Docker
Can be run using docker:
```
Docker compose -f docker-compose.yaml up
```
[wait-for-it.sh](https://github.com/vishnubob/wait-for-it) is used to wait for MySQL to start before allowing spring to
boot itself up. If this fails or not used, then ```restart: on-failure``` is set on docker compose  

# Without Docker
Modify application.properties datasource to:
```
spring.datasource.url=jdbc:mysql://localhost:3306(or your custom port)/journal_db?serverTimezone=UTC
```

Create a database in MySQL:
```
CREATE DATABASE journal_db;
```

# Endpoints  

## REGISTRATION
Default: ```/api/registration```


### Register:  
```POST``` ```/register```  

Request Body:
```
{
    "username":
    "password":
    "email":
    "firstName":
    "lastName":
}
```  

Response Status: 201  
Response Body:  
```
{token}
```


### Verify Token:
```PATCH``` ```/verifyToken?token={token}```  

Response Status: 204


### Resend Token:
```GET``` ```/resendToken?email={email}```  

Response Status: 200  
Response Body:
```
{token}
```



## LOGIN
Default: ```/api/login```


### Login
```POST```  

Request Body:  
```
{
    "username":
    "password":
}
```  

Response Status: 204  
Response Headers:  
```access_token``` ```{jwt}```  
```refresh_token``` ```{jwt}```


### Refresh JWT Token
```GET``` ```/refresh```  

Request Headers:  
```Authorization``` ```Bearer {jwt-refresh-token}```  

Response: 204  
Response Headers:   
```access_token``` ```{jwt}```  
```refresh_token``` ```{jwt}```



## JOURNAL
Default: ```/api/journal```


### Save Journal
```POST```  

Request Headers:  
```Authorization``` ```{jwt}```  
Request Body:
```
{
    "title":
    "content":
}
```  

Response Status: 201  
Response Headers:  
```Location``` ```/api/journal/{createdId}```


### Delete Journal
```DELETE```  ```/{id}```

Request Headers:  
```Authorization``` ```{jwt}```  

Response Status: 204


### View All Journal
```GET```  

Response Status: 200  
Response Body:
```
{
    {journal-list}
}
```


### View one journal
This will return one journal with its comments
```GET``` ```/{id}```  

Response status: 200  
Response Body:
```
{journalWithComments}
```



## COMMENT
Default: ```/api/journal/{journalId}/comment```

### Add Comment
```POST```  

Request Headers:  
```Authorization``` ```{jwt}```  
Request Body:
```
{
    "content":
}
```  

Response Status: 201


### Delete Comment
```DELETE``` ```/{id}```  

Request Headers:  
```Authorization``` ```{jwt}```  

Response Status: 204


### Get/View One Comment
```GET``` ```{id}```  

Response Status: 200  
Response Body:
```
{
    {journal},
    {comment}
}
```



## USER
Default: ```/api/user```


### View User Profile
This will return a user profile with user's journal  
```GET``` ```/{id}```  

Request Headers:  
```Authorization``` ```{jwt}```  

Response Status: 200  
Response Body:
```
{
    {user-information},
    {user-journal-list}
}
```


### Change User Role
For Admin Only   
Roles Available:
```
ADMIN (All permissions)
MODERATOR (Journal & Comments write permissions)
USER (No permissions)
```
```PATCH``` ```/changeRole/{user-id-of-user-to-change}?role={role-name}```  

Request headers:  
```Authorization``` ```{jwt}```  

Response Status: 204



































