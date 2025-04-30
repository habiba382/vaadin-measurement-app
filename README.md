# Vaadin  REST API -sovellus

Tämä sovellus on toteutettu Java Spring Boot -alustalla käyttäen Vaadin kirjastoa. Sovellus toimii selaimessa osoitteessa [http://localhost:9090](http://localhost:9090). Se tarjoaa käyttöliittymän eri tietokantaentiteettien hallintaan sekä REST-rajapinnat taustatoiminnoille.

## 🔧 Käyttöönotto

### Vaatimukset
- Java 17 tai uudempi
- Maven
- Selain

### Käynnistys
1. Kloonaa repository:
   ```bash
   git clone <repository-url>
   ```
2. Aja sovellus projektin juuressa:
   ```bash
   ./mvnw spring-boot:run
   ```
3. Avaa selaimessa: [http://localhost:9090](http://localhost:9090)

## 🔐 Kirjautuminen

Sovellus vaatii kirjautumisen:

- **Admin-tunnus:** `admin / admin`
- **User-tunnus:** `user / user`

## 🖥️ Sovelluksen sisältö

Sovelluksessa on useita näkymiä ja toimintoja:

### 📊 Dashboard
Yleiskuva sovelluksesta ja navigointipaneeli.

### 👤 Person-näkymä
- Henkilötietojen luonti, muokkaus, poisto ja haku.
- 1–1-suhde Address-entiteettiin.

### 🏠 Address-näkymä
- Osoitteiden hallinta.
- Liittyy henkilöihin 1–1-suhteella.

### 🌡️ Measurement-näkymä
- Mittaustietojen hallinta.
- Liittyy paikkaan 1–m-suhteella.

### 📍 Place-näkymä
- Paikkojen hallinta ja niihin liittyvien mittausten tarkastelu.

### 🔑 Admin-näkymä
- Vain admin-käyttäjille.
- Käyttäjien ja roolien hallinta (m–m-suhde).

### 🔓 Login-näkymä
- Käyttäjän tunnistus ja sisäänkirjautuminen.

## 🔍 Ominaisuudet

- CRUD-toiminnot neljälle entiteetille
- Relaatiosuhteet (1–1, 1–m, m–m)
- Suodatusmahdollisuudet Grid-komponenteissa
- Spring Security -pohjainen roolipohjainen käyttöoikeuksien hallinta
- Yksinkertainen tyylien kustomointi
- SPA-tyylinen rakenne navigaatiolla

## 💭 Itsearviointi

Projektin tekeminen syvensi ymmärrystäni Vaadinin rakenteesta, erityisesti suodatusten toteuttamisesta Grid-komponentissa ja roolipohjaisesta tietoturvasta. Relaatiotietojen hallinta ja näkymien jäsentely olivat opettavaisia osia. Sovellus vastaa hyvin annettua tehtävänantoa ja on toiminnassa selaimessa localhost-portissa 9090.
