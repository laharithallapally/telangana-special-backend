$ErrorActionPreference = "Stop"

Write-Host "==> Building jar..." -ForegroundColor Cyan
./mvnw clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed - stopping deploy." -ForegroundColor Red
    exit 1
}

Write-Host "==> Uploading jar to VM..." -ForegroundColor Cyan
gcloud compute scp target/telangana-special-backend-0.0.1-SNAPSHOT.jar springboot-server:/home/Lahari/app.jar --zone=us-central1-a
if ($LASTEXITCODE -ne 0) {
    Write-Host "Upload failed - stopping deploy." -ForegroundColor Red
    exit 1
}

Write-Host "==> Uploading deploy script to VM..." -ForegroundColor Cyan
gcloud compute scp remote_deploy.sh springboot-server:/home/Lahari/remote_deploy.sh --zone=us-central1-a
if ($LASTEXITCODE -ne 0) {
    Write-Host "Script upload failed - stopping deploy." -ForegroundColor Red
    exit 1
}

Write-Host "==> Running deploy script on VM..." -ForegroundColor Cyan
gcloud compute ssh springboot-server --zone=us-central1-a --command="bash /home/Lahari/remote_deploy.sh"

if ($LASTEXITCODE -ne 0) {
    Write-Host "==> REMOTE SCRIPT FAILED (exit code $LASTEXITCODE) - deploy did NOT complete successfully." -ForegroundColor Red
    exit 1
}

Write-Host "==> Deploy complete." -ForegroundColor Green