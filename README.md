# PROTOCOLLO DI COMUNICAZIONE

README che descrive il protocollo di comunicazione utilizzato per il collegamento e l'interazione tra client e server.

## AVVIENE IL COLLEGAMENTO DEL SOCKET AL SERVER

### Inserimento UserName

- **Client:** `CONNECT <nome>`
- **Server:** 
  -  `JOIN <username>`  (se username disponibile, invia globalmente)
  -  `KO user-not-available` (se username non disponibile, invia solo al client)

### Utente Vuole Cambiare Nickname

- **Client:** `CHANGE <new name>`
- **Server:** 
  -  `ACCEPT <username>`  (se username disponibile, invia globalmente)
  -  `KO user-not-available` (se username non disponibile, invia solo al client)

### Chat Privata

- **Client:** `PRIVATE <destinatario> <messaggio>`
- **Server al mittente:**
  -  `OK` (se utente destinatario esistente, invia solo al client mittente)
  -  `KO user-not-found` (se utente destinatario non esiste, invia solo al client mittente)
- **Server destinatario:** `PRIVATE <mittente> <messaggio>` 

### Lista Utenti Attivi

- **Client:** `USERS` (richiede la lista di utenti online al server)
- **Server:** `USERS <...> <...> <...> <...> <...>` (lista utenti divisi da uno spazio, invia solo al client che l'ha richiesto)

### Chat Globale

- **Client:** `GLOBAL <messaggio>`
- **Server manda a tutti:** `GLOBAL <mittente> <messaggio>` (inviato globalmente, tranne al mittente)

### Disconnessione

- **Client:** `ESC`
- **Server:** `BYE <utente>` (inviato globalmente, tranne al mittente)
