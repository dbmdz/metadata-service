How to Upgrade PostgreSQL
=========================

To upgrade the PostgreSQL container there are a few steps
to be done: First dump the current database(s), destroy the old container,
place the dump into the init directory and finally build and start the new container.

Following, the steps are listed in particular:

1. Start your current container(s)

   ```bash
   docker compose start
   ```

2. Ensure that there are not any `*.sql*` or `*.dump*` files in `postgres-initdb.d/`;
   you may "disable" them by appending a tilde (`~`) to existing file names

3. Dump the current container's database into the `postgres-initdb.d/` directory

   ```bash
   docker run --rm \
     --mount type=bind,src=/etc/passwd,dst=/etc/passwd,ro \
     --mount type=bind,src=./postgres-initdb.d/,dst=/extdir \
     --network metasvc_default \
     --user "$(id -u):$(id -g)" \
     postgres:15.13-alpine \
       pg_dump -d postgres://cudami:somepassword@database/cudami -Fc -f /extdir/cudami.dump
   ```

4. Tear down all containers and their volumes

   ```bash
   docker compose down -v --rmi all
   ```

5. Build and start the new containers by *either*

   ```bash
   docker compose up --build --detach
   ```

   *or* in two separate steps

   ```bash
   docker compose build
   docker compose up --detach
   ```

The restore of the dump may take a while. The health status shown in the column `STATUS` when
you run `docker ps` will tell you when the restore is done ("healthy").

You are all done!

> [!Note]
> If the initialisation (restore of the database) takes more than an hour
> then the container might be stopped. In this case, please increase the duration
> specified under `services.database.healthcheck.start_period` in the `compose.yaml`.

