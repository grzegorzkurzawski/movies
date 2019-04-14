# Movies management system

Movies management service exposes REST API.

User can:
* create a movie
`curl -X POST http://localhost:9000/movies -H "Content-Type: application/json" -d '{"title":"myTitle","director":"Adam Kowalski","actors":["Marian Walusz", "Jan Malinowski"]}'`
* add review to selected movie:
`curl -X POST http://localhost:9000/movies/1/review -H "Content-Type: application/json" -d '{"rating":3}'`
* get list of movies:
`curl http://localhost:9000/movies`
* delete selected movie:
`curl -X DELETE http://localhost:9000/movies/1`

More information in [Swaggger file](swagger.yaml)
