$sourcePath = "C:\aryan"
$destinationZip = "C:\aryan\Resume_ATS_Project.zip"

if (Test-Path $destinationZip) {
    Remove-Item $destinationZip
}

Write-Host "Zipping full project structure..."
Compress-Archive -Path "$sourcePath\*" -DestinationPath $destinationZip -Force

Write-Host "Project zipped successfully: $destinationZip"
