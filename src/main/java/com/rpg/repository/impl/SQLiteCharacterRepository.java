package com.rpg.repository.impl;

import com.rpg.character.Character;
import com.rpg.equipment.Equipment;
import com.rpg.equipment.EquipmentSlot;
import com.rpg.equipment.items.Armor;
import com.rpg.equipment.items.Weapon;
import com.rpg.exceptions.DatabaseException;
import com.rpg.repository.CharacterRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SQLiteCharacterRepository implements CharacterRepository {
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/sqlite/rpg_game.db";

    private static final String CREATE_CHARACTER_TABLE = """
            CREATE TABLE IF NOT EXISTS characters (
                name TEXT PRIMARY KEY,
                character_class TEXT NOT NULL,
                level INTEGER NOT NULL,
                health INTEGER NOT NULL,
                base_health INTEGER NOT NULL,
                base_attack INTEGER NOT NULL,
                gold INTEGER NOT NULL,
                experience INTEGER NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                last_played TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )""";

    private static final String CREATE_EQUIPMENT_TABLE = """
            CREATE TABLE IF NOT EXISTS equipment (
                character_name TEXT NOT NULL,
                slot TEXT NOT NULL,
                item_name TEXT NOT NULL,
                item_type TEXT NOT NULL,
                value INTEGER NOT NULL,
                attack_bonus INTEGER NOT NULL,
                defense_bonus INTEGER NOT NULL,
                PRIMARY KEY (character_name, slot),
                FOREIGN KEY (character_name) REFERENCES characters(name) ON DELETE CASCADE
            )""";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found", e);
        }
    }

    public SQLiteCharacterRepository() throws DatabaseException {
        try {
            createDirectories();
            initializeDatabase();
        } catch (Exception e) {
            throw new DatabaseException("Failed to initialize repository", e);
        }
    }

    private void createDirectories() throws IOException {
        Files.createDirectories(Paths.get("src/main/resources/sqlite"));
    }

    private void initializeDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_CHARACTER_TABLE);
            stmt.execute(CREATE_EQUIPMENT_TABLE);
        }
    }

    @Override
    public void save(Character character) throws DatabaseException {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);
            try {
                saveCharacter(conn, character);
                saveEquipment(conn, character);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save character: " + character.getName(), e);
        }
    }

    @Override
    public Optional<Character> findByName(String name) throws DatabaseException {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Character character = loadCharacter(conn, name);
            if (character != null) {
                loadEquipment(conn, character);
                return Optional.of(character);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load character: " + name, e);
        }
    }

    @Override
    public List<String> findAllCharacterNames() throws DatabaseException {
        List<String> names = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT name FROM characters ORDER BY last_played DESC")) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    names.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get character names", e);
        }
        return names;
    }

    @Override
    public boolean deleteByName(String name) throws DatabaseException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM characters WHERE name = ?")) {

            stmt.setString(1, name);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete character: " + name, e);
        }
    }

    @Override
    public boolean existsByName(String name) throws DatabaseException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM characters WHERE name = ? LIMIT 1")) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check character existence: " + name, e);
        }
    }

    private void saveCharacter(Connection conn, Character character) throws SQLException {
        String sql = """
                INSERT OR REPLACE INTO characters
                (name, character_class, level, health, base_health, base_attack, gold, experience, last_played)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)""";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, character.getName());
            stmt.setString(2, character.getCharacterClass());
            stmt.setInt(3, character.getLevel());
            stmt.setInt(4, character.getHealth());
            stmt.setInt(5, character.getMaxHealth() - (character.getLevel() - 1) * 10); // base health
            stmt.setInt(6, character.getAttack() - (character.getLevel() - 1) * 2); // base attack
            stmt.setInt(7, character.getGold());
            stmt.setInt(8, character.getExperience());
            stmt.executeUpdate();
        }
    }

    private void saveEquipment(Connection conn, Character character) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM equipment WHERE character_name = ?")) {
            stmt.setString(1, character.getName());
            stmt.executeUpdate();
        }

        String sql = """
                INSERT INTO equipment (character_name, slot, item_name, item_type, value, attack_bonus, defense_bonus)
                VALUES (?, ?, ?, ?, ?, ?, ?)""";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (var entry : character.getEquipment().entrySet()) {
                Equipment equipment = entry.getValue();
                stmt.setString(1, character.getName());
                stmt.setString(2, entry.getKey().name());
                stmt.setString(3, equipment.getName());
                stmt.setString(4, equipment.getClass().getSimpleName());
                stmt.setInt(5, equipment.getValue());
                stmt.setInt(6, equipment.getAttackBonus());
                stmt.setInt(7, equipment.getDefenseBonus());
                stmt.executeUpdate();
            }
        }
    }

    private Character loadCharacter(Connection conn, String name) throws SQLException {
        String sql = """
                SELECT name, character_class, level, health, base_health, base_attack, gold, experience
                FROM characters WHERE name = ?""";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Character character = new Character(
                            rs.getString("name"),
                            rs.getString("character_class"),
                            rs.getInt("base_health"),
                            rs.getInt("base_attack"));

                    character.setHealth(rs.getInt("health"));
                    character.addGold(rs.getInt("gold") - 100);
                    character.addExperience(rs.getInt("experience"));

                    return character;
                }
            }
        }
        return null;
    }

    private void loadEquipment(Connection conn, Character character) throws SQLException {
        String sql = "SELECT slot, item_name, item_type, value, attack_bonus, defense_bonus FROM equipment WHERE character_name = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, character.getName());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    EquipmentSlot slot = EquipmentSlot.valueOf(rs.getString("slot"));
                    String itemName = rs.getString("item_name");
                    String itemType = rs.getString("item_type");
                    int value = rs.getInt("value");
                    int attackBonus = rs.getInt("attack_bonus");
                    int defenseBonus = rs.getInt("defense_bonus");

                    Equipment equipment = "Weapon".equals(itemType)
                            ? new Weapon(itemName, value, attackBonus)
                            : new Armor(itemName, value, defenseBonus, slot);

                    character.equipItem(equipment);
                }
            }
        }
    }
}
