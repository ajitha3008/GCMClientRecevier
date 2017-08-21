# GCMClientRecevier

This application is a Android client application that is designed to register with GCM cloud server and receive notification. 

The app registers itself with the GCM server using android API's, and sends the registration ID to the php webserver. The php web server stores the registration ID with corresponding email address. The home page of the webserver will list all the users registered with it. The server manager can decide to send a message to any registered device using the web page. After pressing "send" button, the php server will interact with the GCM server using curl scripting to send notification to the registered device. The device receives the notification and informs the client.

This app is compatible with https://github.com/ajitha3008/GCMServerSender

# Steps to run

1) Replace the server URL in Utils/Constants.java, "SERVER_URL"
2) Replace the google sender ID with the one you create for your project "GOOGLE_SENDER_ID"
3) Run the app
  (a) Enter credentials
  (b) Registration updates will be on screen

