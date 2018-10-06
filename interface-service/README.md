## interface-service

### What is interface-service
UI for event-sourced banking application.  

### How to use interface-service
1. Install requirements: `npm install`  
2. Start application: `npm start`  

### How to test interface-service
We use a combination of mock-server and selenium to test our services  
1. Install the mock server module as per instructions in the directory  
2. In the mock-server directory, run the mock-server in the background: `nodejs server.js &`
3. Add client endpoints: `nodejs client.js`
4. Back in the interface-service directory run the service in test mode: `npm start test`  
  * We find it useful to output this into a new file `npm start test > output.txt &`
5. Finally you can test with python pytest and selenium: `pytest /tests`

### Required endpoints:
**account-service: **`http://127.0.0.1:5000`  
**transaction-service: **`http://127.0.0.1:3030`  
**kafka-interface: **`http://127.0.0.1:8080`  
