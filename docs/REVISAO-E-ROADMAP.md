# Revisão geral + Roadmap — Revisão Carro (frota de carros e máquinas)

> Revisão feita em 12/11/2026. Cobre o estado do projeto até o commit `9016d14`, as correções aplicadas
> nesta rodada e tudo o que falta para carros **e** máquinas (combustível, manutenção, gráficos, dashboard).

---

## 1. Nota do projeto (como estava)

| Aspecto | Nota | Comentário |
|---|---|---|
| Organização / arquitetura | **8/10** | Camadas bem separadas (controller → service → repository), `BaseService`/`BaseMapper` genéricos, MapStruct, DTOs de dashboard, front com `core/models`, `core/services`, `pages`, `components`, `shared`. Estava mesmo "organizadinho". |
| Funcionalidade — carros | **6/10** | Cadastro, abastecimento, revisão, próxima revisão por km/mês, dashboard e PDF funcionando. Mas: revisão sem custo/descrição, cálculos errados na tela do carro, editar/excluir de abastecimento/revisão sem implementação. |
| Funcionalidade — máquinas | **1/10** | Modelo e endpoints existiam, mas o POST salvava a máquina **vazia** (import errado do `@RequestBody`), máquina não tinha empresa e a tela era só `cadastro-maquina works!`, sem rota. |
| Segurança | **2/10** | Vários furos graves (ver seção 2). É o ponto que mais derrubava a nota. |
| Testes | **0,5/10** | Só o `contextLoads` e os `.spec.ts` gerados automaticamente. |
| Infra / DevOps | **5/10** | Dockerfiles e compose ok, mas seeder duplicava dados a cada boot, README desatualizado (fala de Postgres), `backend/bin/` com `.class` commitados. |

**Nota geral (antes): 5,5/10.** A estrutura é de 8, mas os problemas de segurança e de "multiempresa"
fariam o sistema vazar dados entre clientes no primeiro uso real.

**Depois desta rodada: ~7,5/10.** Os furos críticos foram fechados e testados; o que separa de um 9 é
máquina no front, testes automatizados e migrations (seções 4–6).

---

## 2. O que foi encontrado e corrigido

### Segurança (críticos)
| Problema | Impacto | Correção |
|---|---|---|
| `GET /abastecimento`, `GET /revisao` retornavam registros de **todas as empresas** | Uma empresa via os dados da outra | Listagens filtradas pela empresa do token |
| `GET/PUT/DELETE /carro/{id}`, `/abastecimento/{id}`, `/revisao/{id}` sem checar empresa | Qualquer usuário lia/editava/apagava dado de outra empresa trocando o ID | Toda busca por id agora é `findByIdAndEmpresaId`; outro tenant recebe **404** |
| DTOs embutiam a entidade `User` inteira | **Hash da senha** saía em `GET /abastecimento`, `/revisao` e `/user` | `@JsonIgnoreProperties` na entidade + `senha` *write-only* no `UserDTO` |
| `PATCH /user/{id}` liberado para qualquer logado | Usuário comum se promovia a ADMIN | `/user/**` só ADMIN |
| `DELETE` só era bloqueado no front | Usuário comum apagava via API | `DELETE /**` só ADMIN no backend |
| `POST /auth/registro` aceitava `roles` e `empresa` do cliente | Criar ADMIN em qualquer empresa (só não funcionava por acidente) | Endpoint removido; usuário é criado pelo ADMIN em `POST /user` |
| Token usava o **nome** como identificador (`findByNome`) | Dois usuários com mesmo nome = login do outro | Subject do JWT agora é o CPF; empresa vem do banco, não do token |
| Token inválido gerava NPE/500; usuário sem permissão recebia 401 | Front deslogava o usuário à toa | 401 = não logado, 403 = sem permissão (e `/error` liberado) |
| CORS `*` em todos os controllers | Qualquer site chamava a API | Origens configuráveis em `CORS_ALLOWED_ORIGINS` |
| Log `trace` do Hibernate | SQL e parâmetros (inclusive hash) no log | Nível `warn` por padrão (`SQL_LOG_LEVEL`) |
| Marca/modelo de outra empresa podiam ser usados no carro | Mistura de dados | Validação por empresa |

> ⚠️ **Ação manual necessária:** a senha de banco `trebabanco#!!2` foi commitada no `application.properties`
> (commit `1000903`) e está no histórico do GitHub. **Troque essa senha no banco** onde ela é usada.
> Remover do histórico (`git filter-repo`) é opcional; trocar a senha é obrigatório.
> Também defina `JWT_SECRET` em produção (o default é público).

### Bugs funcionais
- **Máquina:** `@RequestBody` importado do Swagger → corpo nunca lido → máquinas salvas vazias. Corrigido; máquina agora tem empresa, horímetro, intervalo de revisão, e a revisão de máquina valida horímetro.
- **PUT /carro** convertia o DTO sem id e **inseria um carro novo** (sem empresa). Agora atualiza o existente.
- **Seeder** rodava a cada restart: criava outra "Empresa 2" e ~270 abastecimentos novos toda vez, e gerava datas no futuro. Agora: empresa + ADMIN inicial sempre (idempotente); dados de demo só com `BOOTSTRAP_DEMO_DATA=true` e só se a base estiver vazia.
- **Variável `USERNAME`** do banco não funciona no zsh (macOS) nem no Windows. Renomeada para `DB_USERNAME` / `DB_PASSWORD` (compose atualizado).
- **Tela do carro:** "Total" multiplicava litros × valor (o valor já é o total) e "Média Km/L" era `return 10` fixo. Agora: Valor/L e Total corretos, km/L pelo método tanque-a-tanque e custo/km (combustível + manutenção).
- **Usuário que registrou** abastecimento/revisão nunca era gravado (front mandava `idUser`, backend esperava `user`). Agora vem do token.
- **Excluir** abastecimento/revisão gerava URL `//id` (barrada pelo Spring Security); na tela do carro os botões não faziam nada. Corrigido e implementado.
- **Header/AuthGuard** quebravam deslogado (`getUserRole()` lendo `null`). Link "Usuários" só aparece para ADMIN.
- Carros **sem nenhuma revisão** não apareciam nas "próximas revisões" e a lista mostrava `undefined km`. Agora usam data/km de cadastro como base.
- Duplicidade de marca/modelo era global (empresa 2 não conseguia cadastrar "ford"). Agora é por empresa.
- Lista de carros: "Em Operação" mostrava o total, "Média KM" vazio, botão "Em rota" sem função. Agora mostra disponíveis/manutenção, média real e quantos carros estão com revisão vencida/próxima.
- Erro de console `@fadeInOut` na tela de login (animação nunca definida).

### Melhorias novas
- Revisão de carro e de máquina agora têm **custo (R$)** e **descrição do serviço**.
- Dashboard: gráfico de gasto mensal agora mostra **combustível + manutenção**.
- Endpoints novos de máquina: `GET /maquina/{id}/horas/recente`, `GET /revisao/maquina/maquina/{id}`, `DELETE /revisao/maquina/{id}`.

### Como foi testado
Backend rodado contra um MySQL descartável (Docker) com dados de demo, e testado via HTTP:
isolamento entre empresas (404 em tudo de outra empresa), ausência de hash nas respostas,
USER recebendo 403 em PATCH/DELETE/dashboard, validações de km/horímetro, PUT sem duplicar,
seeder idempotente em 2 boots, CORS e PDF. Front rodado contra esse backend (lista, carro, revisões, dashboard).

### ⚠️ Impacto ao subir em uma base existente
- Tokens antigos deixam de valer (o subject mudou) → todos precisam logar de novo.
- `ddl-auto=update` vai criar as colunas novas (`valor`, `descricao`, `empresa_id` em máquina, etc.) sozinho.
- Máquinas que já existirem ficam com `empresa_id` nulo e **somem da listagem**. Rode:
  `UPDATE tb_maquina SET empresa_id = 1 WHERE empresa_id IS NULL;` (ajuste o id).
- Se o seeder antigo rodou várias vezes, há várias "Empresa 2" e abastecimentos duplicados na base de dev.
  O mais simples é recriar a base de dev.
- Renomeie `USERNAME`/`PASSWORD` para `DB_USERNAME`/`DB_PASSWORD` onde o backend estiver hospedado.

---

## 3. Diagnóstico por área (o que existe hoje)

| Área | Carro | Máquina |
|---|---|---|
| Cadastro | ✅ placa, marca, modelo, km inicial, intervalo km/meses, status | ⚠️ só backend (front é placeholder, sem rota) |
| Medidor de uso | ✅ km (maior entre cadastro, abastecimentos, revisões) | ✅ horímetro (backend) |
| Combustível | ✅ litros, valor total, km, data | ❌ não existe |
| Manutenção | ✅ data, km, custo, descrição | ⚠️ backend com horas, custo, descrição; sem tela |
| Próxima manutenção | ✅ por km **ou** meses | ❌ falta cálculo por horas/meses |
| Dashboard | ✅ gasto (comb.+manut.), litros, km/L, revisões, próximas, PDF | ❌ nada |
| Permissões | ✅ ADMIN/USER, multiempresa | ✅ multiempresa |

---

## 4. Roadmap — Máquinas (o "processo de máquina")

Máquina pesada (retroescavadeira, trator, escavadeira, pá carregadeira, rolo, gerador…) se controla
por **horímetro (horas)**, não por km. Toda métrica de carro tem a equivalente por hora:

| Carro | Máquina |
|---|---|
| km | horas do horímetro |
| km/L | **L/h** (litros por hora) |
| custo por km | **custo por hora** (R$/h) |
| revisão a cada 10.000 km / 6 meses | preventiva a cada **250 h / 500 h / 1000 h / 2000 h** ou meses |

### 4.1 Fase 1 — tornar utilizável (prioridade alta)
- [ ] **Front: cadastro de máquina** (rota `/maquina`): identificação/apelido, tipo, marca, modelo, chassi/série, ano, horímetro inicial, intervalo preventiva (horas e meses). Reaproveitar o layout de `cadastro-carro`.
- [ ] **Front: lista de máquinas + página da máquina** (espelho de `lista-carros` e `carro-unico`), com status e barra de progresso até a próxima preventiva por horas.
- [ ] **Front: registrar manutenção de máquina** (espelho de `cadastro-revisao`), usando horímetro no lugar de km.
- [ ] **Backend: próxima preventiva** (`/revisao/maquina/alarme`): `ultimaRevisao.horas + alarmeRevisaoHoras` e `data + alarmeRevisaoMes`, o que vencer primeiro. Mesmo padrão de `RevisaoService.montarProxima`.
- [ ] **Status da máquina** (disponível / em manutenção / parada / em obra), igual a `CarroStatus`.
- [ ] Adicionar "Máquinas" no header.

### 4.2 Fase 2 — combustível e uso
- [ ] **Abastecimento de máquina**: data, horímetro, litros, valor total, **origem** (posto ou tanque próprio/comboio) e operador. Reaproveitar `Abast` com um `veiculo` genérico, ou criar `AbastMaquina`.
- [ ] **Consumo L/h** = litros abastecidos ÷ horas trabalhadas entre abastecimentos.
- [ ] **Apontamento de horas / diário de uso**: data, operador, obra/local, horímetro início/fim, horas paradas e motivo. Sem isso o custo por obra fica impossível.
- [ ] **Estoque de diesel** (se seu tio tiver tanque próprio): entradas (compra) e saídas (abastecimentos), alerta de divergência.

### 4.3 Fase 3 — manutenção completa
- [ ] **Tipo de manutenção**: preventiva / corretiva / lubrificação / inspeção.
- [ ] **Plano de manutenção por máquina** (checklist por intervalo). Referência típica de fabricante (confirmar no manual de cada máquina):
  - diário / 10 h: graxa nos pinos, nível de óleo do motor, água, óleo hidráulico, vazamentos, pneus/esteira
  - 250 h: óleo do motor + filtro
  - 500 h: filtros de combustível, separador de água, filtro de ar (verificar)
  - 1000 h: óleo e filtros hidráulicos de retorno, óleo de transmissão/diferencial
  - 2000 h: troca do óleo hidráulico e líquido de arrefecimento
- [ ] **Checklist de pré-operação** (o operador marca no celular antes de ligar a máquina).
- [ ] Itens/peças usados, oficina/mecânico, anexo de nota fiscal ou foto.

### 4.4 Dashboard de máquinas
- [ ] Horas trabalhadas por mês (por máquina e total)
- [ ] Consumo L/h por máquina (e alerta quando foge da média)
- [ ] Custo R$/h (combustível + manutenção) e ranking das máquinas mais caras
- [ ] Próximas preventivas por horímetro (tabela igual à de carros)
- [ ] Disponibilidade física: % do tempo em que a máquina não está em manutenção
- [ ] Incluir máquinas no PDF

---

## 5. Roadmap — Carros (o que falta)

### Combustível
- [ ] **Flag "tanque cheio"** no abastecimento. O km/L só é exato entre dois tanques cheios; hoje é uma aproximação. Ajustar também a query mensal de eficiência (`DashboardRepository.carMonthAgg`), que usa `(max km − min km) / litros do mês`.
- [ ] Tipo de combustível (gasolina, etanol, diesel, GNV) e posto.
- [ ] **Alerta de consumo anômalo**: km/L caiu mais de 15% em relação à média do carro → pode ser problema mecânico ou desvio de combustível.
- [ ] Editar abastecimento (o botão foi removido porque não fazia nada).

### Manutenção
- [ ] Tipo (preventiva/corretiva), oficina, itens/peças, anexo de nota.
- [ ] Editar revisão.
- [ ] Pneus (rodízio, troca, km de instalação).

### Documentação e custos fixos (muito pedido em gestão de frota)
- [ ] Vencimentos: **licenciamento, IPVA, seguro, CNH do motorista**, com alerta.
- [ ] Multas (data, valor, motorista responsável).
- [ ] Motorista vinculado ao carro / checklist de saída e devolução.

### Dashboard de carros
- [ ] Linha de KPIs no topo: gasto do mês, comparação com o mês anterior, custo/km da frota, carros com revisão vencida.
- [ ] Custo por km **por carro** e ranking dos mais caros.
- [ ] Separar o gasto de manutenção por carro no gráfico de comparação (`car-stats`).
- [ ] Filtro de período no backend (hoje o front baixa tudo e corta os meses).
- [ ] Incluir custo de manutenção no PDF.

---

## 6. Dívida técnica (por ordem de prioridade)

1. **Testes automatizados.** Começar pelos que protegem o que foi corrigido: isolamento entre empresas (empresa B recebe 404), permissões (USER recebe 403 em DELETE/PATCH) e cálculo de próxima revisão. `@SpringBootTest` + Testcontainers MySQL.
2. **Migrations (Flyway)** no lugar de `ddl-auto=update`. O `update` não remove colunas, não cria índices direito e já criou dor com as colunas novas.
3. **DTOs de saída próprios.** Hoje os DTOs embutem entidades (`User`, `Carro` com `Empresa`). O `@JsonIgnoreProperties` resolveu o vazamento, mas um `UserResumoDTO {id, nome}` é mais seguro.
4. **Configuração do front por ambiente.** `BASE_URL` está fixo em `app.constants.ts` (com a URL de produção comentada). Usar `environment.ts` + `fileReplacements` no `angular.json`.
5. **Unicidade no banco:** índice único em `tb_user.cpf` e `tb_carro.placa` (hoje só validado no código). Fazer junto com o Flyway, depois de limpar duplicados.
6. **Limpeza do repositório:** `backend/bin/` (62 arquivos `.class` antigos) e `.idea/` estão versionados. Remover e colocar no `.gitignore`.
7. **README** desatualizado (fala de PostgreSQL e mostra Dockerfiles antigos). Atualizar com as variáveis da seção 7.
8. **Lista de carros faz N+1 requisições** (`getKmRecente` por carro). Devolver o km atual junto no `GET /carro`.
9. Header usa menus por hover (ruim no celular) e o `JwtModule` tem `allowedDomains` fixo em localhost (o interceptor próprio já cobre; dá pra remover o `JwtModule` e usar só o `JwtHelperService`).
10. Queries usam `DATE_FORMAT` (só MySQL). Tudo bem se o banco for sempre MySQL; tirar a dependência do Postgres do `pom.xml` já foi feito.
11. Troca de senha pelo próprio usuário (o ADMIN inicial nasce com a senha do seed).

---

## 7. Variáveis de ambiente (backend)

| Variável | Default | Para quê |
|---|---|---|
| `MYSQL_HOST` / `MYSQL_PORT` / `MYSQL_DATABASE_APP` | `localhost` / `3306` / `mydb` | Conexão |
| `DB_USERNAME` / `DB_PASSWORD` | `root` / `1234` | Usuário do banco (**antes era `USERNAME`/`PASSWORD`**) |
| `JWT_SECRET` | valor de dev | **Obrigatório em produção** |
| `JWT_EXPIRATION_HOURS` | `8` | Duração do login |
| `CORS_ALLOWED_ORIGINS` | localhost:3000, localhost:4200, revisao-carro.vercel.app | Origens do front |
| `BOOTSTRAP_ADMIN_CPF` / `_SENHA` / `_NOME` | `12345678901` / `123456` / `admin` | ADMIN criado na 1ª subida |
| `BOOTSTRAP_EMPRESA_NOME` / `_CNPJ` | `Empresa Demo` | Empresa id=1 |
| `BOOTSTRAP_DEMO_DATA` | `false` | Gera carros e histórico fictícios (só em base vazia) |
| `SQL_LOG_LEVEL` | `warn` | `debug` para ver o SQL |

---

## 8. Ordem sugerida para as próximas sessões

1. Trocar a senha vazada e configurar `JWT_SECRET` (5 min, manual).
2. Máquinas — Fase 1 (front de cadastro, lista, página e manutenção + próxima preventiva por horas).
3. Testes de isolamento e permissão (protege tudo que foi corrigido aqui).
4. Máquinas — Fase 2 (abastecimento, L/h, apontamento de horas).
5. Dashboard de máquinas e KPIs no topo do dashboard.
6. Documentação/vencimentos de carros (IPVA, licenciamento, seguro).
7. Flyway + limpeza do repositório + README.
