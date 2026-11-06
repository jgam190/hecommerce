package com.br.unifor.payments.mensageria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    public String processPayment(String orderId) {
        int maxAttempts = 3;
        long waitDurationInMillis = 2000;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                logger.info("Tentativa {} de 3 para processar o pagamento do pedido: {}", attempt, orderId);


                simularFalhaNaComunicacao();

                return "Pagamento aprovado para o pedido: " + orderId;

            } catch (RuntimeException ex) {
                logger.warn("Falha na tentativa {}. Motivo: {}", attempt, ex.getMessage());


                if (attempt == maxAttempts) {
                    return paymentFallback(ex, orderId);
                }


                try {
                    Thread.sleep(waitDurationInMillis);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("O processo de repetição foi interrompido.", ie);
                }
            }
        }

        return paymentFallback(new RuntimeException("Todas as tentativas se esgotaram"), orderId);
    }

    public String paymentFallback(RuntimeException ex, String orderId) {
        logger.error("Todas as tentativas falharam para o pedido {}. Erro final: {}", orderId, ex.getMessage());
        return "Pagamento pendente. Por favor, tente novamente mais tarde.";
    }

    private void simularFalhaNaComunicacao() {
        throw new RuntimeException("Timeout na comunicação com o gateway de pagamento.");
    }
}