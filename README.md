# urlShortnerG7

The web app allows the user to shorten the given URIs, allowing the option to use a custom word in the shortening and the option to generate an additional QR code to said shortened URI.

The web app also allows the user to shorten all the URIs given in a csv file with the option of employing a custom word and generate a qr and returns a modified csv with the original URI, the shortened version and the qr version.  
In order to use the bulk conversion, the csv must follow the common format of csv files described in here https://datatracker.ietf.org/doc/html/rfc4180  
An example of an acceptable csv file would be:
```
https://www.example.com/,ejemplo,true
https://www.youtube.com/,videos,false
https://github.com/,,true
https://elpais.com/,,false
```