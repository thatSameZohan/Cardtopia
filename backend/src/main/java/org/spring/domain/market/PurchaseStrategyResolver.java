package org.spring.domain.market;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PurchaseStrategyResolver {

    private final Map<PurchaseType, PurchaseStrategy> strategies;

    public PurchaseStrategyResolver(List<PurchaseStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(PurchaseStrategy::getType, Function.identity()));
    }

    public PurchaseStrategy resolve(PurchaseType type) {

        PurchaseStrategy strategy = strategies.get(type);

        if (strategy == null) {
            throw new IllegalStateException("Неизвестная стратегия покупки: " + type);
        }

        return strategy;
    }
}