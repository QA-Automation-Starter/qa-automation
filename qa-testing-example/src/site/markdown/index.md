## Architecture

Following the rules below, prevents duplication and keeps configuration simple.

* Test scenario classes inherit from some base Test class
* Test scenarios are written only in business terms
* Technical code, namely loops, conditions, protocol access, is written in step
  methods

Whenever in doubt, look for other tests, even in the `experimental` and `attic`
packages. Do not delete tests just because technology or feature is no longer
relevant, keep them in the `attic`.

## Base Classes

Tests can inherit from one of the `XXXSesionTest` classes:

* `Unmanaged` -- no WebDriver is managed; you have to manage it, if ever needed
* `PerClassWeb` -- one WebDriver instance is opened before any test method runs
  and closed after all finished
* `PerMethodWeb` -- one WebDriver instance opened before each test method and
  closed afterwards

Next: [Folder Structure](architecture/folder-structure.html)
