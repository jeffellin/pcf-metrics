#!/bin/bash

echo "hello world"

VERSION=$(cat release/version)
ls
set -ex
ls release
cd release
tar xvf release-${VERSION}.tgz
# pks login -a ${PKS_ENDPOINT} -u ${PKS_USER} -p ${PKS_PASSWORD} -k
# pks get-credentials ${PKS_CLUSTER}
#kubectl get pods
template=`cat "spring-demo-pod.yml" | sed "s/{VERSION}/$VERSION/g"`
# apply the yml with the substituted value
echo "$template" > ../pks-build/spring-demo-modified.yml