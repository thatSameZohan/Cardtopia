import { Middleware } from '@reduxjs/toolkit';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { toast } from 'react-toastify';

import { connect, disconnect, sendAction, setState } from './gameSlice';
import { RootState } from '../store';

import { AnyAction, Dispatch } from '@reduxjs/toolkit';

export const stompMiddleware: Middleware = (storeAPI) => {
  let client: Client | null = null;

  return (next) => (action) => {
    if (connect.match(action)) {
      const { roomId } = action.payload;
      const token = (storeAPI.getState() as RootState).auth.accessToken;

      if (!token) {
        toast.error('Вы не авторизованы');
        return;
      }

      client = new Client({
        connectHeaders: {
          Authorization: `Bearer ${token}`,
        },
        webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
        onConnect: () => {
          client?.subscribe(`/topic/room.${roomId}`, (msg) => {
            const body = JSON.parse(msg.body);
            if (body.type === 'STATE_UPDATE') {
              storeAPI.dispatch(setState(body.payload));
            } else if (body.type === 'ERROR') {
              toast.error(body.payload.message);
            }
          });
        },
        onStompError: (frame) => {
          toast.error(`Broker reported error: ${frame.headers['message']}`);
          toast.error(`Additional details: ${frame.body}`);
        },
      });

      client.activate();
    }

    if (sendAction.match(action) && client) {
      const { roomId } = (storeAPI.getState() as RootState).game;
      const { actionType, payload } = action.payload;
      
      client.publish({
        destination: `/app/${actionType.toLowerCase()}`,
        body: JSON.stringify({ roomId, ...payload }),
      });
    }

    if (disconnect.match(action) && client) {
      client.deactivate();
      client = null;
    }

    return next(action);
  };
};