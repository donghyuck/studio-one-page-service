package studio.one.application.document.web.controller;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import studio.one.application.document.command.DeleteBlockCommand;
import studio.one.application.document.command.UpdateBlockCommand;
import studio.one.application.document.service.DocumentService;
import studio.one.application.document.web.DocumentExceptionHandler;

class MgmtDocumentControllerSecurityTest {

    private MockMvc mockMvc;
    private DocumentService service;

    @BeforeEach
    void setUp() {
        service = org.mockito.Mockito.mock(DocumentService.class);
        MgmtDocumentController controller = new MgmtDocumentController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new DocumentExceptionHandler())
                .addPlaceholderValue("studio.one.features.document.web.mgmt-base-path", "/api/mgmt/documents")
                .apply(springSecurity())
                .build();
    }

    @Test
    void updateBlockBindsDocumentIdIntoCommand() throws Exception {
        mockMvc.perform(put("/api/mgmt/documents/100/blocks/200")
                        .with(authentication(auth(7L)))
                        .contentType("application/json")
                        .content("{\"blockType\":\"text\",\"blockData\":\"payload\",\"sortOrder\":3}"))
                .andExpect(status().isNoContent());

        ArgumentCaptor<UpdateBlockCommand> captor = ArgumentCaptor.forClass(UpdateBlockCommand.class);
        verify(service).updateBlock(captor.capture());

        UpdateBlockCommand command = captor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals(100L, command.getDocumentId());
        org.junit.jupiter.api.Assertions.assertEquals(200L, command.getBlockId());
        org.junit.jupiter.api.Assertions.assertEquals(7L, command.getActorUserId());
    }

    @Test
    void deleteBlockBindsDocumentIdIntoCommand() throws Exception {
        mockMvc.perform(delete("/api/mgmt/documents/101/blocks/202")
                        .with(authentication(auth(9L))))
                .andExpect(status().isNoContent());

        ArgumentCaptor<DeleteBlockCommand> captor = ArgumentCaptor.forClass(DeleteBlockCommand.class);
        verify(service).deleteBlock(captor.capture());

        DeleteBlockCommand command = captor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals(101L, command.getDocumentId());
        org.junit.jupiter.api.Assertions.assertEquals(202L, command.getBlockId());
        org.junit.jupiter.api.Assertions.assertEquals(9L, command.getActorUserId());
    }

    @Test
    void invalidRequestResponseDoesNotLeakInternalDetails() throws Exception {
        doThrow(new NoSuchElementException("block not found: 999"))
                .when(service).deleteBlock(any(DeleteBlockCommand.class));

        mockMvc.perform(delete("/api/mgmt/documents/101/blocks/202")
                        .with(authentication(auth(9L))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.message").value("Invalid request."))
                .andExpect(jsonPath("$.message", not(containsString("999"))));
    }

    @Test
    void createRejectsTooManyProperties() throws Exception {
        StringBuilder body = new StringBuilder();
        body.append("{\"objectType\":1,\"objectId\":1,\"name\":\"doc\",\"title\":\"title\",\"bodyType\":1,\"bodyText\":\"body\",\"properties\":{");
        for (int i = 0; i < 51; i++) {
            if (i > 0) {
                body.append(',');
            }
            body.append("\"k").append(i).append("\":\"v\"");
        }
        body.append("}}");

        mockMvc.perform(post("/api/mgmt/documents")
                        .with(authentication(auth(7L)))
                        .contentType("application/json")
                        .content(body.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBlockRejectsNegativeSortOrder() throws Exception {
        mockMvc.perform(put("/api/mgmt/documents/100/blocks/200")
                        .with(authentication(auth(7L)))
                        .contentType("application/json")
                        .content("{\"blockType\":\"text\",\"blockData\":\"payload\",\"sortOrder\":-1}"))
                .andExpect(status().isBadRequest());
    }

    private UsernamePasswordAuthenticationToken auth(long userId) {
        return new UsernamePasswordAuthenticationToken(new TestPrincipal(userId), null);
    }

    static class TestPrincipal {
        private final long userId;

        TestPrincipal(long userId) {
            this.userId = userId;
        }

        public long getUserId() {
            return userId;
        }
    }
}
