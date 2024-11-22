# DevLab - Back-end

![JAVA](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![SPRING](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
![MongoDB](https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=for-the-badge&logo=mongodb&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)

> Status: Em desenvolvimento 👨‍💻

<div align="center">
  <a href="#executar-projeto">Executando o Projeto</a> •
  <a href="https://github.com/AntonioMartinss/DevLab_front_end">Front-end</a> •
  <a href="#endpoints">Endpoints</a> •
  <a href="#criptografia">Criptografia assimétrica dos dados</a>
</div>

# Sobre o projeto
Esse projeto consiste no desenvolvimento de um site/plataforma de jogos online construído com Java, Java Spring, Lombok, MongoDB como Banco de Dados, Spring Data MongoDB para as operações com o Banco de Dados e Spring Security para o controle de autenticação via token JWT.

Os usuários podem se registrar na plataforma e, após efetuarem login, acessar os jogos da plataforma que rodam em rede local.

## Licença

[![LICENSE](https://img.shields.io/github/license/Ileriayo/markdown-badges?style=for-the-badge)](https://github.com/BryanSLemes/DevLab-Back-end/blob/master/LICENSE)

## Implantação em produção
<!-- - Back-end: Heroku colocar dps
- Front-end: Netlify -->
- Banco de Dados: MongoDB

## Desenvolvedores
  * [Bryan Lemes](https://github.com/bryanslemes)
  * [Antônio Martins](https://github.com/AntonioMartinss)

## UX & UI Designer
  * [Henrique Pimentel](https://www.behance.net/HenriquePimentelCs)

<br>

<h1 id="executar-projeto">Como Executar o projeto</h1>

## Banco de Dados
  Pré-requisito: [Baixar o MongoDB Community](https://www.mongodb.com/try/download/community)
  
### Acesse Mongo Shell ou MongoDB Compass:

```bash
# criar ou selecionar banco de dados
use DevLab_DB

# criar collection
db.createCollection("usuarios")
```

## Front-end
  *  [Link do Repositório](https://github.com/AntonioMartinss/DevLab_front_end)

## Back-end (este repositório)
  Pré-requisito: Java 17 ou superior

```bash
# clonar repositório
git clone https://github.com/BryanSLemes/DevLab-Back-end

# entrar na pasta do projeto
cd DevLab-Back-End

# executar o projeto
./mvnw spring-boot:run
```

<h2 id="endpoints">Endpoints da Aplicação</h2>

Considerando que a URL raiz da plataforma é http://ip:8080/DevLab ,temos os seguinte endpoints:



<b>Obs: 
  * Substitua "ip" da URL pelo IP da sua máquina.
  * Alguns dos Endpoints requerem dados <a href="#criptografia">criptografados</a>.
</b>

### POST CADASTRO
```markdown
POST /usuario/cadastro - Cadastrar um usuário comum no sistema.
```
```javascript
let formData = {
  data: encrypt({
    "usuario": "Rafael",
    "senha": "senhaForte123", // mínimo de 8 caracteres
    "email": "rafael@example.com",
  })
};

fetch('http://localhost:8080/DevLab/usuario/cadastro', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(formData)
})
.then(response => {
  return response.text();
})
.then(data => {
  console.log("Resposta do Servidor: " + data);
})
.catch(error => {
  console.error('Erro:', error);
});
```
### Possíveis Respostas:

```javascript
Status: 200
Response Body: Usuário criado com sucesso
```

```javascript
/*Algum campo está em falta ou é inválido*/
Status: 400
Response Body: /*Resposta dinâmica*/
```

```javascript
/*Usuário ou email já está cadastrado*/
Status: 409
Response Body: /*Resposta dinâmica*/
```

<br>

### POST LOGIN
```markdown
POST /usuario/login - Efetuar login na plataforma.
```
```javascript
let formData = {
  data: encrypt({    
    "usuario": "Rafael",
    "senha": "senhaForte123"
  })
};

fetch('http://localhost:8080/DevLab/usuario/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(formData)
})
.then(response => {
  return response.text();
})
.then(data => {
  sessionStorage.setItem("token", data);
})
.catch(error => {
  console.error('Erro:', error);
});

```
### Possíveis Respostas:
```javascript
/*será retornado um token, defina-o no session Storage*/
Status: 200
Response Body: /* Token */

sessionStorage.setItem("token", data);
```

```javascript
Status: 401
Response Body: Login Falhou
```

<br>

### GET LOGADO
```markdown
POST /usuario/logado - Verifica se a credencial do usuário é válida.
```
```javascript
//Exemplo de requisição para verificar credencial
fetch('http://localhost:8080/DevLab/usuario/logado', {
  method: 'GET',
  headers: {
    'Authorization': sessionStorage.getItem("token")
  }
})
.then(response => {
  return response.text();
})
.then(data => {
  console.log(JSON.parse(data));
})
.catch(error => {
  console.error('Erro:', error);
});
```
### Possíveis Respostas:

```javascript
Status: 200
Response Body: /*Será retornado um JSON com algumas informações do usuário*/

{    
  "usuario": "Rafael",
  "email": "rafael@example.com"
}
```

```javascript
/*Token inválido*/
Status: 401
Response Body: Token inválido
```

```javascript
/*Credencial inválida*/
Status: 403
Response Body: /*o corpo da resposta virá vazio*/
```

<br>

<h2 id="criptografia">Criptografar dados que serão enviados ao servidor</h2>

Este tópico abordará sobre a necessidade de criptografar os dados que serão enviados para servidor.

Método de criptografia assimétrica RSA.

<b>Obs: os dados só serão aceitos pelo servidor se eles estiverem criptografados pela chave pública.</b>

### Chave Pública
```markdown
-----BEGIN PUBLIC KEY-----MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJLJPbG2Gw9A5RmEe4MdLkeYNBQADFVsMhNHdO0f3vvfFz+Pln3F857fUFHBFReJ9L0BlEDCRNRn+0g6cPpnx30CAwEAAQ==-----END PUBLIC KEY-----
```

### Biblioteca JavaScript, JSRSASIGN, para criptografia dos dados

Créditos: https://github.com/kjur/jsrsasign

<br>

## Importar Biblioteca

Adicione este trecho de código à seção <b>\<HEAD></b> do seu código HTML:
```html
<script src="https://cdnjs.cloudflare.com/ajax/libs/jsrsasign/10.5.25/jsrsasign-all-min.js"></script>
```

###  Método de criptografia de dados utilizado pelo projeto

```javascript
function encrypt(data) {
  const publicKeyPEM = `-----BEGIN PUBLICKEY-----MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJLJPbG2Gw9A5RmEe4MdLkeYNBQADFVsMhNHdO0f3vvfFz+Pln3F857fUFHBFReJ9L0BlEDCRNRn+0g6cPpnx30CAwEAAQ==-----END PUBLIC KEY-----`;
  const publicKey = KEYUTIL.getKey(publicKeyPEM);
  const decryptedData = stringToArray(JSON.stringify(data));
  const encryptedData = [];

  decryptedData.forEach(element => {
    const encryptedHex = KJUR.crypto.Cipher.encrypt(element, publicKey);
    encryptedData.push(hextob64(encryptedHex));
  });

  return encryptedData;
}

function stringToArray(inputString) {
  let resultArray = [];
  let currentIndex = 0;
  let maxLength = 53;

  while (currentIndex < inputString.length) {
    let segment = inputString.slice(currentIndex, currentIndex + maxLength);
    resultArray.push(segment);
    currentIndex += maxLength;
  }

  return resultArray;
}
```
