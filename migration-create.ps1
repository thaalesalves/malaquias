param (
    [string]$migrationName
)

$xmlNamespace = "http://www.liquibase.org/xml/ns/dbchangelog/1.9"
$xmlFilePath = ".\src\main\resources\db\changelog\changelog-master.xml"
$xmlMigrationBaseUrl = "db/changelog/migrations"
$templateFilePath = ".\src\main\resources\db\changelog\sample_migration.sql"
$destinationFolder = ".\src\main\resources\db\changelog\migrations"
$timestamp = [DateTimeOffset]::Now.ToUnixTimeSeconds()
$newFileName = "${timestamp}_${migrationName}.sql"
$newFilePath = Join-Path -Path $destinationFolder -ChildPath $newFileName

Write-Output "Creating migration with name ${newFileName}..."

Copy-Item -Path $templateFilePath -Destination $newFilePath
(Get-Content -Path $newFilePath) -replace 'create_sample_table', "${timestamp}_${migrationName}" | Set-Content -Path $newFilePath

$xml = [xml](get-content $xmlFilePath)

$newMigration = $xml.CreateElement("include", $xmlNamespace)
$newMigration.SetAttribute("file","$xmlMigrationBaseUrl/$newFileName")

$xml.databaseChangeLog.AppendChild($newMigration) | out-null
$xml.save($xmlFilePath)

Write-Output "Migration with name ${newFileName} created."
