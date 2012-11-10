# BasicOAuth
An attempt to roll my own OAuth client library in Java. The goals of this are the following.

### Initial Goals
- Become aquainted with Java again.
- Introduction to git and GitHub

### Other Things I am learning
- OAuth 1.0(a)
- Diving deeper into HTTP via the HTTPComponents (Apache) library.
- crypto libraries


## Changelog
* 2012-11-10 : Got the logic to get the unauth token working. Also parsing the output.
* 2012-11-04 : Refactored much of the logic. Also discovered that I
    misinterpreted how to generate an OAuth signature (but I think I might still
    be wrong.
* 2012-10-28 : Created a new class to refactor the logic needed to interact with 
    an OAuth server. Pretty much gave up on HTTPS with Imgur. Something appears
    to be wrong with their certificate (see: echo '' | openssl s_client  -connect api.imgur.com:443)
* 2012-10-23 : Became aware of how to parse the response entity to retrieve the
    http payload.
* 2012-10-21 : Initial build of client, request, and response handling.
    Everthing makes sense except for the response.
