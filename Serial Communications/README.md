# Serial Communication

This assignment is separated in 5 different parts. In each part we need to communicate with the server with a _key number_ that is available only for 2 hours and only with Uni personal info. The different parts are:

1. Echo Request Code

2. Image Request Code (error free)
3. Image Request Code (with errors)
4. GPS Request Code
5. ARQ Protocol - ACK/NACK Code

#### 1) Echo Request Code

---

Our application sends an **Echo Request Code** of type "EXXXX\r" (where XXXX are 4 numbers given by Uni)  and awaits from the server packets of type:

```
PSTART DD-MM-YYYY HH-MM-SS PC PSTOP
```

where DD-MM-YYYY is the date, HH-MM-SS is the time and PC is the packet counter.

#### 2) & 3) Image Request Code

---

Our application sends **Image Request Codes** of type "MXXXX\r" for error free image and "GXXXX\r" for image with errors. Along with the key numbers there are some parameters that can be set. The first parameter is **"CAM=FIX"** which will return an Image from the default position whereas a **"CAM=PTZ"** along with **"DIR=X"** where X = L(left), R(right), U(Up), D(down), will return an image from one of these 4 positions. The image from the server is a **.jpeg** encoded image of the front side of the Faculty of Electrical Engineering in Thessaloniki. 

#### 4) GPS Request Code

---

The application sends the **GPS Request Code** of type "PXXXX\r" two separate times for differrent types of data. The first time the request code is encoded like this: "PXXXXRTSSSSLL\r" where T is the number for 1 of the 9 predefined routes in the memory of the server, SSSS is the start point of the route and LL is the number of points that we want as a return. The data are decoded with respect to the **NMEA Protocol**. The **GPGGA** data returned from the GPS need to be decoded in order to find the coordinates in _degrees, minutes, seconds_. After the data are decoded we need to find the timestamp of each GPGGA message and choose 9 of them that are time-apart at least 4 seconds. After this is done, the coordinates of the 9 points that we chose can be used in order to receive an image with these points in it. To do that the PC needs to communicate again with the server but this time after the "PXXXX" sequence, we can add the sequence "TAABBCC" 9 times, where AA are the degrees, BB are the minutes and CC are the seconds. The results are something like this:

#### 5) ACK/NACK Code

---

The application sends a request code and the server responds with a message like this:

```
PSTART DD-MM-YYYY HH-MM-SS <XXXXXXXXXXXXXXXX> FCS PSTOP
```

where the 16 X's form an encrypted message. In order to verify the clearance of the message the FCS field is provided. The FCS field consists of a number that is the results of the logical operation XOR performed in the original encrypted message for each character sequentially. If these two values match then the message is correct and we respond with the **ACK** request code ("QXXXX\r"), whereas if the don't then we respond with **NACK** request code ("RXXXX\r") in order to inform the server to re-transmit the message.