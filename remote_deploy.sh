#!/bin/bash
set -e

echo "whoami: $(whoami)"

echo "--- BEFORE ---"
sudo md5sum /home/laharithallapally27/telangana-special-backend/target/telangana-special-backend-0.0.1-SNAPSHOT.jar

sudo cp /home/Lahari/app.jar /home/laharithallapally27/telangana-special-backend/target/telangana-special-backend-0.0.1-SNAPSHOT.jar
echo "CP_SUCCESS"

sudo chown laharithallapally27:laharithallapally27 /home/laharithallapally27/telangana-special-backend/target/telangana-special-backend-0.0.1-SNAPSHOT.jar
echo "CHOWN_SUCCESS"

echo "--- AFTER ---"
sudo md5sum /home/laharithallapally27/telangana-special-backend/target/telangana-special-backend-0.0.1-SNAPSHOT.jar

sudo systemctl restart springboot-app.service
echo "RESTART_SUCCESS"

sleep 3
sudo systemctl status springboot-app.service --no-pager