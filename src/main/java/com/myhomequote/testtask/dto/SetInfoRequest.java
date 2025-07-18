package com.myhomequote.testtask.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetInfoRequest {

    @JsonProperty("user_id")
    private int userId;
    @JsonProperty("level_id")
    private int levelId;
    private int result;

}
