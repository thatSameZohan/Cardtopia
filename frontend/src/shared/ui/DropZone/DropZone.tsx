// 'use client';
// import React, { useId } from 'react';

// import styles from './DropZone.module.scss';
// import { Card, CardType } from '@/features/Card';
// export function DropZone({
//   cards,
//   onDrop,
//   title,
// }: {
//   cards: CardType[];
//   onDrop: (card: CardType) => void;
//   title: string;
// }) {
//   const id = useId(); // Генерируем уникальный ID
//   const [{ isOver }, drop] = useDrop(
//     () => ({
//       accept: 'card',
//       drop: (item: CardType) => onDrop(item),
//       collect: (monitor) => ({ isOver: !!monitor.isOver() }),
//     }),
//     [onDrop],
//   );

//   return (
//     <div
//       ref={drop as any}
//       className={styles.root}
//       style={{
//         background: isOver ? '#e0ffe0' : '#f9f9f9', // <- здесь подсветка
//       }}
//     >
//       <h3 style={{ width: '100%' }}>{title}</h3>
//       {cards.map((card, index) => (
//         <Card key={`${id}-${index}`} card={card} /> // Используем уникальный ID в ключе
//       ))}
//     </div>
//   );
// }
