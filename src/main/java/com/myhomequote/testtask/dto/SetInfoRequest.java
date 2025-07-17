package com.myhomequote.testtask.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetInfoRequest {

    private int userId;
    private int levelId;
    private int result;

}
