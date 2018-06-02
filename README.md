# SupFile API [![pipeline status](https://gitlab.com/Douffy/supdrive-api/badges/master/pipeline.svg)](https://gitlab.com/Douffy/supdrive-api/commits/master)

## Database
* Host : https://supdrive.lemairepro.fr/adminer
* User : `supdrive`
* Password : `wfLMnM9KZGu6`
* Database name : `supdrive`

## Production URL
* https://supdrive.lemairepro.fr/api

## Postman Collection (not updated)
* https://s3.eu-west-3.amazonaws.com/supdrive/Supdrive+API.postman_collection.json

## Sql scripts
* Let Spring create the schema on startup
* [Insert roles and offers](https://gitlab.com/Douffy/supdrive-api/snippets/1717564)

You can start using this API !

## Start with docker
```bash
$ git clone https://gitlab.com/Douffy/supdrive-api.git
$ cd supdrive-api
$ docker-compose up
```
Play [sql scripts](#sql-scripts) before start using !

## Contributors
* Jean Debuisson [jean.debuisson@supinfo.com](mailto:jean.debuisson@supinfo.com)
