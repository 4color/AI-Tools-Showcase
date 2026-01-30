package com.aitools.backend.service;

import com.aitools.backend.entity.Favorite;
import com.aitools.backend.mapper.FavoriteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteMapper favoriteMapper;

    public Integer getFavoriteCount(String entityType, Long entityId) {
        return favoriteMapper.countByEntity(entityType, entityId);
    }

    public boolean isFavoritedByUser(Long userId, String entityType, Long entityId) {
        return favoriteMapper.countByUserAndEntity(userId, entityType, entityId) > 0;
    }

    public boolean toggleFavorite(Long userId, String entityType, Long entityId) {
        Favorite existingFavorite = favoriteMapper.selectList(null).stream()
                .filter(f -> userId.equals(f.getUserId()) && entityType.equals(f.getEntityType()) && entityId.equals(f.getEntityId()))
                .findFirst().orElse(null);

        if (existingFavorite != null) {
            favoriteMapper.deleteById(existingFavorite.getId());
            return false; // unfavorited
        } else {
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setEntityType(entityType);
            favorite.setEntityId(entityId);
            favoriteMapper.insert(favorite);
            return true; // favorited
        }
    }

    public List<Favorite> getUserFavorites(Long userId) {
        return favoriteMapper.findByUserId(userId);
    }
}