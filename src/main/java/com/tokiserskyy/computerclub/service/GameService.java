package com.tokiserskyy.computerclub.service;

import com.tokiserskyy.computerclub.model.Computer;
import com.tokiserskyy.computerclub.model.Game;
import com.tokiserskyy.computerclub.model.Person;
import com.tokiserskyy.computerclub.repository.ComputerRepository;
import com.tokiserskyy.computerclub.repository.GameRepository;
import com.tokiserskyy.computerclub.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {
    @Autowired
    GameRepository gameRepository;
    @Autowired
    private ComputerRepository computerRepository;
    @Autowired
    private PersonRepository personRepository;

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public List<Game> findByGamesByGenre(String genre) {
        return gameRepository.getGamesByGenre(genre);
    }

    public Game getGameById(int id) {
        return gameRepository.findById(id).orElse(null);
    }

    @Transactional
    public Game addGame(Game game) {
        return gameRepository.save(game);
    }

    @Transactional
    public Game addGameOnComputer(int gameId,int computerId) {
        Game game = getGameById(gameId);
        Computer computer = computerRepository.getComputersById(computerId);
        game.getComputers().add(computer);
        computer.getGames().add(game);
        return gameRepository.save(game);
    }

    @Transactional
    public Game addFavouriteGame(int gameId,int userId) {
        Game game = getGameById(gameId);
        Person person = personRepository.getById(userId);
        game.getPersons().add(person);
        person.getFavoriteGames().add(game);
        return gameRepository.save(game);
    }

    public Game updateGame(int id, Game gameDetails) {
        Game game = getGameById(id);
        if (game != null) {
            game.setName(gameDetails.getName());
            game.setGenre(gameDetails.getGenre());
            return gameRepository.save(game);
        }
        return null;
    }

    @Transactional
    public void deleteGameById(int id) {
        gameRepository.deleteById(id);
    }
}
