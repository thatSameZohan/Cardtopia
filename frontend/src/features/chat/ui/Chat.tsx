'use client';

import { useEffect, useState } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

export default function Chat() {
  const [client, setClient] = useState<Client | null>(null);
  const [connected, setConnected] = useState(false);

  const [room, setRoom] = useState("general");
  const [username, setUsername] = useState("User" + Math.floor(Math.random() * 1000));
  const [message, setMessage] = useState("");
  const [messages, setMessages] = useState<any[]>([]);
 
  useEffect(() => {
    const wsUrl = 'http://localhost:8080/ws';
    console.log("Connecting to WebSocket at:", wsUrl);

    const socket = new SockJS(wsUrl);
    const stompClient = new Client({
      webSocketFactory: () => socket as any,
        reconnectDelay: 500,
       debug: (msg) => console.log("STOMP:", msg),
    });

    stompClient.onConnect = () => {
      console.log("WebSocket connected!");
      setConnected(true);

      stompClient.subscribe(`/topic/room/${room}`, (msg) => {
        console.log("Message received:", msg.body);
        const body = JSON.parse(msg.body);
        setMessages((prev) => [...prev, body]);
      });
    };

    stompClient.onStompError = (frame) => {
      console.error("STOMP error:", frame.headers, frame.body);
    };

    stompClient.onWebSocketError = (evt) => {
      console.error("WebSocket transport error:", evt);
    };

    stompClient.onWebSocketClose = (evt) => {
      console.warn("WebSocket closed:", evt);
    };

    stompClient.activate();
    setClient(stompClient);

    return () => {
      stompClient.deactivate().catch(console.error);
    };
  }, [room]);

  const sendMessage = () => {
    if (!client || !connected) {
      console.warn("Not connected. Can't send message");
      return;
    }

    console.log("Sending message:", message);

    client.publish({
      destination: "/app/send",
      body: JSON.stringify({
        room,
        sender: username,
        text: message,
      }),
    });

    setMessage('');
  };
 console.log("messages", messages);
  return (
    <div style={{ padding: 20 }}>
      <h2>Комната: {room}</h2>

      <input value={room} onChange={(e) => setRoom(e.target.value)} placeholder="Название комнаты" />
      <input value={username} onChange={(e) => setUsername(e.target.value)} placeholder="Никнейм" />

      <div style={{ display: "flex", gap: 10 }}>
        <input
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          placeholder="Введите сообщение"
          style={{ flex: 1 }}
        />
        <button onClick={sendMessage}>Отправить</button>
      </div>

      <div style={{ marginTop: 20 }}>
        <h3>Сообщения:</h3>
        {messages.map((m, i) => (
          <div key={i} style={{ borderBottom: "1px solid #eee", padding: 5 }}>
            <b>{m.sender}:</b> {m.text}
          </div>
        ))}
      </div>
    </div>
  );
}
