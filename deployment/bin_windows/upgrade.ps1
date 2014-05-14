function Write-Log()
{
	[CmdletBinding()]
    param (        
        [string]$Message
    )
	
	Add-Content "c:\temp\codeine_upgrade.txt" $Message 
}

function Clean-Log()
{
	Remove-Item "c:\temp\codeine_upgrade.txt"
}

function Download-File()
{
	[CmdletBinding()]
    param (
        [Parameter(Mandatory=$true, ValueFromPipelineByPropertyName=$true)]
        [string]$Source,
        [Parameter(Mandatory=$true, ValueFromPipelineByPropertyName=$true)]
        [string]$Destination
    )
	
	$wc = New-Object System.Net.WebClient
	$wc.DownloadFile($Source, $destination)
}

function Expand-ZipFile()
{
	[CmdletBinding()]
    param (
        [Parameter(Mandatory=$true, ValueFromPipelineByPropertyName=$true)]
        [string]$File,
        [Parameter(Mandatory=$true, ValueFromPipelineByPropertyName=$true)]
        [string]$Destination
    )
	
    $shell = new-object -com shell.application
    $zip = $shell.NameSpace($File)

	if (-Not (Test-Path $Destination))
    {
        mkdir $Destination
    }
	
    foreach($item in $zip.items())
    {
        $shell.Namespace($Destination).copyhere($item, 1556)
    }
}

function Start-MyService()
{
    [CmdletBinding()]
    param (
        [Parameter(Mandatory=$true, ValueFromPipelineByPropertyName=$true)]
        [string]$ServiceName,
        [Parameter(Mandatory=$false, ValueFromPipelineByPropertyName=$true)]
        [int]$WaitForSeconds = 1
    )

    $Service = Get-Service -Name $ServiceName

	if ($Service.Status -ne "Running")
	{
        sc.exe start $ServiceName

		Start-Sleep -Seconds $WaitForSeconds
		
        Write-Log "The service '$ServiceName' is started!"
	}
	else
	{	
		Write-Log "The service '$ServiceName' is already started"
	}
}

function Stop-MyService()
{
    [CmdletBinding()]
    param (
        [Parameter(Mandatory=$true, ValueFromPipelineByPropertyName=$true)]
        [string]$ServiceName,
        [Parameter(Mandatory=$false, ValueFromPipelineByPropertyName=$true)]
        [int]$WaitForSeconds = 1
    )

	if ($Service.Status -ne "Stopped")
	{
		Stop-Service $ServiceName

        Start-Sleep -Seconds $WaitForSeconds

        Write-Log "The service '$ServiceName' is stopped!"
	}
	else
	{	
		Write-Log "The service '$ServiceName' is already stopped"
	}
}

function Get-ServiceInfo()
{
	[CmdletBinding()]
    param (
        [Parameter(Mandatory=$true)]
        [string]$ServiceName
    )

    $output = (dir ((Get-ItemProperty ('HKLM:\System\CurrentControlSet\Services\' + $ServiceName)).ImagePath).Split('"')[1]) | 
        Select FullName, Name, Directory, 
            @{Name='FileVersion'; Expression = {($_.VersionInfo.FileVersion)}},
            @{Name='Path'; Expression = {[string]($_.Directory.ToString())}}

    return $output
}

function Upgrade()
{
	[CmdletBinding()]
	param ()
	
	Clean-Log
	
	Write-Log "Getting service info.."	
    $info = Get-ServiceInfo -ServiceName "Codeine"
	
	Write-Log "Downloading.."	
	Download-File -Source "http://www.iil.intel.com/swiss/netbatch/dist/codeine/beta/dist/codeine.zip" -Destination "c:\temp\codeine.zip"
	
	Write-Log "Unzipping.."
    Expand-ZipFile -File "c:\temp\codeine.zip" -Destination "c:\temp\codeine"
    
	Write-Log "Stopping service.."
    Stop-MyService -ServiceName "Codeine"

	Write-Log "Backing up files.."
	
	$baseDir = $info.Path + "\.."
	
	# /XF "upgrade.ps1" "run.bat" "run_debug.bat" "wrapper_run.bat" "install.bat" "wrapper.exe" 
	#Move-Item -Path ($baseDir + '\*') -Exclude "*.exe,*.bat" -Destination ($baseDir + '\.old\') -Force		
	robocopy ($baseDir + "\") ($baseDir + "\_old\") /XD "_old" /MOVE /E /IS /NJH /NJS /NDL /NS /NC
	
	Write-Log "Deploying new files.."
	
    #Move-Item ("c:\temp\codeine_setup\dist\*") -Destination ($baseDir) -Force
	robocopy "c:\temp\codeine\dist\" ($baseDir + "\") /MOVE /E /IS /NJH /NJS /NDL /NS /NC
	
	Remove-Item "c:\temp\codeine" -Recurse -Force
	Remove-Item "c:\temp\codeine.zip" -Force
	
	Write-Log "Starting service.."
    Start-MyService -ServiceName "Codeine"
}

Upgrade -ErrorAction Stop
