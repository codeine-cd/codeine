$LOG_FILE = "c:\temp\codeine_upgrade.txt"

function Write-Log()
{
	[CmdletBinding()]
    param (
        [string]$Message
    )

	Add-Content $LOG_FILE $Message
}

function Clean-Log()
{
	if (Test-Path $LOG_FILE) {
		Remove-Item $LOG_FILE
	}
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
        [string]$Destination,
		[Parameter(Mandatory=$true, ValueFromPipelineByPropertyName=$true)]
        [string]$ZipExec
    )

	#if (-Not (Test-Path $Destination))
	#{
	#	mkdir $Destination
	#}

	Write-Log "Extracting..."

	$cmd = ('$ZipExec' + " x " + $File + " -o" + $Destination + " -y")

	Write-Host ("Exec " + $cmd)

	Invoke-Expression ("& " + $cmd)

	#$shell = new-object -com shell.application
	#$zip = $shell.NameSpace($File)

	#foreach($item in $zip.items())
	#{
	#	Write-Log ("Extracting..." + $item)
	#	$shell.Namespace($Destination).copyhere($item, 1556)
	#}

	Write-Log "Extracting Done"
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

function Run-Robocopy()
{
	[CmdletBinding()]
    param (
        [Parameter(Mandatory=$true, ValueFromPipelineByPropertyName=$true)]
        [string]$Source,
        [Parameter(Mandatory=$true, ValueFromPipelineByPropertyName=$true)]
        [string]$Destination,
		[Parameter(Mandatory=$true, ValueFromPipelineByPropertyName=$true)]
        [string]$What,
		[Parameter(Mandatory=$true, ValueFromPipelineByPropertyName=$true)]
        [string[]]$Options
	)

	$cmdArgs = @($Source,$Destination,$What,$Options)
	Write-Log "Running robocopy $cmdArgs"
	robocopy @cmdArgs
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

	Write-Output "Unzipping.."
    Expand-ZipFile -File "c:\temp\codeine.zip" -Destination "c:\temp\codeine\" -ZipExec ($info.Path + "\7za.exe")

	Write-Log "Stopping service.."
    Stop-MyService -ServiceName "Codeine"

	Write-Log "Backing up files.."

	$baseDir = ($info.Path + "\..")

	$from = '"' + $baseDir + '\."';
	$to = '"' + $baseDir + '\..\_old"';
	$what = "*.*";
	$options = @("/XD","_old","/MOVE","/E","/IS","/NJH","/NJS","/NDL","/NS","/NC");
	Run-Robocopy $from $to $what $options

	Write-Log "Deploying new files.."

	$from = 'c:\temp\codeine\dist\.';
	$to = $baseDir + '\.';
	$what = "*.*";
	$options = @("/MOVE","/E","/IS","/NJH","/NJS","/NDL","/NS","/NC");
	Run-Robocopy $from $to $what $options

	Remove-Item "c:\temp\codeine" -Recurse -Force
	Remove-Item "c:\temp\codeine.zip" -Force

	Write-Log "Starting service.."
    Start-MyService -ServiceName "Codeine"
}

Upgrade -ErrorAction Stop