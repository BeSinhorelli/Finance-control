<h1 align="center"> 💰 Finance Control - Sistema de Controle Financeiro Inteligente </h1>

👨‍💻 Desenvolvido por **Bernardo**

---

## 📋 Sobre o Projeto

O **Finance control** é um sistema completo de controle financeiro pessoal que permite aos usuários gerenciar receitas e despesas de forma simples e intuitiva.

A aplicação combina diferentes tecnologias:

- ⚙️ **Java + Spring Boot** → Backend principal (API REST)
- 🧠 **Python (Flask)** → Análise inteligente de dados financeiros
- 🗄️ **MySQL** → Persistência de dados
- 🌐 **Frontend Web** → HTML, CSS e JavaScript
- 📊 **Chart.js** → Visualização de dados com gráficos

---

## 🎯 Funcionalidades

- ✅ Autenticação de usuários (cadastro e login)
- ✅ CRUD completo de transações (receitas e despesas)
- ✅ Organização por categorias (Alimentação, Transporte, Lazer, etc.)
- ✅ Dashboard interativo com gráficos dinâmicos
- ✅ Filtros por período (semana, mês e ano)
- ✅ Insights financeiros inteligentes gerados por Python
- ✅ Visualização da evolução mensal dos gastos
- ✅ Interface responsiva e moderna

---

## 🚀 Tecnologias Utilizadas

![Java](https://skillicons.dev/icons?i=java) 
![Spring](https://skillicons.dev/icons?i=spring)
![Python](https://skillicons.dev/icons?i=python)
![MySQL](https://skillicons.dev/icons?i=mysql)
![HTML](https://skillicons.dev/icons?i=html)
![CSS](https://skillicons.dev/icons?i=css)
![JavaScript](https://skillicons.dev/icons?i=javascript)

---

## 🔧 Instalação e Configuração

### 📌 Pré-requisitos

- Java 21 ou superior  
- Python 3.11 ou superior  
- MySQL 8.0  
- Maven

## 📊 Inteligência com Python

O serviço em Python é responsável por:

- 📈 Analisar padrões de gastos
- 💡 Gerar insights financeiros
- 📊 Identificar categorias com maior consumo
- 📉 Acompanhar evolução financeira ao longo do tempo
## 🧠 Diferenciais
- 🔥 Integração entre Java (Spring Boot) e Python
- 📊 Dashboard com visualização de dados em tempo real
- 🧩 Arquitetura organizada e escalável

---

<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/f058792b-f314-4e1a-a610-35cafc083f67" />


### 1️⃣ Clonar o repositório

```bash
git clone https://github.com/BeSinhorelli/Finance-control.git
cd financeapp

```

### 2️⃣ Configurar e executar o Python Service

```bash
cd python-service

# Instalar dependências
pip install -r requirements.txt

# Executar o serviço
python app.py
```

## 🗄️ Configuração do Banco de Dados

### 1️⃣ Acessar o MySQL e importar o banco.sql

```bash
mysql -u root -p schema < schema.sql
