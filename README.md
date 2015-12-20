This is a Multi-Client Server Chat Application.
Steps to run the project
1. Import the project in eclipse. Importing instructions - http://agile.csc.ncsu.edu/SEMaterials/tutorials/import_export/index.html#section1_0
2. Run the Server.java file first.
3. Run the Client.java file.
4. Make sure you enter the appropriate Server Address (e.g 127.0.0.1) and Unique username
5. The port number is kept constant (4556) to avoid any confusion.

The Server GUI makes use of Java swing package. 
The Client GUI makes use of Javafx package.

I am using two different GUI packages intentionally, just a variety.
For each incoming connection that the server receives, the server generates a new thread for each client. Each thread has its own functionality of Server.In this way a server handles multiple clients.

The Application also handles most of the validation.
Some of which are:
port validation
IP address validation
Unique username validation
and Button click validations

