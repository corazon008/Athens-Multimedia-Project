# Streaming Application

## Description
This Project contain three packages :
- **client**: This package contains the client code that sends data to the server.
- **server**: This package contains the server code that receives data from the client and processes it.
- **shared**: This package contains the common code that is shared between the client and server.

## Requirements
- ffmpeg and ffplay

## Usage
### Server
Since making all the resolution and format of the video takes a lot of time, this function is by default commented. \
To enable it, uncomment the first line in `Server.java` and run the server:
```java
public static void main(String[] args) throws Exception {
    //FfmpegHandler.FfmpegMakeAllResAndFormat();
    ...
}
```
