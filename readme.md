 
 # Run project 
 mvn clean spring-boot:run ou mvn spring-boot-run

 # Registro Cliente
 curl -i -X POST http://localhost:8080/users/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Nome do Cliente","cpf":"14587080047","login":"maria","password":"123456"}'

# login
TOKEN=$(curl -s -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{"login":"maria","password":"123456"}' | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')
echo "$TOKEN"

## Token gerado
ao gerar o token coloque no lugar do $TOKEN 

# 1) depósito
curl -i -X POST http://localhost:8080/bank/transactions \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"type":"deposit","amount":100.00}'

# 2) pagamento
curl -i -X POST http://localhost:8080/bank/transactions \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"type":"payment","amount":200.00}'

# 3) depósito (aplica 2% — saldo final 98)
curl -i -X POST http://localhost:8080/bank/transactions \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"type":"deposit","amount":200.00}'

# 4) histórico
curl -i -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/bank/me/summary?limit=50"



# CPF para usar
37009879605 → 370.098.796-05

28510048088 → 285.100.480-88

16250320725 → 162.503.207-25

96550207460 → 965.502.074-60

09530151900 → 095.301.519-00

68941855659 → 689.418.556-59

19910496097 → 199.104.960-97

82373150905 → 823.731.509-05

35299656858 → 352.996.568-58

45213846254 → 452.138.462-54