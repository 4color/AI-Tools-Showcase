package com.aitools.backend.controller;

import com.aitools.backend.entity.Favorite;
import com.aitools.backend.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping("/{entityType}/{entityId}/count")
    public ResponseEntity<Integer> getFavoriteCount(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        return ResponseEntity.ok(favoriteService.getFavoriteCount(entityType, entityId));
    }

    @GetMapping("/{entityType}/{entityId}/status")
    public ResponseEntity<Boolean> isFavorited(
            @PathVariable String entityType,
            @PathVariable Long entityId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(favoriteService.isFavoritedByUser(userId, entityType, entityId));
    }

    @PostMapping("/{entityType}/{entityId}")
    public ResponseEntity<Boolean> toggleFavorite(
            @PathVariable String entityType,
            @PathVariable Long entityId,
            @RequestParam Long userId) {
        boolean favorited = favoriteService.toggleFavorite(userId, entityType, entityId);
        return ResponseEntity.ok(favorited);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Favorite>> getUserFavorites(@PathVariable Long userId) {
        return ResponseEntity.ok(favoriteService.getUserFavorites(userId));
    }
}