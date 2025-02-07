# HAG API testing katalog

En maskinporten token server og katalog av HTTP requests som automatisk henter tokens.

Serveren bruker din eksisterende `kubectl` config for å hente maskinporten tokens.

Husk at du må være logget inn med  `gcloud auth login`.

## Start token server

```
./release/bin/start
```
Eller
```
gradle run
```

## Eksempler

### **Curl:**  
Les en dialog fra Dialogporten med id `0194bc95-97b4-7240-961f-9663743d4518` med token for `sykepenger-im-lps-api`  
```
curl -X 'GET' \
'https://platform.tt02.altinn.no/dialogporten/api/v1/serviceowner/dialogs/0194bc95-97b4-7240-961f-9663743d4518' \
-H 'accept: application/json' \
-H "Authorization: Bearer $(curl -sX GET http://localhost:4242/token/sykepenger-im-lps-api)" \
| jq
```
### **Bruno:**
```
brew install bruno && open /Applications/Bruno.app
```
Mac menu bar: `Bruno` > `Open Collection` > `hag-api-testing-katalog/kataloger/dialogporten`

### **Postman:**
```
brew install postman && open /Applications/Postman.app
```
Mac menu bar: `File` > `Import Collection` > `hag-api-testing-katalog/kataloger/postman.json`


## Endpoints


```
HTTP GET: 
http://localhost:4242/token/sykepenger-im-lps-api

Response: 
eyJraOiwKvLNpXieJdPncEitGjVFokDmal... (maskinporten token)
```

## Hvordan fungerer serveren?

Serveren bruker lokalt konfigurert kubectl config i dev-gcp miljøet.

**Route: `http://localhost:4242/token/{tjeneste-navn}`**

Serveren finner første substring match for `{tjeneste-navn}` i listen av maskinporten secrets tilgjengelige for helsearbeidsgiver.

Maskinporten JWK secrets for dev-gcp blir "cached" i minne til serveren.

Disse JWK secrets brukes for å hente nye maskinporten tokens for hver request for `{tjeneste-navn}`.


## Obs!

- Om du prøver en å hente token for en ny tjeneste som ikke er "cached" fra tidligere så kan du være utlogget på GCloud.
- JWK secrets kan blu utdaterte! Da må serveren startes på nytt for å få "cache" nye JWK verdier.


## Ekstra

Et [påskeegg](https://www.nrk.no/filmpolitiet/et-annerledes-paskeegg-1.17237361) er at port 4242 som serveren bruker er *hag* skrevet på [T9 tastatur](https://no.wikipedia.org/wiki/T9):

```
 _______________________
| 1     | 2 abc | 3 def |
| -     |       |       |
|_______|_______|_______|
| 4 ghi | 5 jkl | 6 mno |
|       |       |       |
|_______|_______|_______|
| 7 pqrs| 8 tuv | 9 wxyz|
|       |       |       |
|_______|_______|_______|
|   *   |   0   |   #   |
|       |       |       |
|_______|_______|_______|
```