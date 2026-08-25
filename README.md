# MS etudiant-backend

Backend qui gère les APIs des utilisateurs de la bibliothèque et les CRUD des étudiants.

## Configuration du backend

    - name: etudiant-backend
    - port: 8080

## Pré-requis pour le bon fonctionnement du service :

    -> JDK 21
    -> Docker
    -> Docker Compose
    -> Maven 3.9.3 (https://archive.apache.org/dist/maven/maven-3/3.9.3/binaries/) ou plus

## Démarrage du backend
Pour démarrer le projet backend, il faut : 
- avoir démarré Docker-Desktop sur votre poste de travail local.
- dans une console, se placer à la racine du projet et exécuter la commande Maven suivante :
```
mvn spring-boot:run
```

Cette commande va : 
 - initialiser le container Docker qui contient la base de données 
 - lancer le serveur du backend et le connecter à la base de données précédemment créée

Les traces logs devraient ressemblées à ceci : 
```
.   ____          _            __ _ _
/\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
\\/  ___)| |_)| | | | | || (_| |  ) ) ) )
'  |____| .__|_| |_|_| |_\__, | / / / /
=========|_|==============|___/=/_/_/_/

:: Spring Boot ::                (v3.5.5)

[etudiant-backend] [           main] c.o.etudiant.EtudiantBackendApplication  : Starting EtudiantBackendApplication using Java 21.0.3 with PID 6964
[etudiant-backend] [           main] c.o.etudiant.EtudiantBackendApplication  : No active profile set, falling back to 1 default profile: "default"
[etudiant-backend] [           main] .s.b.d.c.l.DockerComposeLifecycleManager : Using Docker Compose file ******etudiant-backend\compose.yaml*****
[etudiant-backend] [utReader-stderr] o.s.boot.docker.compose.core.DockerCli   :  Container etudiant-backend-mysql-1  Created
[etudiant-backend] [utReader-stderr] o.s.boot.docker.compose.core.DockerCli   :  Container etudiant-backend-mysql-1  Starting
[etudiant-backend] [utReader-stderr] o.s.boot.docker.compose.core.DockerCli   :  Container etudiant-backend-mysql-1  Started
[etudiant-backend] [utReader-stderr] o.s.boot.docker.compose.core.DockerCli   :  Container etudiant-backend-mysql-1  Waiting
[etudiant-backend] [utReader-stderr] o.s.boot.docker.compose.core.DockerCli   :  Container etudiant-backend-mysql-1  Healthy
[etudiant-backend] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
[etudiant-backend] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 39 ms. Found 1 JPA repository interface.
[etudiant-backend] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
[etudiant-backend] [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
[etudiant-backend] [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.44]
[etudiant-backend] [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
[etudiant-backend] [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1354 ms
[etudiant-backend] [           main] o.hibernate.jpa.internal.util.LogHelper  : HHH000204: Processing PersistenceUnitInfo [name: default]
[etudiant-backend] [           main] org.hibernate.Version                    : HHH000412: Hibernate ORM core version 6.6.26.Final
[etudiant-backend] [           main] o.h.c.internal.RegionFactoryInitiator    : HHH000026: Second-level cache disabled
[etudiant-backend] [           main] o.s.o.j.p.SpringPersistenceUnitInfo      : No LoadTimeWeaver setup: ignoring JPA class transformer
[etudiant-backend] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
[etudiant-backend] [           main] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection com.mysql.cj.jdbc.ConnectionImpl@4db16677
[etudiant-backend] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
[etudiant-backend] [           main] org.hibernate.orm.connections.pooling    : HHH10001005: Database info:
[etudiant-backend] [           main] o.h.e.t.j.p.i.JtaPlatformInitiator       : HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
[etudiant-backend] [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
[etudiant-backend] [           main] JpaBaseConfiguration$JpaWebConfiguration : spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
[etudiant-backend] [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 1 endpoint beneath base path '/actuator'
[etudiant-backend] [           main] eAuthenticationProviderManagerConfigurer : Global AuthenticationManager configured with AuthenticationProvider bean with name authenticationProvider
[etudiant-backend] [           main] r$InitializeUserDetailsManagerConfigurer : Global AuthenticationManager configured with an AuthenticationProvider bean. UserDetailsService beans w
ill not be used by Spring Security for automatically configuring username/password login. Consider removing the AuthenticationProvider bean. Alternatively, consider using the UserDetailsService in a manually instantiated DaoAuth
enticationProvider. If the current configuration is intentional, to turn off this warning, increase the logging level of 'org.springframework.security.config.annotation.authentication.configuration.InitializeUserDetailsBeanManagerConfigurer' to ERROR
[etudiant-backend] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
[etudiant-backend] [           main] c.o.etudiant.EtudiantBackendApplication  : Started EtudiantBackendApplication in 10.27 seconds (process running for 10.642)
```

Sur Docker-Desktop, vous devriez voir apparaître un container MySQL qui correspond au projet.

![1-docker-desktop](pictures/1-docker-desktop.png)

Vous pouvez vous connecter à la base de données et vérifier que la table ```user``` a été créée automatiquement.
Pour cela, cliquez sur le lien `mysql-1` ce qui vous amènera sur la vue complète de la base de données. 
Dans l'onglet ```Exec```, il faut : 

1. se connecter à la base de données. Tapez la commande ci-dessous

    ```
    mysql -u etudiant_db -p
    ```
   L'invite de commande demandera le mot de passe. Il est identique au nom d'utilisateur, c'est-à-dire ```etudiant_db```.


2. Se connecter au schéma de base de données `etudiant_db`. Dans l'invite de commande, tapez la commande ci-dessous :

    ```
    use etudiant_db;
    ```
  
3. Vérifier que la table `user` existe (elle est néanmoins vide pour le moment).

    ```
    select * from user;
    ```
    Le résultat devrait être : `Empty set (0.00 sec)`

La capture d'écran ci-dessous résume les étapes précédentes : 

![2-docker-desktop-bdd](pictures/2-docker-desktop-bdd.png)


## APIs exposées

Toutes les routes `/api/**` sont protégées par authentification **JWT**, sauf `POST /api/register` et `POST /api/login`.

| Méthode | Route                | Description                                              |
|---------|----------------------|----------------------------------------------------------|
| POST    | `/api/register`      | Création d'un utilisateur (agent de la bibliothèque)     |
| POST    | `/api/login`         | Authentification, renvoie un token JWT                   |
| GET     | `/api/me`            | Informations de l'utilisateur connecté (token requis)    |
| GET     | `/api/students`      | Liste des étudiants                                      |
| GET     | `/api/students/{id}` | Détail d'un étudiant                                     |
| POST    | `/api/students`      | Création d'un étudiant                                   |
| PUT     | `/api/students/{id}` | Modification d'un étudiant                               |
| DELETE  | `/api/students/{id}` | Suppression d'un étudiant                                |

Les requêtes sur les routes protégées doivent porter l'en-tête :

```
Authorization: Bearer <token>
```

Une collection Bruno de test est disponible dans le dossier `API - Backend` du dépôt : elle couvre l'authentification, le CRUD étudiants et les cas d'erreur (token absent, email invalide ou déjà utilisé, ressource introuvable).

## Exécution des tests

Pour exécuter les tests JUnit, il faut :
- avoir démarré Docker-Desktop sur votre poste de travail local. Cette étape est nécessaire car les tests d'intégration utilisent **Testcontainers** pour créer des bases de données MySQL temporaires de test.
- dans une console, se placer à la racine du projet et exécuter la commande Maven suivante :

```
mvn test
```

Pour exécuter tous les tests **et** vérifier le seuil de couverture JaCoCo :

```
mvn verify
```

### Détails des tests

- **39 tests** au total, tous verts : tests unitaires (services, mappers, JwtService) et tests d'intégration (`StudentControllerTest`, `UserControllerTest`).
- Les tests d'intégration démarrent un container MySQL **8.4** via Testcontainers (l'image `mysql:latest` est incompatible avec la configuration injectée par défaut).
- Testcontainers est en version **1.21.4**, requise pour être compatible avec Docker Engine 29 / Docker Desktop récent.
- Chaque test d'intégration repart d'une base propre : le `@AfterEach` vide les tables et réinitialise l'`AUTO_INCREMENT`.

### Couverture JaCoCo

La commande `mvn verify` génère le rapport dans `target/site/jacoco/` et applique un seuil minimal de **80 % de lignes couvertes** sur les packages `service` et `mapper` :

- `service` : **98,63 %**
- `mapper` : **88,89 %**

## Fonctionnalités portées

    - API de création d'un utilisateur (agent de la bibliothèque)
    - API d'authentification avec génération et validation d'un token JWT
    - API d'information sur l'utilisateur connecté (/api/me)
    - APIs CRUD complètes des étudiants de la bibliothèque
    - Sécurisation des routes par filtre JWT (Spring Security)
    - Validation Bean Validation (@Valid, @Email) et gestion centralisée des erreurs


## Écrans ou blocs concernés

    - Authentification (login / register du frontend)
    - Liste, détail, création et édition des étudiants



