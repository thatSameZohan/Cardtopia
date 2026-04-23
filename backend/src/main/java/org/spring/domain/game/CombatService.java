package org.spring.domain.game;

import lombok.extern.slf4j.Slf4j;
import org.spring.dto.AttackRequest;
import org.spring.dto.CardInstance;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;
import org.spring.exc.GameCommonException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Component
public class CombatService {

    public void attack(GameState gs, PlayerState player, PlayerState opponent, AttackRequest req){
        int attack = player.getCurrentAttack();

        // 1 Есть аванпосты — атакуем их
        if (!opponent.getOutposts().isEmpty()) {
            if (!TargetType.OUTPOST.equals(req.targetType())) {
                throw new GameCommonException("OUTPOST_FIRST", "Сначала нужно уничтожить аванпост");
            }
            attackStructure(player, opponent, opponent.getOutposts(), req.targetId());
            return;
        }

        // 2️ База (по желанию)
        if (TargetType.BASE.equals(req.targetType())) {
            attackStructure(player, opponent, opponent.getBases(), req.targetId());
            return;
        }

        // 3️ Атака игрока
        opponent.setHealth(opponent.getHealth() - attack);
        player.setCurrentAttack(0);

        log.info("Атака: игрок {} -> оппонент {}, здоровье оппонента {}", player.getPlayerId(), opponent.getPlayerId(), opponent.getHealth());

        if (opponent.getHealth() <= 0) {
            gs.setStatus(GameStatus.FINISHED);
            gs.setWinnerId(player.getPlayerId());
            log.info("Игра завершена. Победитель: {}", player.getPlayerId());
        }
    }

    private void attackStructure(PlayerState attacker,
                                 PlayerState opponent,
                                 List<CardInstance> structures,
                                 String targetId) {

        CardInstance target = structures.stream()
                .filter(c -> c.getId().equals(targetId))
                .findFirst()
                .orElseThrow(() -> new GameCommonException("TARGET_NOT_FOUND", "Цель не найдена"));

        int defense = target.getDefense();

        if (attacker.getCurrentAttack() < defense) {
            throw new GameCommonException("LOW_ATTACK","Недостаточно атаки");
        }

        attacker.setCurrentAttack(attacker.getCurrentAttack() - defense);
        structures.remove(target);
        opponent.getDiscardPile().add(target);
    }

    public void destroyBase(PlayerState player, PlayerState opponent, String baseId){
        CardInstance base = opponent.getBases().stream()
                .filter(c -> c.getId().equals(baseId))
                .findFirst()
                .orElseThrow(() -> new GameCommonException("BASE_NOT_FOUND", "База не найдена"));

        opponent.getBases().remove(base);
        opponent.getDiscardPile().add(base);

        player.setDestroyBase(player.getDestroyBase() - 1);

        log.info("Игрок {} разрушил базу {} игрока {}", player.getPlayerId(), base.getName(), opponent.getPlayerId());
    }
}
