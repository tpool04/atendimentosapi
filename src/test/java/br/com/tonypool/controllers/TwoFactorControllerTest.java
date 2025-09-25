package br.com.tonypool.controllers;

import br.com.tonypool.entities.Cliente;
import br.com.tonypool.repositories.IClienteRepository;
import br.com.tonypool.services.TwoFactorAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Map;
import java.util.Optional;

class TwoFactorControllerTest {
    @Mock
    private IClienteRepository clienteRepository;

    @Mock
    private TwoFactorAuthService twoFactorAuthService;

    @InjectMocks
    private TwoFactorController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAtivar2FA_Success() {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1);
        cliente.setEmail("test@example.com");
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(twoFactorAuthService.setup2FA(cliente)).thenReturn("http://qrcode.url");

        ResponseEntity<?> response = controller.ativar2FA(1);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof Map);
        Map<?,?> body = (Map<?,?>) response.getBody();
        assertEquals("http://qrcode.url", body.get("qrCodeUrl"));
    }

    @Test
    void testAtivar2FA_ClienteNotFound() {
        when(clienteRepository.findById(2)).thenReturn(Optional.empty());
        ResponseEntity<?> response = controller.ativar2FA(2);
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Cliente não encontrado.", response.getBody());
    }

    @Test
    void testAtivar2FA_InternalError() {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(3);
        when(clienteRepository.findById(3)).thenReturn(Optional.of(cliente));
        when(twoFactorAuthService.setup2FA(cliente)).thenThrow(new RuntimeException("Erro interno"));
        ResponseEntity<?> response = controller.ativar2FA(3);
        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof Map);
        Map<?,?> body = (Map<?,?>) response.getBody();
        assertTrue(((String) body.get("error")).contains("RuntimeException: Erro interno"));
    }
}