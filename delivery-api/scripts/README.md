## 🧪 Execução de Testes Automatizados

Para rodar os testes localmente com verificação de cobertura:

### 🔧 Pré-requisitos:
- Java 17+
- Maven 3.8+

### ▶️ Executar testes com cobertura:

```bash
./run-tests.sh
```
---


### 📊 Relatório de Cobertura:
Após execução, acesse:

```bash
target/site/jacoco/index.html
```

### 🛑 Failsafe:
O build irá falhar se a cobertura de código estiver abaixo de 80%.

```bash


---

## ✅ Checklist da Atividade 4.2

| Tarefa                                                        | Status |
|---------------------------------------------------------------|--------|
| Execução automática de testes com Maven (`mvn verify`)        | ✅     |
| Script para execução local com cobertura                      | ✅     |
| Cobertura mínima configurada e integrada ao build             | ✅     |
| Documentação clara no `README.md`                             | ✅     |

---

