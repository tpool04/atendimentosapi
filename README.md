# 🗓️ Sistema de Agendamentos - API REST

API desenvolvida com **Spring Boot** para gerenciamento de atendimentos, clientes, profissionais e serviços. O sistema possui autenticação via JWT, verificação em dois fatores (2FA), estrutura modular com DTOs, testes automatizados e documentação Swagger.

---

## 🚀 Tecnologias utilizadas

- Java 17  
- Spring Boot  
- Spring Security + JWT  
- JPA / Hibernate  
- MySQL  
- Maven  
- Swagger  
- Google Authenticator (2FA)  
- JUnit

---

## 🔐 Autenticação

- Login com e-mail e senha  
- Geração de token JWT  
- Extração de CPF via `TokenSecurity`  
- Verificação de segundo fator via código TOTP (Google Authenticator)

---

## 📅 Funcionalidades principais

- Criar conta de cliente  
- Acessar conta com autenticação JWT  
- Ativar/verificar 2FA  
- Cadastrar, consultar, reagendar e cancelar atendimentos  
- Gerenciar profissionais e serviços  
- Consultar perfil e status de 2FA

---

## 📦 Estrutura de pacotes

br.com.tonypool
├── controllers
├── dto
├── entities
├── repositories
├── requests
├── responses
├── security
├── services
├── config

---

## 📂 Endpoints principais

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST   | `/api/clientes` | Criar conta de cliente |
| POST   | `/api/login` | Autenticar e gerar JWT |
| POST   | `/api/2fa/confirmar` | Confirmar código de verificação |
| POST   | `/api/atendimentos` | Cadastrar atendimento |
| PUT    | `/api/atendimentos/{id}/reagendar` | Reagendar atendimento |
| DELETE | `/api/atendimentos/{id}` | Cancelar atendimento |

---

## 🧪 Testes

- Testes unitários com JUnit  
- Cobertura para controllers e serviços  
- Exemplos: `ClienteConsultaControllerTest`, `TwoFactorControllerTest`

---

## 📚 Documentação

- Swagger disponível em:  
  http://localhost:8080/swagger-ui/index.html

---

## 🛠️ Como rodar localmente

```bash
git clone https://github.com/tpool04/atendimentosapi.git
cd atendimentosapi
mvn spring-boot:run
## 📌 Autor

**Tony Pool**  
Desenvolvedor Backend | Java & Spring Boot  
GitHub: [@tpool04](https://github.com/tpool04)
