# Environment Setup

More complex testing scenarios may require running multiple types of browsers
on multiple types of operating systems and running multiple types of mobile
emulators or even real devices.

## Self-Signed Certificates

Some services may require special root CA installed. While
on `$JAVA_HOME/jre/lib/security` run:

```
keytool -keystore cacerts -import -alias root_ca.pem
```

The default password is `changeit`.

## Selenium

For a quick standalone Selenium Grid with several Nodes, refer
to [Selenium Hub Docker](selenium-hub-docker.yml). The Grid UI will be
at <http://localhost:4444/ui>.

### Selenium Hub

1. `choco install -y selenium --params "'/role:hub /service /autostart'"`
   >
   > Now, Selenium Grid should be available
   at <http://localhost:4444/grid/console>
   >
   > Additional reading <https://github.com/dhoer/choco-selenium#hub>

### Selenium Node

TBD

## Appium Node

1. `choco install -y nodejs` -- and restart the console to refresh the env vars
2. `npm --proxy http(s)://<host>:<port> install -g appium`
   >
   > Now, **Appium** should be at `%APPDATA%\npm`.
3. Configure Appium
    1. add `nodeconfig.json` to Appium's installation directory
       ```json
        {
          "capabilities": [
            {
              "browserName": "",
              "version": "",
              "maxInstances": 1,
              "platform": "WINDOWS"
            }
          ],
          "configuration": {
            "cleanUpCycle": 2000,
            "timeout": 30000,
            "proxy": "org.openqa.grid.selenium.proxy.DefaultRemoteProxy",
            "url": "http://<machine-hostname>:4723/wd/hub",
            "host": "<machine-hostname>",
            "port": 4723,
            "maxSession": 1,
            "register": true,
            "registerCycle": 5000,
            "hubPort": 4444,
            "hubHost": "<grid-hostname>",
            "hubProtocol": "http"
          }
        }
       ```
    2. add `appium-startup.cmd` to Appium's installation directory
       ```shell
       appium.cmd --nodeconfig %APPDATA%\npm\nodeconfig.json ^
       --log %APPDATA%\npm\appium.log ^
       --log-timestamp ^
       --log-level error:debug ^
       --log-no-colors
       ```
4. add Appium to Windows' Start-up Tasks:
    1. open `%APPDATA%\Microsoft\Windows\Start Menu\Programs\Startup`
    2. create shortcut to `appium-startup.cmd`
    3. run it
   >
   > Open the Selenium Grid Console to ensure proper registration.

## Mobile Testing

### Android Emulator

>
see [Known Issues](https://github.com/QA-Automation-Starter/qa-automation/blob/main/docs/KNOWN-ISSUES.md)
> see also <https://gist.github.com/mrk-han/66ac1a724456cadf1c93f4218c6060ae>

1. `choco install -y android-sdk`
2. `cd %ANDROID_HOME%`
3. `.\tools\bin\sdkmanager.bat --no_https --proxy=http --proxy_host=<host> --proxy_port=<port>
   --install "system-images;android-30;google_apis_playstore;x86"`
4. `.\tools\bin\sdkmanager.bat --no_https --proxy=http --proxy_host=<host> --proxy_port=<port>
   --install "platform-tools"`

> `.\platform-tools\adb.exe devices` -- should list your devices either real or
> emulated

Check Android setup by running `adb devices` -- it should list your device
either real or emulated.

### iOS Simulator

> Install XCode to get the iOS Simulator.

For hybrid applications, check DOM Inspector connects to application
via <chrome://inspect/#devices>

## Windows Applications Testing

1. Enable Windows Developer Mode
2. `choco install -y winappdriver`
   >
   > Now, **WinAppDriver** should be
   > at `%ProgramFiles(x86)%\Windows Application Driver`.
3. for GUI element discovery --
   <https://github.com/microsoft/WinAppDriver/releases/tag/UIR-v1.1> or similar
   tool

If remote file access is required, then OpenSSH, or similar, is required:

`Add-WindowsCapability -Online -Name OpenSSH.Server~~~~0.0.1.0`

## Running GUI Tests on a Remote Windows Machine

For running Android Emulators, Web-Browsers, or Windows applications
on remote machines there must be an open desktop session:

1. `choco install -y autologon`
2. `autologon %USERNAME% $USERDOMAIN% <user-password>`

Next: [Adding Tests](adding-tests.html)
