## Folder Structure

In order to find where things are located we need to follow these conventions:

- [src/main/java](src/main/java) - contains: states, actions, verifications and
  other utility code
    - `steps` package -- steps used in `scenarios`, see below
    - `model` package (optional) -- may contain data model of the tested system
    - `utils` package (optional) -- may contain utility code
- [src/test/java](src/test/java)
    - `scenarios` package -- contains test flows; these are using the fixtures,
      actions and verifications from [src/main/java](src/main/java)
    - `data` package (optional) -- may contain test data generators
- [src/test/resources](src/test/resources)
    - `scenarios` package -- contains Unitils/DBUnit data-set files
    - `data` package (optional) -- may contain test data files, namely static
      data
    - `environments` folder -- property files for various environments
    - other configuration files for logging, ssh, databases, etc.

Next: [Adding Tests](adding-tests.html)
