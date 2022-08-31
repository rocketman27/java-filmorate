package ru.yandex.practicum.filmorate.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Релизация SlopeOne для Filmorate. ((f1x1 + f2x2 + ... + fnxn) / f1 + f2 + ... + fn)
 */
@Slf4j
public class SlopeOneForFilmorate {
    private final Map<Long, Map<Long, Integer>> inputUserScores = new HashMap<>(); // <userId<filmId, score>>
    private final Map<Long, Map<Long, Double>> diff = new HashMap<>(); //<filmId<filmId, score>>
    private final Map<Long, Map<Long, Integer>> freq = new HashMap<>(); //<filmId<filmId, frequency>>
    private final List<Long> films = new ArrayList<>(); // все фильмы из оценок пользователей
    private final Map<Long, Double> predictedScores = new HashMap<>(); //<filmId, score> - предполагаемые оценки

    public SlopeOneForFilmorate(Map<Long, Map<Long, Integer>> inputUserScores) {
        this.inputUserScores.putAll(inputUserScores);
    }

    /**
     * Вычисляем итоговые оценки для пользователя
     */
    public Map<Long, Double> predictScores(long userId) {
        buildMatrices();

        Map<Long, Double> predScores = new HashMap<>();
        Map<Long, Integer> uFreq = new HashMap<>();

        for (Long filmId : diff.keySet()) {
            predScores.put(filmId, 0D);
            uFreq.put(filmId, 0);
        }
        Map<Long, Double> cleanScores = new HashMap<>();
        Map<Long, Integer> userScores = inputUserScores.get(userId);
        for (Long filmId : userScores.keySet()) {
            for (Long filmId2 : diff.keySet()) {
                try {
                    double predictedScore = userScores.get(filmId) + diff.get(filmId2).get(filmId);
                    double finalScore = predictedScore * freq.get(filmId2).get(filmId);
                    predScores.put(filmId2, predScores.get(filmId2) + finalScore);
                    uFreq.put(filmId2, uFreq.get(filmId2) + freq.get(filmId2).get(filmId));
                } catch (NullPointerException ex) {
                    log.warn("NullForSlopeOne");
                }
            }
        }

        for (Long filmId : predScores.keySet()) {
            if (uFreq.get(filmId) > 0) {
                cleanScores.put(filmId, predScores.get(filmId) / uFreq.get(filmId));
            }
        }

        for (Long filmId : films) {
            if (userScores.containsKey(filmId)) {
                cleanScores.put(filmId, userScores.get(filmId).doubleValue());
            } else if (!cleanScores.containsKey(filmId)) {
                cleanScores.put(filmId, -1D);
            }
        }
        predictedScores.putAll(cleanScores);
        return new HashMap<>(predictedScores);
    }

    /**
     * Заполняем матрицы начальными дельтами и частотами(x1, x2, ..., xn) (f1, f2, ..., fn)
     */
    private void buildMatrices() {
        for (Map<Long, Integer> userScores : inputUserScores.values()) {
            for (Map.Entry<Long, Integer> score : userScores.entrySet()) {
                if (!films.contains(score.getKey())) {
                    films.add(score.getKey());
                }

                if (!diff.containsKey(score.getKey())) {
                    diff.put(score.getKey(), new HashMap<>());
                    freq.put(score.getKey(), new HashMap<>());
                }

                for (Map.Entry<Long, Integer> score2 : userScores.entrySet()) {
                    Double oldDiff = diff.get(score.getKey()).getOrDefault(score2.getKey(), 0D);
                    Integer oldFreq = freq.get(score.getKey()).getOrDefault(score2.getKey(), 0);
                    Integer observedDiff = score.getValue() - score2.getValue();
                    diff.get(score.getKey()).put(score2.getKey(), oldDiff + observedDiff);
                    freq.get(score.getKey()).put(score2.getKey(), oldFreq + 1);
                }
            }
        }

        for (Long filmId : diff.keySet()) {
            for (Long filmId2 : diff.get(filmId).keySet()) {
                Double oldScore = diff.get(filmId).get(filmId2);
                Integer oldFreq = freq.get(filmId).get(filmId2);
                diff.get(filmId).put(filmId2, oldScore / oldFreq);
            }
        }
    }
}
