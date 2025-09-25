package br.com.tonypool.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.tonypool.entities.Cliente;
import br.com.tonypool.repositories.IClienteRepository;
import br.com.tonypool.services.TwoFactorAuthService;

@RestController
@RequestMapping("/api/2fa")
public class TwoFactorController {

    @Autowired
    private IClienteRepository clienteRepository;

    @Autowired
    private TwoFactorAuthService twoFactorAuthService;

    @PostMapping("/ativar/{idCliente}")
    public ResponseEntity<?> ativar2FA(@PathVariable Integer idCliente) {
        try {
            Cliente cliente = clienteRepository.findById(idCliente).orElse(null);
            if (cliente == null) {
                return ResponseEntity.status(404).body("Cliente não encontrado.");
            }
            String otpUrl = twoFactorAuthService.setup2FA(cliente);
            return ResponseEntity.ok(java.util.Collections.singletonMap("otpAuthUrl", otpUrl));
        } catch (Exception e) {
            String tipoErro = e.getClass().getSimpleName();
            String mensagem = tipoErro + ": " + e.getMessage();
            return ResponseEntity.status(500).body(java.util.Collections.singletonMap("error", mensagem));
        }
    }
}