// Card.tsx
'use client';

import { useRef } from 'react';
import clsx from 'clsx';
import styles from './Card.module.scss';
import { useDrag } from 'react-dnd';
import { CardType } from '@/features/game/type/type';

type CardProps = CardType & {
  disabled?: boolean;
  variant?: 'face' | 'back';
  dndType?: string;
  onClick?: () => void;
  draggable?: boolean;
  image?: string;
  rarity?: 'common' | 'rare' | 'legendary';
  className?: string;
};

export const Card = (props: CardProps) => {
  const {
    id,
    name,
    type,
    cost,
    defense,
    abilities = [],
    disabled,
    variant = 'face',
    dndType = 'card',
    draggable = true,
    onClick,
    image,
    rarity = 'common',
    className,
  } = props;

  const ref = useRef<HTMLDivElement>(null);

  const [{ isDragging }, drag] = useDrag(
    () => ({
      type: dndType,
      item: { id, name, type, cost, defense, abilities, image },
      canDrag: draggable && !disabled && variant === 'face',
      collect: (monitor) => ({
        isDragging: monitor.isDragging(),
      }),
    }),
    [
      id,
      name,
      type,
      cost,
      defense,
      abilities,
      image,
      disabled,
      dndType,
      draggable,
      variant,
    ],
  );

  drag(ref);
  console.log(props, 'props');
  // Демо-данные для визуала как на скриншоте
  const demoImage =
    image ||
    'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400&h=500&fit=crop';
  const demoName = name || 'Wistful Willow';
  const demoRarity = rarity || 'legendary';
  const demoCost = cost ?? 4;
  const demoAttack = 4;
  const demoHealth = 4;
  const demoAbilities =
    abilities.length > 0
      ? abilities
      : [
          { icon: '👣', type: 'foot' as const },
          { icon: '⏳', type: 'hourglass' as const },
        ];

  return (
    <div
      ref={ref}
      className={clsx(
        styles.cardContainer,
        variant === 'back' && styles.flipped,
        disabled && styles.disabled,
        isDragging && styles.dragging,
        demoRarity === 'legendary' && styles.legendary,
        className,
      )}
      style={{ opacity: isDragging ? 0.3 : 1 }}
      onClick={() => !isDragging && onClick?.()}
    >
      <div className={styles.card}>
        <div className={styles.face}>
          {demoRarity === 'legendary' && (
            <div className={styles.legendaryGlow} />
          )}

          <div className={styles.frame}>
            <div className={styles.goldBorder}>
              <div className={styles.inner}>
                {/* Image Area with ornate frame */}
                <div className={styles.imageArea}>
                  <div className={styles.ornateFrame}>
                    <div className={styles.imageContainer}>
                      <img
                        src={demoImage}
                        className={styles.image}
                        alt={demoName}
                        draggable={false}
                      />
                      <div className={styles.imageOverlay} />
                    </div>

                    {/* Red diamond gem (top-left) */}
                    <div className={styles.diamondGem} />

                    {/* Ability icons (top-right) */}
                    <div className={styles.abilityIcons}>
                      {demoAbilities.map((ability, index) => (
                        <div
                          key={index}
                          className={clsx(
                            styles.abilityIcon,
                            styles[
                              `ability${ability.type.charAt(0).toUpperCase() + ability.type.slice(1)}`
                            ],
                          )}
                        >
                          'ability.icon'
                        </div>
                      ))}
                    </div>
                  </div>
                </div>

                {/* Stats bar with hex gems */}
                <div className={styles.statsBar}>
                  <div className={clsx(styles.statGem, styles.attackGem)}>
                    <span className={styles.statValue}>{demoAttack}</span>
                  </div>

                  <div className={clsx(styles.statGem, styles.costGemCenter)}>
                    <span className={styles.costValue}>{demoCost}</span>
                  </div>

                  <div className={clsx(styles.statGem, styles.healthGem)}>
                    <span className={styles.statValue}>{demoHealth}</span>
                  </div>
                </div>

                {/* Card info */}
                <div className={styles.info}>
                  <div className={styles.name}>{demoName}</div>
                  <div className={styles.rarity}>{demoRarity}</div>
                  <div className={styles.separator} />
                  <div className={styles.text}>
                    <span className={styles.keyword}>Play:</span> Give 2 Elves
                    +6|+6.
                    <br />
                    <span className={styles.keyword}>Start Turn:</span> Return
                    me to your hand.
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className={styles.shine} />
        </div>

        <div className={styles.back}>
          <div className={styles.backInner}>
            <div className={styles.backLogo}>🃏</div>
          </div>
        </div>
      </div>
    </div>
  );
};
