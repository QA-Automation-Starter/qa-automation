# Known Issues

# IntelliJ

* AspectJ + Lombok projects are incorrectly imported (must ensure post-compile
  weaving for each module)

# Android on Local Emulator/Device

* On Samsung S9 the application crashes on Appium's `hideKeyboard()` method
* NoSuchContextException: An unknown server-side error occurred while processing
  the command. Original error: No Chromedriver found that can automate Chrome '
  66.0.3359' --> upgrade Chrome on device/emulator.
* Sometimes the Android Debug Bridge (adb) is getting stuck. This can be
  verified by issuing a `adb devices` command. If the command does not return
  then `adb` process must be killed.
