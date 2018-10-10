# belem42

## build
mvn clean package

## Launch
mvn tomcat7:run-war


# Bienvenue sur l'application Hackathon PackApp de l'équipe Belem42 !

## Pourquoi Belem42 ?
Bah... c'est simple, le Belem est un des premiers portes conteneurs, bien avant Mesos, Swarm ou Kubernetes ! Et 42 car c'est la réponse à la question.

 <img src="https://raw.githubusercontent.com/bnpinel/belem42/master/readme_belem.png"
     alt="Le Belem"
     width="70%" />
     
## L'équipe

TODO photo de l'équipe avec les Tshirt

De gauche à droite :
* Jean-Louis Etienne
* Julien Vinet
* Benjamin Pinel
* Fabien Hippolyte

Toute l'équipe est issue du pôle Usine Logicielle LCL


## Démarche

A fond, à fond, à fond, comme à l'Usine Logicielle ! :grimacing:

Avant d'attaquer la partie déploiement sur AWS, nous avons commencé par créer des micro-services REST/json avec spring-boot pour séparer le front-end du back-end.
Comme base de données conteneur ready, nous avons choisi mongoDB car nous l'utilisons au quotidien sur l'usine logicielle LCL, sous Docker, nous maitrisons cette technologie et elle donne de bons résultats.
Dans un premier temps nous avons décidé de conserver le front jsp et nous l'avons modifié pour qu'il invoque les micro-services.


TODO : à compléter


## Use cases couverts

- [X] `ViewAdvisors` : voir une liste de conseillers et leurs spécialités (none, savings, credits ou insurance)<br/>
- [X] `ViewCustomer` : voir les informations relatives à un client<br/>
- [X] `EditCustomer` : mettre à jour les informations relatives à un client<br/>
- [X] `AddCustomer` : ajouter un nouveau client au système<br/>
- [X] `ViewCard` : voir les informations relatives à une carte bleue<br/>
- [X] `EditCard` : mettre à jour les informations relatives à une carte bleue<br/>
- [X] `AddCard` : ajouter une nouvelle carte bleue au système<br/>
- [X] `ViewPayment` : voir des informations relatives à l'historique de paiement d’une carte bleue<br/>
- [X] `AddPayment` : ajouter des informations relatives à un paiement (nature du paiement)<br/>
- [ ] `Monitoring` : monitorer chaque appels et leur durée<br/>
- [ ] `Logging` : centraliser les logs de l'application


    