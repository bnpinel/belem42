PROJECT=${PWD##*/}
VERSION=$1
TAG=$2
REGISTRY=registry.hackathon-container.com/packapp04

if [ "$#" -ne 1 ]; then
    echo "Indiquer le numéro de version"
fi

mvn versions:set -DnewVersion=$VERSION

mvn clean package

docker build --build-arg version=$VERSION  --build-arg jarname=$PROJECT -t $REGISTRY/$PROJECT:$VERSION .

if [ "$TAG" = "tag" ]; then
	docker login registry.hackathon-container.com -u "hackathon" -p "welcome-k8s!"
	docker push $REGISTRY/$PROJECT:$VERSION
fi
