package studio.one.application.document.persistence.jdbc;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import studio.one.application.document.command.DeleteBlockCommand;
import studio.one.application.document.domain.exception.DocumentNotFoundException;

class DocumentDaoJdbcSecurityTest {

    @Test
    void deleteBlockRejectsBlockFromAnotherDocument() {
        NamedParameterJdbcTemplate jdbc = mock(NamedParameterJdbcTemplate.class);
        DocumentDaoJdbc dao = new DocumentDaoJdbc(jdbc);
        ReflectionTestUtils.setField(dao, "selectBlockMetaSql", "SELECT_BLOCK_META");

        Map<String, Object> meta = Map.of(
                "DOCUMENT_ID", 999L,
                "SORT_ORDER", 1,
                "PARENT_BLOCK_ID", 10L,
                "BLOCK_TYPE", "text",
                "BLOCK_DATA", "payload",
                "UPDATED_AT", OffsetDateTime.now());

        when(jdbc.queryForMap(eq("SELECT_BLOCK_META"), anyMap())).thenReturn(meta);

        DeleteBlockCommand cmd = new DeleteBlockCommand(100L, 200L, 7L, OffsetDateTime.now());

        assertThrows(DocumentNotFoundException.class, () -> dao.deleteBlock(cmd));

        verify(jdbc).queryForMap(eq("SELECT_BLOCK_META"), anyMap());
        verifyNoMoreInteractions(jdbc);
    }
}
