package studio.one.application.document.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import studio.one.application.document.command.DeleteBlockCommand;
import studio.one.application.document.command.UpdateBlockCommand;
import studio.one.application.document.service.DocumentService;
import studio.one.application.document.web.DocumentExceptionHandler;

@WebMvcTest(MgmtDocumentController.class)
@Import({ DocumentExceptionHandler.class, MgmtDocumentControllerWebMvcSecurityTest.TestSecurityConfig.class })
class MgmtDocumentControllerWebMvcSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentService service;

    @Test
    void webMvcContextPreservesDocumentIdBindingForUpdate() throws Exception {
        mockMvc.perform(put("/api/mgmt/documents/300/blocks/400")
                        .with(authentication(auth(11L)))
                        .contentType("application/json")
                        .content("{\"blockType\":\"text\",\"blockData\":\"payload\",\"sortOrder\":1}"))
                .andExpect(status().isNoContent());

        ArgumentCaptor<UpdateBlockCommand> captor = ArgumentCaptor.forClass(UpdateBlockCommand.class);
        verify(service).updateBlock(captor.capture());
        org.junit.jupiter.api.Assertions.assertEquals(300L, captor.getValue().getDocumentId());
        org.junit.jupiter.api.Assertions.assertEquals(400L, captor.getValue().getBlockId());
    }

    @Test
    void webMvcContextRejectsValidationFailureBeforeServiceCall() throws Exception {
        mockMvc.perform(put("/api/mgmt/documents/300/blocks/400")
                        .with(authentication(auth(11L)))
                        .contentType("application/json")
                        .content("{\"blockType\":\"text\",\"blockData\":\"payload\",\"sortOrder\":-2}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void webMvcContextPreservesDocumentIdBindingForDelete() throws Exception {
        mockMvc.perform(delete("/api/mgmt/documents/301/blocks/401")
                        .with(authentication(auth(12L))))
                .andExpect(status().isNoContent());

        ArgumentCaptor<DeleteBlockCommand> captor = ArgumentCaptor.forClass(DeleteBlockCommand.class);
        verify(service).deleteBlock(captor.capture());
        org.junit.jupiter.api.Assertions.assertEquals(301L, captor.getValue().getDocumentId());
        org.junit.jupiter.api.Assertions.assertEquals(401L, captor.getValue().getBlockId());
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

    @TestConfiguration
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    static class TestSecurityConfig {

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            return http.csrf().disable()
                    .authorizeRequests(authorize -> authorize.anyRequest().authenticated())
                    .build();
        }

        @Bean("endpointAuthz")
        EndpointAuthz endpointAuthz() {
            return new EndpointAuthz();
        }
    }

    static class EndpointAuthz {
        public boolean can(String feature, String action) {
            return true;
        }
    }
}
