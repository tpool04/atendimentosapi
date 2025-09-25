package br.com.tonypool.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReagendarAtendimentoRequest {
    private String novaDataHora; // formato: "dd/MM/yyyy HH:mm"
    private String novaObservacao; // observação opcional
    private Integer idServico; // novo serviço
    private Integer idProfissional; // novo profissional
}