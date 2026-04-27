package org.spring.domain.game;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.domain.card.CardCode;
import org.spring.domain.card.CardFaction;
import org.spring.domain.card.CardType;
import org.spring.domain.effect.EffectService;
import org.spring.dto.CardInstance;
import org.spring.dto.GameState;
import org.spring.dto.PlayCardRequest;
import org.spring.dto.PlayerState;
import org.spring.exc.GameCommonException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;


@Slf4j
@RequiredArgsConstructor
@Component
public class CardActionService {

    private final EffectService effectService;

    public void play(GameState gs, PlayerState player, PlayCardRequest req){

        CardInstance card = player.getHand().stream()
                .filter(c -> c.getId().equals(req.cardId()))
                .findFirst()
                .orElseThrow(() -> new GameCommonException("CARD_NOT_IN_HAND", "Такой карты нет в руке"));

        // Выбор фракции для карты Наемник
        if (card.getCode() == CardCode.CORE_MERCENARY) {

            if (req.cardFaction() == null || req.cardFaction() == CardFaction.NEUTRAL) {
                throw new GameCommonException("FACTION_REQUIRED", "Для этой карты необходимо выбрать фракцию");
            }

            card.setCardFaction(req.cardFaction());
        }

        // Применяем способности карты
        effectService.applyPlayEffects(card,player,gs);

        // Применяем эффекты сброса карты
        if (req.scrap()) {
            effectService.applyScrapEffects(card,player,gs);
            log.info("Игрок {} удалил из игры карту {}", player.getPlayerId(), card.getName());
        } else {
            // Перемещаем карту в сыгранные
            player.getPlayedCards().add(card);
            log.info("Карта ушла в сыгранные");
        }

        if (card.getType() == CardType.BASE) {
            player.getBases().add(card);
            player.getPlayedCards().remove(card);
        }

        if (card.getType() == CardType.OUTPOST) {
            player.getOutposts().add(card);
            player.getPlayedCards().remove(card);
        }
        player.getHand().remove(card);

        log.info("Игрок {} сыграл карту {}. Золото: {}, Атака: {}", player.getPlayerId(), card.getName(), player.getCurrentGold(), player.getCurrentAttack());
    }

    public void scrapStructure(GameState gs, PlayerState player, String cardId){
        CardInstance card = Stream.concat(
                        player.getBases().stream(),
                        player.getOutposts().stream()
                )
                .filter(c -> c.getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new GameCommonException("STRUCTURE_NOT_FOUND", "Структура не найдена"));

        effectService.applyScrapEffects(card, player, gs);

        if (card.getType()==CardType.BASE){
            player.getBases().remove(card);
        } else if (card.getType()==CardType.OUTPOST){
            player.getOutposts().remove(card);
        } else throw new GameCommonException("CARD_IS_NOT_STRCTURE","Карта не является базой или аванпостом");
        player.getDiscardPile().add(card);
    }

    public void exile(GameState gs, PlayerState player, String cardId, CardCode cardCode){
        CardInstance card;

        // КОСТЫЛЬ если носорог или улитка, удаляем из рынка
        if (cardCode.equals(CardCode.CORE_RHINO) || cardCode.equals(CardCode.CORE_SNAIL)) {
            card = gs.getMarket().stream()
                    .filter(c -> c.getId().equals(cardId))
                    .findFirst()
                    .orElseThrow(() -> new GameCommonException("CARD_NOT_FOUND", "Карта не найдена на рынке"));
            gs.getMarket().remove(card);
            // в остальных случаях удаляем из руки или сброса
        } else {
            card = Stream.of(player.getHand(), player.getDiscardPile())
                    .flatMap(List::stream)
                    .filter(c -> c.getId().equals(cardId))
                    .findFirst()
                    .orElseThrow(() -> new GameCommonException("CARD_NOT_FOUND", "Карта не найдена в руке или сбросе игрока"));
            player.getHand().remove(card);
            player.getDiscardPile().remove(card);
        }
        player.setRightExile(player.getRightExile() - 1);
        log.info("Игрок {} удалил из игры карту {}", player.getPlayerId(), card.getName());
    }

    public void forceDiscard(PlayerState player, String cardId) {

        CardInstance card = player.getHand().stream()
                .filter(c -> c.getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new GameCommonException("CARD_NOT_IN_HAND", "Карта не найдена в руке"));

        player.getHand().remove(card);
        player.getPlayedCards().add(card);

        player.setForcedDiscard(player.getForcedDiscard() - 1);

        log.info("Игрок {} принудительно сбросил карту {}", player.getPlayerId(), card.getName());
    }
}
