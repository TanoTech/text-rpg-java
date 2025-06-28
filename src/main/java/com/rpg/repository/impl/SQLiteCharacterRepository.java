package com.rpg.repository.impl;

import com.rpg.character.Character;
import com.rpg.equipment.Equipment;
import com.rpg.equipment.EquipmentSlot;
import com.rpg.equipment.items.Armor;
import com.rpg.equipment.items.Weapon;
import com.rpg.exceptions.DatabaseException;
import com.rpg.exceptions.ExceptionShield;
import com.rpg.logging.GameLogger;
import com.rpg.repository.CharacterRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SQLiteCharacterRepository implements CharacterRepository {
    private static final GameLogger logger = GameLogger.getInstance();
    private static final String DATABASE_DIR = "data";
    private static final String DATABASE_FILE = DATABASE_DIR + "/rpg_game.db";
    private static final String BACKUP_DIR = DATABASE_DIR + "/backups";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            logger.info("SQLite JDBC driver loaded successfully");
        } catch (ClassNotFoundException e) {
            logger.error("Failed to load SQLite JDBC driver", e);
            throw new RuntimeException("SQLite JDBC driver not found", e);
        }
    }

    public SQLiteCharacterRepository() throws DatabaseException {
        ExceptionShield.executeDatabaseOperation(() -> {
            createDataDirectory();
            initializeDatabase();
        }, "repository initialization");
    }

    private void createDataDirectory() throws IOException {
        Path dataDir = Paths.get(DATABASE_DIR);
        Path backupDir = Paths.get(BACKUP_DIR);

        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
            logger.info("Created data directory: " + DATABASE_DIR);
        }

        if (!Files.exists(backupDir)) {
            Files.createDirectories(backupDir);
            logger.info("Created backup directory: " + BACKUP_DIR);
        }
    }

    private void initializeDatabase() throws SQLException {
        try (Connection conn = getConnection()) {
            createTables(conn);
            logger.info("Database initialized successfully");
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + DATABASE_FILE);
    }

    private void createTables(Connection conn) throws SQLException {
        String createCharacterTable = """
                    CREATE TABLE IF NOT EXISTS characters (
                        name TEXT PRIMARY KEY,
                        character_class TEXT NOT NULL,
                        level INTEGER NOT NULL,
                        health INTEGER NOT NULL,
                        max_health INTEGER NOT NULL,
                        attack INTEGER NOT NULL,
                        gold INTEGER NOT NULL,
                        experience INTEGER NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        last_played TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """;

        String createEquipmentTable = """
                    CREATE TABLE IF NOT EXISTS equipment (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        character_name TEXT NOT NULL,
                        slot TEXT NOT NULL,
                        item_name TEXT NOT NULL,
                        item_type TEXT NOT NULL,
                        value INTEGER NOT NULL,
                        attack_bonus INTEGER NOT NULL,
                        defense_bonus INTEGER NOT NULL,
                        FOREIGN KEY (character_name) REFERENCES characters(name) ON DELETE CASCADE,
                        UNIQUE(character_name, slot)
                    )
                """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createCharacterTable);
            stmt.execute(createEquipmentTable);
        }
    }

    @Override
    public void save(Character character) throws DatabaseException {
        ExceptionShield.executeDatabaseOperation(() -> {
            try (Connection conn = getConnection()) {
                conn.setAutoCommit(false);
                try {
                    saveCharacterData(conn, character);
                    saveEquipmentData(conn, character);
                    conn.commit();
                    logger.info("Character saved: " + character.getName());
                } catch (Exception e) {
                    conn.rollback();
                    throw e;
                }
            }
        }, "saving character");
    }

    @Override
    public Optional<Character> findByName(String name) throws DatabaseException {
        return ExceptionShield.executeDatabaseOperationWithReturn(() -> {
            try (Connection conn = getConnection()) {
                Character character = loadCharacterData(conn, name);
                if (character != null) {
                    loadEquipmentData(conn, character);
                    logger.info("Character loaded: " + name);
                    return Optional.of(character);
                }
                return Optional.empty();
            }
        }, "loading character");
    }

    @Override
    public List<String> findAllCharacterNames() throws DatabaseException {
        return ExceptionShield.executeDatabaseOperationWithReturn(() -> {
            List<String> names = new ArrayList<>();
            String selectNames = "SELECT name FROM characters ORDER BY last_played DESC";

            try (Connection conn = getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(selectNames);
                    ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    names.add(rs.getString("name"));
                }
            }
            return names;
        }, "getting all character names");
    }

    @Override
    public boolean deleteByName(String name) throws DatabaseException {
        return ExceptionShield.executeDatabaseOperationWithReturn(() -> {
            String deleteCharacter = "DELETE FROM characters WHERE name = ?";

            try (Connection conn = getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(deleteCharacter)) {

                pstmt.setString(1, name);
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    logger.info("Deleted character: " + name);
                    return true;
                } else {
                    logger.warn("No character found with name: " + name);
                    return false;
                }
            }
        }, "deleting character");
    }

    @Override
    public boolean existsByName(String name) throws DatabaseException {
        return ExceptionShield.executeDatabaseOperationWithReturn(() -> {
            String selectCount = "SELECT COUNT(*) FROM characters WHERE name = ?";

            try (Connection conn = getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(selectCount)) {

                pstmt.setString(1, name);
                try (ResultSet rs = pstmt.executeQuery()) {
                    return rs.next() && rs.getInt(1) > 0;
                }
            }
        }, "checking character existence");
    }

    private void saveCharacterData(Connection conn, Character character) throws SQLException {
        String insertOrUpdateCharacter = """
                    INSERT OR REPLACE INTO characters
                    (name, character_class, level, health, max_health, attack, gold, experience, last_played)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(insertOrUpdateCharacter)) {
            pstmt.setString(1, character.getName());
            pstmt.setString(2, character.getCharacterClass());
            pstmt.setInt(3, character.getLevel());
            pstmt.setInt(4, character.getHealth());
            pstmt.setInt(5, character.getMaxHealth());
            pstmt.setInt(6, character.getAttack());
            pstmt.setInt(7, character.getGold());
            pstmt.setInt(8, character.getExperience());

            pstmt.executeUpdate();
        }
    }

    private void saveEquipmentData(Connection conn, Character character) throws SQLException {
        String deleteEquipment = "DELETE FROM equipment WHERE character_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteEquipment)) {
            pstmt.setString(1, character.getName());
            pstmt.executeUpdate();
        }

        String insertEquipment = """
                    INSERT INTO equipment
                    (character_name, slot, item_name, item_type, value, attack_bonus, defense_bonus)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(insertEquipment)) {
            for (Map.Entry<EquipmentSlot, Equipment> entry : character.getEquipment().entrySet()) {
                Equipment equipment = entry.getValue();

                pstmt.setString(1, character.getName());
                pstmt.setString(2, entry.getKey().name());
                pstmt.setString(3, equipment.getName());
                pstmt.setString(4, equipment.getClass().getSimpleName());
                pstmt.setInt(5, equipment.getValue());
                pstmt.setInt(6, equipment.getAttackBonus());
                pstmt.setInt(7, equipment.getDefenseBonus());

                pstmt.executeUpdate();
            }
        }
    }

    private Character loadCharacterData(Connection conn, String name) throws SQLException {
        String selectCharacter = """
                    SELECT name, character_class, level, health, max_health, attack, gold, experience
                    FROM characters WHERE name = ?
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(selectCharacter)) {
            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Character character = new Character(
                            rs.getString("name"),
                            rs.getString("character_class"),
                            rs.getInt("max_health"),
                            rs.getInt("attack"));

                    character.setHealth(rs.getInt("health"));
                    character.addGold(rs.getInt("gold") - 100);
                    character.addExperience(rs.getInt("experience"));

                    return character;
                }
            }
        }
        return null;
    }

    private void loadEquipmentData(Connection conn, Character character) throws SQLException {
        String selectEquipment = """
                    SELECT slot, item_name, item_type, value, attack_bonus, defense_bonus
                    FROM equipment WHERE character_name = ?
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(selectEquipment)) {
            pstmt.setString(1, character.getName());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    EquipmentSlot slot = EquipmentSlot.fromString(rs.getString("slot"));
                    String itemName = rs.getString("item_name");
                    String itemType = rs.getString("item_type");
                    int value = rs.getInt("value");
                    int attackBonus = rs.getInt("attack_bonus");
                    int defenseBonus = rs.getInt("defense_bonus");

                    Equipment equipment;
                    if ("Weapon".equalsIgnoreCase(itemType)) {
                        equipment = new Weapon(itemName, value, attackBonus);
                    } else {
                        equipment = new Armor(itemName, value, defenseBonus, slot);
                    }

                    character.equipItem(equipment);
                }
            }
        }
    }
}