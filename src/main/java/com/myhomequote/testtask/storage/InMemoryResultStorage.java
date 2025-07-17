package com.myhomequote.testtask.storage;

import com.myhomequote.testtask.model.UserLevelResult;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Component
public class InMemoryResultStorage {

    // Stores latest result for user-level pairs
    private final ConcurrentHashMap<Integer, Map<Integer, Integer>> userLevelResults = new ConcurrentHashMap<>();

    // Stores top-20 results per level
    private final ConcurrentHashMap<Integer, NavigableSet<UserLevelResult>> levelTopResults = new ConcurrentHashMap<>();

    private static final int MAX_TOP = 20;

    public synchronized void updateResult(int userId, int levelId, int result) {
        // Update user-level map
        userLevelResults.computeIfAbsent(userId, k -> new ConcurrentHashMap<>()).put(levelId, result);

        // Update level-top set
        levelTopResults.computeIfAbsent(levelId, k -> new ConcurrentSkipListSet<>());

        NavigableSet<UserLevelResult> levelSet = levelTopResults.get(levelId);

        // Remove old if exists
        levelSet.removeIf(userLevelResult -> userLevelResult.getUserId() == userId && userLevelResult.getLevelId() == levelId);
        levelSet.add(new UserLevelResult(userId, levelId, result));

        // Maintain only top 20
        if (levelSet.size() > MAX_TOP) {
            Iterator<UserLevelResult> iterator = levelSet.descendingIterator();
            while (levelSet.size() > MAX_TOP && iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
        }
    }

    public List<UserLevelResult> getUserTopResults(int userId) {
        Map<Integer, Integer> levels = userLevelResults.get(userId);
        if (levels == null) return Collections.emptyList();

        List<UserLevelResult> results = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : levels.entrySet()) {
            results.add(new UserLevelResult(userId, entry.getKey(), entry.getValue()));
        }

        // Sort by result DESC, then levelId DESC
        results.sort((a, b) -> {
            int cmp = Integer.compare(b.getResult(), a.getResult());
            return (cmp != 0) ? cmp : Integer.compare(b.getLevelId(), a.getLevelId());
        });

        return results.subList(0, Math.min(MAX_TOP, results.size()));
    }

    public List<UserLevelResult> getLevelTopResults(int levelId) {
        NavigableSet<UserLevelResult> set = levelTopResults.get(levelId);
        if (set == null) return Collections.emptyList();
        return new ArrayList<>(set);
    }
}
