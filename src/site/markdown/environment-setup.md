# Environment Setup

More complex testing scenarios may require running multiple types of browsers
on multiple types of operating systems and running multiple types of mobile
emulators or even real devices.

Assuming Windows 10
with [Chocolatey installed](https://docs.chocolatey.org/en-us/choco/setup).

## Self-Signed Certificates

Some services may require special root CA installed. While
on `$JAVA_HOME/jre/lib/security` run:

```
keytool -keystore cacerts -import -alias root_ca.pem
```

The default password is `changeit`.

## Selenium

[Selenium Grid](https://www.selenium.dev/documentation/grid/) 4 is required.

See https://www.selenium.dev/documentation/grid/getting_started/

After downloading the apropriate JAR, starting a standalone grid is done via:

```shell
java -jar selenium-server-<version>.jar standalone --selenium-manager true
```

The grid's UI should be available at http://localhost:4444/ui/

Assuming Google Chrome and/or Mozilla Firefox are installed, these will become
available for automation via the grid.

## Appium

[Appium 2](https://appium.io/docs/en/2.5/) is required.

See https://appium.io/docs/en/2.5/quickstart/install/

1. `choco install -y nodejs` -- and restart the console to refresh the env vars
2. `npm --proxy http(s)://<host>:<port> install -g appium`
3. `npm --proxy http(s)://<host>:<port> update -g appium`
4. `appium` - configuration files will be scanned from working dir upwards

## Mobile Testing

### Android Emulator

>
see [Known Issues](https://github.com/QA-Automation-Starter/qa-automation/blob/main/docs/KNOWN-ISSUES.md)
> see also <https://gist.github.com/mrk-han/66ac1a724456cadf1c93f4218c6060ae>

1. `choco install -y jdk8` -- is required for current `android-sdk`
2. `choco install -y android-sdk`
3. `cd %ANDROID_HOME%`
4. `.\tools\bin\sdkmanager --no_https --proxy=http --proxy_host=<host> --proxy_port=<port>
   --install "platform-tools"`
5. `.\tools\bin\sdkmanager --no_https --proxy=http --proxy_host=<host> --proxy_port=<port>
   --install "system-images;android-30;google_apis_playstore;x86_64"`
6. `.\tools\bin\avdmanager create avd --name "google_apis_playstore"
   --package "system-images;android-30;google_apis_playstore;x86_64"`
7. `.\tools\emulator @google_apis_playstore`

> `.\platform-tools\adb.exe devices` -- should list your devices either real or
> emulated

Check Android setup by running `adb devices` -- it should list your device
either real or emulated.

### iOS Simulator

> Install XCode to get the iOS Simulator.

For hybrid applications, check DOM Inspector connects to application
via <chrome://inspect/#devices>

## Windows Applications Testing

Since Selenium 4 it is no longer possible to access WinAppDriver directly,
and Appium 2 is required to act as a bridge in between.

1. Enable Windows Developer Mode
2. `appium driver install --source=npm appium-windows-driver`
3. `appium` - start/restart; should list `windows@2.12.21 (automationName '
   Windows')`
   >
   > The endpoint should be available at http://127.0.0.1:4723.
4. optional: install Appium Inspector --
   https://appium.github.io/appium-inspector/latest/quickstart/installation/
5. optional: check the setup by starting a session using a profile like this:

```json
{
  "appium:automationName": "windows",
  "appium:platformName": "windows",
  "appium:app": "Microsoft.WindowsCalculator_8wekyb3d8bbwe!App"
}
```

`mvn verify -Ptesting-windows` should lauch the Calculator test.

If remote file access is required, then OpenSSH, or similar, is required:

`Add-WindowsCapability -Online -Name OpenSSH.Server~~~~0.0.1.0`

## Running GUI Tests on a Remote Windows Machine

For running Android Emulators, Web-Browsers, or Windows applications
on remote machines there must be an open desktop session:

1. `choco install -y autologon`
2. `autologon %USERNAME% $USERDOMAIN% <user-password>`

Next: [Adding Tests](adding-tests.html)
