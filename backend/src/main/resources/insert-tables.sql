-- ================= core_set =================
INSERT INTO core_set (set_name, qty, name, type, faction, cost, defense, role) VALUES
   ('Core Set', 1, 'Battle Blob', 'Ship', 'Blob', 6, 0, 'Trade Deck'),
   ('Core Set', 2, 'Battle Pod', 'Ship', 'Blob', 2, 0, 'Trade Deck'),
   ('Core Set', 1, 'Blob Carrier', 'Ship', 'Blob', 6, 0, 'Trade Deck'),
   ('Core Set', 2, 'Blob Destroyer', 'Ship', 'Blob', 4, 0, 'Trade Deck'),
   ('Core Set', 3, 'Blob Fighter', 'Ship', 'Blob', 1, 0, 'Trade Deck'),
   ('Core Set', 3, 'Blob Wheel', 'Base', 'Blob', 3, 5, 'Trade Deck'),
   ('Core Set', 1, 'Blob World', 'Base', 'Blob', 8, 0, 'Trade Deck'),
   ('Core Set', 1, 'Mothership', 'Ship', 'Blob', 7, 0, 'Trade Deck'),
   ('Core Set', 2, 'Ram', 'Ship', 'Blob', 3, 0, 'Trade Deck'),
   ('Core Set', 1, 'The Hive', 'Base', 'Blob', 5, 5, 'Trade Deck'),
   ('Core Set', 3, 'Trade Pod', 'Ship', 'Blob', 2, 0, 'Trade Deck'),
   ('Core Set', 1, 'Battle Mech', 'Ship', 'Machine Cult', 5, 0, 'Trade Deck'),
   ('Core Set', 2, 'Battle Station', 'Base', 'Machine Cult', 3, 5, 'Trade Deck'),
   ('Core Set', 1, 'Brain World', 'Base', 'Machine Cult', 8, 6, 'Trade Deck'),
   ('Core Set', 1, 'Junkyard', 'Base', 'Machine Cult', 6, 5, 'Trade Deck'),
   ('Core Set', 1, 'Machine Base', 'Base', 'Machine Cult', 7, 6, 'Trade Deck'),
   ('Core Set', 1, 'Mech World', 'Base', 'Machine Cult', 5, 6, 'Trade Deck'),
   ('Core Set', 3, 'Missile Bot', 'Ship', 'Machine Cult', 2, 0, 'Trade Deck'),
   ('Core Set', 1, 'Missile Mech', 'Ship', 'Machine Cult', 6, 0, 'Trade Deck'),
   ('Core Set', 2, 'Patrol Mech', 'Ship', 'Machine Cult', 4, 0, 'Trade Deck'),
   ('Core Set', 1, 'Stealth Needle', 'Ship', 'Machine Cult', 4, 0, 'Trade Deck'),
   ('Core Set', 3, 'Supply Bot', 'Ship', 'Machine Cult', 3, 0, 'Trade Deck'),
   ('Core Set', 3, 'Trade Bot', 'Ship', 'Machine Cult', 1, 0, 'Trade Deck'),
   ('Core Set', 1, 'Battlecruiser', 'Ship', 'Star Empire', 6, 0, 'Trade Deck'),
   ('Core Set', 2, 'Corvette', 'Ship', 'Star Empire', 2, 0, 'Trade Deck'),
   ('Core Set', 1, 'Dreadnaught', 'Ship', 'Star Empire', 7, 0, 'Trade Deck'),
   ('Core Set', 1, 'Fleet HQ', 'Base', 'Star Empire', 8, 8, 'Trade Deck'),
   ('Core Set', 3, 'Imperial Fighter', 'Ship', 'Star Empire', 1, 0, 'Trade Deck'),
   ('Core Set', 3, 'Imperial Frigate', 'Ship', 'Star Empire', 3, 0, 'Trade Deck'),
   ('Core Set', 2, 'Recycling Station', 'Base', 'Star Empire', 4, 4, 'Trade Deck'),
   ('Core Set', 1, 'Royal Redoubt', 'Base', 'Star Empire', 6, 6, 'Trade Deck'),
   ('Core Set', 2, 'Space Station', 'Base', 'Star Empire', 4, 4, 'Trade Deck'),
   ('Core Set', 3, 'Survey Ship', 'Ship', 'Star Empire', 3, 0, 'Trade Deck'),
   ('Core Set', 1, 'War World', 'Base', 'Star Empire', 5, 4, 'Trade Deck'),
   ('Core Set', 2, 'Barter World', 'Base', 'Trade Federation', 4, 4, 'Trade Deck'),
   ('Core Set', 1, 'Central Office', 'Base', 'Trade Federation', 7, 6, 'Trade Deck'),
   ('Core Set', 1, 'Command Ship', 'Ship', 'Trade Federation', 8, 0, 'Trade Deck'),
   ('Core Set', 3, 'Cutter', 'Ship', 'Trade Federation', 2, 0, 'Trade Deck'),
   ('Core Set', 1, 'Defense Center', 'Base', 'Trade Federation', 5, 5, 'Trade Deck'),
   ('Core Set', 2, 'Embassy Yacht', 'Ship', 'Trade Federation', 3, 0, 'Trade Deck'),
   ('Core Set', 3, 'Federation Shuttle', 'Ship', 'Trade Federation', 1, 0, 'Trade Deck'),
   ('Core Set', 1, 'Flagship', 'Ship', 'Trade Federation', 6, 0, 'Trade Deck'),
   ('Core Set', 2, 'Freighter', 'Ship', 'Trade Federation', 4, 0, 'Trade Deck'),
   ('Core Set', 1, 'Port of Call', 'Base', 'Trade Federation', 6, 6, 'Trade Deck'),
   ('Core Set', 1, 'Trade Escort', 'Ship', 'Trade Federation', 5, 0, 'Trade Deck'),
   ('Core Set', 2, 'Trading Post', 'Base', 'Trade Federation', 3, 4, 'Trade Deck'),
   ('Core Set', 10, 'Explorer', 'Ship', 'Unaligned', 2, 0, 'Explorer Pile'),
   ('Core Set', 16, 'Scout', 'Ship', 'Unaligned', 0, 0, 'Personal Deck'),
   ('Core Set', 4, 'Viper', 'Ship', 'Unaligned', 0, 0, 'Personal Deck');

-- ================= abilities =================

-- 1. Battle Blob
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (1, 'COMBAT', 8, NULL),
                                                            (1, 'DRAW', 1, 'Blob Ally'),
                                                            (1, 'COMBAT', 4, 'Scrap');

-- 2. Battle Pod
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (2, 'COMBAT', 4, NULL),
                                                            (2, 'SCRAP', 0, 'May scrap a card in the trade row'),
                                                            (2, 'COMBAT', 2, 'Blob Ally');

-- 3. Blob Carrier
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (3, 'COMBAT', 7, NULL),
                                                            (3, 'ACQUIRE', 0, 'Blob Ally: Acquire any ship for free and put it on top of your deck');

-- 4. Blob Destroyer
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (4, 'COMBAT', 6, NULL),
                                                            (4, 'DESTROY', 0, 'May destroy target base'),
                                                            (4, 'SCRAP', 0, 'Blob Ally');

-- 5. Blob Fighter
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (5, 'COMBAT', 3, NULL),
                                                            (5, 'DRAW', 1, 'Blob Ally');

-- 6. Blob Wheel
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (6, 'COMBAT', 1, NULL),
                                                            (6, 'TRADE', 3, 'Scrap');

-- 7. Blob World
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (7, 'COMBAT', 5, NULL),
                                                            (7, 'DRAW', 0, 'Draw a card for each Blob card played this turn');

-- 8. Mothership
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (8, 'COMBAT', 6, NULL),
                                                            (8, 'DRAW', 1, NULL),
                                                            (8, 'DRAW', 1, 'Blob Ally');

-- 9. Ram
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (9, 'COMBAT', 5, NULL),
                                                            (9, 'COMBAT', 2, 'Blob Ally'),
                                                            (9, 'TRADE', 3, 'Scrap');

-- 10. The Hive
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (10, 'COMBAT', 3, NULL),
                                                            (10, 'DRAW', 1, 'Blob Ally');

-- 11. Trade Pod
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (11, 'TRADE', 3, NULL),
                                                            (11, 'COMBAT', 2, 'Blob Ally');

-- 12. Battle Mech
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (12, 'COMBAT', 4, NULL),
                                                            (12, 'SCRAP', 0, 'May scrap a card in hand or discard pile'),
                                                            (12, 'DRAW', 1, 'Machine Cult Ally');

-- 13. Battle Station
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (13, 'SCRAP', 0, NULL),
                                                            (13, 'COMBAT', 5, NULL);

-- 14. Brain World
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (14, 'SCRAP', 0, 'Up to 2 cards in hand or discard pile'),
                                                            (14, 'DRAW', 0, 'Draw a card for each card scrapped');

-- 15. Junkyard
INSERT INTO abilities (card_id, type, value, condition) VALUES
    (15, 'SCRAP', 0, 'Scrap a card in hand or discard pile');

-- 16. Machine Base
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (16, 'DRAW', 1, NULL),
                                                            (16, 'SCRAP', 0, 'Scrap a card from hand');

-- 17. Mech World
INSERT INTO abilities (card_id, type, value, condition) VALUES
    (17, 'ALLY', 0, 'Counts as ally for all factions');

-- 18. Missile Bot
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (18, 'COMBAT', 2, NULL),
                                                            (18, 'SCRAP', 0, 'May scrap a card in hand or discard pile'),
                                                            (18, 'COMBAT', 2, 'Machine Cult Ally');

-- 19. Missile Mech
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (19, 'COMBAT', 6, NULL),
                                                            (19, 'DESTROY', 0, 'Target base'),
                                                            (19, 'DRAW', 1, 'Machine Cult Ally');

-- 20. Patrol Mech
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (20, 'TRADE', 3, NULL),
                                                            (20, 'COMBAT', 5, NULL),
                                                            (20, 'SCRAP', 0, 'Machine Cult Ally');

-- 21. Stealth Needle
INSERT INTO abilities (card_id, type, value, condition) VALUES
    (21, 'COPY', 0, 'Copy another ship played this turn; faction includes Machine Cult');

-- 22. Supply Bot
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (22, 'TRADE', 2, NULL),
                                                            (22, 'SCRAP', 0, 'May scrap a card in hand or discard pile'),
                                                            (22, 'COMBAT', 2, 'Machine Cult Ally');

-- 23. Trade Bot
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (23, 'TRADE', 1, NULL),
                                                            (23, 'SCRAP', 0, 'May scrap a card in hand or discard pile'),
                                                            (23, 'COMBAT', 2, 'Machine Cult Ally');

-- 24. Battlecruiser
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (24, 'COMBAT', 5, NULL),
                                                            (24, 'DRAW', 1, NULL),
                                                            (24, 'DISCARD', 1, 'Target opponent'),
                                                            (24, 'SCRAP', 0, 'May destroy target base');

-- 25. Corvette
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (25, 'COMBAT', 1, NULL),
                                                            (25, 'DRAW', 1, NULL),
                                                            (25, 'COMBAT', 2, 'Star Empire Ally');

-- 26. Dreadnaught
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (26, 'COMBAT', 7, NULL),
                                                            (26, 'DRAW', 1, NULL),
                                                            (26, 'COMBAT', 5, 'Scrap');

-- 27. Fleet HQ
INSERT INTO abilities (card_id, type, value, condition) VALUES
    (27, 'COMBAT', 1, 'Only ships played while Fleet HQ in play gain 1 combat');

-- 28. Imperial Fighter
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (28, 'COMBAT', 2, NULL),
                                                            (28, 'DISCARD', 1, 'Target opponent'),
                                                            (28, 'COMBAT', 2, 'Star Empire Ally');

-- 29. Imperial Frigate
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (29, 'COMBAT', 4, NULL),
                                                            (29, 'DISCARD', 1, 'Target opponent'),
                                                            (29, 'COMBAT', 2, 'Star Empire Ally'),
                                                            (29, 'DRAW', 1, 'Scrap');

-- 30. Recycling Station
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (30, 'TRADE', 1, NULL),
                                                            (30, 'DISCARD', 2, 'May discard up to 2 cards'),
                                                            (30, 'DRAW', 0, 'Draw that many cards');

-- 31. Royal Redoubt
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (31, 'COMBAT', 3, NULL),
                                                            (31, 'DISCARD', 1, 'Target opponent');

-- 32. Space Station
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (32, 'COMBAT', 2, NULL),
                                                            (32, 'COMBAT', 2, 'Star Empire Ally'),
                                                            (32, 'TRADE', 4, 'Scrap');

-- 33. Survey Ship
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (33, 'TRADE', 1, NULL),
                                                            (33, 'DRAW', 1, NULL),
                                                            (33, 'DISCARD', 1, 'Target opponent');

-- 34. War World
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (34, 'COMBAT', 3, NULL),
                                                            (34, 'COMBAT', 4, 'Star Empire Ally');

-- 35. Barter World
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (35, 'AUTHORITY', 2, NULL),
                                                            (35, 'TRADE', 2, NULL),
                                                            (35, 'COMBAT', 5, 'Scrap');

-- 36. Central Office
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (36, 'TRADE', 2, NULL),
                                                            (36, 'DRAW', 1, 'Trade Federation Ally');

-- 37. Command Ship
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (37, 'AUTHORITY', 4, NULL),
                                                            (37, 'COMBAT', 5, NULL),
                                                            (37, 'DRAW', 2, NULL),
                                                            (37, 'DESTROY', 0, 'Trade Federation Ally');

-- 38. Cutter
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (38, 'AUTHORITY', 4, NULL),
                                                            (38, 'TRADE', 2, NULL),
                                                            (38, 'COMBAT', 4, 'Trade Federation Ally');

-- 39. Defense Center
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (39, 'AUTHORITY', 3, NULL),
                                                            (39, 'COMBAT', 2, 'Trade Federation Ally');

-- 40. Embassy Yacht
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (40, 'AUTHORITY', 3, NULL),
                                                            (40, 'TRADE', 2, NULL),
                                                            (40, 'DRAW', 2, 'If you have two or more bases in play');

-- 41. Federation Shuttle
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (41, 'TRADE', 2, NULL),
                                                            (41, 'AUTHORITY', 4, 'Trade Federation Ally');

-- 42. Flagship
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (42, 'COMBAT', 5, NULL),
                                                            (42, 'DRAW', 1, NULL),
                                                            (42, 'AUTHORITY', 5, 'Trade Federation Ally');

-- 43. Freighter
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (43, 'TRADE', 4, NULL),
                                                            (43, 'TRADE', 0, 'Next ship you acquire this turn goes on top of deck');

-- 44. Port of Call
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (44, 'TRADE', 3, NULL),
                                                            (44, 'DRAW', 1, 'Scrap: may destroy target base');

-- 45. Trade Escort
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (45, 'AUTHORITY', 4, NULL),
                                                            (45, 'COMBAT', 4, NULL),
                                                            (45, 'DRAW', 1, 'Trade Federation Ally');

-- 46. Trading Post
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (46, 'AUTHORITY', 1, NULL),
                                                            (46, 'TRADE', 1, NULL),
                                                            (46, 'COMBAT', 3, 'Scrap');

-- 47. Explorer
INSERT INTO abilities (card_id, type, value, condition) VALUES
                                                            (47, 'TRADE', 2, NULL),
                                                            (47, 'COMBAT', 2, 'Scrap');

-- 48. Scout
INSERT INTO abilities (card_id, type, value, condition) VALUES
    (48, 'TRADE', 1, NULL);

-- 49. Viper
INSERT INTO abilities (card_id, type, value, condition) VALUES
    (49, 'COMBAT', 1, NULL);
