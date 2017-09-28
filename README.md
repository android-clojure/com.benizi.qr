# com.benizi.qr

Simple Clojure/Android app to read QR codes from a phone to use on a computer.

# Background

Mostly an excuse to try out `lein droid`, I wrote most of this in 2015, but am
just now publishing it.  The whole purpose is to easily read QR codes on my
phone, but get the text on my PC (without having to use another
transport/sharing mechanism).

# Usage

Build (requires Leiningen):

```
lein droid doall
```

## Buttons

- "GET QR": use Zebra Xing to scan a QR code, then send it via UDP
- "FROM FIELD": send the text from the text box via UDP (for testing)
- "PACKET": send a packet (for even-lower-level testing)
- "TOUCH ME": the Hello, World! button (displays a message to see it's working)

# Features

- [x] Read a QR code and get it on my PC
- [ ] Don't hard-code the UDP destination
- [ ] Add "batch" mode (but, current way is very clear that QR code was sent)
- [ ] Add ability to initiate QR code read from PC

# License

Copyright © 2015–2017 Benjamin R. Haskell

Distributed under [The MIT License](LICENSE).
