'use client';
import React, { useState, useEffect } from 'react';
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import { DropZone } from '@/shared/ui/DropZone';
import { BossPanel } from '@/features/boss/ui/BossPanel';
import { CardType } from '@/features/cards/model/card.schema';
import { drawRandomCards, markCardsUsed } from '@/features/cards/lib/cards.helpers';
import styles from './homepage.module.scss';
import { Button } from '@/shared/ui/Button';
import { useRouter } from 'next/navigation';

export const HomepageView = () => {
  const router = useRouter();
  const initialCardsList: CardType[] = Array.from({ length: 30 }, (_, i) => ({
    id: i + 1,
    name: `Корабль ${i + 1}`,
    countAttack: Math.floor(Math.random() * 5) + 1,
    used: false,
  }));

  const [initialCards, setInitialCards] = useState<CardType[]>(initialCardsList);
  const [deck, setDeck] = useState<CardType[]>([]);
  const [table, setTable] = useState<CardType[]>([]);
  const [shopCards, setShopCards] = useState<CardType[]>([]);
  const [boss, setBoss] = useState(50);

  // Стартовая рука
  useEffect(() => {
    const newDeck = drawRandomCards(initialCards, 3);
    setDeck(newDeck);
    setInitialCards(markCardsUsed(initialCards, newDeck));
  }, []);

  // Стартовый магазин
  useEffect(() => {
    refillShop();
  }, []);

  const moveToTable = (card: CardType) => {
    // только карты из колоды
    if (!deck.find((c) => c.id === card.id)) return;
    setTable((prev) => [...prev, card]);
    setDeck((prev) => prev.filter((c) => c.id !== card.id));
  };

  const moveToDeck = (card: CardType) => {
    // из магазина или со стола
    if (!deck.find((c) => c.id === card.id)) setDeck((prev) => [...prev, card]);
    setTable((prev) => prev.filter((c) => c.id !== card.id));
    setShopCards((prev) => prev.filter((c) => c.id !== card.id));
    setInitialCards((prev) => (prev.find((c) => c.id === card.id) ? prev : [...prev, card]));
  };

  const moveToShop = (card: CardType) => {
    // только из колоды или со стола
    if (!shopCards.find((c) => c.id === card.id)) setShopCards((prev) => [...prev, card]);
    setDeck((prev) => prev.filter((c) => c.id !== card.id));
    setTable((prev) => prev.filter((c) => c.id !== card.id));
  };

  const moveAllToTable = () => {
    setTable((prev) => [...prev, ...deck]);
    setDeck([]);
  };

  const hitBoss = () => {
    const damage = table.reduce((sum, c) => sum + c.countAttack, 0);
    setBoss((prev) => Math.max(prev - damage, 0));

    setTable([]);
    const newCards = drawRandomCards(initialCards, 5);
    setInitialCards(markCardsUsed(initialCards, newCards));
    setDeck((prev) => [...prev, ...newCards]);
    refillShop();
  };

  // Генерация случайных карт для магазина
  const refillShop = () => {
    const needed = 5 - shopCards.length;
    if (needed > 0) {
      const newCards = drawRandomCards(initialCards, needed);
      setShopCards((prev) => [...prev, ...newCards]);
    }
  };

  const createGame = () => {
    const gameId = Math.random().toString(36).substring(7);
    router.push(`/game/${gameId}`);
  };


  return (
    <DndProvider backend={HTML5Backend}>
      <main className={styles.main}>
        <BossPanel health={boss} onAttack={hitBoss} disabled={deck.length > 0} />
        <DropZone cards={table} onDrop={moveToTable} title="Стол" />
        <DropZone cards={deck} onDrop={moveToDeck} title="Колода" />
        <DropZone cards={shopCards} onDrop={moveToDeck} title="Магазин" />
        <Button onClick={moveAllToTable}>Переместить карты</Button>
        <Button onClick={createGame}>Создать игру</Button>
      </main>
    </DndProvider>
  );
};
