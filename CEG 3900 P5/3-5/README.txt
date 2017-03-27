Notes on running the app:

The server java program was run on a cloud host. It uses port 8080. On the android apk, the server hostname is hardcoded into com.example.frodo.QueryPassSecurityService.java and would have to be changed to run the app if the server is on a different host. It is also possible to change the client/server port numbers by changing the appropriate constants in PasswordSecurityServer.java and com.example.frodo.QueryPassSecurityService.java.

Documentation of the code is contained in the source code files (PasswordSecurityServer.java and inside PasswordSecure/app/src/main/java/com/example/frodo)
