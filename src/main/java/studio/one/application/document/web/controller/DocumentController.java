package studio.one.application.document.web.controller;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import studio.one.application.document.command.CreateBlockCommand;
import studio.one.application.document.command.CreateDocumentCommand;
import studio.one.application.document.command.CreateVersionCommand;
import studio.one.application.document.command.DeleteBlockCommand;
import studio.one.application.document.command.DeleteDocumentCommand;
import studio.one.application.document.command.MoveBlockCommand;
import studio.one.application.document.command.UpdateBlockCommand;
import studio.one.application.document.command.UpdateDocumentMetaCommand;
import studio.one.application.document.domain.model.DocumentBlock;
import studio.one.application.document.domain.model.Document;
import studio.one.application.document.domain.model.DocumentVersion;
import studio.one.application.document.domain.model.DocumentVersionBundle;
import studio.one.application.document.service.DocumentService;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService service;

    public static class CreateDocumentRequest {
        @NotNull
        public Integer objectType;
        @NotNull
        public Long objectId;
        public Long parentDocumentId;
        public Integer sortOrder;
        @NotBlank
        public String name;
        @NotBlank
        public String title;
        @NotNull
        public Integer bodyType;
        @NotNull
        public String bodyText;
        public Map<String, String> properties;
    }

    public static class CreateVersionRequest {
        @NotBlank
        public String title;
        @NotNull
        public Integer bodyType;
        @NotNull
        public String bodyText;
        public Map<String, String> properties;
    }

    public static class UpdateMetaRequest {
        @NotBlank
        public String name;
        public String pattern;
    }

    public static class CreateBlockRequest {
        public Long parentBlockId;
        @NotBlank
        public String blockType;
        public String blockData;
        public Integer sortOrder;
    }

    public static class UpdateBlockRequest {
        public Long parentBlockId;
        @NotBlank
        public String blockType;
        public String blockData;
        public Integer sortOrder;
    }

    public static class MoveBlockRequest {
        public Long parentBlockId;
        public Integer sortOrder;
    }

    public static class DocumentBlockNode {
        public final DocumentBlock block;
        public final List<DocumentBlockNode> children = new ArrayList<>();

        public DocumentBlockNode(DocumentBlock block) {
            this.block = block;
        }
    }

    @PostMapping
    @PreAuthorize("@endpointAuthz.can('features:document','create')")
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CreateDocumentRequest req,
            @AuthenticationPrincipal(expression = "userId") Long userId) {
        long id = service.create(new CreateDocumentCommand(
                req.objectType, req.objectId, req.parentDocumentId, req.sortOrder, req.name, req.title,
                req.bodyText, req.bodyType, req.properties, requireUserId(userId)));
        return ResponseEntity.ok(Map.of("documentId", id, "versionId", 1));
    }

    @PostMapping("/{documentId}/versions")
    @PreAuthorize("@endpointAuthz.can('features:document','write')")
    public ResponseEntity<Map<String, Object>> newVersion(@PathVariable long documentId,
            @Valid @RequestBody CreateVersionRequest req,
            @AuthenticationPrincipal(expression = "userId") Long userId) {
        int v = service.newVersion(documentId, new CreateVersionCommand(
                req.title, req.bodyText, req.bodyType, req.properties, requireUserId(userId)));
        return ResponseEntity.ok(Map.of("documentId", documentId, "versionId", v));
    }

    @GetMapping("/{documentId}")
    @PreAuthorize("@endpointAuthz.can('features:document','read')")
    public ResponseEntity<DocumentVersionBundle> latest(@PathVariable long documentId) {
        return withEtag(service.getLatest(documentId));
    }

    @GetMapping("/{documentId}/versions/{versionId}")
    @PreAuthorize("@endpointAuthz.can('features:document','read')")
    public ResponseEntity<DocumentVersionBundle> version(@PathVariable long documentId, @PathVariable int versionId) {
        return withEtag(service.getVersion(documentId, versionId));
    }

    @PutMapping("/{documentId}/meta")
    @PreAuthorize("@endpointAuthz.can('features:document','write')")
    public ResponseEntity<Void> updateMeta(@PathVariable long documentId,
            @Valid @RequestBody UpdateMetaRequest req,
            @RequestHeader(value = "If-Match", required = false) String ifMatch,
            @AuthenticationPrincipal(expression = "userId") Long userId) {
        service.updateMeta(new UpdateDocumentMetaCommand(
                documentId, req.name, req.pattern, requireUserId(userId), parseIfMatch(ifMatch)));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{documentId}/blocks")
    @PreAuthorize("@endpointAuthz.can('features:document','write')")
    public ResponseEntity<Map<String, Object>> createBlock(@PathVariable long documentId,
            @Valid @RequestBody CreateBlockRequest req,
            @AuthenticationPrincipal(expression = "userId") Long userId) {
        long blockId = service.createBlock(new CreateBlockCommand(
                documentId, req.parentBlockId, req.blockType, req.blockData, req.sortOrder, requireUserId(userId)));
        return ResponseEntity.ok(Map.of("blockId", blockId));
    }

    @PutMapping("/{documentId}/blocks/{blockId}")
    @PreAuthorize("@endpointAuthz.can('features:document','write')")
    public ResponseEntity<Void> updateBlock(@PathVariable long documentId,
            @PathVariable long blockId,
            @Valid @RequestBody UpdateBlockRequest req,
            @RequestHeader(value = "If-Match", required = false) String ifMatch,
            @AuthenticationPrincipal(expression = "userId") Long userId) {
        service.updateBlock(new UpdateBlockCommand(
                blockId, req.parentBlockId, req.blockType, req.blockData, req.sortOrder,
                requireUserId(userId), parseIfMatch(ifMatch)));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{documentId}/blocks/{blockId}/move")
    @PreAuthorize("@endpointAuthz.can('features:document','write')")
    public ResponseEntity<Void> moveBlock(@PathVariable long documentId,
            @PathVariable long blockId,
            @Valid @RequestBody MoveBlockRequest req,
            @RequestHeader(value = "If-Match", required = false) String ifMatch,
            @AuthenticationPrincipal(expression = "userId") Long userId) {
        service.moveBlock(new MoveBlockCommand(
                blockId, req.parentBlockId, req.sortOrder, requireUserId(userId), parseIfMatch(ifMatch)));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{documentId}/blocks/{blockId}")
    @PreAuthorize("@endpointAuthz.can('features:document','delete')")
    public ResponseEntity<Void> deleteBlock(@PathVariable long documentId,
            @PathVariable long blockId,
            @RequestHeader(value = "If-Match", required = false) String ifMatch,
            @AuthenticationPrincipal(expression = "userId") Long userId) {
        service.deleteBlock(new DeleteBlockCommand(blockId, requireUserId(userId), parseIfMatch(ifMatch)));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{documentId}/blocks")
    @PreAuthorize("@endpointAuthz.can('features:document','read')")
    public ResponseEntity<List<DocumentBlock>> listBlocks(@PathVariable long documentId) {
        List<DocumentBlock> blocks = service.listBlocks(documentId);
        return withEtag(blocks);
    }

    @GetMapping("/{documentId}/versions/{versionId}/blocks")
    @PreAuthorize("@endpointAuthz.can('features:document','read')")
    public ResponseEntity<List<DocumentBlock>> listBlocksByVersion(@PathVariable long documentId,
            @PathVariable int versionId,
            @RequestParam(name = "includeDeleted", defaultValue = "false") boolean includeDeleted,
            @RequestParam(name = "parentBlockId", required = false) Long parentBlockId) {
        List<DocumentBlock> blocks;
        if (includeDeleted) {
            blocks = filterByParent(service.listBlocksIncludingDeleted(documentId, versionId), parentBlockId);
        } else {
            blocks = filterByParent(service.listBlocks(documentId, versionId), parentBlockId);
        }
        return withEtag(blocks);
    }

    @GetMapping("/{documentId}/blocks/tree")
    @PreAuthorize("@endpointAuthz.can('features:document','read')")
    public ResponseEntity<List<DocumentBlockNode>> listBlocksTree(@PathVariable long documentId,
            @RequestParam(name = "versionId", required = false) Integer versionId,
            @RequestParam(name = "includeDeleted", defaultValue = "false") boolean includeDeleted) {
        List<DocumentBlock> blocks = versionId == null
                ? service.listBlocks(documentId)
                : (includeDeleted
                    ? service.listBlocksIncludingDeleted(documentId, versionId)
                    : service.listBlocks(documentId, versionId));
        List<DocumentBlockNode> tree = buildTree(blocks);
        return ResponseEntity.ok(tree);
    }

    @DeleteMapping("/{documentId}")
    @PreAuthorize("@endpointAuthz.can('features:document','delete')")
    public ResponseEntity<Void> delete(@PathVariable long documentId,
            @RequestHeader(value = "If-Match", required = false) String ifMatch) {
        service.delete(new DeleteDocumentCommand(documentId, parseIfMatch(ifMatch)));
        return ResponseEntity.noContent().build();
    }

    private long requireUserId(Long userId) {
        if (userId == null) {
            throw new AccessDeniedException("principal userId required");
        }
        return userId;
    }

    private OffsetDateTime parseIfMatch(String ifMatch) {
        if (ifMatch == null || ifMatch.isBlank()) {
            return null;
        }
        String value = ifMatch.trim();
        if (value.startsWith("\"") && value.endsWith("\"") && value.length() > 1) {
            value = value.substring(1, value.length() - 1);
        }
        try {
            return OffsetDateTime.parse(value);
        } catch (java.time.format.DateTimeParseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid If-Match header");
        }
    }

    private List<DocumentBlock> filterByParent(List<DocumentBlock> blocks, Long parentBlockId) {
        if (parentBlockId == null) {
            return blocks;
        }
        return blocks.stream()
                .filter(block -> Objects.equals(block.getParentBlockId(), parentBlockId))
                .collect(java.util.stream.Collectors.toList());
    }

    private ResponseEntity<List<DocumentBlock>> withEtag(List<DocumentBlock> blocks) {
        OffsetDateTime latest = null;
        for (DocumentBlock block : blocks) {
            OffsetDateTime candidate = block.getUpdatedAt() != null ? block.getUpdatedAt() : block.getCreatedAt();
            if (candidate == null) {
                continue;
            }
            if (latest == null || candidate.isAfter(latest)) {
                latest = candidate;
            }
        }
        if (latest == null) {
            return ResponseEntity.ok(blocks);
        }
        return ResponseEntity.ok().eTag("\"" + latest.toString() + "\"").body(blocks);
    }

    private ResponseEntity<DocumentVersionBundle> withEtag(DocumentVersionBundle bundle) {
        OffsetDateTime latest = latestUpdatedAt(bundle);
        if (latest == null) {
            return ResponseEntity.ok(bundle);
        }
        return ResponseEntity.ok().eTag("\"" + latest.toString() + "\"").body(bundle);
    }

    private OffsetDateTime latestUpdatedAt(DocumentVersionBundle bundle) {
        if (bundle == null) {
            return null;
        }
        Document doc = bundle.getDocument();
        DocumentVersion ver = bundle.getVersion();
        OffsetDateTime docTime = doc == null ? null : Optional.ofNullable(doc.getUpdatedAt()).orElse(doc.getCreatedAt());
        OffsetDateTime verTime = ver == null ? null : Optional.ofNullable(ver.getUpdatedAt()).orElse(ver.getCreatedAt());
        if (docTime == null) {
            return verTime;
        }
        if (verTime == null) {
            return docTime;
        }
        return docTime.isAfter(verTime) ? docTime : verTime;
    }

    private List<DocumentBlockNode> buildTree(List<DocumentBlock> blocks) {
        Map<Long, DocumentBlockNode> nodes = new HashMap<>();
        List<DocumentBlockNode> roots = new ArrayList<>();
        for (DocumentBlock block : blocks) {
            nodes.put(block.getBlockId(), new DocumentBlockNode(block));
        }
        for (DocumentBlock block : blocks) {
            DocumentBlockNode node = nodes.get(block.getBlockId());
            Long parentId = block.getParentBlockId();
            if (parentId == null || !nodes.containsKey(parentId)) {
                roots.add(node);
                continue;
            }
            nodes.get(parentId).children.add(node);
        }
        sortTree(roots);
        return roots;
    }

    private void sortTree(List<DocumentBlockNode> nodes) {
        nodes.sort((a, b) -> compareBlocks(a.block, b.block));
        for (DocumentBlockNode node : nodes) {
            if (!node.children.isEmpty()) {
                sortTree(node.children);
            }
        }
    }

    private int compareBlocks(DocumentBlock a, DocumentBlock b) {
        Integer sa = a.getSortOrder();
        Integer sb = b.getSortOrder();
        if (sa == null && sb == null) {
            return Long.compare(a.getBlockId(), b.getBlockId());
        }
        if (sa == null) {
            return 1;
        }
        if (sb == null) {
            return -1;
        }
        int cmp = Integer.compare(sa, sb);
        return cmp != 0 ? cmp : Long.compare(a.getBlockId(), b.getBlockId());
    }
}
