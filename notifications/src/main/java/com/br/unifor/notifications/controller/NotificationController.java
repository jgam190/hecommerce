package com.br.unifor.notifications.controller;

import com.br.unifor.notifications.mensageria.dto.NotificationModel;
import com.br.unifor.notifications.mensageria.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping // Mantenha sem valor para não duplicar com o /notifications dos métodos
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationModel>> listarNotificacoes() {
        return ResponseEntity.ok(notificationService.obterTodasNotificacoes());
    }

    @GetMapping("/notifications/cliente/{clienteId}")
    public ResponseEntity<List<NotificationModel>> listarPorCliente(@PathVariable String clienteId) {
        return ResponseEntity.ok(notificationService.obterPorCliente(clienteId));
    }

    @PatchMapping("/notifications/{id}/read")
    public ResponseEntity<Void> marcarLida(@PathVariable String id) {
        notificationService.marcarComoLida(id);
        return ResponseEntity.noContent().build();
    }
}