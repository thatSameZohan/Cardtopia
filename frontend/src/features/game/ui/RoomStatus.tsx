type Props = { connected: boolean; waiting: boolean; isUserInRoom: boolean };

export const RoomStatus = ({ connected, waiting, isUserInRoom }: Props) => {
  if (!connected) return <p>Соединение отсутствует</p>;
  if (!isUserInRoom) return null;
  return waiting ? <p>Ожидаем второго игрока…</p> : null;
};
