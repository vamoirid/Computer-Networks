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