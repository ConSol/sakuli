# Configuratio of the Database Receiver
  
  * Enable the database as an receiver for the results of a Sakuli test run in the `sakuli.properties`:
    
    ```
    # DEFAULT: false
    sakuli.receiver.database.enabled
    ```

  * Set also up your database connection information like:

	```
	jdbc.port=3307
	jdbc.database=sahi
	jdbc.host=[IPofOMD]
	jdbc.user=sahi
	jdbc.pw=sahi
	```