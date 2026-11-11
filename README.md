Gruppe F

## Deltagere

| Navn | CPH-email | GitHub-navn | Hold |
|------|-----------|-------------|------|
| Seif Rabih Ahmad | cph-sa560@stud.ek.dk | Britzgrank1 | Hold B |
| Tobias Hardø | cph-th464@stud.ek.dk | tobiashardoe | Hold B |
| Emil Ozel | cph-eo97@stud.ek.dk | EmilOzel | Hold B |
| Nana Yaw | cph-na289@stud.ek.dk | nykantwi | Hold B |


Video-demo (2–3 min):
https://drive.google.com/file/d/1PDARhyqS7rFYRa7FFv4AhUMFH6puR0q1/view?usp=sharing

Rapport:
https://docs.google.com/document/d/1aDJy6kmP69k9YUZ9B35q5qwoAw_JaEwPi0A0umvpu7I/edit?tab=t.0#heading=h.6nzdcirtywlx


## Deployment på egen server

Demobutik: https://cupcake.antwi.dk/

Byg med Java 21+ og Maven: `mvn clean package`. JAR-filen er `target/lifehack.jar`.
Docker Compose-filen bruger netværkene `cit_default` og `carport_default` på serveren.
Cupcake har sin egen database og bruger i `carport-db-1`. Data ligger i den
PostgreSQL-containers vedvarende volume.

`DEPLOYED=true` aktiverer miljøvariablerne `JDBC_USER`, `JDBC_PASSWORD`,
`JDBC_CONNECTION_STRING` (med `%s` som databasenavn) og `JDBC_DB`.
`SERVER_ORIGIN=https://cupcake.antwi.dk` aktiverer sikre sessionscookies og kontrol
af afsenderen ved POST-requests. `DEMO_MODE=true` giver nye brugere 100 fiktive kroner.
Den tilstand vises som en demomeddelelse i brugerfladen.

Det rene skema og produktdata ligger i `src/main/resources/sql/init-production.sql`.
Kør filen én gang i en tom database som databaseejeren. Den importerer ingen gamle
kunder, adgangskoder eller ordrer. Ved brug af et eksisterende lokalt skema skal
password-kolonnen først udvides til hashes med
`ALTER TABLE customers ALTER COLUMN password TYPE VARCHAR(255);`.

På serveren ligger installationen i `/opt/cupcake`. Adgangskoden i `.env` genereres
på serveren og skal holdes uden for Git. Opdater ved at kopiere den byggede JAR til
`/opt/cupcake/target/lifehack.jar` og køre `docker compose up -d --build` derfra.
Se status med `docker compose ps` og logs med `docker compose logs --tail=100`.

Caddy i `/opt/cit/Caddyfile` sender `cupcake.antwi.dk` til `cupcake-app:7070`.
Databasen kan sikkerhedskopieres på serveren med
`docker exec carport-db-1 pg_dump -U cupcake -d cupcake > cupcake-backup.sql`.
Gem backups privat og kopiér dem også væk fra serveren.
