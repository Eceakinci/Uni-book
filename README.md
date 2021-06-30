# Unibook

Member: Ece AKINCI - 20170808038

## Introduction

Unibook is an application that helps its users to book changes. For this purpose, users communicate between the chat part of the application. For protecting user’s data, I implement the Advanced Encryption Standard(AES).

## What is AES?

AES is a type of cipher that helps us to transfer data. It is the symmetric type of encryption, which means that we can use the same key for both encryption and decryption and it is a block cipher which means the algorithm encrypts data in a block of bits. Key length may vary based on application/project needs. In my project, I used a 128-bit key for better battery consumption. 

## Why chat encryption is important?

Data traveling over the internet may create security issues and it is easy to reach for others. If we build a system that encrypts the message on the sender side, then traveled data becomes the encrypted message, if and only if the receiver (which has the same encryption key) can see it from its device. So, encryption operation on devices lessens the burden of the database. 

## My Solution

I define 128-bit encryption key, cipher, and decipher as global variables. On the sender side I call the AESEncryption() method, which takes parameters as a string and encodes messages with ISO 8859-1 (8-bit single-byte coded graphic character sets) to a byte array with the help of cipher. On the receiver side, I call the AESDecryption() method, which takes parameters as a string and transforms that string to a byte array. With the help of decipher it decodes the byte array and returns actual message-plain text.

![image](https://user-images.githubusercontent.com/32226692/122815796-9a67d200-d2de-11eb-9cd2-e56ec13d7461.png)

## Implementation Link 
https://github.com/Eceakinci/Uni-book/blob/master/app/src/main/java/com/loory/unibook/MessageActivity.java




