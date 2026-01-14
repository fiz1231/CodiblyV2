package com.demo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;

import com.demo.service.Facade;

import jakarta.websocket.server.PathParam;

import com.demo.dao.DataDao;

import com.demo.dao.generationData.GenerationOutput;
import com.demo.dao.windonData.WindonDataOutput;

@RestController
@RequestMapping("/v1/codibly")
@AllArgsConstructor
public class DataController {
    private final Facade facade;

    @CrossOrigin
    @GetMapping("/getData")
    public ResponseEntity<? super DataDao> getDataOnEnergyMix(){

        GenerationOutput body = facade.getData();
        return ResponseEntity.ok(body);
    }

    
    @CrossOrigin
    @GetMapping("/getLoadingWindow/{timeWindow}")
    public ResponseEntity<? super DataDao> getLoadingWindow(@PathVariable(name = "timeWindow") Integer timeWindow){
        WindonDataOutput body = facade.getLoadinfWindown(timeWindow.intValue());
        return ResponseEntity.ok(body);
    }
}
