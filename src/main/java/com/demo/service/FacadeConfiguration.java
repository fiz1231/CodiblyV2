package com.demo.service;

import com.demo.api.ApiRepository;

public class FacadeConfiguration {
    public static Facade createCodiblyFacade(final ApiRepository repository){
        return new Facade(new DataGetterExternalApi(repository),new DataLoadingWindowDeterminer(repository));
    }
}
