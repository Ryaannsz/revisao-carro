package com.revisao.demo.seeder;


import com.revisao.demo.enums.CarroStatus;
import com.revisao.demo.enums.UserRoles;
import com.revisao.demo.models.*;
import com.revisao.demo.repository.*;
import com.revisao.demo.service.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.revisao.demo.models.*;
import com.revisao.demo.repository.*;


import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import java.sql.Date;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor
@Component
public class BootstrapSeeder implements CommandLineRunner {

    private final EmpresaRepository empresaRepository;
    private final UserRepository userRepository;
    private final MarcaRepository marcaRepository;
    private final ModeloRepository modeloRepository;
    private final CarroRepository carroRepository;
    private final AbastRepository abastRepository;
    private final RevisaoRepository revisaoRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager em;
    private final BootstrapProperties bootstrapProperties;

    // --------- Ajuste rápido de volume de dados ---------
    private static final int MESES_RETROATIVOS = 18;
    private static final int ABAST_POR_MES_POR_CARRO = 3;
    private static final double KM_MEDIO_POR_ABAST = 520; // média de km entre abastecimentos
    private static final double VARIACAO_KM = 120;        // +/- variação
    private static final double LITROS_BASE = 40;         // média de litros por abastecimento
    private static final double VARIACAO_LITROS = 12;     // +/- variação
    private static final double PRECO_LITRO_BASE = 5.89;  // base do valor do combustível
    private static final double VARIACAO_PRECO = 0.80;    // +/- variação
    private static final double KM_REVISAO_INTERVALO = 10000; // revisão a cada ~10k km
    private static final double VARIACAO_REVISAO_KM = 800;    // +/- variação
    // ----------------------------------------------------

    @Override
    @Transactional
    public void run(String... args) {
        // 1) Garantir Empresa id=1
        Empresa empresa = ensureEmpresaId1();


        User user = ensureUserId(empresa,1, bootstrapProperties.getCpf(), bootstrapProperties.getNomeUser());

        //teste empresa 2
        Empresa empresa2 = new Empresa();
        empresa2.setNome("Empresa 2");
        empresa2.setCnpj("awdfawd");
        empresa2.setAtivo(true);
        empresaRepository.save(empresa2);

        //teste user2
        User user2 = ensureUserId(empresa2, 2, "09876543210", "User 2");

        // 3) Marca & Modelo (sem grande diversidade)
        Marca marca = marcaRepository.findByMarca("Volkswagen").orElseGet(() -> {
            Marca m = new Marca();
            m.setMarca("Volkswagen");
            m.setEmpresa(empresa);
            return marcaRepository.save(m);
        });

        Marca marca2 = marcaRepository.findByMarca("Ford").orElseGet(() -> {
            Marca m = new Marca();
            m.setMarca("Ford");
            m.setEmpresa(empresa);
            return marcaRepository.save(m);
        });

        Modelo modelo = modeloRepository.findByModelo("Gol 1.6").orElseGet(() -> {
            Modelo md = new Modelo();
            md.setModelo("Gol 1.6");
            md.setEmpresa(empresa);
            return modeloRepository.save(md);
        });

        Modelo modelo2 = modeloRepository.findByModelo("Ranger").orElseGet(() -> {
            Modelo md = new Modelo();
            md.setModelo("Ranger");
            md.setEmpresa(empresa);
            return modeloRepository.save(md);
        });

        // 4) Dois carros da empresa 1
        Carro carro1 = ensureCarro("ABC-1A23", empresa, marca, modelo, 42000);
        Carro carro2 = ensureCarro("DEF-4B56", empresa, marca, modelo, 15800);

        Carro carro3 = ensureCarro("PLA-8K72", empresa, marca2, modelo2, 6391);
        Carro carro4 = ensureCarro("IAK-9B73", empresa, marca2, modelo2, 23521);

        // 5) Gerar Abastecimentos & Revisões diversificados
        gerarDadosDiversificados(user, empresa, Arrays.asList(carro1, carro2, carro3, carro4));
    }

    private Empresa ensureEmpresaId1() {
        return empresaRepository.findById(1).orElseGet(() -> {
            // força id=1 via insert nativo se necessário
            em.createNativeQuery("INSERT INTO tb_empresa (id, nome) VALUES (1, ?1)")
                    .setParameter(1, "Empresa Demo")
                    .executeUpdate();

            return empresaRepository.findById(1).orElseThrow();
        });
    }

    private User ensureUserId(Empresa empresa, Integer id, String cpf, String nome) {
        Optional<User> existing = userRepository.findById(id);
        if (existing.isPresent()) return existing.get();

        // cria via SQL nativo para fixar id=1 (respeitando seu requisito)
        // Ajuste os nomes/colunas conforme seu esquema real.
        String senha = passwordEncoder.encode(bootstrapProperties.getSenha());


        em.createNativeQuery("""
    INSERT INTO tb_user (id, cpf, senha, nome, empresa_id, roles)
    VALUES (?6, ?1, ?2, ?3, ?4, ?5)
""")
                .setParameter(1, cpf)
                .setParameter(2, senha)
                .setParameter(3, nome)
                .setParameter(4, empresa.getId())
                .setParameter(5, "ADMIN")
                .setParameter(6, id)
                .executeUpdate();

        return userRepository.findById(id).orElseThrow();
    }

    private Carro ensureCarro(String placa, Empresa empresa, Marca marca, Modelo modelo, double kmInicial) {
        return carroRepository.findByPlaca(placa).orElseGet(() -> {
            Carro c = new Carro();
            c.setPlaca(placa);
            c.setEmpresa(empresa);
            c.setMarca(marca);
            c.setModelo(modelo);
            c.setDtAdicionado(date(LocalDate.now().minusMonths(MESES_RETROATIVOS + 1).withDayOfMonth(1)));
            c.setKmAdicionado(kmInicial);
            c.setAlarmeRevisaoKm(10000.0);
            c.setAlarmeRevisaoMes(6);
            c.setStatus(CarroStatus.DISPONIVEL);
            return carroRepository.save(c);
        });
    }

    private void gerarDadosDiversificados(User user, Empresa empresa, List<Carro> carros) {
        Random r = new Random(2025);

        LocalDate hoje = LocalDate.now();
        LocalDate inicio = hoje.minusMonths(MESES_RETROATIVOS).withDayOfMonth(1);

        for (Carro carro : carros) {
            // km corrente por carro começa do kmAdicionado
            double kmAtualCarro = Optional.ofNullable(carro.getKmAdicionado()).orElse(0d);

            LocalDate cursor = inicio;
            while (!cursor.isAfter(hoje)) {
                // vários abastecimentos por mês, com datas aleatórias dentro do mês
                for (int i = 0; i < ABAST_POR_MES_POR_CARRO; i++) {
                    // incremento de km com ruído
                    double incremento = KM_MEDIO_POR_ABAST + randDelta(r, VARIACAO_KM);
                    kmAtualCarro += Math.max(100, incremento); // garante incremento mínimo

                    // litros e preço com variação
                    double litros = clamp(LITROS_BASE + randDelta(r, VARIACAO_LITROS), 20, 70);
                    double precoLitro = clamp(PRECO_LITRO_BASE + randDelta(r, VARIACAO_PRECO), 4.29, 7.99);
                    double valor = round2(litros * precoLitro);

                    // data randômica no mês
                    LocalDate diaAbast = randomDayInMonth(r, cursor);
                    Date dtAbast = date(diaAbast);

                    // Persistir Abast
                    Abast ab = new Abast();
                    ab.setCarro(carro);
                    ab.setUser(user);
                    ab.setDtAbast(dtAbast);
                    ab.setKmAtual(round0(kmAtualCarro));
                    ab.setLitroComb(round2(litros));
                    ab.setValorComb(valor);
                    abastRepository.save(ab);

                    // chance de um segundo abastecimento “próximo” (mais diversidade)
                    if (r.nextDouble() < 0.25) {
                        double inc2 = 120 + r.nextDouble() * 180;
                        kmAtualCarro += inc2;
                        double litros2 = clamp(LITROS_BASE - 8 + r.nextDouble() * 10, 15, 55);
                        double valor2 = round2(litros2 * precoLitro * (0.98 + r.nextDouble() * 0.04));

                        Abast ab2 = new Abast();
                        ab2.setCarro(carro);
                        ab2.setUser(user);
                        ab2.setDtAbast(date(diaAbast.plusDays(r.nextInt(6) + 1)));
                        ab2.setKmAtual(round0(kmAtualCarro));
                        ab2.setLitroComb(round2(litros2));
                        ab2.setValorComb(valor2);
                        abastRepository.save(ab2);
                    }

                    // revisão quando cruza múltiplos de ~10k (com folga)
                    if (deveFazerRevisao(kmAtualCarro, KM_REVISAO_INTERVALO, VARIACAO_REVISAO_KM, r)) {
                        Revisao rev = new Revisao();
                        rev.setCarro(carro);
                        rev.setUser(user);
                        rev.setDtRevisao(date(diaAbast.plusDays(r.nextInt(10))));
                        rev.setKmAtual(round0(kmAtualCarro + randDelta(r, 150)));
                        revisaoRepository.save(rev);
                    }
                }
                cursor = cursor.plusMonths(1);
            }
        }
    }

    // ---------- Utils ----------
    private static double randDelta(Random r, double amplitude) {
        return (r.nextDouble() * 2 - 1) * amplitude; // [-amp, +amp]
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private static double round0(double v) {
        return Math.round(v);
    }

    private static LocalDate randomDayInMonth(Random r, LocalDate baseMonth) {
        int maxDay = baseMonth.lengthOfMonth();
        int day = 1 + r.nextInt(maxDay);
        return baseMonth.withDayOfMonth(day);
    }

    private static java.sql.Date date(LocalDate ld) {
        return java.sql.Date.valueOf(ld); // converte direto para java.sql.Date
    }

    private static boolean deveFazerRevisao(double kmAtual, double intervalo, double tolerancia, Random r) {
        // calcula se o km atual está próximo de um múltiplo do intervalo (ex: 10.000 km, 20.000 km, etc.)
        double resto = kmAtual % intervalo;

        // se está dentro da tolerância, gera revisão
        if (resto < tolerancia || resto > (intervalo - tolerancia)) {
            // chance de realmente acontecer (para não gerar revisão em todo mundo)
            return r.nextDouble() < 0.7; // 70% de chance
        }
        return false;
    }
    
}
