$ErrorActionPreference = "Stop"

Write-Host "==> Building jar..." -ForegroundColor Cyan
./mvnw clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed — stopping deploy." -ForegroundColor Red
    exit 1
}

Write-Host "==> Uploading jar to VM..." -ForegroundColor Cyan
gcloud compute scp target/telangana-special-backend-0.0.1-SNAPSHOT.jar springboot-server:/home/Lahari/app.jar --zone=us-central1-a

if ($LASTEXITCODE -ne 0) {
    Write-Host "Upload failed — stopping deploy." -ForegroundColor Red
    exit 1
}

Write-Host "==> Deploying and restarting service on VM..." -ForegroundColor Cyan
gcloud compute ssh springboot-server --zone=us-central1-a --command="sudo cp /home/Lahari/app.jar /home/laharithallapally27/telangana-special-backend/target/telangana-special-backend-0.0.1-SNAPSHOT.jar && sudo chown laharithallapally27:laharithallapally27 /home/laharithallapally27/telangana-special-backend/target/telangana-special-backend-0.0.1-SNAPSHOT.jar && sudo systemctl restart springboot-app.service && sleep 3 && sudo systemctl status springboot-app.service --no-pager"

Write-Host "==> Deploy complete." -ForegroundColor Green