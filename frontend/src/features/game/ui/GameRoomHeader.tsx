type Props = { roomId: string; connected: boolean; username?: string };

export const GameRoomHeader = ({ roomId, connected, username }: Props) => (
  <div>
    <h1>
      Room: {roomId}{' '}
      <span
        style={{
          width: 10,
          height: 10,
          display: 'inline-block',
          borderRadius: '50%',
          backgroundColor: connected ? 'green' : 'red',
        }}
      />
    </h1>
    <h2>{username}</h2>
  </div>
);
