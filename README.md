# Chat Application Summary

This chat application was developed using the **client-server model**, where the client sends and receives data from the server, and **TCP/IP protocol**, which ensures message integrity and establishes reliable client-server connections.

---

## Server

The `Server` class has three main responsibilities:

- It contains a `ServerSocket` that **listens for incoming TCP connections**.
- It maintains a **list of all connected clients**.
- It listens on a **specific port** that clients use to connect.

The core logic is implemented in the `run()` method. This method uses `server.accept()`, a **blocking call** that waits for a TCP connection and returns a `Socket` when a client connects. Each socket is then handled by an instance of the `ClientHandler` class.

Two important methods in the `Server` class are:

- `broadcast()`: sends a message to **all connected clients except the sender**.
- `sendPrivateMessage()`: sends a message **to a specific client** based on their nickname.

---

## ClientHandler

To keep classes focused on single responsibilities, a separate class called `ClientHandler` is used to **manage communication between the server and each individual client**.

When a client connects, the socket returned by `server.accept()` is passed to a new `ClientHandler`. Each connected client has its **own ClientHandler instance** running on a separate thread.

This class implements the `Runnable` interface and will run **as long as the client is connected**. Its `BufferedReader` and `BufferedWriter` are linked to the client’s socket, enabling **bidirectional communication** between the client and the server.

The thread constantly listens for client messages to forward them to the server, and vice versa.

---

## Client

The `Client` class runs two separate threads in parallel:

- One to **send messages** to the server.
- One to **receive messages** from the server.

It uses a `Socket` to connect to the server and `BufferedReader`/`BufferedWriter` streams to handle communication. The nickname is requested when the client connects and is used to identify users in the chat.

---

## 🧠 Key Concepts

- Multi-threading to support multiple simultaneous clients.
- Separation of concerns via `ClientHandler`.
- Message handling using TCP sockets to ensure reliable delivery.
- Public and private message support.
