type Props = { connected: boolean; waiting: boolean; isUserInRoom: boolean };

export const GameRoomStatus = ({ connected, waiting, isUserInRoom }: Props) => {
  if (!connected) return <p>Соединение отсутствует</p>;
  if (!isUserInRoom) return null;
  return <p>{waiting ? 'Ожидаем второго игрока…' : 'Игра началась'}</p>;
};
