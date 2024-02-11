# ExtendingPortKnockingRM3_eTrunk
Extending Port Knocking Authorization with Deception Mechanisms

#How to build the project

Download all the folder and open the main webspa folder where you see the pom.xml file, there enter below command

-> mvn clean install -DskipTests

#How to start the client

Go to target folder and enter below command

-> java -jar webspa.jar -client

#How to start the server

Go to target folder and enter below command

-> java -jar webspa.jar -server
-> service start
  -> user add
  -> user activate
  -> action add
  
 New Security Features:
 
 1. Sending 5 extra knocks with every client request in non chronological order every time.
 2. Added Private/Public key encrption to pass-phrases/keys taht store in Database.
 3. New Database TABLE (DECOY_USER) was introduced to capture the decoy knock requests.
 4. Capturing and detection of the decoy users in system to identify Man-in-Middle Attack, if any.
