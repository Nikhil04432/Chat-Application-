**Chat Application with Authentication and Persistent Storage**


This is a simple client-server chat in Java. This supports multi-client, the authentication goes through a MySQL database and stores messages persistently on the database.
The server handles authentication and message broadcasting and saving to the database, while the client just allows users to send and receive messages.

**Features**

-Multi-client: Multiple clients can connect at the same time to the server and pass messages.

-Authentication: Users need to log in using valid credentials stored in a MySQL database.

-Persistent Storage: The messages are stored in a MySQL database for later retrieval.

-Broadcast Messaging: Messages are broadcast to all the connected clients.
