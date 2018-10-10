deploy:
	kubectl create -f ./deployment.yml

deploy-all:
	kubectl create -f ../../back-advisor/deployment/deployment.yml
	kubectl create -f ../../back-customer/deployment/deployment.yml
	kubectl create -f ../../back-card/deployment/deployment.yml
	kubectl create -f ../../back-payment/deployment/deployment.yml
	kubectl create -f ./deployment.yml

deploy-mongodb:
	kubectl create -f ./mongodb-deployment.yml

destroy-mongodb:
	kubectl delete -f ./mongodb-deployment.yml

destroy:
	kubectl delete -f ./deployment.yml

destroy-all:
	kubectl delete -f ./deployment.yml
	kubectl delete -f ../../back-advisor/deployment/deployment.yml
	kubectl delete -f ../../back-customer/deployment/deployment.yml
	kubectl delete -f ../../back-card/deployment/deployment.yml
	kubectl delete -f ../../back-payment/deployment/deployment.yml

