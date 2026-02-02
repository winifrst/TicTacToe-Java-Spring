package org.tictactoe.datasource.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.tictactoe.datasource.model.GameEntity;
import org.tictactoe.domain.model.GameStatus;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface GameRepository extends CrudRepository<GameEntity, UUID> {
    List<GameEntity> findByStatus(GameStatus status);

    List<GameEntity> findByPlayerXIdOrPlayerOId(UUID playerXId, UUID playerOId);

    // Опиши запрос базы данных для получения всех завершенных игр по UUID пользователя
    @Query("SELECT g FROM GameEntity g WHERE " +
            "(g.playerXId = :userId OR g.playerOId = :userId) AND " +
            "(g.status = 'PLAYER_X_WON' OR g.status = 'PLAYER_O_WON' OR g.status = 'DRAW') " +
            "ORDER BY g.createdAt DESC")
    List<GameEntity> findFinishedGamesByUserId(@Param("userId") UUID userId);

    // Запрос для статистики пользователя
    @Query("SELECT COUNT(g) FROM GameEntity g WHERE " +
            "(g.playerXId = :userId OR g.playerOId = :userId) AND " +
            "(g.status = 'PLAYER_X_WON' OR g.status = 'PLAYER_O_WON' OR g.status = 'DRAW')")
    int countFinishedGamesByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(g) FROM GameEntity g WHERE " +
            "((g.playerXId = :userId AND g.status = 'PLAYER_X_WON') OR " +
            "(g.playerOId = :userId AND g.status = 'PLAYER_O_WON'))")
    int countWinsByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(g) FROM GameEntity g WHERE " +
            "((g.playerXId = :userId AND g.status = 'PLAYER_O_WON') OR " +
            "(g.playerOId = :userId AND g.status = 'PLAYER_X_WON'))")
    int countLossesByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(g) FROM GameEntity g WHERE " +
            "g.status = 'DRAW' AND (g.playerXId = :userId OR g.playerOId = :userId)")
    int countDrawsByUserId(@Param("userId") UUID userId);

    // Опиши запрос базы данных n лидеров
    @Query(value = """
        SELECT u.id as userId, u.username, 
               COUNT(CASE 
                   WHEN (g.player_x_id = u.id AND g.status = 'PLAYER_X_WON') OR 
                        (g.player_o_id = u.id AND g.status = 'PLAYER_O_WON') 
                   THEN 1 END) as wins,
               COUNT(CASE 
                   WHEN (g.player_x_id = u.id AND g.status = 'PLAYER_O_WON') OR 
                        (g.player_o_id = u.id AND g.status = 'PLAYER_X_WON') 
                   THEN 1 END) as losses,
               COUNT(CASE 
                   WHEN g.status = 'DRAW' AND 
                        (g.player_x_id = u.id OR g.player_o_id = u.id) 
                   THEN 1 END) as draws
        FROM users u
        LEFT JOIN games g ON (g.player_x_id = u.id OR g.player_o_id = u.id) 
            AND (g.status = 'PLAYER_X_WON' OR g.status = 'PLAYER_O_WON' OR g.status = 'DRAW')
        GROUP BY u.id, u.username
        HAVING COUNT(g.id) > 0
        ORDER BY wins DESC, losses ASC, draws ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findTopPlayers(@Param("limit") int limit);
}