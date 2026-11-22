package com.br.unifor.notifications.mensageria.dto;

import java.time.LocalDateTime;

public class NotificationModel {
    private String id;
    private String orderId;
    private String clienteId;
    private String status;
    private Double valorTotal;
    private boolean lida;
    private LocalDateTime timestamp;


    public NotificationModel() {
    }

    public NotificationModel(String id, String orderId, String clienteId, String status, Double valorTotal) {
        this.id = id;
        this.orderId = orderId;
        this.clienteId = clienteId;
        this.status = status;
        this.valorTotal = valorTotal;
        this.lida = false;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

    public boolean isLida() { return lida; }
    public void setLida(boolean lida) { this.lida = lida; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}