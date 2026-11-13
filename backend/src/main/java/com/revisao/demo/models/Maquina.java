package com.revisao.demo.models;

import java.sql.Date;

import com.revisao.demo.enums.MaquinaStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_maquina")
public class Maquina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date dtAdicionado;

    private Double horas;

    @ManyToOne
    @JoinColumn(name = "marca_id")
    private Marca marca;

    @ManyToOne
    @JoinColumn(name = "modelo_id")
    private Modelo modelo;

    private String chassi;

    /** Identificação livre (apelido/prefixo da frota), ex.: "Retro 02". */
    private String identificacao;

    /** Retroescavadeira, trator, escavadeira, pá carregadeira, rolo, gerador... */
    private String tipo;

    private Integer ano;

    @Enumerated(EnumType.STRING)
    private MaquinaStatus status;

    /** Intervalo de manutenção preventiva em horas (horímetro), ex.: 250h. */
    private Double alarmeRevisaoHoras;

    /** Intervalo de manutenção preventiva em meses. */
    private Integer alarmeRevisaoMes;

    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

}
