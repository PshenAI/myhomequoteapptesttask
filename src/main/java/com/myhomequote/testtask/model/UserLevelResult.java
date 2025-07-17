package com.myhomequote.testtask.model;

import lombok.Data;

@Data
public class UserLevelResult implements Comparable<UserLevelResult> {

    private final int userId;
    private final int levelId;
    private final int result;

    @Override
    public int compareTo(UserLevelResult o) {
        int res = Integer.compare(o.result, this.result);
        if(res != 0) {
            return res;
        }

        res = Integer.compare(o.userId, this.userId);
        if(res != 0) {
            return res;
        }

        return Integer.compare(o.levelId, this.levelId);
    }
}
