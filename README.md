# Carservice demo

built on Spring-boot, Webflux, H2 in-memory, H2-R2DBC

## Build & test


```bash
mvnw clean test
```

## Run


```bash
mvnw spring-boot:run
```

## Usage

* List cars:
```python
curl -X GET 'localhost:8080/car'
```
* Create car. VIN is a unique car identification. Make and model is validated against https://vpic.nhtsa.dot.gov/api/ :
```python
curl -X POST 'localhost:8080/car' -d '{"vin":"330-25864-8", "make":"AUDI", "model":"A7"}' --header 'Content-Type: application/json'
curl -X POST 'localhost:8080/car' -d '{"vin":"5555-4444-3333", "make":"HONDA", "model":"Civic", "plateNumber":"HHH-000"}' --header 'Content-Type: application/json'
```
* Get car lock state:
```python
curl -X GET 'localhost:8080/car/330-25864-8/lockstate'
```
* Lock car. Keep "keylock" returned. Will be required for unlock operation:
```python
curl -v -X PATCH 'localhost:8080/car/330-25864-8/lock'
```
* Unlock car:
```python
curl -v -X PATCH "localhost:8080/car/330-25864-8/unlock/{keyLock returned from lockCar}"
```

