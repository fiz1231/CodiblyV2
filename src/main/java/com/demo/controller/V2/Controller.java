package com.demo.controller.V2;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;

import com.demo.service.serviceV2.Facade;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import com.demo.dao.V2.Endpoint1;


@RestController
@RequestMapping("/v2/codibly")
@AllArgsConstructor
@ComponentScan("com.demo.service.serviceV2")
@CrossOrigin(origins = "${cors.allowed.origins}", maxAge = 3600)
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
    public ResponseEntity<Endpoint1> getLoadingWindow(@PathVariable(name = "timeWindow")@Min(1) @Max(6) int timeWindow){
        Endpoint1 body = facade.getLoadinfWindown(timeWindow);
        return ResponseEntity.ok(body);
    }
}
