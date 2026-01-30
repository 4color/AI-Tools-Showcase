package com.aitools.backend.controller;

import com.aitools.backend.entity.Tag;
import com.aitools.backend.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<?> getAllTags(@RequestParam(required = false) String type,
                                        @RequestParam(required = false, defaultValue = "false") boolean namesOnly) {
        // 复用同一个接口：namesOnly=true 时返回去重后的 tag name 列表
        if (namesOnly) {
            if (type == null || type.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("type is required when namesOnly=true");
            }
            return ResponseEntity.ok(tagService.getTagNamesByType(type));
        }
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tag> getTagById(@PathVariable Long id) {
        return tagService.getTagById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag) {
        return ResponseEntity.ok(tagService.saveTag(tag));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tag> updateTag(@PathVariable Long id, @RequestBody Tag tag) {
        tag.setId(id);
        return ResponseEntity.ok(tagService.saveTag(tag));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.ok().build();
    }
}