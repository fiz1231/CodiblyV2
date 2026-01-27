package com.demo.controller.V2;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;

import com.demo.service.serviceV2.Facade;


import com.demo.dao.V2.Endpoint1;


@RestController
@RequestMapping("/v1/codibly")
@AllArgsConstructor
public class Controller {
    private final Facade facade;

    @CrossOrigin
    @GetMapping("/getData")
    public ResponseEntity<List<Endpoint1>> getDataOnEnergyMix(){

        List<Endpoint1> body = facade.getaverageSharesForDays();
        return ResponseEntity.ok(body);
    }

    
    @CrossOrigin
    @GetMapping("/getLoadingWindow/{timeWindow}")
    public ResponseEntity<Endpoint1> getLoadingWindow(@PathVariable(name = "timeWindow") Integer timeWindow){
        Endpoint1 body = facade.getLoadinfWindown(timeWindow.intValue());
        return ResponseEntity.ok(body);
    }
}
