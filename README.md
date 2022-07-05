# Goal

Create simple url shortener. In first version it will be unauthenticated service which means that anyone can use API to shorted URL without registration.

# Requirements

## API endpoints

### /url/shorten

Given a full URL, returns a short URL. Currently, the API only supports shortening a single URL per API call.

**Method:** GET

**Request Params:** longUrl

**Response:** Returns short url data on success

**Example Call:**

```
http://localhost/url/shorten?longUrl=https://www.imdb.com/title/tt8690918/episodes
```

Example Response:

```json
{
  "longUrl": "https://www.imdb.com/title/tt8690918/episodes",
  "shortUrl": "http://localhost/u/924"
}
```

### /url/expand

Given a short URL, returns the original full URL.

**Method:** GET

**Request Params:** shortUrl

**Response:** Returns original url data on success

**Example Call:**

```
http://localhost/url/expand?shortUrl=http://localhost/u/924
```

Example Response:

```json
{
  "longUrl": "https://www.imdb.com/title/tt8690918/episodes",
  "shortUrl": "http://localhost/u/924"
}
```


## Short URL handler

Service must handle GET requests to `/u/XXX` endpoint and redirect client to long url using HTTP 301 code. There are several redirection codes, but we should use 301 which is permanent redirect. It means user's browser will remember where to redirect next time user opens short url without calling our service.
