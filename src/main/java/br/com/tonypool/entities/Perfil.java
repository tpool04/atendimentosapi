package br.com.tonypool.entities;

import java.util.List;
import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "perfil")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Perfil {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "nome", length = 50, nullable = false)
    private String nome;

    @Column(name = "descricao", length = 150)
    private String descricao;

    @OneToMany(mappedBy = "perfil")
    private List<Cliente> clientes;
}