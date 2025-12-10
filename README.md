# 📚 Application de Gestion et Achat de Livres (LABO RTI - Partie 2)

Ce projet est une application client-serveur **Java** multi-threadée dédiée à la gestion d'une librairie et à l'achat de livres. Il constitue la seconde partie du laboratoire de **Réseaux et Technologies Internet**.

L'application implémente une architecture complète utilisant **Java Sockets**, **JDBC avec le pattern DAO**, et une interface graphique **Swing**.

## ✨ Fonctionnalités

### 🖥️ Partie Serveur (`ServeurAchat` -\> `MainServeur`)

* **Architecture Multi-threadée :**
    * **Mode Pool de Threads :** Gestion simultanée de plusieurs clients via un pool de threads pré-instanciés.
    * **Mode À la demande :** Création dynamique des threads en fonction des connexions entrantes.
* **Protocole :** Implémentation du protocole personnalisé **BSPP** (Books Shopping Payment Protocol).
* **Sécurité (TLS) :** Support du chiffrement TLS pour les sockets (activable/désactivable via configuration).
* **Base de Données :** Accès aux données via JDBC, encapsulé proprement dans des objets DAO.
* **Configuration (`configServeur.properties`) :** Fichier permettant de définir :
    * Le mode de gestion des threads (`pool=true/false`).
    * Le numéro de port d'écoute.
    * La taille du pool (nombre de threads créés à l'avance).

### 🛒 Partie Client (`MainWindowsClientAchat`)

* **Interface Vendeur :** GUI réalisée en **Swing** pour les vendeurs en magasin.
* **Fonctionnalités Principales :**
    * Gestion du panier (Caddy).
    * Recherche de livres.
    * Identification et gestion des clients.
* **Sécurité (TLS) :** Option de configuration pour activer/désactiver le chiffrement TLS lors de la connexion au serveur.

-----

## 📂 Architecture du Code

```text
src/
|-- model/
|   |-- DAO/                # Accès aux données (AuthorDAO, ConnectDB, ...)
|   |-- entity/             # Objets métiers (Author, Book, Caddy, ...)
|   |-- networking/         # Gestion des sockets et implémentation du protocole BSPP
|   |-- searchViewModel/    # Logique de recherche des différentes entités
|   |-- test/               # Tests unitaires (DAO, entités et connexion BD)
|
|-- ServeurGeneriqueTCP/    # Implémentation du pattern d'un Serveur TCP générique
|   |-- ...
|
|-- VIEW/                   # Interface graphique (Swing) pour le Client Achat
```

-----

## ⚙️ Prérequis

* **Java JDK :** Version 21.
* **Base de Données :** MySQL Server.
* **Bibliothèques (situées dans `/lib`) :**
    * `mysql-connector-j-xxxx.jar`
    * `protobuf-java`

-----

## 🚀 Installation et Configuration

### 1\. Base de Données

Le script de création de la base de données est disponible ici :
👉 [Script de création BD (GitHub)](https://github.com/mbaluce42/RTI_LABO/blob/master/BD_Books/CreationBD.cpp)

> **Note :** Modifiez les paramètres de connexion (`mysql_real_connect(...)` ou équivalent SQL) pour correspondre à votre instance locale MySQL.

### 2\. Configuration JDBC

Avant de lancer l'application, mettez à jour les informations de connexion (URL, User, Password) dans le fichier source suivant :
`src/MODEL/DAO/ConnectDB.java`

### 3\. Configuration Réseau

Le serveur écoute par défaut sur le port **50001**. Assurez-vous que ce port est ouvert et disponible sur votre machine.

### 4\. Sécurité (TLS)

Pour activer ou désactiver la couche sécurisée (TLS) :

* Changez le booléen `SECURE` (`true` pour sécurisé, `false` pour clair) directement dans le **Main du Client** et le **Main du Serveur**.

-----
