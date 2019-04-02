# README

Implementazione dell'algoritmo NEAT ([Evolving Neural Networks through Augmenting Topologies, Kenneth O. Stanley, Risto Miikkulainen](http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf)).

## Definizione prossima generazione

Dato:

- popolazione di N elementi
- M specie

La successiva generazione sarà generata:

- ordinando le __M__ specie in base al miglior score
- [x] elimino le specie anziane che non hanno avuto un miglioramento dello score migliore di almeno __X__ %
- [x] per ogni specie anziana, elimino gli ultimi __I__ geni
- [x] per ogni specie con almeno 5 membri, copio i campioni nella prossima generazione, altrimenti copio il campione mutato
- [ ] fino ad arrivare a __N__ elementi nella nuova specie:
  - [x] seleziono in modo random un gene dal pool, con probabilità basata su __f'__
  - [x] lo copio, quindi:
    - [x] con probabilità __p1__, applico mutazioni (aggiunta nodi, aggiunta connessioni, disabilitazione connessioni)
    - [x] con probabilità __p2__, applico mutazioni (variazione pesi)
    - [ ] con probabilità __p3__, offspring: seleziono in modo random un gene dal pool, con probabilità basata su __f'__, differente da quello appena selezionato

## Installazione

```cmd
mvn clean package
```

## Esecuzione

```cmd
java -jar target\neat-impl-0.0.1.jar
```