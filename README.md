# 💸 ByteBudget

> Un'applicazione moderna e leggera per la gestione delle spese personali, sviluppata in **Java** con interfaccia Web interattiva e persistenza su **MySQL**.

---

## 🚀 Caratteristiche
* **Architettura modulare:** Separazione pulita tra logica di backend (Java HTTP Server), gestione del database (JDBC) e interfaccia frontend.
* **Interfaccia Web Moderna:** Design minimale in stile dark mode con animazioni fluide, pop-up dinamici e calcolo del totale in tempo reale tramite API asincrone (`fetch`).
* **Doppia modalità d'uso:** Accessibile sia tramite un comodo server locale sul browser che da riga di comando (CLI).
* **Sicurezza:** Utilizzo di `PreparedStatement` per prevenire attacchi di SQL Injection.

---

## 🛠️ Tecnologie Utilizzate
* **Linguaggio:** Java (JDK 21+)
* **Database:** MySQL & JDBC
* **Server HTTP:** `com.sun.net.httpserver` (Libreria nativa Java)
* **Frontend:** HTML5, CSS3, JavaScript (Vanilla)

---

## ⚙️ Come Avviare il Progetto

### Prerequisiti
Assicurati di avere installato sul tuo computer:
1. **Java JDK** (versione 17 o superiore)
2. **MySQL Server** attivo in locale (`localhost:3306`)
3. Il driver JDBC MySQL (`mysql-connector-j`) aggiunto alle librerie del progetto.
4. Assicurati prima di avviare che tutte le informazioni su DatabaseSpese.java siano corrette per il collegamento al Database

### Configurazione del Database
Il programma creerà automaticamente il database `expense_tracker` e la tabella `spese` al primo avvio. Se necessario, puoi modificare le credenziali di accesso nel codice sorgente (`DatabaseSpese.java`).

### Avvio tramite IDE (es. IntelliJ IDEA)
1. Clona o scarica la repository.
2. Apri il progetto in IntelliJ.
3. Esegui la classe `WebServerSpese.java` (nel package `http`).
4. Apri il browser e vai all'indirizzo: `http://localhost:8080`.

---

## 📸 Anteprima
[PREVIEW](https://youtu.be/YnuN91-2gYc)

---

## 👤 Autore
Creato con passione da **DarkDev**. Se il progetto ti è piaciuto, lascia una ⭐ alla repository!
