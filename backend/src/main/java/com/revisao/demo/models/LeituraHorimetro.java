package com.revisao.demo.models;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Leitura avulsa do horímetro (ex.: operador anota no fim do dia).
 * Mantém o "horas atual" da máquina em dia entre uma manutenção e outra.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_leitura_horimetro")
public class LeituraHorimetro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date dtLeitura;

    private Double horas;

    @ManyToOne
    @JoinColumn(name = "maquina_id")
    private Maquina maquina;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
