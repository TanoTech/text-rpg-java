package com.rpg.service;

import com.rpg.exceptions.GameException;
import com.rpg.repository.CharacterRepository;
import com.rpg.repository.impl.SQLiteCharacterRepository;
import com.rpg.service.impl.CharacterServiceImpl;
import com.rpg.service.impl.GameServiceImpl;
import com.rpg.service.impl.ShopServiceImpl;

public class ServiceFactory {
    private static ServiceFactory instance;
    private CharacterService characterService;
    private GameService gameService;
    private ShopService shopService;

    private ServiceFactory() throws GameException {
        initializeServices();
    }

    public static ServiceFactory getInstance() throws GameException {
        if (instance == null) {
            instance = new ServiceFactory();
        }
        return instance;
    }

    private void initializeServices() throws GameException {
        CharacterRepository characterRepository = new SQLiteCharacterRepository();
        this.characterService = new CharacterServiceImpl(characterRepository);
        this.shopService = new ShopServiceImpl();
        this.gameService = new GameServiceImpl();
    }

    public CharacterService getCharacterService() {
        return characterService;
    }

    public GameService getGameService() {
        return gameService;
    }

    public ShopService getShopService() {
        return shopService;
    }
}