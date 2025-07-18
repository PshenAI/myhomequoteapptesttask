package com.myhomequote.testtask.service;

import com.myhomequote.testtask.dto.ResultDTO;
import com.myhomequote.testtask.dto.SetInfoRequest;
import com.myhomequote.testtask.storage.InMemoryResultStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResultService {

    private final InMemoryResultStorage store;

    public void setInfo(SetInfoRequest request) {
        store.updateResult(request.getUserId(), request.getLevelId(), request.getResult());
        log.info("User: {}, Level: {}, Result: {}. Data's successfully stored", request.getUserId(),
                request.getLevelId(), request.getResult());
    }

    public List<ResultDTO> getUserInfo(int userId) {
        return store.getUserTopResults(userId).stream()
                .map(r -> new ResultDTO(r.getUserId(), r.getLevelId(), r.getResult()))
                .collect(Collectors.toList());
    }

    public List<ResultDTO> getLevelInfo(int levelId) {
        return store.getLevelTopResults(levelId).stream()
                .map(r -> new ResultDTO(r.getUserId(), r.getLevelId(), r.getResult()))
                .collect(Collectors.toList());
    }
}
